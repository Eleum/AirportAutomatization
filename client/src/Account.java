import java.io.Serializable;

public class Account implements Serializable {
    public static final long serialVersionUID = 726616452;

    private int id;
    private String login;
    private String passwordHash;
    private String passwordSalt;
    private String type;

    public Account() {}

    public Account(String login, String passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}