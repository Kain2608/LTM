import com.poker.util.JwtUtil;

public class TestJWT {
    public static void main(String[] args) {
        try {
            String token = JwtUtil.generateToken(2L, "testuser");
            System.out.println("Token: " + token);
            Long id = JwtUtil.validateTokenAndGetUserId(token);
            System.out.println("Parsed ID: " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
