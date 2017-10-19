package cc.gospy.chollima.api;

import cc.gospy.chollima.entity.Message;
import cc.gospy.chollima.util.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // TODO Local File Connection
        if (request.getHeader("Referer") == null) {
            logger.info("Special local user 'file://', skip permissions check");
            return super.preHandle(request, response, handler);
        }

        String userToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user-token")) {
                    userToken = cookie.getValue();
                    break;
                }
            }
        }
        if (userToken == null) {
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new Message(false, "Please <a href='./login.html'>login</a> first").toJson());
            return false;
        }
        if (TokenManager.validToken(userToken)) {
            return super.preHandle(request, response, handler);
        } else {
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new Message(false, "Token expired, please <a href='./login.html'>re-login</a>").toJson());
            return false;
        }
    }
}
