package poker.model;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class UserManager {
    private static final String FILE_PATH = "users.txt";

    private static void ensureFileExists() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("admin:admin");
                writer.println("player1:123456");
                writer.println("user1:123456");
                System.out.println("Created initial mock users.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean validateLogin(String username, String password) {
        ensureFileExists();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(":");
                if (parts.length == 2) {
                    if (parts[0].equalsIgnoreCase(username.trim()) && parts[1].equals(password.trim())) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean userExists(String username) {
        ensureFileExists();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(":");
                if (parts.length >= 1 && parts[0].equalsIgnoreCase(username.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean registerUser(String username, String password) {
        ensureFileExists();
        if (userExists(username)) {
            return false;
        }
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(username.trim() + ":" + password.trim());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
