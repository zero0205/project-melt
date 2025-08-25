package com.melt;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Container container = new Container();

        container.registerBean("userService", new UserService());

        UserService userService1 = (UserService) container.getBean("userService");
        UserService userService2 = container.getBean("userService", UserService.class);
        UserService userService3 = container.getBean(UserService.class);

        userService1.printMsg();
        System.out.println(userService1 == userService2);
        System.out.println(userService2 == userService3);
    }
}