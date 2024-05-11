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
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author basha
 */
public class Server {

    boolean isListening = false;
    int port;
    ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients;
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
//        this.start();
        run();
    }

//    @Override
    public void run() {
        while (isListening) {
            try {
                System.out.println("server waiting for clients...");
                Socket clientSocket = serverSocket.accept();//blocking
                String cinfo = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
                System.out.println(cinfo + " :: connected to server....");
                ServerFrm.clientsListModel.addElement(cinfo);

//                System.out.println("client connected to server...");  
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();

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
        for (ClientHandler sClient : clients) {
            String cinfo = sClient.client.getInetAddress().toString() + ":" + sClient.client.getPort();
            ServerFrm.clientsListModel.addElement(cinfo);
        }

    }

}

class ClientHandler extends Thread {

    Socket client;
    DataInputStream in;
    DataOutputStream out;

    public String url = "jdbc:mysql://localhost:3306/cmpy";
    public String username = "root";
    public String password = "20142007";

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
        handleClient(client);
    }

    public void handleClient(Socket clientSocket) {
        while (true) {
            try {

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
                    signIn(email, passwordData);

                } // sign ups section 
                else if (operationCode.equals("2")) {
                    String name = data[1];
                    String lastName = data[2];
                    String email = data[3];
                    String passwordData = data[4];
                    signUp(name, lastName, email, passwordData);

                } else if (operationCode.equals("3")) {
                    String pEmail = data[1];
                    String pName = data[2];
                    String userName = data[3];
                    String userLastName = data[4];
                    createProject(userName, userLastName, pEmail, pName);

                } else if (operationCode.equals("4")) {
                    String pName = data[1];
                    String pKey = data[2];
                    String clientName = data[3];
                    String clientLastName = data[4];
                    String clientEmail = data[5];
                    joinProject(pName, pKey, clientName, clientLastName, clientEmail);

                }

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // here tommorw
    // when we create new project we should add the user to the userProjects table at the same time
    private synchronized void createProject(String userName, String userLastName, String email, String projectName) {
        String randomKey = generateKey();
        String messageToserver = "";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String insertQuery = "INSERT INTO Projects (manager_email, project_name,project_key) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setString(1, email);
            pstmt.setString(2, projectName);
            pstmt.setString(3, randomKey);

            pstmt.executeUpdate();
            System.out.println("Project inserted successfully");
            try {
//                increaseProjectusers(projectName);
                // here we will generate a random key for entering the project
                addUserToProject(userName, userLastName, email, projectName);
                messageToserver = "";
                messageToserver += "31,";
                messageToserver += randomKey;
                out.writeUTF(messageToserver);
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            try {
                System.out.println("Name not allowed: Project name is already used");
                out.writeUTF("30,");
//                System.out.println(e.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
                    out.writeUTF("000");
                } catch (IOException ex) {
                    Logger.getLogger(Server.class
                            .getName()).log(Level.SEVERE, null, ex);
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
                        out.writeUTF("111");
//                                DicconnectClient(client);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        System.out.println("Insertion failed");
                        out.writeUTF("insertion failed");
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class
                                .getName()).log(Level.SEVERE, null, ex);
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
                        // we will send the user data when he sign in succefully
                        String nameTosend = resultSet.getString("name");
                        String lastNameToSend = resultSet.getString("surname");
                        String emailToSend = resultSet.getString("email");

                        String respone = "11";
                        respone += "," + nameTosend + "," + lastNameToSend + "," + emailToSend + ",";
                        respone += getUserProjects(emailToSend);
                        out.writeUTF(respone);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    // Passwords don't match
                    try {
                        System.out.println("email in the database but wrong password");
                        String respone = "10";
                        out.writeUTF(respone);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                // User not found
                try {
                    System.out.println("Email not registered");
                    out.writeUTF("0");

                } catch (IOException ex) {
                    Logger.getLogger(Server.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void increaseProjectusers(String projectName) {
        String updateQuery = "UPDATE UserProjects SET num_users = num_users + 1 WHERE project_name = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
                PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setString(1, projectName);
            pstmt.executeUpdate();
            System.out.println("Number of users incremented for project: " + projectName);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to increment number of users for project: " + projectName);
        }
    }

    private String generateKey() {
        StringBuilder key = new StringBuilder();
        String chrs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int KEY_LENGTH = 5;
        Random random = new Random();
        for (int i = 0; i < KEY_LENGTH; i++) {
            int index = random.nextInt(chrs.length());
            key.append(chrs.charAt(index));
        }

        return key.toString();
    }

    private synchronized void joinProject(String pName, String projectKey, String name, String lastName, String email) throws IOException {
        if (checkProjectKey(pName, projectKey)) {
            addUserToProject(name, lastName, email, pName);
            System.out.println(name + "Join the project " + pName);
            out.writeUTF("41");
        } else {
            out.writeUTF("40");
        }
    }

    public synchronized void addUserToProject(String name, String lastName, String email, String pName) {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String insertQuery = "INSERT INTO userProjects (name, lat_name, email, project_name) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, name);
                pstmt.setString(2, lastName);
                pstmt.setString(3, email);
                pstmt.setString(4, pName);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User added successfully to userProjects table.");
                } else {
                    System.out.println("Failed to add user to userProjects table.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add user to userProjects table.");
        }
    }

    public synchronized boolean checkProjectKey(String projectName, String projectKey) {
        boolean isValid = false;
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String selectQuery = "SELECT project_key FROM Projects WHERE project_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                pstmt.setString(1, projectName);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String storedProjectKey = rs.getString("project_key");
                    isValid = storedProjectKey.equals(projectKey);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to check project key");
        }
        return isValid;
    }

    private synchronized String getUserProjects(String email) {
        StringBuilder projectNameBuilder = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String selectQuery = "SELECT project_name FROM userProjects WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    projectNameBuilder.append(rs.getString("project_name")).append(',');
                    System.out.println(rs.getString("project_name"));
                }
                System.out.println("data sent from getUserProjects : " + projectNameBuilder.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to retrieve user projects");
        }
        return projectNameBuilder.toString();
    }

    private synchronized void getProjectMembers(String name) {
        
    }

}

// this the code to create or tables for the server
//CREATE TABLE Users (
//    user_id INT AUTO_INCREMENT PRIMARY KEY,
//    name VARCHAR(255) NOT NULL,
//    surname VARCHAR(255) NOT NULL,
//    email VARCHAR(255) NOT NULL UNIQUE,
//    password VARCHAR(255) NOT NULL
//);
//
//
//CREATE TABLE Projects (
//    project_id INT AUTO_INCREMENT PRIMARY KEY,
//    manager_email VARCHAR(255) not null,
//    project_name VARCHAR(255) NOT NULL,
//    project_key varchar(5) not null,
//    UNIQUE(project_name)
//);
//
//CREATE TABLE userProjects (
//	name VARCHAR(255) not null,
//    lat_name VARCHAR(255) not null,
//    email VARCHAR(255) not null,
//    manager_email VARCHAR(255) not null,
//    project_name VARCHAR(255) NOT NULL
//);
// ALTER TABLE userProjects DROP COLUMN manager_email;

