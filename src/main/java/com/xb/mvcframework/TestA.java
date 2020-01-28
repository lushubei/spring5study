package com.xb.mvcframework;

import org.junit.Test;

import java.lang.annotation.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestA {

    public String name;
    private String age;

    @TestAnnotation(value = {ElementType.TYPE,ElementType.METHOD},url = {ElementType.TYPE,ElementType.PARAMETER})
    public String getName() {
        System.out.println(String.format("获取俺的名字为：%s",name));
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

//    public void setAge(String age) {
//        this.age = age;
//    }



//    public static void main(String[] args) {
//
//        TestA testA = new TestA();
//        testA.setName("xiaobeishiye");
//        testA.getName();
//        System.out.println(testA.getClass().toGenericString());
//    }

}



@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface TestAnnotation{
    ElementType[] value();
    ElementType[] url() default ElementType.TYPE;

}


interface HelloWorld {
    void sayHelloWorld();
    void sayGoodBye();
}

class HelloWorldImpl implements HelloWorld {

    public void sayHelloWorld() {
        // TODO Auto-generated method stub
        System.out.println("Hello World");
    }

    public void sayGoodBye() {
        // TODO Auto-generated method stub
        System.out.println("GoodBye");
    }
}

class Demo01_Proxy implements InvocationHandler {
    //真实对象
    private Object target = null;

    /**
     * 建立代理对象和真实对象的代理关系，并返回代理对象
     */
    public Object bind(Object target) {
        this.target = target;
        return Proxy.newProxyInstance(Demo01_Proxy.class.getClassLoader(), Demo01_Proxy.class.getInterfaces(), this);
    }

    /**
     * 代理方法逻辑
     *  porxy 代理对象
     *  method 当前调度方法
     *  args 当前方法参数
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("进入代理逻辑方法");
        System.out.println("在调度真实对象之前的服务");
        Object obj = method.invoke(target, args); //相当于调用sayHelloWorld方法
        System.out.println("在调度真实对象之后的服务");
        return obj;
    }
    /**
     *测试代码
     */
    @Test
    public void testJdkProxy() {
        Demo01_Proxy jdk = new Demo01_Proxy();
        HelloWorldImpl helloWorld = new HelloWorldImpl();
        //绑定关系，因为挂在HelloWorld接口下，所有声明代理对象HelloWorld proxy
        HelloWorld proxy = (HelloWorld) jdk.bind(helloWorld);
        //注意，此时proxy已经是代理对象，他会进入代理的逻辑方法invoke中
        proxy.sayGoodBye();
    }

}

