import java.sql.Connection;

public abstract class AbstractDAO {
    protected static Connection connection;

    public AbstractDAO(Connection connection) {
        AbstractDAO.connection = connection;
    }
}
