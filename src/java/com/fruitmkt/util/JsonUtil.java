package com.fruitmkt.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JsonUtil — Tiện ích JSON dùng Jackson cho webhook và AJAX response.
 * 
 * Đặt jackson-databind-*.jar vào WEB-INF/lib/
 * Download: https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
 *
 * @author fruitmkt-team
 */
public final class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** Serialize object thành JSON string */
    public static String toJson(Object obj) throws Exception {
        return MAPPER.writeValueAsString(obj);
    }

    /** Deserialize JSON string thành object */
    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return MAPPER.readValue(json, clazz);
    }

    /** Ghi JSON response cho AJAX/webhook endpoint */
    public static void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            resp.getWriter().write(MAPPER.writeValueAsString(data));
        } catch (Exception e) {
            throw new IOException("Lỗi ghi dữ liệu JSON: " + e.getMessage(), e);
        }
    }

    private JsonUtil() {}
}
