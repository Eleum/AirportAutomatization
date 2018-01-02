import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RoutesDAO extends AbstractDAO {
    public RoutesDAO(Connection connection) {
        super(connection);
    }

    public boolean create(Route route) {
        Statement sql;

        try {
            sql = connection.createStatement();

            String appendToSQL;

            appendToSQL = "(" + "'" + route.getId() + "'" + "," + "'" + route.getSource() + "'" + ","
                    + "'" + route.getDestination()+ "'" + "," + "'" + route.getDepartureTime() + "'" + ","
                    + "'" + route.getDays() + "'" + ");";

            sql.execute("INSERT Routes VALUES " + appendToSQL);

            appendToSQL = "(" + "'" + route.getId() + "'" + "," + "'" + route.getSource() + "'" + ","
                    + "'" + route.getDepartureTime()+ "'" + "," + "'" + route.getArrivalTime() + "'" + ","
                    + "'" + route.getDays() + "'" + ", '" + route.getPlaneType()+ "'" + ", '" + route.getGate() + "');";

            sql.execute("INSERT RoutesInfo(ID, Source, DepartureTime, ArrivalTime, Days, PlaneType, Gate) VALUES " + appendToSQL);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try {
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean find(Route route) {
        Statement sql = null;
        ResultSet resultSet = null;

        try {
            sql = connection.createStatement();
            resultSet = sql.executeQuery("SELECT * FROM RoutesInfo");

            while(resultSet.next()) {
                Route compare = new Route();
                compare.setId(resultSet.getString("ID"));
                compare.setSource(resultSet.getString("Source"));
                compare.setDays(resultSet.getString("Days"));
                compare.setDepartureTime(resultSet.getString("DepartureTime"));

                //if route with the same id, source, days
                if(route.getId().equals(compare.getId()) &&
                        route.getSource().equals(compare.getSource()) &&
                        route.getDays().equals(compare.getDays()) &&
                        route.getDepartureTime().equals(compare.getDepartureTime())) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if(resultSet != null) {
                resultSet.close();
            }
            if(sql != null) {
                sql.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Route> find(String info, String fieldsInfo) throws SQLException {
        Statement sql = null;

        List<Route> routes = new ArrayList<>();

        String[] findArray = info.split("\\|");
        String[] fields = fieldsInfo.split("\\|");

        String appendToSQL = " ";

        for(int i = 0; i < fields.length; i++) {
            if(fields[i].equals("Source") || fields[i].equals("Destination") || fields[i].equals("ID")) {
                appendToSQL += "t1.";
            }
            appendToSQL += fields[i] + " LIKE " + "'%";
            if(findArray.length > 1) {
                appendToSQL += findArray[i];
            } else {
                appendToSQL += findArray[0];
            }
            appendToSQL += "%'" + " OR ";
        }
        appendToSQL = appendToSQL.substring(0, appendToSQL.length()-4);

        try {
            sql = connection.createStatement();

            ResultSet resultSet = sql.executeQuery("SELECT t1.Destination, t2.* " +
                    "FROM [Routes] t1 INNER JOIN RoutesInfo t2 " +
                    "ON t1.ID = t2.ID AND t1.[Source] = t2.[Source] AND " +
                    "t1.DepartureTime = t2.DepartureTime AND t1.[Days] = t2.[Days]" +
                    "WHERE" + appendToSQL +
                    "ORDER BY t2.DepartureTime");

            while(resultSet.next()) {
                Route route = new Route(resultSet.getString("id"),
                        resultSet.getString("Source"), resultSet.getString("Destination"),
                        resultSet.getString("DepartureTime"), resultSet.getString("ArrivalTime"),
                        resultSet.getString("Days"), resultSet.getString("PlaneType"),
                        resultSet.getString("Status"), resultSet.getString("Gate"));
                routes.add(route);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(sql != null) {
                sql.close();
            }
        }
        return routes;
    }

    public List<Route> findAll(int day, String type, String clock) throws SQLException {
        Statement sql = null;
        ResultSet resultSet;
        List<Route> list = new ArrayList<>();
        String append = "";

        switch (type) {
            case "Destination": {
                append = "AND t1.Source LIKE 'Минск'";
                if (!clock.equals("00:00")) {
                    append += "AND t1.DepartureTime >= '" + clock + "'";
                }
                break;
            }
            case "Source": {
                append = "AND t1.Destination LIKE 'Минск'";
                if (!clock.equals("00:00")) {
                    append += "AND t2.ArrivalTime >= '" + clock + "'";
                }
                break;
            }
            default: {
                append += "AND ((t1.Source = 'Минск' AND t1.DepartureTime > '" + clock + "') " +
                        "OR (t1.Destination = 'Минск' and t2.ArrivalTime > '" + clock + "'))";
            }
        }

        try {
            sql = connection.createStatement();
            String query = "SELECT t1.Destination, t2.* " +
                    "FROM [Routes] t1 INNER JOIN RoutesInfo t2 " +
                    "ON t1.ID = t2.ID AND t1.[Source] = t2.[Source] " +
                    "AND t1.DepartureTime = t2.DepartureTime AND t1.[Days] = t2.[Days] " +
                    "WHERE t1.[Days] like '%" + day + "%' ";
            if (!type.equals("currentAll")) {
                resultSet = sql.executeQuery(query + append +
                        "ORDER BY t2.DepartureTime");
            } else {
                if (type.equals("currentAll")) {
                    query += append;
                }

                query += "ORDER BY (CASE WHEN t2.Source = 'Минск' " +
                        "THEN t2.DepartureTime " +
                        "ELSE t2.ArrivalTime END)";
                resultSet = sql.executeQuery(query);
            }

            while (resultSet.next()) {
                list.add(new Route(resultSet.getString("ID"), resultSet.getString("Source"),
                        resultSet.getString("Destination"), resultSet.getString("DepartureTime"),
                        resultSet.getString("ArrivalTime"), resultSet.getString("Days"),
                        resultSet.getString("PlaneType"), resultSet.getString("Status"),
                        resultSet.getString("Gate")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(sql != null) {
                sql.close();
            }
        }
        return list;
    }

    public String deleteRoute(Route route) throws SQLException {
        Statement sql = null;
        int rowsAffected = 0;
        try {
            sql = connection.createStatement();

            rowsAffected = sql.executeUpdate("DELETE FROM Routes WHERE id = '" + route.getId() + "'" +
                    " AND Source = '" + route.getSource() + "'" + " AND DepartureTime = '" + route.getDepartureTime() + "'" +
                    " AND Days = '" + route.getDays() + "'");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(sql != null) {
                sql.close();
            }
        }
        if(rowsAffected == 0) return "error";
        return "OK";
    }

    public void updateStatus(String id, String source, String time, String day, String status) throws SQLException {
        Statement sql = null;
        try {
            sql = connection.createStatement();
            String timeType = source.equals("Минск") ? "DepartureTime = '" : "ArrivalTime = '";
            sql.executeUpdate("UPDATE RoutesInfo SET Status = '" + status + "' WHERE ID = '" + id + "'" +
                    " AND " + timeType + time + "'" + " AND Days LIKE '%" + day + "%'");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(sql != null) {
                sql.close();
            }
        }
    }

    public void updateRoute(Route updateRoute, Route previousRoute) throws SQLException {
        Statement sql = null;
        try {
            sql = connection.createStatement();
            String id = previousRoute.getId(),
                    source =  previousRoute.getSource(),
                    departureTime = previousRoute.getDepartureTime(),
                    days = previousRoute.getDays(),
                    whereStatement =  " WHERE ID = '" + id + "' AND Source = '" + source +
                            "' AND DepartureTime = '" + departureTime + "' AND Days LIKE '%" + days + "%'";

            sql.executeUpdate("UPDATE Routes SET ID = '" + updateRoute.getId() + "', Source = '" + updateRoute.getSource() +
                    "', Destination = '" + updateRoute.getDestination() + "', DepartureTime = '" + updateRoute.getDepartureTime() +
                    "', Days = '" + updateRoute.getDays() + "'" + whereStatement);
            sql.executeUpdate("UPDATE RoutesInfo SET ArrivalTime = '" + updateRoute.getArrivalTime() +
                    "', PlaneType = '" + updateRoute.getPlaneType() +
                    "', Gate = '" + updateRoute.getGate() + "'" + whereStatement);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(sql != null) {
                sql.close();
            }
        }
    }

    public void refreshStatus(String day) throws SQLException {
        Statement sql = null;
        try {
            sql = connection.createStatement();
            sql.executeUpdate("UPDATE RoutesInfo SET Status = '' WHERE Days LIKE '%" + day + "%'");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(sql != null) {
                sql.close();
            }
        }
    }

    public PlaneInformation getPlaneInformation(String plane) throws SQLException {
        Statement sql = null;
        PlaneInformation planeInformation = null;
        try {
            sql = connection.createStatement();

            ResultSet resultSet = sql.executeQuery("SELECT Planes.[Name], t2.*, t3.* " +
                    "FROM (Planes " +
                    "INNER JOIN PlanesInfo t2 ON Planes.[Type] = t2.[Type]) " +
                    "INNER JOIN [Load] t3 ON t2.LoadID = t3.LoadID " +
                    "WHERE Planes.[Type] LIKE '" + plane + "'");

            while(resultSet.next()) {
                planeInformation = new PlaneInformation(
                        resultSet.getString("Name"), resultSet.getInt("MaxTakeOff"),
                        resultSet.getInt("MaxLanding"), resultSet.getInt("Capacity"),
                        resultSet.getInt("MaxLoad"), resultSet.getInt("Height"),
                        resultSet.getInt("Fuel"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           if(sql != null) {
               sql.close();
           }
        }
        return planeInformation;
    }

    public int getFlightChartInfo(String day) throws SQLException {
        Statement sql = null;
        try {
            sql = connection.createStatement();
            ResultSet resultSet = sql.executeQuery("SELECT COUNT(*) AS count FROM RoutesInfo WHERE Days LIKE '%" + day + "%'");
            while (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(sql != null) {
                sql.close();
            }
        }
        return 0;
    }
}
