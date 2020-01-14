import java.io.Serializable;

public interface User extends Serializable {
    boolean tryPwd(String password);
    String getUserId();
}
