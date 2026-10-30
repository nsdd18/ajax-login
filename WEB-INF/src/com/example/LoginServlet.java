package com.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录接口（AJAX 的后端）
 *
 * 访问地址：POST /api/login
 * 参数：username、password
 * 返回：JSON  {"success":true/false, "message":"...", "username":"..."}
 *
 * 说明：这里用 Map 模拟数据库中的用户表，真实项目应改为查询数据库。
 */
@WebServlet(name = "loginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // 模拟数据库：用户名 -> 密码
    private static final Map<String, String> USERS = new HashMap<>();

    static {
        USERS.put("admin", "123456");
        USERS.put("user", "123456");
        USERS.put("teacher", "888888");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 请求体是中文时要先设编码，否则会乱码
        request.setCharacterEncoding("UTF-8");
        // 告诉浏览器返回的是 JSON，并且用 UTF-8 编码
        response.setContentType("application/json;charset=UTF-8");

        String username = trim(request.getParameter("username"));
        String password = trim(request.getParameter("password"));

        boolean success = false;
        String message;

        // ---- 服务端校验 ----
        if (username.isEmpty() || password.isEmpty()) {
            message = "用户名和密码不能为空";
        } else if (!USERS.containsKey(username)) {
            message = "用户名不存在";
        } else if (!USERS.get(username).equals(password)) {
            message = "密码错误";
        } else {
            success = true;
            message = "登录成功";
            // 登录成功：把用户名放进 session，后续页面可据此判断是否已登录
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", username);
        }

        // ---- 手动拼接 JSON（作业不引入第三方 JSON 库）----
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":").append(success).append(",");
        json.append("\"message\":").append(quote(message)).append(",");
        json.append("\"username\":").append(quote(username));
        json.append("}");

        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print("{\"success\":false,\"message\":\"请使用 POST 提交登录请求\"}");
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    /** 简单转义，避免用户输入里的引号破坏 JSON 格式 */
    private static String quote(String s) {
        if (s == null) {
            return "null";
        }
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
