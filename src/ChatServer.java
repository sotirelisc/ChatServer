
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Christos Sotirelis
 */
public class ChatServer {

    static ArrayList<User> users;
    
    public ChatServer() {
        System.out.println("[SERVER LOG]: Starting server..");
        try {
            // Dhmiourgia ServerSocket pou akouei sthn port 6666
            ServerSocket server = new ServerSocket(6666, 50);
            System.out.println("[SERVER LOG]: Server listening on port " + server.getLocalPort() + ".");
            // Lista sundedemenwn xrhstwn
            users = new ArrayList();
            int connected_clients = 0;
            while (true) {
                // Sundesh neou xrhsth
                Socket client_socket = server.accept();
                User new_user = new User(this, client_socket, "John Doe");
                users.add(new_user);
                new_user.start();
                connected_clients++;
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
    }

    void sendMessageToAll(Message msg) {
        for (User user : users) {
            user.sendMessage(msg);
        }
    }
}
