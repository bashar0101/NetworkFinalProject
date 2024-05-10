/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package computernetworks.finalprojectnetwork;

import static computernetworks.finalprojectnetwork.Server.ipAddress;
//import static computernetworks.finalprojectnetwork.Server.port;
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
    static InetAddress ipAddress;

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
        while (isListening) {
            try {
                System.out.println("server waiting for clients...");
                Socket clientSocket = serverSocket.accept();//blocking
                System.out.println("client connected to server...");
                // we will handle all the clients when connect to server in the same time
//                handleClient(clientSocket);
                new Thread(() -> handleClient(clientSocket)).start();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void handleClient(Socket clientSocket) {
        try {
            // Your existing client handling logic goes here
            // Remember to close the streams and socket when done with the client
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            Client client = new Client(ipAddress.toString(), port);
            clients.add(client);
            String cinfo = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
            ServerFrm.clientsListModel.addElement(cinfo);

            String clientMessage = in.readUTF();
            /// this is the messsage from the client we will check like four things
            // 1 if the message starts with one it means clients want to sign in

            System.out.println("Message form client" + clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort());
            System.out.println(clientMessage);
            // we will split the data came from the client

            String[] data = clientMessage.split(",");

            String operationCode = data[0];
            // sign in section  1 means sigin in
            if (operationCode.equals("1")) {
                String email = data[1];
                String passwordData = data[2];
//                new Thread(() -> signIn(email, passwordData)).start();
                signIn(email, passwordData);

            } // sign ups section 
            else if (operationCode.equals("2")) {
                String name = data[1];
                String lastName = data[2];
                String email = data[3];
                String passwordData = data[4];
//                new Thread(() -> signUp(name, lastName, email, passwordData)).start();
                signUp(name, lastName, email, passwordData);

            } else if (operationCode.equals("3")) {

            }

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized void signUp(String name, String lastName, String email, String passwordData) {
        // Sign up logic
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String checkIfExistsSql = "SELECT COUNT(*) AS count FROM clients WHERE email = ?";
            PreparedStatement checkIfExistsStatement = connection.prepareStatement(checkIfExistsSql);
            checkIfExistsStatement.setString(1, email);
            ResultSet resultSet = checkIfExistsStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt("count");
            if (count > 0) {
                // Email already exists
                try {
                    System.out.println("Email already exists");
                    out.writeUTF("email already exists");
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                // Email doesn't exist, proceed with insertion
                String insertSql = "INSERT INTO clients (name, surname, email, password) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                insertStatement.setString(1, name);
                insertStatement.setString(2, lastName);
                insertStatement.setString(3, email);
                insertStatement.setString(4, passwordData);
                int rowsAffected = insertStatement.executeUpdate();
                if (rowsAffected > 0) {
                    try {
                        System.out.println("Data inserted successfully");
                        out.writeUTF("data inserted");
//                                DicconnectClient(client);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        System.out.println("Insertion failed");
                        out.writeUTF("insertion failed");
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private synchronized void signIn(String email, String passwordData) {
        // Sign in logic
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT * FROM clients WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // User found, check password
                String storedPassword = resultSet.getString("password");
                if (storedPassword.equals(passwordData)) {
                    // Passwords match
                    try {
                        System.out.println("User in the database");
                        /* *****  */
                        String nameTosend = resultSet.getString("name");
                        String lastNameToSend = resultSet.getString("surname");
                        String emailToSend = resultSet.getString("email");
                        /* *****  */
                        String respone = "11";
                        respone += "," + nameTosend + "," + lastNameToSend + "," + emailToSend;
                        out.writeUTF(respone);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    // Passwords don't match
                    try {
                        System.out.println("User in the database but wrong password");
                        out.writeUTF("10");
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                // User not found
                try {
                    System.out.println("No email registered");
                    out.writeUTF("0");
//                            if (in.readUTF().equals("3")) {
//                            out.writeUTF("client" + client.port + " disconnected form the server");
//                            DicconnectClient(client);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
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

class ClientHandler implements Runnable {

    Socket client;
    DataInputStream in;
    DataOutputStream out;

    ClientHandler(Socket clientSocket) {
        try {
            this.client = clientSocket;
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {

        }
    }

}

//    public void handleClient(Socket clientSocket) {
//        try {
//            // Your existing client handling logic goes here
//            // Remember to close the streams and socket when done with the client
//            in = new DataInputStream(clientSocket.getInputStream());
//            out = new DataOutputStream(clientSocket.getOutputStream());
//            Client client = new Client(ipAddress.toString(), port);
//            clients.add(client);
//            String cinfo = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
//            ServerFrm.clientsListModel.addElement(cinfo);
//
//            String clientMessage = in.readUTF();
//            /// this is the messsage from the client we will check like four things
//            // 1 if the message starts with one it means clients want to sign in
//
//            System.out.println("Message form client" + clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort());
//            System.out.println(clientMessage);
//            // we will split the data came from the client
//
//            String[] data = clientMessage.split(",");
//
//            String operationCode = data[0];
//            // sign in section  1 means sigin in
//            if (operationCode.equals("1")) {
//                String email = data[1];
//                String passwordData = data[2];
//
//                // we will check the sign in data in the database
//                try {
//                    Connection connection = DriverManager.getConnection(url, username, password);
//                    String sql = "SELECT * FROM clients WHERE email = ?";
//                    PreparedStatement statement = connection.prepareStatement(sql);
//                    statement.setString(1, email);
//                    ResultSet resultSet = statement.executeQuery();
//                    if (resultSet.next()) {
//                        // User found, check password
//                        String storedPassword = resultSet.getString("password");
//                        if (storedPassword.equals(passwordData)) {
//                            // Passwords match
//                            try {
//                                System.out.println("User in the database");
//                                /* *****  */
//                                String nameTosend = resultSet.getString("name");
//                                String lastNameToSend = resultSet.getString("surname");
//                                String emailToSend = resultSet.getString("email");
//                                /* *****  */
//                                String respone = "11";
//                                respone += "," + nameTosend + "," + lastNameToSend + "," + emailToSend;
//                                out.writeUTF(respone);
//                            } catch (IOException ex) {
//                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        } else {
//                            // Passwords don't match
//                            try {
//                                System.out.println("User in the database but wrong password");
//                                out.writeUTF("10");
//                            } catch (IOException ex) {
//                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }
//                    } else {
//                        // User not found
//                        try {
//                            System.out.println("No email registered");
//                            out.writeUTF("0");
////                            if (in.readUTF().equals("3")) {
////                            out.writeUTF("client" + client.port + " disconnected form the server");
////                            DicconnectClient(client);
//                        } catch (IOException ex) {
//                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//
//                } catch (SQLException e) {
//                    System.out.println(e.getMessage());
//                }
//
//            } // sign ups section 
//            else if (operationCode.equals("2")) {
//                String name = data[1];
//                String lastName = data[2];
//                String email = data[3];
//                String passwordData = data[4];
//                try {
//                    Connection connection = DriverManager.getConnection(url, username, password);
//                    String checkIfExistsSql = "SELECT COUNT(*) AS count FROM clients WHERE email = ?";
//                    PreparedStatement checkIfExistsStatement = connection.prepareStatement(checkIfExistsSql);
//                    checkIfExistsStatement.setString(1, email);
//                    ResultSet resultSet = checkIfExistsStatement.executeQuery();
//                    resultSet.next();
//                    int count = resultSet.getInt("count");
//                    if (count > 0) {
//                        // Email already exists
//                        try {
//                            System.out.println("Email already exists");
//                            out.writeUTF("email already exists");
//                        } catch (IOException ex) {
//                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    } else {
//                        // Email doesn't exist, proceed with insertion
//                        String insertSql = "INSERT INTO clients (name, surname, email, password) VALUES (?, ?, ?, ?)";
//                        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
//                        insertStatement.setString(1, name);
//                        insertStatement.setString(2, lastName);
//                        insertStatement.setString(3, email);
//                        insertStatement.setString(4, passwordData);
//                        int rowsAffected = insertStatement.executeUpdate();
//                        if (rowsAffected > 0) {
//                            try {
//                                System.out.println("Data inserted successfully");
//                                out.writeUTF("data inserted");
////                                DicconnectClient(client);
//                            } catch (IOException ex) {
//                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        } else {
//                            try {
//                                System.out.println("Insertion failed");
//                                out.writeUTF("insertion failed");
//                            } catch (IOException ex) {
//                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }
//                    }
//
//                } catch (SQLException e) {
//                    System.out.println(e.getMessage());
//                }
//                // if the user closed the sign in frame
//                // e is for creating new project
//            } else if (operationCode.equals("3")) {
//
//            }
////            else if (operationCode.equals("cdis")) {
////                out.writeUTF("client" + client.port + " disconnected form the server");
////                DicconnectClient(client);
//////                    this.clients.remove(client);
//////                    ServerFrm.clientsListModel.removeAllElements();
//////                    for (Client sClient : clients) {
//////                        String newcinfo = sClient.socket.getInetAddress().toString() + ":" + sClient.socket.getPort();
//////                        ServerFrm.clientsListModel.addElement(newcinfo);
//////                    }
////            }
//
//        } catch (IOException ex) {
//            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
