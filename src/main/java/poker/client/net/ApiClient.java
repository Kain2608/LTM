package poker.client.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final String BASE_URL = readBaseUrl();
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();
    private static final Gson gson = new Gson();

    private static String currentToken = null;
    private static String currentUsername = null;
    private static Long currentUserId = null;

    private static String readBaseUrl() {
        String configuredUrl = System.getenv("POKER_API_URL");
        if (configuredUrl == null || configuredUrl.isBlank()) {
            return "http://localhost:8080";
        }
        return configuredUrl.endsWith("/")
                ? configuredUrl.substring(0, configuredUrl.length() - 1)
                : configuredUrl;
    }

    public static String getCurrentToken() {
        return currentToken;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static Long getCurrentUserId() {
        return currentUserId;
    }

    public static void setSession(String token, Long userId, String username) {
        currentToken = token;
        currentUserId = userId;
        currentUsername = username;
    }

    public static void clearSession() {
        currentToken = null;
        currentUserId = null;
        currentUsername = null;
    }

    public static boolean isServerAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/health"))
                    .GET()
                    .timeout(Duration.ofSeconds(2))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public static CompletableFuture<AuthResult> login(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Try HTTP API request first
                JsonObject body = new JsonObject();
                body.addProperty("username", username);
                body.addProperty("password", password);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/auth/signin"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                        .timeout(Duration.ofSeconds(3))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                    String token = json.has("token") ? json.get("token").getAsString() : "";
                    Long id = json.has("id") && !json.get("id").isJsonNull() ? json.get("id").getAsLong() : 1L;
                    String uname = json.has("username") ? json.get("username").getAsString() : username;

                    setSession(token, id, uname);
                    return new AuthResult(true, "Login successful!", token, uname);
                } else {
                    String msg = "Invalid credentials";
                    try {
                        JsonObject err = gson.fromJson(response.body(), JsonObject.class);
                        if (err.has("error")) msg = err.get("error").getAsString();
                    } catch (Exception ignored) {}
                    return new AuthResult(false, msg, null, null);
                }
            } catch (Exception e) {
                return new AuthResult(false, "Cannot connect to authentication server", null, null);
            }
        });
    }

    public static CompletableFuture<AuthResult> register(String username, String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject body = new JsonObject();
                body.addProperty("username", username);
                body.addProperty("email", email);
                body.addProperty("password", password);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/auth/signup"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                        .timeout(Duration.ofSeconds(3))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return new AuthResult(true, "Account created successfully! Please login.", null, username);
                } else {
                    String msg = "Registration failed";
                    try {
                        JsonObject err = gson.fromJson(response.body(), JsonObject.class);
                        if (err.has("error")) msg = err.get("error").getAsString();
                    } catch (Exception ignored) {}
                    return new AuthResult(false, msg, null, null);
                }
            } catch (Exception e) {
                return new AuthResult(false, "Cannot connect to registration server", null, null);
            }
        });
    }

    public static CompletableFuture<String> fetchLeaderboardWins() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/statistics/leaderboard/wins"))
                        .GET()
                        .timeout(Duration.ofSeconds(3))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static CompletableFuture<String> fetchMyProfile() {
        return CompletableFuture.supplyAsync(() -> {
            if (currentToken == null) return null;
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/profile/me"))
                        .header("Authorization", "Bearer " + currentToken)
                        .GET()
                        .timeout(Duration.ofSeconds(3))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static CompletableFuture<String> fetchFriends() {
        return CompletableFuture.supplyAsync(() -> {
            if (currentToken == null) return "[]";
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/friends"))
                        .header("Authorization", "Bearer " + currentToken)
                        .GET()
                        .timeout(Duration.ofSeconds(3))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "[]";
        });
    }

    public static CompletableFuture<String> fetchFriendRequests() {
        return CompletableFuture.supplyAsync(() -> {
            if (currentToken == null) return "[]";
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/friends/requests"))
                        .header("Authorization", "Bearer " + currentToken)
                        .GET()
                        .timeout(Duration.ofSeconds(3))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "[]";
        });
    }

    public static CompletableFuture<Boolean> sendFriendRequest(Long friendId) {
        return CompletableFuture.supplyAsync(() -> {
            if (currentToken == null || friendId == null) return false;
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/friends/request/" + friendId))
                        .header("Authorization", "Bearer " + currentToken)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .timeout(Duration.ofSeconds(3))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.statusCode() == 200;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public static CompletableFuture<Boolean> acceptFriendRequest(Long friendshipId) {
        return CompletableFuture.supplyAsync(() -> {
            if (currentToken == null || friendshipId == null) return false;
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/friends/accept/" + friendshipId))
                        .header("Authorization", "Bearer " + currentToken)
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .timeout(Duration.ofSeconds(3))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.statusCode() == 200;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public static CompletableFuture<Boolean> removeFriend(Long friendshipId) {
        return CompletableFuture.supplyAsync(() -> {
            if (currentToken == null || friendshipId == null) return false;
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/friends/remove/" + friendshipId))
                        .header("Authorization", "Bearer " + currentToken)
                        .DELETE()
                        .timeout(Duration.ofSeconds(3))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.statusCode() == 200;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public static class AuthResult {
        public final boolean success;
        public final String message;
        public final String token;
        public final String username;

        public AuthResult(boolean success, String message, String token, String username) {
            this.success = success;
            this.message = message;
            this.token = token;
            this.username = username;
        }
    }
}
