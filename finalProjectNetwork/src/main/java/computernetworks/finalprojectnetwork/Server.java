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
    static int port;
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

    public void CheckData() {
        String data = "";
        try {
            data = in.readUTF();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] dataToCheck = data.split(",");
        String email = dataToCheck[0];
        String passwordData = dataToCheck[1];

        // we will check the sign in data in the database
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
                        out.writeUTF("11");
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
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        while (isListening) {
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
                /// ////////////////////////
                // we should siplt the sign in and the sign uup to seprite threads
                /// /////////////////////////
//                String message = in.readUTF();
//                System.out.println("Message form client" + clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort());
//                System.out.println(message);
                // we will split the data came from the client

//                String[] data = message.split(",");
//                String email = data[0];
//                String passwordData = data[1];
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
//                                out.writeUTF("11");
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
//                        } catch (IOException ex) {
//                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                } catch (SQLException e) {
//                    System.out.println(e.getMessage());
//                }
//                String SignUpmessage = in.readUTF();
//                String[] signupdata = SignUpmessage.split(",");
//                String name = signupdata[0];
//                String lastName = signupdata[1];
//                String email = signupdata[2];
//                String passwordData = signupdata[3];
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
//                } catch (SQLException e) {
//                    System.out.println(e.getMessage());
//                }

//            try {
//                Connection connection = DriverManager.getConnection(url, username, password);
//                String sql = "SELECT * FROM clients WHERE email = ? AND password = ?";
//                PreparedStatement statement = connection.prepareStatement(sql);
//                statement.setString(1, email);
//                statement.setString(2, passwordData);
//                ResultSet resultSet = statement.executeQuery();
//                if (resultSet.next()) {
//                    try {
//                        System.out.println("user in the database");
//                        out.writeUTF("true");
//                    } catch (IOException ex) {
//                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                } else {
//                    try {
//                        System.out.println("user not in the database");
//                        out.writeUTF("false");
//                    } catch (IOException ex) {
//                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            } catch (SQLException e) {
//                System.out.println(e.getMessage());
//            }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
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

class checkSignInDataClass extends Thread {

    DataInputStream in;
    DataOutputStream out;
    String data;
    String url;
    String username;
    String password;

    checkSignInDataClass(String data, DataInputStream in, DataOutputStream out, String url, String username, String password) {
        this.in = in;
        this.out = out;
        this.data = data;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
        String[] dataToCheck = data.split(",");
        String email = dataToCheck[0];
        String passwordData = dataToCheck[1];

        // we will check the sign in data in the database
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
                        out.writeUTF("11");
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
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
