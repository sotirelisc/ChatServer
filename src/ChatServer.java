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

public class ChatServer {
    private static SSLSocketFactory factory;
    private static int Port = 6666;
    private List<User> users;
    private ArrayList<Message> messageLog;
    private String command;

    public ChatServer() {
        // Lista sundedemenwn xrhstwn
        users = Collections.synchronizedList(new ArrayList());
        messageLog = new ArrayList();

    }
    
    public void startServer() {

        System.out.println("[SERVER LOG]: Starting server..");
        try {

            //Δημιουργούμε αντικειμενο SSLContext
            SSLContext context = SSLContext.getInstance("SSL");

            //Περνάμε null στα keystore και truststore καθώς δε χρησιμοποιούμε κάποια
            // CA ή κάποιο certificate. Το securerandom αποτελεί nonce για τη κρυπτογράφηση
            context.init(null, null, null);
            SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            SSLServerSocket server = (SSLServerSocket) factory.createServerSocket(Port);
            
            
            System.out.println("[SERVER LOG]: Server listening on port " + server.getLocalPort() + ".");
            while (true) {
                // Sundesh neou xrhsth
                SSLSocket client_socket = (SSLSocket)server.accept();
                client_socket.setNeedClientAuth(false);
                
                final String[] enabledCipherSuites = { "SSL_DH_anon_WITH_RC4_128_MD5" };
                client_socket.setEnabledCipherSuites(enabledCipherSuites);

                
                // Βάζουμε ένα listener για να ξέρουμε πότε ολοκληρώθηκε το handshake
                client_socket.addHandshakeCompletedListener(new MyHandshakeListener());
                
                User new_user = new User(this, client_socket);
                new_user.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        // Enarksh tou chat
        ChatServer chatServer = new ChatServer();
        chatServer.startServer();
    }

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

    synchronized void addMessage(Message msg) {
        messageLog.add(msg);
        sendMessageToAll(msg);
    }

    void sendMessageToAll(Message msg) {
        for (User user : users) {
            user.sendMessage(msg);
        }
    }

    void sendListToAll() {
        for (User user : users) {
            user.sendUserList();
        }
    }

    void addUser(User theUser) {
        users.add(theUser);
        sendListToAll();
    }

    //ConcurrentModificationException will be thrown in this case, even when 
    //there’s only a single thread running. To fix this problem,
    //we can’t use the for-each loop since we have to use the remove() 
    //method of the iterator, which is not accessible within the for-each loop. 
    //Instead we have to do this:
    void removeUser(Socket socket) {
        System.out.println("removing user");
        for (Iterator<User> iter = users.iterator(); iter.hasNext();) {
            User s = iter.next();
            if (s.getSocket() == socket) {
                iter.remove();
                sendListToAll();
            }
        }
    }

    public ArrayList<String> getUserList() {
        ArrayList<String> list = new ArrayList();
        for (User user : users) {
            list.add(user.getUsername());
        }
        return list;
    }
}

class MyHandshakeListener implements HandshakeCompletedListener {
	  public void handshakeCompleted(HandshakeCompletedEvent e) {
	    System.out.println("Handshake succesful! from " + e.getSocket());
	    System.out.println("Using cipher suite: " + e.getCipherSuite());
	  }
}
