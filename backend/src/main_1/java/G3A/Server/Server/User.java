package G3A.Server.Server;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("User")
public class User {

    @Id
    public String id;

    public String username;
    public String password;
    public java.sql.Time time;

    public User(String username, String password) {

    }
}
