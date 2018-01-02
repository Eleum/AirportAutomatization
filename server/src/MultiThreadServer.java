import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {
    private static ExecutorService exec = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(1826);

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Server is running...");

            exec.execute(new ObjectThread());

            while (true) {
                if(br.ready()) {
                    String cmd = br.readLine();

                    if(cmd.equalsIgnoreCase("/exit")) {
                        System.out.println("Shutting down the server...");
                        server.close();
                        break;
                    }
                }
                Socket socket = server.accept();
                System.out.println(socket.getInetAddress() + " connected!");

                exec.execute(new ServerThread(socket));
                //System.out.println("Connection accepted.");
                /*ServerThread thread = new ServerThread(socket);
                thread.run();*/
            }
            exec.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
