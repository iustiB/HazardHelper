package licenta.iusti.hazardhelper.domain;

import java.util.Date;

/**
 * Created by Iusti on 6/17/2017.
 */

public class Message {
    private String id;
    private String username; // sau UID depinde
    private String content;
    private String date;

    public Message(){}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Message(String id, String username, String content, String date) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.date = date;
    }
}
