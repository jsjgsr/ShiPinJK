package config;

/**
 * Created by gsr-pc on 2016/5/3.
 */
public class User {
    private String uid;
    private String uname;
    public User() {
    }

    public User(String uid , String uname) {
        this.uid = uid;
        this.uname = uname;
    }
    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return this.uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

}
