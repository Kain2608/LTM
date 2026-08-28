import com.sun.net.httpserver.Headers;

public class TestHeaders {
    public static void main(String[] args) {
        Headers h = new Headers();
        h.add("authorization", "Bearer token");
        System.out.println("Uppercase Authorization: " + h.getFirst("Authorization"));
        System.out.println("Lowercase authorization: " + h.getFirst("authorization"));
    }
}
