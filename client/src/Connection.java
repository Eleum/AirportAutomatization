import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class Connection {
    public static Socket socket = null;
    private static ObjectOutputStream os;
    public static ObjectInputStream is;
    //private static PrintStream printStream;
    private static BufferedReader bufferedReader;

    public static void start() throws IOException {
        socket = new Socket(InetAddress.getLocalHost(), 1826);

        os = new ObjectOutputStream(socket.getOutputStream());
        is = new ObjectInputStream(socket.getInputStream());

        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static boolean serverAvailableCheck() {
        try (Socket s = new Socket(InetAddress.getLocalHost(), 1826)) {
            return true;
        } catch (IOException ex) {
            /* ignore */
        }
        return false;
    }

    public static boolean signUp(Account account) throws IOException {
        os.writeObject("registration");
        os.writeObject(account);
        os.flush();
        return bufferedReader.readLine().equals("OK");
    }

    public static String logIn(Account account) throws IOException {
        os.writeObject("login");
        os.writeObject(account);
        os.flush();
        if(bufferedReader.readLine().equals("OK")) {
            return bufferedReader.readLine();
        }
        return "error";
    }

    public static boolean addRoute(Route route) throws IOException {
        os.writeObject("addroute");
        os.writeObject(route);
        os.flush();
        return bufferedReader.readLine().equals("OK");
    }

    public static List<Route> getRoutes(int day, String type, String time) throws IOException, ClassNotFoundException {
        os.writeObject("getroutes");
        os.writeObject(day);
        os.writeObject(type);
        os.writeObject(time);
        os.flush();
        return (List<Route>)is.readObject();
    }

    public static void refreshStatus(String day) throws IOException {
        os.writeObject("refreshStatus");
        os.writeObject(day);
    }

    public static void updateRoute(Route updateRoute, Route previousRoute) throws IOException {
        os.writeObject("updateRoute");
        os.writeObject(updateRoute);
        os.writeObject(previousRoute);
        os.flush();
    }

    public static List<Route> getRoutes(String route, int type) throws IOException, ClassNotFoundException {
        if(type == 0) {
            os.writeObject("getSearchRoute");
        } else {
            os.writeObject("getSearchRouteDispatcher");
        }
        os.writeObject(route);
        os.flush();
        return (List<Route>)is.readObject();
    }

    public static String deleteRoute(Route route) throws IOException {
        os.writeObject("deleteroute");
        os.writeObject(route);
        return bufferedReader.readLine();
    }

    public static List<Account> getUsers() throws IOException, ClassNotFoundException {
        os.writeObject("getusers");
        os.flush();
        return (List<Account>)is.readObject();
    }

    public static boolean deleteUser(int id) throws IOException {
        os.writeObject("deleteuser");
        os.writeObject(id);
        os.flush();
        return bufferedReader.readLine().equals("OK");
    }

    public static void editUserType(int id, String type) throws IOException {
        os.writeObject("edituser");
        os.writeObject(id);
        os.writeObject(type);
        os.flush();
    }

    public static void updateStatus(String id, String source, String time, int day, String status) throws IOException {
        os.writeObject("updatestatus");
        os.writeObject(id);
        os.writeObject(source);
        os.writeObject(time);
        os.writeObject(day);
        os.writeObject(status);
        os.flush();
    }

    public static String checkTaskStatus() throws IOException {
        os.writeObject("checkTaskStatus");
        return bufferedReader.readLine();
    }

    public static PlaneInformation getPlaneInformation(String plane) throws IOException, ClassNotFoundException {
        os.writeObject("getPlaneInformation");
        os.writeObject(plane);
        return (PlaneInformation)is.readObject();
    }

    public static int getGraphInfo(String day) throws IOException, ClassNotFoundException {
        os.writeObject("getChartInfo");
        os.writeObject(day);
        return (int)is.readObject();
    }

    public static void end() throws IOException {
        if(socket == null) {
            System.exit(666);
        }
        os.writeObject("/exit");
        //TODO check if streams closing is needed
    }
}
