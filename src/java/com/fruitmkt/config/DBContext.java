package com.fruitmkt.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBContext — Quản lý kết nối cơ sở dữ liệu SQL Server.
 * 
 * PHƯƠNG THỨC KẾT NỐI:
 * Sử dụng Microsoft JDBC Driver cho SQL Server.
 * Đảm bảo cổng 1433 đã được kích hoạt trong SQL Server Configuration Manager
 * (TCP/IP).
 * 
 * @author fruitmkt-team
 */
public class DBContext {

    // =====================================================================
    // Cấu hình thông tin kết nối tới SQL Server của bạn ở đây
    // =====================================================================
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "1433";
    private static final String DB_NAME = "OnlineFruitShopping";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "123"; // Thay bằng mật khẩu thật của sa
    // =====================================================================

    private static final String JDBC_URL = "jdbc:sqlserver://" + DB_HOST + ":" + DB_PORT
            + ";databaseName=" + DB_NAME
            + ";encrypt=false;trustServerCertificate=true";

    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println(
                    "Không tìm thấy MSSQL JDBC driver trong classpath. Vui lòng thêm thư viện vào WEB-INF/lib.");
        }
    }

    /**
     * Lấy kết nối cơ sở dữ liệu tới SQL Server.
     * Người gọi phải đóng kết nối trong khối try-with-resources.
     * 
     * @return Connection tới database
     * @throws SQLException nếu thông tin đăng nhập hoặc kết nối lỗi
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Phương thức main để kiểm tra nhanh trạng thái kết nối tới SQL Server.
     */
    public static void main(String[] args) {
        System.out.println("Đang kiểm tra kết nối tới SQL Server...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("=================================================");
                System.out.println(" KẾT NỐI DATABASE THÀNH CÔNG RỒI BẠN ƠI! 🎉");
                System.out.println(" Tên Database: " + conn.getCatalog());
                System.out.println("=================================================");
            }
        } catch (SQLException e) {
            System.err.println("=================================================");
            System.err.println(" KẾT NỐI THẤT BẠI. LỖI:");
            System.err.println(" " + e.getMessage());
            System.err.println("=================================================");
            e.printStackTrace();
        }
    }
}
