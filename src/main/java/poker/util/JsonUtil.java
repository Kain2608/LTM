package poker.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }
}

