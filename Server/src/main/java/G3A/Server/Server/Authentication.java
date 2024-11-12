package G3A.Server.Server;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Authentication {
    public static class User {
        public String userId;
    }
    @PostMapping(value = "/LogIn", produces = "application/json", consumes="application/json")
    public ResponseEntity<User> logIn(@RequestBody User user) {
        String userId = UUID.randomUUID().toString();
        user.userId = userId;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/CreateAccount", produces = "application/json", consumes="application/json")
    public ResponseEntity<User> createAccount(@RequestBody User user) {
        String userId = UUID.randomUUID().toString();
        user.userId = userId;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
