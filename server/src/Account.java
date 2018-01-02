import java.io.Serializable;

public class Account implements Serializable {
    public static final long serialVersionUID = 726616452;

    private int id;
    private String login;
    private String passwordHash;
    private String passwordSalt;
    private String type;

    public Account() {}

    public Account(int id, String type) {
        this.id = id;
        this.type = type;
    }

    /*public Account(int id, String login, String passwordHash, String passwordSalt, String type) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.type = type;
    }*/

    /*public int getId() {
        return id;
    }*/

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public void setType(String type) {
        this.type = type;
    }
}
