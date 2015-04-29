
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christos Sotirelis
 */
public class ChatServer {

    private ArrayList<User> users;
    private int connected_users;
    private String command;
    
    public ChatServer() {
        // Lista sundedemenwn xrhstwn
        users = new ArrayList();
        connected_users = 0;
        System.out.println("[SERVER LOG]: Starting server..");
        try {
            // Dhmiourgia ServerSocket pou akouei sthn port 6666
            ServerSocket server = new ServerSocket(6666, 50);
            System.out.println("[SERVER LOG]: Server listening on port " + server.getLocalPort() + ".");
            while (true) {
                // Sundesh neou xrhsth
                Socket client_socket = server.accept();
                // Αποθηκεύουμε τις δύο ροές
                ObjectOutputStream out = new ObjectOutputStream(client_socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(client_socket.getInputStream());
                
                command = (String) in.readObject();
                // Όταν λάβουμε start απαντάμε πάντα waiting
                if (command.equals("START")) {
                    System.out.println("got start");
                    out.writeObject("WAITING");
                }
                
                // ANTI GIA TO PARAPANW
                /*
                Message welcome = (Message) in.readObject();
                if (welcome.getMessage().equals("START")) {
                    out.writeObject(new Message("WAITING"));
                }
                */
                
                // LEW TO PRWTO MHNUMA TOU CLIENT NA EINAI TO ONOMA TOU GIA ELEGXO
                // ARA:
                Message welcome = (Message) in.readObject();
                if (!exists(welcome.getMessage())) {
                    out.writeObject(new Message("OK"));
                } else {
                    out.writeObject(new Message("ERROR_USERNAME_EXISTS"));
                    out.close();
                }
                
                // To welcome.getMessage() tha dinei to onoma tou xrhsth afou einai to 1o mhnuma
                User new_user = new User(this, client_socket, welcome.getMessage());
                users.add(new_user);
                new_user.start();
                connected_users++;
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Enarksh tou chat
        ChatServer chatServer = new ChatServer();
    }

    private boolean exists(String name) {
        for (User user : users) {
            if (user.getUsername().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    void sendMessageToAll(Message msg) {
        for (User user : users) {
            user.sendMessage(msg);
        }
    }
    
    private void removeUser(Socket socket) {
        for (User user : users) {
            if (user.getSocket() == socket) {
                users.remove(user);
                connected_users--;
            }
        }
    }
}
