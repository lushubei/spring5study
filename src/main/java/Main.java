import com.xb.mvcframework.TestA;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.xb.mvcframework.v1.servlet.XBDispatcherServlet;
import java.util.logging.Logger;
import com.xb.demo.common.LogFormatter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class Main {

    public static void main(String[] args){

        XBDispatcherServlet xbDispatcherServlet = new XBDispatcherServlet();

        try {
            xbDispatcherServlet.init();
        } catch (ServletException e) {
            e.printStackTrace();
        }

    }
//    public static void main(String[] args) {
//
//
//        Class clazz = null;
//        try {
//            Logger log = LogFormatter.getLog(Main.class);
//            log.info("This is test java util log");
//            log.info("这里是 main");
//            clazz = Class.forName("com.xb.mvcframework.TestA");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        Method[] methods = clazz.getMethods();
//        Field[] fields = clazz.getDeclaredFields();
//
//        Method m = null;
//        try {
//            m = clazz.getMethod("getName");
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//
//        Object s = null;
//        TestA testA = null;
//        try {
//            testA = (TestA) clazz.newInstance();
//            testA.setName("xiaobei");
//            s = m.invoke(testA);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        for(Field f:fields){
//            if(f.getName().equals("age")){
//                f.setAccessible(true);
//            }
//
//            try {
//                f.set(testA,"just a test");
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//
//        System.out.println("Hello World!:" + s);
//        System.out.println("hi" + testA.getAge());
//
//    }
//


}
