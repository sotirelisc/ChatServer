/* Μουστάκας Γεώργιος 321 / 2011 102
   Χατζηαναστασιάδης Μιχαήλ Μάριος 321 / 2011 176
   Σωτηρέλης Χρήστος 321 / 2012 182
*/
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User extends Thread {
    private final ChatServer server;
    private final Socket socket;
    private String username;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    //Constructor
    public User(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    // Getters
    public Socket getSocket() {
        return this.socket;
    }

    public String getUsername() {
        return this.username;
    }

    // Μέθοδος αποστολής μηνύματος στο συγκεκριμένο user
    public void sendMessage(Message msg) {
        try {
            this.out.writeObject("MESSAGE");
            this.out.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        
        // Αποθηκεύουμε τις ροές
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }

        String command;
        try {
            // Loop πρωτοκόλλου
            while ((command = (String) in.readObject()) != null) {
                
                // Μήνυμα έναρξης της επικοινωνίας
                if (command.equals("START")) {
                    System.out.println("starting connection from " + socket.getInetAddress() + ":" + socket.getPort());
                    // Αποστέλλουμε Waiting
                    out.writeObject("WAITING");
                } 
                // Εφόσον λάβουμε εντολή για το username
                else if (command.equals("USERNAME")) {
                    
                    // Το αποθηκεύουμε
                    username = (String) in.readObject();
                    System.out.println(username + " tries to connect");

                    // Εφόσον δεν υπάρχει το όνομα ενημερώνουμε τον χρήστη
                    if (!server.exists(username)) {
                        out.writeObject("DOES NOT EXIST");
                        server.addUser(this);
                        System.out.println(username + " is connected");
                        
                        //Κάθε φορά που συνδέεται ένας χρήστης του αποστέλλουμε
                        //την λίστα με τους χρήστες που είναι συνδεδεμένοι
                        sendUserList();
                        
                    } else {
                        // Εφόσον υπάρχει το όνομα, ενημερώνουμε τον χρήστη
                        System.out.println("Username already exists");
                        out.writeObject("EXISTS");
                    }
                    
                // Εφόσον λάβουμε εντολή για αποστολή νέου μηνύματος
                } else if (command.equals("MESSAGE")) {
                    Message msg = new Message((String) in.readObject(), username);
                    System.out.println(username + " says :" + msg.getMessage());
                    server.addMessage(msg);
                }
            }
        } catch (IOException ex) {
            // Εφόσον αποσυνδεθεί ο χρήστης λαμβάνουμε ένα EOF exception
            // το οποίο συμπεριλαμβάνεται στο IOException
            System.out.println(username + " disconnected");
            // Αφαιρούμε τον χρήστη απο τη λίστα
            server.removeUser(socket);
            try {
                // Κλείνουμε το socket το οποίο με τη σειρά του κλείνει και τις ροές
                // Objectinput/output stream
                socket.close();
            } catch (IOException ex1) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Μέθοδος αποστολής της λίστας συνδεδεμένων χρηστών στο συγκεκριμένο χρήστη
    public void sendUserList() {
        try {
            System.out.println("Sending user list");
            out.writeObject("USERLIST");
            out.writeObject(server.getUserList());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}