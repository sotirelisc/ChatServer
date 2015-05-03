/* Μουστάκας Γεώργιος 321 / 2011 102
   Χατζηαναστασιάδης Μιχαήλ Μάριος 321 / 2011 176
   Σωτηρέλης Χρήστος 321 / 2012 182
*/
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ChatServer extends Thread {
    private static SSLSocketFactory factory;
    private static final int Port = 6666;
    private List<User> users;
    private ArrayList<Message> messageLog;
    private String command;
    private SSLServerSocket server;

    public ChatServer() {
        // Λίστα συνδεδεμένων χρηστών
        // Χρησιμοποιούμε Collections γιατι η απλή ArrayList δε μας
        // δίνει τη δυνατότητα να γράφουμε απο πολλά thread
        users = Collections.synchronizedList(new ArrayList());
        
        // Λίστα αποθήκευσης μηνυμάτων
        messageLog = new ArrayList();
    }
    
    // Constructor
    public void startServer() {

        System.out.println("[SERVER LOG]: Starting server..");
        try {

            // Δημιουργούμε αντικειμενο SSLContext
            SSLContext context = SSLContext.getInstance("SSL");

            // Περνάμε null στα keystore και truststore καθώς δε χρησιμοποιούμε κάποια
            // CA ή κάποιο certificate. 
            // Το securerandom αποτελεί nonce για τη κρυπτογράφηση
            context.init(null, null, new SecureRandom());
            
            // Δημιουργούμε τον server
            SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            server = (SSLServerSocket) factory.createServerSocket(Port);
            
            // Κάνουμε έναρξη νέου thread
            start();
            
            System.out.println("[SERVER LOG]: Server listening on port " + server.getLocalPort() + ".");
                        
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        @Override
    public void run() {
         // Loop για τη σύνδεση κάθε νέου χρήστη
            while (true) {
                try {
                    // Αποδοχή σύνδεσης νέου χρήστη
                    SSLSocket client_socket = (SSLSocket)server.accept();
                    
                    // Ορίζουμε να μην ζητήσουμε αυθεντικοποίηση (μέσω πιστοποιητικού)
                    // απο τον χρήστη για απλούστευση της διαδικασίας
                    client_socket.setNeedClientAuth(false);
                    
                    // Ορίζουμε κρυπτογραφική σουίτα ανώνυμη η οποία δεν απαιτεί πιστοποιητικό
                    // για να απλουστεύσουμε την υολοποίησή μας
                    final String[] enabledCipherSuites = { "SSL_DH_anon_WITH_RC4_128_MD5" };
                    
                    // τοποθετούμε τη σουίτα
                    client_socket.setEnabledCipherSuites(enabledCipherSuites);
                    
                    // Βάζουμε ένα listener για να ξέρουμε πότε ολοκληρώθηκε το handshake
                    client_socket.addHandshakeCompletedListener(new MyHandshakeListener());
                    
                    // Νέο αντικείμενο χρήστη
                    User new_user = new User(this, client_socket);
                    
                    // Ξεκινάμε το thread
                    new_user.start();
                } catch (IOException ex) {
                    Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
                }
}
    }
    

    public static void main(String[] args) {
        // Έναρξη του εξυπηρετητή
        ChatServer chatServer = new ChatServer();
        chatServer.startServer();
    }

    // Μέθοδος που βρίσκει άν υπάρχει συνδεδεμένος χρήστης
    // με το ίδιο όνομα
    public boolean exists(String name) {
        System.out.println("searching for user " + name);
        
        for (User user : users) {
            System.out.println("comparing " + name + " with " + user.getUsername());

            if (user.getUsername().equals(name)) {
                return true;
            }
        }
        return false;
    }

    // Συγχρονισμένη μέθοδος που προσθέτει κάθε μήνυμα που αποστέλλεται 
    // σε μια ArrayList
    synchronized void addMessage(Message msg) {
        messageLog.add(msg);
        sendMessageToAll(msg);
    }

    // Μέθοδος που αναλαμβάνει να αποστείλει ένα μήνυμα σε 
    // όλους τους χρήστες, έναν προς έναν
    void sendMessageToAll(Message msg) {
        for (User user : users) {
            user.sendMessage(msg);
        }
    }

    // Μέθοδος που αναλαμβάνει να αποστείλει τη λίστα των 
    // συνδεδεμένων χρηστών σε όλους τους χρήστες, έναν προς έναν
    void sendListToAll() {
        for (User user : users) {
            user.sendUserList();
        }
    }

    // Μέθοδος προσθήκης αντικειμένου χρήστη στη λίστα
    void addUser(User theUser) {
        users.add(theUser);
        sendListToAll();
    }
    
    // Μέθοδος διαγραφής ενός αντικειμένου χρήστη απο τη λίστα
    void removeUser(Socket socket) {
        System.out.println("removing user");
        // Λόγω του error ConcurrentModificationException που εμφανίζεται δεν έχουμε
        // τη δυνατότητα να χρησιμοποιήσουμε ένα απλό loop για την αναζήτηση στη λίστα
        // αλλα πρεπει να βασιστούμε στον Iterator για να επιβεβαιώσει οτι δεν χρησιμοποιεί κάποιο
        // άλλο thread τη λίστα για να γράψει ή να σβήσει
        for (Iterator<User> iter = users.iterator(); iter.hasNext();) {
            User s = iter.next();
            if (s.getSocket() == socket) {
                iter.remove();
                sendListToAll();
            }
        }
    }

    // Μέθοδος επιστροφής των ονομάτων των συνδεδεμένων χρηστών σε
    // *νέα* ArrayList προκειμένου να την αποστείλουμε σε κάθε χρήστη που θα 
    // τη ζητήσει
    public ArrayList<String> getUserList() {
        ArrayList<String> list = new ArrayList();
        for (User user : users) {
            list.add(user.getUsername());
        }
        return list;
    }
}

// Βοηθητική Κλάση που μας ενημερώνει για την εξέλιξη του SSL Handshake
class MyHandshakeListener implements HandshakeCompletedListener {
	  public void handshakeCompleted(HandshakeCompletedEvent e) {
	    System.out.println("Handshake succesful! from " + e.getSocket());
	    System.out.println("Using cipher suite: " + e.getCipherSuite());
	  }
}
