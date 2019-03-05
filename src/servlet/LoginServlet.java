package servlet;

import model.User;
import utils.AppUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
        dispatcher.forward(request, response);
    }


    private static final Map<String, User> mapUsers = new HashMap<String, User>();

    static {
        initUsers();
    }

    private static void initUsers() {
        User emp = new User("employee1", "123", User.ROLE_EMPLOYEE);
        User mng = new User("manager1", "123", User.ROLE_EMPLOYEE, User.ROLE_MANAGER);
        mapUsers.put(emp.getUserName(), emp);
        mapUsers.put(mng.getUserName(), mng);
    }

    // Tìm kiếm người dùng theo userName và password.
    public static User findUser(String userName, String password) {
        User u = mapUsers.get(userName);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        User userAccount = findUser(userName, password);

        //  SecurityFilter securityFilter = new SecurityFilter();
        if (userAccount == null) {
            String errorString = "Invalid userName or Password";
            request.setAttribute("errorString", errorString);
            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
            dispatcher.forward(request, response);
            //return;
        } else {
            //Dang nhap thanh cong, lưu vào session
            request.getSession().setAttribute("loginedUser", userAccount);
        }


        int redirectId = -1;
        try {
            redirectId = Integer.parseInt(request.getParameter("redirectId"));
        } catch (Exception e) {
        }
        String requestUri = AppUtils.getRedirectAfterLoginUrl(request.getSession(), redirectId);
        if (requestUri != null) {
            response.sendRedirect(requestUri);
        } else {
            response.sendRedirect(request.getContextPath() + "/userInfo");
        }
    }

}