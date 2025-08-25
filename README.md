# project-melt
미니 of 미니 스프링 프레임워크 따라 만들며 스프링의 구조와 원리 학습하기

## 📋 전체 목표

Spring의 핵심 기능들을 직접 구현하며 프레임워크의 내부 동작 원리 이해하기

---

## 📅 주차별 상세 계획

### 2주차: Java 환경 설정 + IoC 컨테이너 기초

**🎯 구현 목표:**

- 개발 환경 설정
- **IoC (Inversion of Control) 컨테이너** 기본 구현

**📚 병행 학습 (1시간 중 20분):**

- **Java 기본 개념**
    - 클래스, 패키지, import 시스템
    - 접근 제어자 (public, private, protected)
    - Java Collections (HashMap, ArrayList)
- **IoC와 DI 개념** - **핵심!**

**🧠 핵심 개념 이해:**

- **IoC (Inversion of Control)**: 객체 생성과 관리의 제어권을 개발자가 아닌 프레임워크가 가지는 것
- **Container**: 객체들을 생성하고 관리하는 공간
- **Bean**: Spring 컨테이너가 관리하는 객체

**💻 구현 내용 (1시간 중 40분):**

```java
package com.example.container;

import java.util.HashMap;
import java.util.Map;

// IoC의 기본 아이디어를 구현한 간단한 컨테이너
public class IoContainer {
    private Map<String, Object> beans = new HashMap<>();

    // 컨테이너가 객체를 생성하고 관리 (IoC 원리)
    public void register(String name, Object bean) {
        beans.put(name, bean);
        System.out.println("컨테이너가 " + name + " 빈을 관리합니다.");
    }

    public Object getBean(String name) {
        return beans.get(name);
    }
}

```

**🔑 학습 키워드:**

- **IoC (Inversion of Control)** ⭐⭐⭐
- **Container**, **Bean** 개념
- Java 패키지 시스템, Collections Framework

---

### 3주차: Java Reflection + Annotation 기초

**🎯 구현 목표:**

- **Java Reflection** - Spring 구현의 핵심 기술
- **Java Annotation** - 메타데이터를 코드에 추가하는 방법

**📚 병행 학습 (20분):**

- **Java Reflection API** - Spring의 모든 기능의 기반 기술
- **Java Annotation** - 커스텀 어노테이션 정의와 처리
- **Java 예외 처리** - try-catch, throws 키워드

**🧠 핵심 개념 이해:**

- **Reflection**: 런타임에 클래스의 정보를 조회하고 조작하는 기능
- **Annotation**: 코드에 메타데이터를 붙이는 방법
- **Dynamic Object Creation**: 문자열로 클래스를 찾아서 인스턴스 생성

**💻 구현 내용 (40분):**

```java
// 커스텀 어노테이션 정의
@Retention(RetentionPolicy.RUNTIME)  // 런타임까지 유지
@Target(ElementType.TYPE)  // 클래스에만 적용
public @interface Component {
    String value() default "";
}

@Component  // 상속 개념
public @interface Service {
    String value() default "";
}

// Reflection + Annotation 조합
public class AnnotationContainer {
    private Map<String, Object> beans = new HashMap<>();

    // 클래스 이름으로 동적 객체 생성
    public Object createBean(String className) throws Exception {
        Class<?> clazz = Class.forName(className);  // 클래스 정보 로드

        // 어노테이션 확인
        if (clazz.isAnnotationPresent(Component.class)) {
            return clazz.getDeclaredConstructor().newInstance();  // 인스턴스 생성
        }
        return null;
    }
}

// 실제 사용
@Component
public class UserService {
    // Spring이 이 클래스를 자동으로 Bean으로 등록
}

```

**🔑 학습 키워드:**

- **Java Reflection API** ⭐⭐⭐
- **Java Annotation** ⭐⭐⭐
- **@Component, @Service** (Stereotype Annotations)
- `Class.forName()`, `getDeclaredConstructor()`, `newInstance()`
- `@Retention`, `@Target`, `isAnnotationPresent()`

---

### 4주차: Component Scan + 의존성 주입(DI) 구현

**🎯 구현 목표:**

- **Component Scan**: 어노테이션이 붙은 클래스 자동으로 찾아서 Bean 등록
- **DI (Dependency Injection)** 핵심 기능 구현 + `@Autowired`

**📚 병행 학습 (20분):**

- **의존성 주입(DI) 개념** - **핵심!**
- **패키지 스캐닝 기초**
- **디자인 패턴** (Singleton, Factory)

**🧠 핵심 개념 이해:**

- **Component Scan**: 패키지를 훑어보며 어노테이션 붙은 클래스들을 자동으로 Bean으로 등록
- **DI (Dependency Injection)**: 객체가 필요한 의존성을 직접 생성하지 않고 외부에서 주입받는 방식
- **@Autowired**: 의존성을 자동으로 주입해주는 어노테이션
- **Bean Lifecycle**: Bean이 생성되고 의존성이 주입되는 과정

**💻 구현 내용 (40분):**

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {}

@Component
public class UserService {
    @Autowired
    private UserRepository userRepository; // 이걸 자동으로 주입하게 만들기

    public void saveUser(User user) {
        userRepository.save(user); // DI를 통해 결합도 낮춤
    }
}

// Component Scan + DI 처리 로직
public void scanComponents(String packageName) throws Exception {
    // 1. 패키지 내 모든 .class 파일 찾기
    // 2. Class.forName()으로 클래스 로드
    // 3. 어노테이션 확인 후 Bean 등록
    // 4. 의존성 주입 처리

    if (clazz.isAnnotationPresent(Component.class)) {
        Object bean = clazz.getDeclaredConstructor().newInstance();
        registerBean(clazz.getSimpleName(), bean);
        injectDependencies(bean); // DI 처리
    }
}

public void injectDependencies(Object bean) throws Exception {
    Field[] fields = bean.getClass().getDeclaredFields();
    for (Field field : fields) {
        if (field.isAnnotationPresent(Autowired.class)) {
            Object dependency = getBean(field.getType().getSimpleName());
            field.setAccessible(true);
            field.set(bean, dependency); // 의존성 주입!
        }
    }
}

```

**🔑 학습 키워드:**

- **Component Scan** ⭐⭐⭐
- **DI (Dependency Injection)** ⭐⭐⭐
- **@Autowired** ⭐⭐⭐
- **Bean Lifecycle**, **Coupling/Decoupling**
- `Field.set()`, `Field.get()`, `setAccessible()`

---

### 5주차: MVC 패턴 + DispatcherServlet 구현

**🎯 구현 목표:**

- **MVC (Model-View-Controller) 패턴** 구현
- **DispatcherServlet**: 모든 HTTP 요청을 받아서 적절한 Controller로 분배

**📚 병행 학습 (20분):**

- **MVC 패턴**
- **HTTP 기본 개념**
- **Servlet API 기초**

**🧠 핵심 개념 이해:**

- **MVC Pattern**: Model(데이터), View(화면), Controller(제어) 분리
- **DispatcherServlet**: Spring MVC의 핵심, 모든 요청의 진입점
- **HandlerMapping**: URL과 Controller 메소드를 매핑
- **Front Controller Pattern**: 모든 요청을 하나의 Controller가 받아서 처리

**💻 구현 내용 (40분):**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value();
}

@Controller  // MVC의 Controller 역할
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/users")  // URL 매핑
    public String getUsers() {
        List<User> users = userService.getAllUsers();
        return "User count: " + users.size(); // 간단한 응답
    }
}

// DispatcherServlet 구현
public class MyDispatcherServlet extends HttpServlet {
    private Map<String, Object> controllers = new HashMap<>();
    private Map<String, Method> handlerMapping = new HashMap<>();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        Method handlerMethod = handlerMapping.get(uri);

        if (handlerMethod != null) {
            try {
                Object controller = controllers.get(handlerMethod.getDeclaringClass().getSimpleName());
                String result = (String) handlerMethod.invoke(controller);
                resp.getWriter().write(result);
            } catch (Exception e) {
                resp.getWriter().write("Error: " + e.getMessage());
            }
        } else {
            resp.getWriter().write("Handler not found for: " + uri);
        }
    }
}

```

**🔑 학습 키워드:**

- **MVC Pattern** ⭐⭐⭐
- **DispatcherServlet** ⭐⭐⭐
- **@Controller**, **@RequestMapping**
- **HandlerMapping**, **Front Controller Pattern**
- `HttpServlet`, `doGet()`, `doPost()`

---

### 6주차: REST API + AOP 기초

**🎯 구현 목표:**

- **RESTful API** 구현
- **AOP (Aspect-Oriented Programming)** 기초 - 횡단 관심사 분리

**📚 병행 학습 (20분):**

- **REST API 원칙**
- **JSON 라이브러리** (Jackson 또는 Gson)
- **AOP 개념**

**🧠 핵심 개념 이해:**

- **REST API**: Representational State Transfer, HTTP를 잘 활용한 API 설계 원칙
- **AOP (Aspect-Oriented Programming)**: 횡단 관심사(로깅, 보안, 트랜잭션)를 분리하는 프로그래밍 패러다임
- **Cross-cutting Concerns**: 여러 모듈에 걸쳐 나타나는 공통 기능들
- **Proxy Pattern**: AOP 구현의 핵심 패턴

**💻 구현 내용 (40분):**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestController {}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetMapping {
    String value();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostMapping {
    String value();
}

@RestController  // REST API 컨트롤러
public class ApiController {

    @Autowired
    private UserService userService;

    @GetMapping("/api/users")  // HTTP GET 메소드
    public String getUsers() {
        List<User> users = userService.getAllUsers();
        // 간단한 JSON 형태로 반환 (실제로는 Jackson 사용)
        return "{\\"users\\": " + users.size() + "}";
    }

    @PostMapping("/api/users")  // HTTP POST 메소드
    public String createUser() {
        // POST 요청 처리 로직
        return "{\\"message\\": \\"User created\\"}";
    }
}

// AOP 기초 - 로깅 Aspect (간단 버전)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {}

public class LoggingAspect {
    public static Object logExecutionTime(Method method, Object target, Object[] args) throws Exception {
        long start = System.currentTimeMillis();
        Object result = method.invoke(target, args);  // 실제 메소드 실행
        long end = System.currentTimeMillis();
        System.out.println(method.getName() + " 실행 시간: " + (end - start) + "ms");
        return result;
    }
}

```

**🔑 학습 키워드:**

- **REST API** ⭐⭐⭐
- **AOP (Aspect-Oriented Programming)** ⭐⭐⭐
- **@RestController**, **@GetMapping**, **@PostMapping**
- **Cross-cutting Concerns**, **Proxy Pattern**
- **JSON 직렬화/역직렬화**

---

### 7주차: 데이터 액세스 + 트랜잭션 관리

**🎯 구현 목표:**

- **DAO/Repository 패턴** 구현
- **JdbcTemplate** 기초 구현
- **트랜잭션 관리** 기본 개념

**📚 병행 학습 (20분):**

- **JDBC API**
- **DAO/Repository 패턴**
- **트랜잭션 개념**

**🧠 핵심 개념 이해:**

- **DAO (Data Access Object)**: 데이터 액세스 로직을 캡슐화하는 패턴
- **Repository Pattern**: 도메인과 데이터 액세스 계층을 분리하는 패턴
- **JdbcTemplate**: Spring의 JDBC 추상화 - 반복적인 코드 제거
- **Transaction**: 데이터베이스 작업의 원자성(All or Nothing) 보장
- **@Transactional**: 선언적 트랜잭션 관리

**💻 구현 내용 (40분):**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {}

@Repository  // 데이터 액세스 계층 표시
public class UserRepository {

    @Autowired
    private MyJdbcTemplate jdbcTemplate;  // JDBC 추상화

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, rs -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            return user;
        });
    }

    @Transactional  // 트랜잭션 관리
    public void saveUser(User user) {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        jdbcTemplate.update(sql, user.getName(), user.getEmail());
    }
}

// 간단한 JdbcTemplate 구현
public class MyJdbcTemplate {
    private DataSource dataSource;

    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(rowMapper.mapRow(rs));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException;
}

```

**🔑 학습 키워드:**

- **DAO/Repository Pattern** ⭐⭐⭐
- **JdbcTemplate** ⭐⭐⭐
- **@Repository**, **@Transactional**
- **Transaction Management**
- `Connection`, `PreparedStatement`, `ResultSet`
- **Connection Pool**, **DataSource**

---

### 8주차: 프로젝트 완성 및 통합 테스트

**🎯 구현 목표:**

- 전체 기능 통합 및 테스트
- 간단한 웹 애플리케이션 개발
- 성능 최적화 및 리팩토링

**📚 병행 학습 (20분):**

- 전체 복습 및 실제 Spring과 비교
- 부족한 부분 보충 학습

**💻 최종 결과물 (40분):**

- 전체 기능이 통합된 미니 Spring 프레임워크
- 이를 사용한 간단한 웹 애플리케이션 (사용자 관리 시스템)
- 단위 테스트 및 통합 테스트

**🔑 학습 키워드:**

- **Integration Testing**
- **Unit Testing**
- **Performance Optimization**
- **Refactoring**
- **Spring Boot** (비교 학습)

---

## 🎯 Spring 핵심 개념 총정리

### 📌 반드시 이해해야 할 Spring 핵심 개념들:

**1. IoC (Inversion of Control) ⭐⭐⭐**

- 제어의 역전: 객체 생성과 관리를 개발자가 아닌 Spring이 담당
- 전통적 방식: 개발자가 직접 `new` 키워드로 객체 생성
- Spring 방식: 컨테이너가 객체를 생성하고 관리

**2. DI (Dependency Injection) ⭐⭐⭐**

- 의존성 주입: 객체가 필요한 의존성을 외부에서 주입받는 방식
- 결합도를 낮춰서 테스트하기 쉽고 유지보수성 향상

**3. Bean & Container ⭐⭐⭐**

- Bean: Spring이 관리하는 객체
- Container: Bean들을 생성, 관리, 소멸시키는 공간 (ApplicationContext)

**4. Annotation 기반 설정 ⭐⭐⭐**

- @Component, @Service, @Repository, @Controller
- @Autowired, @RequestMapping, @Transactional
- XML 설정 대신 어노테이션으로 간편하게 설정

**5. AOP (Aspect-Oriented Programming) ⭐⭐**

- 횡단 관심사(로깅, 보안, 트랜잭션) 분리
- 핵심 비즈니스 로직과 부가 기능을 분리하여 코드 중복 제거

**6. MVC Pattern ⭐⭐⭐**

- Model: 데이터와 비즈니스 로직
- View: 사용자 인터페이스
- Controller: 사용자 입력 처리 및 Model과 View 연결

**7. Template Pattern (JdbcTemplate) ⭐⭐**

- 반복적인 코드(Connection 생성/해제 등) 추상화
- 개발자는 SQL과 비즈니스 로직에만 집중

---

## ⏰ 하루 일정 예시 (1시간 기준)

### 매일 1시간 투자:

**40분:** 구현 작업 (실제 코딩) - **핵심 기능만20분:** 필수 Java 개념 학습 (Reflection, Annotation 등)

### 주말 (선택사항):

- 평일에 놓친 부분 보충
- 여유가 있을 때만 심화 학습

---

## 🚨 각 주차별 최소 달성 목표

**절대 놓치면 안 되는 것들:**

### 2주차: IoC 컨테이너로 객체 저장/조회하는 기본 코드

### 3주차: Class.forName()으로 객체 생성 + @Component 어노테이션 인식

### 4주차: Component Scan + @Autowired로 의존성 주입 확인

### 5주차: 웹 브라우저에서 요청 보내면 Controller에서 응답 받기

### 6주차: REST API로 JSON 형태 데이터 반환하기

### 7주차: 데이터베이스에서 데이터 읽어오기

### 8주차: 전체 기능이 동작하는 웹 애플리케이션 완성

---

## 💡 현실적 접근법 (1시간/일 기준)

### ✅ 시간 절약 팁:

- **완벽 추구 금지**: 동작만 하면 OK
- **복붙 허용**: 인터넷 예제 코드 적극 활용
- **기능 축소**: 너무 복잡하면 단순화
- **에러 무시**: 30분 이상 막히면 일단 넘어가기

### 📚 학습 우선순위 (시간 부족시):

1. **필수 (꼭 해야 함)**: IoC, DI, Reflection, Annotation
2. **중요 (시간 있으면)**: AOP, MVC 패턴
3. **부가 (나중에)**: 트랜잭션, 성능 최적화

---

## 🎯 주차별 산출물

### 매주 공유할 내용:

1. **구현 코드**: GitHub 리포지토리 링크
2. **학습 노트**: 주요 개념 정리 및 구현 과정에서 배운 점
3. **이슈 및 해결책**: 구현 중 만난 문제와 해결 방법
4. **다음 주 계획**: 구현할 기능과 예상 난이도

### 최종 산출물:

- 완성된 미니 Spring 프레임워크
- 프레임워크를 사용한 샘플 웹 애플리케이션
- 전체 학습 과정 정리 문서

---

## 📚 추천 학습 자료

### 필수 개념:

- Java Reflection API
- Java Annotation
- Servlet API
- Design Patterns (Singleton, Factory, Proxy, Template Method)

### 참고 자료:

- Spring Framework 공식 문서
- "토비의 스프링" 도서
- "스프링 인 액션" 도서
- Spring Framework 소스 코드 (GitHub)

---

## 💡 추가 팁

1. **단계별 접근**: 각 주차의 기능이 완성된 후 다음 주차로 진행
2. **테스트 주도**: 각 기능별로 간단한 테스트 코드 작성
3. **코드 리뷰**: 스터디원들과 상호 코드 리뷰 진행
4. **실제 Spring과 비교**: 구현한 기능을 실제 Spring과 비교하며 차이점 분석
