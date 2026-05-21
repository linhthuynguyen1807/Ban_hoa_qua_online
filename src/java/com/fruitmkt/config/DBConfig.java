package com.fruitmkt.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DBConfig — Quản lý cấu hình DB bằng cách ủy quyền (delegation) cho DBContext.
 * Điều này tránh trùng lặp cấu hình và đảm bảo một nguồn sự thật (Single Source of Truth) duy nhất.
 *
 * @author fruitmkt-team
 */
public class DBConfig {

    /**
     * Lấy kết nối từ DBContext.
     *
     * @return Connection tới SQL Server
     * @throws SQLException nếu kết nối thất bại
     */
    public static Connection getConnection() throws SQLException {
        return DBContext.getConnection();
    }

    private DBConfig() { /* Không khởi tạo */ }
}
