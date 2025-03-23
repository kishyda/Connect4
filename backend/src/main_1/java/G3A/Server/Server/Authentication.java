package G3A.Server.Server;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class Authentication {

    @Autowired
    private final UserRepository userRepository;

    public Authentication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @PostMapping(value = "/LogIn", produces = "application/json", consumes="application/json")
    public void logIn(
            @RequestBody User user, 
            @CookieValue(value="JWT", defaultValue = "") String jwt, 
            HttpServletResponse response) 
    {
        User dbUser = this.userRepository.findByUsername(user.username);
        if (dbUser != null) {
            if (dbUser.password == user.username) {
                Cookie cookie = new Cookie("token", "hello");
                cookie.setSecure(true);
                cookie.setHttpOnly(true);
                cookie.setMaxAge(60 * 60 * 24);
                response.addCookie(cookie);
                response.setStatus(200);
                return;
            }
        }
        try {
            response.sendError(404); 
        } catch (Exception e) {
        }
    }

    @PostMapping(value = "/CreateAccount", produces = "application/json", consumes="application/json")
    public ResponseEntity<User> createAccount(@RequestBody User user, HttpServletResponse response) {
        userRepository.save(new User(user.username, user.password));
        Cookie cookie = new Cookie("", "hello");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
