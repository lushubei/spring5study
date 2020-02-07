import org.junit.Test;

import java.io.*;


public class StudyTest {

    @Test
    /**
     * 测试序列化代码
     */
    public void test01(){

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream( new FileOutputStream("./use.out"));

            objectOutputStream.writeObject(new User(11));
            objectOutputStream.flush();
            objectOutputStream.close();


            ObjectInputStream objectInputStream = new ObjectInputStream( new FileInputStream("./use.out"));
            User user = (User) objectInputStream.readObject();
            objectInputStream.close();
            System.out.println(user.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
}

}