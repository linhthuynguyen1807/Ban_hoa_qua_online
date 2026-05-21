package com.fruitmkt.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * ValidationUtil — Tiện ích validate input từ form/request. Dùng trước khi gọi Service.
 *
 * @author fruitmkt-team
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0|\\+84)[3|5|7|8|9][0-9]{8}$");

    /** Kiểm tra email hợp lệ */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /** Kiểm tra số điện thoại VN hợp lệ (10 số) */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /** Kiểm tra mật khẩu đủ mạnh (8-64 ký tự) */
    public static boolean isValidPassword(String pwd) {
        if (pwd == null) return false;
        int len = pwd.length();
        return len >= 8 && len <= 64;
    }

    /** Chuỗi không null và không rỗng */
    public static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /** Số nguyên dương */
    public static boolean isPositiveInt(int n) {
        return n > 0;
    }

    /** Giá trị price hợp lệ (> 0) */
    public static boolean isPositiveDecimal(BigDecimal bd) {
        return bd != null && bd.compareTo(BigDecimal.ZERO) > 0;
    }

    private ValidationUtil() {}
}
