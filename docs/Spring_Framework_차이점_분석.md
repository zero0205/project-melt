# Spring Framework vs Melt Spring 아키텍처 차이점 분석

## 📋 개요

이 문서는 실제 Spring Framework의 HTTP 요청 처리 아키텍처와 우리가 구현한 Melt Spring 프레임워크의 차이점을 분석합니다.

---

## 🏗️ 전체 아키텍처 비교

### **Spring Framework (실제)**
```
HTTP 요청
    ↓
FrameworkServlet (HttpServletBean 상속)
    ↓
processRequest() - 공통 전처리
    ↓
doService() - 템플릿 메소드
    ↓
DispatcherServlet - 실제 디스패칭
    ↓
HandlerMapping → HandlerAdapter → Controller
    ↓
ViewResolver → View
```

### **Melt Spring (현재 구현)**
```
HTTP 요청
    ↓
DispatcherServlet (HttpServlet 직접 상속)
    ↓
HandlerMapping → Controller
    ↓
JSON 응답 직접 처리
```

---

## 🔍 주요 차이점 상세 분석

### **1. 서블릿 계층 구조**

#### **Spring Framework (실제 상속 구조)**
```java
HttpServlet
    ↓
HttpServletBean         // Spring 서블릿 기본 설정 (Bean 프로퍼티 매핑)
    ↓
FrameworkServlet        // 웹 프레임워크 공통 기능 (추상 클래스)
    ↓
DispatcherServlet       // MVC 디스패칭 로직 구현
```

**핵심 메소드 흐름:**
```java
// FrameworkServlet에서 모든 HTTP 메소드가 여기로 위임
protected final void processRequest(HttpServletRequest req, HttpServletResponse resp)

// 템플릿 메소드 - DispatcherServlet에서 구현
protected abstract void doService(HttpServletRequest req, HttpServletResponse resp)

// DispatcherServlet의 실제 구현
protected void doDispatch(HttpServletRequest req, HttpServletResponse resp)
```

#### **Melt Spring**
```java
HttpServlet
    ↓
DispatcherServlet       // 모든 기능을 한 번에 처리
```

**🔹 영향:**
- **Spring**: 계층별 책임 분리로 확장성 높음
- **Melt**: 단순하지만 확장 시 복잡해질 수 있음

---

### **2. 요청 처리 흐름**

#### **Spring Framework**
```java
public abstract class FrameworkServlet extends HttpServletBean {

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) {
        processRequest(req, resp);  // 공통 처리 진입점
    }

    protected final void processRequest(HttpServletRequest req, HttpServletResponse resp) {
        // 1. LocaleContext 설정
        // 2. RequestAttributes 설정
        // 3. 공통 전처리 (로깅, 보안, 트랜잭션 등)

        try {
            doService(req, resp);  // 템플릿 메소드 호출
        } finally {
            // 4. 공통 후처리 (리소스 정리 등)
        }
    }

    protected abstract void doService(HttpServletRequest req, HttpServletResponse resp);
}

public class DispatcherServlet extends FrameworkServlet {
    @Override
    protected void doService(HttpServletRequest req, HttpServletResponse resp) {
        // 순수 MVC 디스패칭 로직만 집중
        doDispatch(req, resp);
    }
}
```

#### **Melt Spring**
```java
public class DispatcherServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        handleRequest("GET", req, resp);  // 직접 처리
    }

    private void handleRequest(String method, HttpServletRequest req, HttpServletResponse resp) {
        // 모든 로직이 한 곳에 집중
        // 1. 핸들러 찾기
        // 2. 파라미터 바인딩
        // 3. 컨트롤러 실행
        // 4. 응답 처리
    }
}
```

**🔹 영향:**
- **Spring**: `processRequest()`에서 AOP, 인터셉터 등 적용 용이
- **Melt**: 공통 처리 로직 적용할 지점이 명확하지 않음

---

### **3. 컨텍스트 계층 구조**

#### **Spring Framework**
```
ApplicationContext (Root Context)
    ↓
WebApplicationContext (Servlet Context)
    ↓
각 DispatcherServlet마다 별도의 WebApplicationContext
```

#### **Melt Spring**
```
ApplicationContext
    ↓
WebApplicationContext (단일)
```

**🔹 영향:**
- **Spring**: 여러 서블릿 지원, 계층적 컨텍스트 관리
- **Melt**: 단순하지만 확장성 제한

---

### **4. 핸들러 처리 방식**

#### **Spring Framework (실제 복잡성)**

**컨트롤러 메소드 하나의 실행 과정:**
```java
@Controller
public class UserController {

    @RequestMapping("/users")
    public ModelAndView getUsers() {
        // 단순해 보이지만 실제로는 아래 6단계를 거침
        return new ModelAndView("userList", "users", userList);
    }
}
```

**실제 내부 처리 과정:**
```java
// DispatcherServlet.doDispatch() 메소드에서
public class DispatcherServlet extends FrameworkServlet {

    protected void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        // 1. HandlerMapping: URL에 맞는 핸들러 찾기
        HandlerExecutionChain mappedHandler = getHandler(req);

        // 2. HandlerAdapter: 핸들러 실행 방식 결정
        //    - RequestMappingHandlerAdapter (어노테이션 기반)
        //    - SimpleControllerHandlerAdapter (Controller 인터페이스 기반)
        //    - HttpRequestHandlerAdapter (HttpRequestHandler 기반)
        HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

        // 3. 인터셉터 전처리
        if (!mappedHandler.applyPreHandle(req, resp)) return;

        // 4. 실제 핸들러 실행 (여기서 또 복잡한 과정)
        //    4-1. ArgumentResolver: 메소드 파라미터 준비
        //    4-2. 실제 메소드 호출
        //    4-3. ReturnValueHandler: 반환값 처리
        ModelAndView mv = ha.handle(req, resp, mappedHandler.getHandler());

        // 5. 인터셉터 후처리
        mappedHandler.applyPostHandle(req, resp, mv);

        // 6. 뷰 렌더링
        //    6-1. ViewResolver: 뷰 이름 → 실제 View 객체 변환
        //    6-2. View.render(): 모델 데이터와 함께 최종 응답 생성
        processDispatchResult(req, resp, mappedHandler, mv, dispatchException);
    }
}
```

**RequestMappingHandlerAdapter의 내부 처리:**
```java
// 4단계에서 실제로 일어나는 일
public class RequestMappingHandlerAdapter {

    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        // 4-1. ArgumentResolver가 메소드 파라미터 준비
        //      - @RequestParam, @PathVariable, @RequestBody 등 처리
        //      - HttpServletRequest, Model 등 자동 주입
        Object[] args = getMethodArgumentValues(req, resp, handler);

        // 4-2. 실제 컨트롤러 메소드 호출
        Object returnValue = invokeMethod(handler, args);

        // 4-3. ReturnValueHandler가 반환값 처리
        //      - ModelAndView → 그대로 사용
        //      - String → ViewResolver로 뷰 해석
        //      - @ResponseBody → JSON 변환
        return handleReturnValue(returnValue, req, resp);
    }
}
```

#### **Melt Spring**
```java
// 1. HandlerMapping에서 직접 실행
HandlerMethod handler = handlerMapping.getHandler(uri, httpMethod);

// 2. 파라미터 바인딩
Object[] methodArgs = handlerMapping.prepareMethodArguments(handler, req, uri);

// 3. 메소드 직접 호출
Object result = handler.getMethod().invoke(handler.getController(), methodArgs);

// 4. JSON 응답 직접 처리
resp.getWriter().write(String.valueOf(result));
```

**🔹 Spring의 HandlerAdapter 패턴 상세:**
```java
// Spring은 다양한 타입의 컨트롤러를 지원하기 위해 어댑터 패턴 사용
public interface HandlerAdapter {
    boolean supports(Object handler);  // 이 어댑터가 해당 핸들러를 처리할 수 있는지
    ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler);
}

// 1. 어노테이션 기반 컨트롤러용
public class RequestMappingHandlerAdapter implements HandlerAdapter {
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod;  // @RequestMapping 메소드
    }
}

// 2. Controller 인터페이스 구현체용
public class SimpleControllerHandlerAdapter implements HandlerAdapter {
    public boolean supports(Object handler) {
        return handler instanceof Controller;  // Controller 인터페이스
    }
}

// 3. 함수형 핸들러용 (Spring 5+)
public class RouterFunctionAdapter implements HandlerAdapter {
    public boolean supports(Object handler) {
        return handler instanceof RouterFunction;  // 함수형 라우터
    }
}
```

**🔹 영향:**
- **Spring**:
  - HandlerAdapter 패턴으로 다양한 핸들러 타입 지원
  - 새로운 컨트롤러 스타일 추가 시 어댑터만 구현하면 됨
  - ArgumentResolver, ReturnValueHandler로 세밀한 제어
- **Melt**:
  - Method 기반으로만 제한됨
  - 파라미터 바인딩이 HandlerMapping에 하드코딩됨
  - 확장 시 기존 코드 수정 필요

---

### **5. 뷰 처리 방식**

#### **Spring Framework**
```java
// ViewResolver를 통한 뷰 해석
ViewResolver viewResolver = getViewResolver();
View view = viewResolver.resolveViewName(viewName, locale);

// 모델 데이터와 함께 뷰 렌더링
view.render(model, req, resp);
```

#### **Melt Spring**
```java
// JSON 문자열 직접 응답
resp.setContentType("application/json;charset=UTF-8");
resp.getWriter().write(String.valueOf(result));
```

**🔹 영향:**
- **Spring**: 다양한 뷰 기술 지원 (JSP, Thymeleaf, JSON 등)
- **Melt**: JSON만 지원

---

## 🎯 Melt Spring의 장단점

### **✅ 장점**

1. **단순함**: 이해하기 쉬운 구조
2. **빠른 개발**: 복잡한 설정 없이 바로 사용 가능
3. **학습 용이**: Spring의 핵심 개념 학습에 집중 가능
4. **가벼움**: 최소한의 기능으로 가볍게 동작

### **❌ 단점**

1. **확장성 제한**: 새로운 기능 추가 시 기존 코드 수정 필요
2. **AOP 적용 어려움**: 공통 처리 로직 적용할 지점이 불명확
3. **인터셉터 패턴 부재**: 요청/응답 가로채기 어려움
4. **뷰 기술 제한**: JSON만 지원
5. **테스트 어려움**: 통합된 구조로 인한 단위 테스트 제약

---

## 🚀 향후 개선 방향

### **Phase 1: 현재 (7주차)**
- REST API 어노테이션 확장
- 기본 AOP 구현
- 현재 구조 유지하면서 기능 확장

### **Phase 2: 중기 개선**
```java
// FrameworkServlet 도입
public abstract class FrameworkServlet extends HttpServlet {
    protected final void processRequest(HttpServletRequest req, HttpServletResponse resp) {
        // 공통 전처리
        doService(req, resp);
        // 공통 후처리
    }
    protected abstract void doService(HttpServletRequest req, HttpServletResponse resp);
}
```

### **Phase 3: 장기 개선**
- HandlerAdapter 패턴 도입
- ViewResolver 구현
- 인터셉터 체인 지원
- 다중 DispatcherServlet 지원

---

## 💡 학습 포인트

### **현재 구조에서 배울 수 있는 것**
1. **Spring MVC 핵심 개념**: Controller, HandlerMapping, DI
2. **어노테이션 기반 설정**: @Controller, @RequestMapping 등
3. **리플렉션 활용**: 동적 메소드 호출, 파라미터 바인딩
4. **IoC 컨테이너**: Bean 생성 및 관리

### **실제 Spring과의 차이로 배울 수 있는 것**
1. **아키텍처 설계 원칙**: 계층 분리, 책임 분리
2. **확장성 고려사항**: 템플릿 메소드 패턴, 어댑터 패턴, 전략 패턴
3. **프레임워크 설계 철학**: 유연성 vs 단순성의 트레이드오프
4. **복잡성의 이유**:
   - 하나의 컨트롤러 메소드도 6단계 처리 과정
   - ArgumentResolver, ReturnValueHandler, ViewResolver 등 각각의 역할
   - HandlerAdapter 패턴으로 다양한 컨트롤러 스타일 지원
   - 인터셉터 체인으로 AOP 지원

---

## 🔚 결론

Melt Spring은 **학습 목적**에는 매우 적합한 구조입니다:

- Spring의 핵심 개념을 이해하기 쉬움
- 복잡한 설정 없이 빠른 실습 가능
- MVC 패턴의 본질에 집중 가능

하지만 **실제 프로덕션 환경**에서는 Spring Framework의 계층적 구조가 필요한 이유도 명확합니다:

- 확장성과 유지보수성
- 다양한 요구사항 대응
- 엔터프라이즈급 기능 지원

이러한 차이점을 이해하고 단계적으로 개선해 나가는 것이 Spring Framework의 설계 철학을 더 깊이 이해하는 길입니다.