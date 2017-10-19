package cc.gospy.chollima.service;

import cc.gospy.chollima.repo.Users;
import cc.gospy.chollima.util.TokenManager;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String handleLogin(String username, String password) {
        if (password.equals(Users.users.get(username))) {
            return TokenManager.createToken(username.concat(password));
        } else {
            throw new RuntimeException("Incorrect username or password");
        }
    }

    public void handleLogout(String token) {
        TokenManager.deleteIfExist(token);
    }
}
