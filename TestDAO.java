import com.poker.database.UserDAO;
import com.poker.model.User;

public class TestDAO {
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();
        try {
            System.out.println("Testing createUser...");
            User u = dao.createUser("testuser", "test@test.com", "password");
            if (u != null) {
                System.out.println("User created: " + u.getUserId());
            } else {
                System.out.println("User creation returned null!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
