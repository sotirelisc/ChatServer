
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
    private final String name;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    
    public User(ChatServer server, Socket socket, String name) {
        this.server = server;
        this.socket = socket;
        this.name = name;
    }
    
    // Apostolh mhnumatos ston xrhsth
    public void sendMessage(Message msg) {
        try {
            this.oos.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
            
            Message new_msg;
            try {
                new_msg = (Message) ois.readObject();
                server.sendMessageToAll(new_msg);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
