package com.fruitmkt.servlet.auth;

import com.fruitmkt.config.AppConfig;
import com.fruitmkt.util.SessionUtil;
import com.fruitmkt.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * ForgotPasswordServlet — Controller cho chức năng: Hiển thị form quên mật khẩu
 *
 * URL: /auth/forgot
 * GET : Hiển thị form quên mật khẩu
 * POST: Gửi mã reset và đặt lại mật khẩu
 *
 * QUY TẮC SERVLET:
 *   1. Không viết SQL ở đây — gọi Service
 *   2. Sau POST thành công dùng PRG pattern (sendRedirect)
 *   3. Lưu flash message vào session trước redirect
 *   4. Forward đến /WEB-INF/jsp/auth/... (không để truy cập trực tiếp)
 *   5. Kiểm tra quyền bằng SessionUtil trước khi xử lý
 *
 * @author fruitmkt-team
 */
@WebServlet("/auth/forgot")
public class ForgotPasswordServlet extends HttpServlet {

    // TODO: Inject service — thêm service cần dùng ở đây
    // private final XxxService xxxService = new XxxService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Forward trực tiếp đến trang JSP quên mật khẩu an toàn nằm sau WEB-INF
        req.getRequestDispatcher("/WEB-INF/jsp/auth/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Cảnh báo tạm thời khi submit forgot password nhưng chưa code logic:
        req.setAttribute("errorMsg", "Chức năng quên mật khẩu đang được cấu hình. Vui lòng tự code tay logic đặt lại mật khẩu!");
        req.getRequestDispatcher("/WEB-INF/jsp/auth/forgot-password.jsp").forward(req, resp);
    }

}
