package com.fruitmkt.servlet.auth;

import com.fruitmkt.config.AppConfig;
import com.fruitmkt.model.entity.User;
import com.fruitmkt.util.SessionUtil;
import com.fruitmkt.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * RegisterServlet — Controller cho chức năng: Hiển thị form đăng ký
 *
 * URL: /auth/register
 * GET : Hiển thị form đăng ký
 * POST: Tạo tài khoản mới
 *
 * QUY TẮC SERVLET:
 * 1. Không viết SQL ở đây — gọi Service
 * 2. Sau POST thành công dùng PRG pattern (sendRedirect)
 * 3. Lưu flash message vào session trước redirect
 * 4. Forward đến /WEB-INF/jsp/auth/... (không để truy cập trực tiếp)
 * 5. Kiểm tra quyền bằng SessionUtil trước khi xử lý
 *
 * @author fruitmkt-team
 */
@WebServlet("/auth/register")
public class RegisterServlet extends HttpServlet {

    // TODO: Inject service — thêm service cần dùng ở đây
    // private final XxxService xxxService = new XxxService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Forward trực tiếp đến trang JSP đăng ký an toàn nằm sau WEB-INF
        req.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        /*
         * ====================================================================
         * HƯỚNG DẪN TỰ CODE TAY CHO SERVLET VÀ DAO NGHIỆP VỤ ĐĂNG KÝ (SIGN-UP):
         * ====================================================================
         * 
         * 1. Nhận các tham số từ form đăng ký (register.jsp):
         *    String role = req.getParameter("accountType"); // 'CUSTOMER' hoặc 'SHOP_OWNER'
         *    String fullName = req.getParameter("fullName");
         *    String email = req.getParameter("email");
         *    String phone = req.getParameter("phone");
         *    String password = req.getParameter("password");
         *    String confirmPassword = req.getParameter("confirmPassword");
         * 
         * 2. Validate dữ liệu ở phía server:
         *    if (fullName == null || fullName.trim().length() < 3) {
         *        req.setAttribute("errorMsg", "Họ tên phải có ít nhất 3 ký tự!");
         *        req.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(req, resp);
         *        return;
         *    }
         *    if (!password.equals(confirmPassword)) {
         *        req.setAttribute("errorMsg", "Mật khẩu xác nhận không khớp!");
         *        req.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(req, resp);
         *        return;
         *    }
         * 
         * 3. Thực hiện đăng ký thông qua DB Connection và băm mật khẩu bằng BCrypt:
         *    Để sử dụng BCrypt, hãy import:
         *    import org.mindrot.jbcrypt.BCrypt;
         * 
         *    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
         * 
         *    try (Connection conn = com.fruitmkt.config.DBConfig.getConnection()) {
         *        conn.setAutoCommit(false); // Bắt đầu transaction đảm bảo toàn vẹn dữ liệu
         *        try {
         *             User user = new User();
         *             user.setFullName(fullName);
         *             user.setEmail(email);
         *             user.setPasswordHash(hashedPassword);
         *             user.setPhone(phone);
         *             user.setRole(role);
         *             
         *             // Khởi tạo các DAO tương ứng:
         *             // UserDAO userDAO = new UserDAO();
         *             // ShopProfileDAO shopProfileDAO = new ShopProfileDAO();
         *             
         *             // Bước 3.1: Lưu User và lấy về userId tự tăng tự sinh
         *             // int userId = userDAO.saveAndGetId(user, conn);
         *             
         *             // Bước 3.2: Nếu vai trò là SHOP_OWNER, lưu hồ sơ shop profile
         *             // if ("SHOP_OWNER".equals(role)) {
         *             //     String storeName = req.getParameter("storeName");
         *             //     String address = req.getParameter("address");
         *             //     shopProfileDAO.save(userId, storeName, address, conn);
         *             // }
         *             
         *             conn.commit(); // Hoàn tất thành công -> Commit transaction
         *             
         *             req.getSession().setAttribute("flash_success", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
         *             resp.sendRedirect(req.getContextPath() + "/auth/login");
         *             return;
         *        } catch (SQLException e) {
         *             conn.rollback(); // Lỗi SQL -> Hủy toàn bộ thay đổi
         *             throw e;
         *        }
         *    } catch (Exception e) {
         *        req.setAttribute("errorMsg", "Có lỗi xảy ra: " + e.getMessage());
         *        req.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(req, resp);
         *    }
         */
        
        // Cảnh báo tạm thời khi người dùng nhấn submit đăng ký nhưng chưa code logic:
        req.setAttribute("errorMsg", "Chức năng đăng ký phía server đang được cấu hình. Vui lòng tự code tay logic doPost theo hướng dẫn chi tiết trong file RegisterServlet.java!");
        req.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(req, resp);
    }

}
