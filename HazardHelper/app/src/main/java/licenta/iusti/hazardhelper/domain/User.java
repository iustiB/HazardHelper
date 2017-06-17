package licenta.iusti.hazardhelper.domain;

/**
 * Created by Iusti on 6/11/2017.
 */

public class User {
    private String UID;
    private String username;

    public User(){

    }
    public User(String uid, String username) {
        UID = uid;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
