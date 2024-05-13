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
public class Server extends Thread {

    boolean isListening = false;
    int port;
    ServerSocket serverSocket;
    public static ArrayList<Client> connectedClients;
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
            connectedClients = new ArrayList<>();
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
                Socket clientSocket = serverSocket.accept();// blocking
                String cinfo = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
                System.out.println("client connected to server ---> " + cinfo);
                ServerFrm.clientsListModel.addElement(cinfo);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.Create(222);
        server.Listen();
    }

    public static void getConnectedClients() {
        for (Client client : connectedClients) {
            System.out.println(client.socket.getInetAddress() + ":" + client.socket.getPort());
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

    // public void DicconnectClient(Client client) {
    // this.connectedClients.remove(client);
    // ServerFrm.clientsListModel.removeAllElements();
    // for (ClientHandler sClient : connectedClients) {
    // String cinfo = sClient.client.getInetAddress().toString() + ":" +
    // sClient.client.getPort();
    // ServerFrm.clientsListModel.addElement(cinfo);
    // }
    //
    // }
}

class ClientHandler extends Thread {

    Client client;
    // Socket client;
    DataInputStream in;
    DataOutputStream out;

    public String url = "jdbc:mysql://localhost:3306/cmpy";
    public String username = "root";
    public String password = "20142007";

    ClientHandler(Socket clientSocket) {
        try {
            // the client will
            client = new Client("localhost", 222);
            this.client.socket = clientSocket;
            in = new DataInputStream(client.socket.getInputStream());
            out = new DataOutputStream(client.socket.getOutputStream());
            Server.connectedClients.add(client);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        handleClient(client.socket);
    }

    public void handleClient(Socket clientSocket) {
        while (true) {
            try {

                String clientMessage = in.readUTF();
                /// this is the messsage from the client we will check like four things
                // 1 if the message starts with one it means clients want to sign in

                System.out.println("Message form client" + clientSocket.getInetAddress().toString() + ":"
                        + clientSocket.getPort());
                System.out.println(clientMessage);
                // we will split the data came from the client

                String[] data = clientMessage.split(",");

                String operationCode = data[0];
                // sign in section 1 means sigin in
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

                } else if (operationCode.equals("5")) {
                    String pName = data[1];
                    String message = data[2];
                    sendMessage(pName, message);
                }

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private synchronized void sendMessage(String pName, String message) {
        // i will send the massege to all clients they are in the same project and the
        // are conneccted online
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String query = "SELECT email FROM userProjects WHERE project_name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            // Set the project name parameter in the prepared statement
            stmt.setString(1, pName);
            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Iterate over the result set and print user details
            while (rs.next()) {
                String email = rs.getString("email");
                for (Client client : Server.connectedClients) {
                    if (email.equals(client.cleintEmail)) {
                        try {
                            out.writeUTF("51");
                            out.writeUTF(message);
                        } catch (IOException ex) {
                            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            out.writeUTF("50");
                        } catch (IOException ex) {
                            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // here tommorw
    // when we create new project we should add the user to the userProjects table
    // at the same time
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
                // increaseProjectusers(projectName);
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
                // System.out.println(e.getMessage());
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
                        // DicconnectClient(client);
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
                        // Project.connectedToPRojectClients.add(client);
                        //////////////////////////
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

    private String getProjectPrivateKey(String email) {
        String key = "";
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String sql = "SELECT project_key FROM Projects WHERE manager_email = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                key = "-->";
                key += resultSet.getString("project_key");
            }
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            // Handle any SQL errors
            e.printStackTrace();
        }
        return key;
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

    private synchronized void joinProject(String pName, String projectKey, String name, String lastName, String email)
            throws IOException {
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
            String insertQuery = "INSERT INTO userProjects (name, last_name, email, project_name) VALUES (?, ?, ?, ?)";
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

    private synchronized void getProjectMembers(String pName) {

    }

}
