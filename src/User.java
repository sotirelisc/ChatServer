
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christos Sotirelis
 */
public class User extends Thread {

    private final ChatServer server;
    private final Socket socket;
    private String username;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public User(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getUsername() {
        return this.username;
    }

    // Apostolh mhnumatos ston xrhsth
    public void sendMessage(Message msg) {
        try {
            this.out.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }

        String command;
        try {
            while ((command = (String) in.readObject()) != null) {
                if (command.equals("START")) {
                    System.out.println("got start");
                    out.writeObject("WAITING");
                } else if (command.equals("USERNAME")) {

                    username = (String) in.readObject();
                    System.out.println("got username " + username);

                    if (!server.exists(username)) {
                        out.writeObject("EXISTS");
                        System.out.println("user exists");
//                        server.addUser(this);
//                        System.out.println("username does not exist");
//
//                        System.out.println(username + " is connected");
//                        out.writeObject("USERLIST");
//                        out.writeObject(server.getUserList());
                    } else {
                        System.out.println("user exists");

                        out.writeObject("EXISTS");
                    }

                } else {
                    System.out.println("got message");

                    Message msg = new Message(command, username);
                    System.out.println(username + " says :" + command);
                    server.addMessage(msg);
                }
            }
        } catch (IOException ex) {
            //Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(username + " disconnected");
            //server.removeUser(socket);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
