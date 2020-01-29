import org.junit.Test;

import com.xb.vip.spring.mvcframework.context.XBApplicationContext;

public class Vipspringtest {


    @Test
    public void run(){
        XBApplicationContext xbApplicationContext = new XBApplicationContext("./application.properties");

        try {
            Object xb = xbApplicationContext.getBean("xb");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
