import com.poker.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class TestAuth {
    public static void main(String[] args) {
        Long originalId = 2L;
        String token = JwtUtil.generateToken(originalId, "testuser");
        System.out.println("Generated token: " + token);
        
        Long parsedId = JwtUtil.validateTokenAndGetUserId(token);
        System.out.println("Parsed ID: " + parsedId);
    }
}
