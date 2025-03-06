package G3A.Server.Server;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Authentication {
    // HttpClient client = HttpClient.newBuilder()
    // .version(Version.HTTP_2)
    // .followRedirects(Redirect.NORMAL)
    // .proxy(ProxySelector.of(new InetSocketAddress("www-proxy.com", 8080)))
    // .authenticator(Authenticator.getDefault())
    // .build();

    // HttpRequest request = HttpRequest.newBuilder()
    // .uri(URI.create("http://localhost:12345"))
    // .GET()
    // .build();

    public static class User {
        public String userId;
    }

    @PostMapping(value = "/LogIn", produces = "application/json", consumes = "application/json")
    public ResponseEntity<User> logIn(@RequestBody User user) {
        // try {
        // client.send(request, null);
        // } catch (Exception e) {
        // }

        // String userId = UUID.randomUUID().toString();
        // user.userId = userId;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/CreateAccount", produces = "application/json", consumes = "application/json")
    public ResponseEntity<User> createAccount(@RequestBody User user) {
        String userId = UUID.randomUUID().toString();
        user.userId = userId;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
