import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread extends Thread implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintStream os;
    private InetAddress address;

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        this.address = socket.getInetAddress();
        os = new PrintStream(socket.getOutputStream());
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            final String DB_URL = "jdbc:sqlserver://ELEUMLOYCE;databaseName=AirportSystem";
            final String USER = "admin";
            final String PASS = "admin";

            Connection connection;

            ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
            send.flush();
            ObjectInputStream rc = new ObjectInputStream(socket.getInputStream());

            connection = DriverManager.getConnection(DB_URL, USER, PASS);

            boolean activeClient = true;

            while (true) {
                String msg = (String) rc.readObject();
                if(!msg.equals("checkTaskStatus")) {
                    System.out.println(address + " on port " + socket.getPort() +
                            " >   " + msg);
                }
                switch (msg) {
                    case "registration": {
                        AccountDAO adao = new AccountDAO(connection);

                        Account newUser;
                        newUser = (Account)rc.readObject();

                        if(!adao.find(newUser)) {
                            String password = newUser.getPasswordHash();

                            String bsalt = BCrypt.gensalt(7);

                            password += bsalt;

                            String hash = BCrypt.hashpw(password, bsalt);

                            newUser.setPasswordHash(hash);
                            newUser.setPasswordSalt(bsalt);
                            newUser.setType("user");

                            if(adao.create(newUser)) {
                                os.println("OK");
                            } else {
                                os.println("ERROR");
                            }
                        } else {
                            os.println("ERROR");
                        }
                        break;
                    }
                    case "login": {
                        AccountDAO adao = new AccountDAO(connection);

                        Account user;
                        user = (Account)rc.readObject();

                        List<Account> userInDB;
                        if((userInDB = adao.find(user.getLogin(), "login")).size() != 0) {
                           String password = user.getPasswordHash();
                           String passwordHash = userInDB.get(0).getPasswordHash();
                           String passwordSalt = userInDB.get(0).getPasswordSalt();

                           String hash = BCrypt.hashpw(password+passwordSalt, passwordSalt);
                           if(hash.equals(passwordHash)) {
                               os.println("OK");
                               String returnString = userInDB.get(0).getId() + "|" + userInDB.get(0).getType();
                               os.println(returnString);
                               System.out.println("> " + address + " NOW LOGGED AS " + userInDB.get(0).getType());
                               break;
                           }
                        }
                        System.out.println("> " + address + " LOGIN FAILED!");
                        os.println("Error");
                        break;
                    }
                    case "addroute": {
                        RoutesDAO rdao = new RoutesDAO(connection);
                        Route route;
                        route = (Route)rc.readObject();
                        if(!rdao.find(route)) {
                            if(rdao.create(route)) {
                                os.println("OK");
                            } else {
                                os.println("ERROR");
                            }
                        } else {
                            os.println("ERROR");
                        }
                        break;
                    }
                    case "updateRoute": {
                        RoutesDAO rdao = new RoutesDAO(connection);
                        Route updateRoute = (Route)rc.readObject(),
                                previousRoute = (Route)rc.readObject();
                        rdao.updateRoute(updateRoute, previousRoute);
                        break;
                    }
                    case "getroutes": {
                        RoutesDAO rdao = new RoutesDAO(connection);
                        int day = (int) rc.readObject();
                        String type = (String) rc.readObject();
                        String clock = (String) rc.readObject();
                        send.writeObject(rdao.findAll(day, type, clock));
                        if(ObjectThread.task) {
                            ObjectThread.task = false;
                        }
                        break;
                    }
                    case "getSearchRoute": {
                        RoutesDAO rdao = new RoutesDAO(connection);
                        String routeID = (String)rc.readObject();
                        send.writeObject(rdao.find(routeID, "ID"));
                        break;
                    }
                    case "getSearchRouteDispatcher": {
                        RoutesDAO rdao = new RoutesDAO(connection);
                        String routeID = (String)rc.readObject();
                        send.writeObject(rdao.find(routeID, "Source|Destination|"));
                        break;
                    }
                    case "deleteroute": {
                        RoutesDAO rdao = new RoutesDAO(connection);
                        Route route = (Route)rc.readObject();
                        os.println(rdao.deleteRoute(route));
                        break;
                    }
                    case "getusers": {
                        AccountDAO adao = new AccountDAO(connection);
                        send.writeObject(adao.findAll());
                        break;
                    }
                    case "deleteuser": {
                        AccountDAO adao = new AccountDAO(connection);
                        String id = Integer.toString((int)rc.readObject());
                        if(adao.delete(id)) {
                            os.println("OK");
                        } else {
                            os.println("ERROR");
                        }
                        break;
                    }
                    case "edituser": {
                        AccountDAO adao = new AccountDAO(connection);
                        String id = Integer.toString((int)rc.readObject());
                        String type = (String)rc.readObject();
                        adao.edit(id, type);
                        break;
                    }
                    case "updatestatus": {
                        RoutesDAO rdao = new RoutesDAO(connection);
                        String id = (String)rc.readObject(),
                                source = (String)rc.readObject(),
                                time = (String)rc.readObject(),
                                day = Integer.toString((int)rc.readObject()),
                                status = (String)rc.readObject();
                        rdao.updateStatus(id, source, time, day, status);

                        ObjectThread.task = true;
                        break;
                    }
                    case "refreshStatus" : {
                        RoutesDAO rdao = new RoutesDAO(connection);
                        rdao.refreshStatus((String)rc.readObject());
                        break;
                    }
                    case "checkTaskStatus": {
                        if(ObjectThread.task) {
                            os.println("active");
                        } else {
                            os.println("notActive");
                        }
                        break;
                    }
                    case "getPlaneInformation": {
                        RoutesDAO rdao = new RoutesDAO(connection);
                        String plane = (String)rc.readObject();

                        send.writeObject(rdao.getPlaneInformation(plane));
                        break;
                    }
                    case "getChartInfo": {
                        RoutesDAO rdao = new RoutesDAO(connection);
                        String day = (String)rc.readObject();
                        int info = rdao.getFlightChartInfo(day);
                        send.writeObject(info);
                        break;
                    }
                    case "/exit": {
                        System.out.println(address + " disconnected.");
                        rc.close();
                        send.close();
                        activeClient = false;
                    }
                }
                if(!activeClient) break;
            }
        } catch(SQLException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException e) {
            System.out.println("\nConnection dropped on " + socket.getInetAddress() + "\n");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            end();
        }
    }

    private void end() {
        try {
            if (os != null) {
                os.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.interrupt();
        }
    }
}
