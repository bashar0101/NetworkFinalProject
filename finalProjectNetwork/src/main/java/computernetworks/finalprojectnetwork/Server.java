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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public String url = "jdbc:mysql://localhost:3306/cmpy";
    public String username = "root";
    public String password = "20142007";

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
            // we will split the data came from the client
            String[] data = message.split(",");
            String email = data[0];
            String passwordData = data[1];

            // we will check the sign in data in the database
            try {
                System.out.println("We are here2");
                Connection connection = DriverManager.getConnection(url, username, password);
                String sql = "SELECT * FROM clients WHERE email = ? AND password = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, email);
                statement.setString(2, passwordData);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    try {
                        System.out.println("user in the database");
                        out.writeUTF("true");
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        System.out.println("user not in the database");
                        out.writeUTF("false");
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

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
