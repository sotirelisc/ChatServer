
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServer {

    private ArrayList<User> users;
    private ArrayList<Message> messageLog;
    private String command;

    public ChatServer() {
        // Lista sundedemenwn xrhstwn
        users = new ArrayList();
        messageLog = new ArrayList();
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
                new_user.start();
            }
        } catch (IOException ex) {
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
    void addUser(User theUser) {
        users.add(theUser);
    }
    

    synchronized void removeUser(Socket socket) {
        System.out.println("removing user");
        for (User user : users) {
            System.out.println("current user = " + user.getUsername());
            if (user.getSocket() == socket) {
                users.remove(user);
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