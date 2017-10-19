package cc.gospy.chollima.api;


import cc.gospy.chollima.entity.Message;
import cc.gospy.chollima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@RequestMapping("/user/**")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = POST, produces = "application/json;charset=UTF-8")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        try {
            String userToken = userService.handleLogin(username, password);
            response.addCookie(new Cookie("user-token", userToken));
            return new Message(true, userToken).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/logout", method = POST, produces = "application/json;charset=UTF-8")
    public String logout(@RequestParam String token, HttpServletRequest request, HttpServletResponse response) {
        try {
            userService.handleLogout(token);
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(token)) {
                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
            return new Message(true, null).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }
}
