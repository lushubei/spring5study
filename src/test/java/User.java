import java.io.Serializable;

public class User implements Serializable {
    private final Long serialVersionUID = 37363L;
    int age;

    public User(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "serialVersionUID=" + serialVersionUID +
                ", age=" + age +
                '}';
    }
}