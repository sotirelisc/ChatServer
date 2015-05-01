
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
    private String command;
    
    public ChatServer() {
        // Lista sundedemenwn xrhstwn
        users = new ArrayList();
        
    }
    
    public void startServer() {

        System.out.println("[SERVER LOG]: Starting server..");
        try {
            // Dhmiourgia ServerSocket pou akouei sthn port 6666
            ServerSocket server = new ServerSocket(6666, 50);
            System.out.println("[SERVER LOG]: Server listening on port " + server.getLocalPort() + ".");
            while (true) {
                // Sundesh neou xrhsth
                Socket client_socket = server.accept();
                
                User new_user = new User(this, client_socket);
                
                users.add(new_user);
                
                new_user.start();
                
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Enarksh tou chat
        ChatServer chatServer = new ChatServer();
        chatServer.startServer();
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
    
    void removeUser(Socket socket) {
        for (User user : users) {
            if (user.getSocket() == socket) {
                users.remove(user);
            }
        }
    }
}
