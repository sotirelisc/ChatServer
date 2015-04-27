
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
    private final Socket socket;
    private final String name;
    
    public User(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }
    
    // Apostolh mhnumatos ston xrhsth
    public void sendMessage(ObjectOutputStream oos, Message msg) {
        try {
            oos.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            
            Message new_msg;
            try {
                new_msg = (Message) ois.readObject();
                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
