/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package computernetworks.finalprojectnetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author basha
 */
public class Server extends Thread {

    boolean isListening = false;
    int port;
    ServerSocket serverSocket;
    private ArrayList<Client> clients;
    InetAddress ipAddress;

    DataInputStream in;
    DataOutputStream out;

    // database information 
    Server() {

    }

    public boolean Create(int port) {
        try {
            this.port = port;
            serverSocket = new ServerSocket(this.port);
            ipAddress = serverSocket.getInetAddress();
            System.out.println("Server started...");
            clients = new ArrayList<>();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public void Listen() {
        this.isListening = true;
        this.start();
    }

    @Override
    public void run() {
        try {
            System.out.println("server waiting for clients...");
            Socket clientSocket = serverSocket.accept();//blocking
            System.out.println("client connected to server...");
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            Client client = new Client(ipAddress.toString(), port);
            clients.add(client);
            String cinfo = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
            ServerFrm.clientsListModel.addElement(cinfo);
            // get signin info
            String message = in.readUTF();
            System.out.println("Message form client" + clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort());
            System.out.println(message);

            // we will check the sign in data in the database
            

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // this function will stop the server
    public void Stop() {
        try {
            this.isListening = false;
            this.serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void DicconnectClient(Client client) {
        this.clients.remove(client);
        ServerFrm.clientsListModel.removeAllElements();
        for (Client sClient : clients) {
            String cinfo = sClient.socket.getInetAddress().toString() + ":" + sClient.socket.getPort();
            ServerFrm.clientsListModel.addElement(cinfo);
        }

    }

}
