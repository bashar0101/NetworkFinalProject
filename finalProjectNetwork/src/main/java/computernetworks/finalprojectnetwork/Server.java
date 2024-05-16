/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package computernetworks.finalprojectnetwork;

import static computernetworks.finalprojectnetwork.ProjectFrm.comingMessagesListModel;
import static computernetworks.finalprojectnetwork.Server.connectedClients;
import static computernetworks.finalprojectnetwork.Server.ipAddress;
//import static computernetworks.finalprojectnetwork.Server.port;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
    ClientHandler clientHandler;

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
                clientHandler = new ClientHandler(clientSocket, this);
                clientHandler.start();

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.Create(5000);
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
    Server server;
    public String url = "jdbc:mysql://localhost:3306/cmpy";
    public String username = "root";
    public String password = "20142007";

    ClientHandler(Socket clientSocket, Server server) {
        try {
            // the client will
            this.server = server;
            client = new Client("localhost", 5000);
            this.client.socket = clientSocket;
            System.out.println("-------------------" + client.socket.getPort());
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
                    createProject(pEmail, pName);

                } else if (operationCode.equals("4")) {
                    String pName = data[1];
                    String pKey = data[2];
                    String clientEmail = data[3];
                    joinProject(pName, pKey, clientEmail);

                } else if (operationCode.equals("5")) {
                    String pName = data[1];
                    String senderName = data[2];
                    String senderLastname = data[3];
                    String message = data[4];
                    sendMessage(message, pName, senderName, senderLastname);
                } else if (operationCode.equals("6")) {
                    String pName = data[1];
                    String email = data[2];
                    getProjectMembers(pName, email);

                } else if (operationCode.equals("7")) {
                    String pName = data[1];
                    getOnlineClientsOfProject(pName);
                } else if (operationCode.equals("8")) {
                    String email = data[1];
                    getProjectPrivateKey(email);
                }
                if (operationCode.equals("9")) {
                    String pName = data[1];
                    String senderName = data[2];
                    String csenderLastName = data[3];
                    String toSend = data[4];
                    String message = data[5];
                    sendMessageSolo(message, pName, senderName, csenderLastName, toSend);
                }
                if (operationCode.equals("exit")) {
                    String cEmail = data[1];
                    disconnectClient(cEmail);
                }

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void disconnectClient(String email) {
        for (Client connectedClient : connectedClients) {
            try {
                if (connectedClient.cleintEmail.equals(email)) {
                    connectedClients.remove(connectedClient);
                }
                out.writeUTF("exitAcc");
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

//    public void sendFile(String fielPath, String pName) throws IOException {
//        ArrayList<Client> c = getOnlineClientsInProject(pName);
//        for (Client connectedClient : c) {
//            connectedClient.sendFile(fielPath);
//            System.out.println("1");
//        }
//    }
    public void sendMessageSolo(String message, String pName, String senderName, String senderLastName, String toSend) {
        ArrayList<Client> c = getOnlineClientsInProject(pName);
        for (Client connectedClient : c) {
            if ((connectedClient.clientName + " " + connectedClient.clientLastName).equals(toSend)) {
                connectedClient.sendMessageSolo(message, senderName, senderLastName);
            }
        }
    }

    public void sendMessage(String message, String pName, String senderName, String senderLastName) {

        ArrayList<Client> c = getOnlineClientsInProject(pName);
        for (Client connectedClient : c) {
            connectedClient.sendMessage(message, senderName, senderLastName);
        }
    }

    public ArrayList<Client> getOnlineClientsInProject(String projectName) {
        ArrayList<Client> connectedClientsInProject = new ArrayList<>();
        StringBuilder fullNameBuilder = new StringBuilder();

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String query = "SELECT clients.name, clients.surname "
                    + "FROM userProjects "
                    + "JOIN clients ON userProjects.email = clients.email "
                    + "WHERE userProjects.project_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, projectName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String surname = resultSet.getString("surname");
                fullNameBuilder.append(name).append(" ").append(surname).append(",");
            }

            String[] users = fullNameBuilder.toString().split(",");
            HashSet<String> projectMembersSet = new HashSet<>(Arrays.asList(users));

            for (Client client : Server.connectedClients) {
                String clientFullName = client.clientName + " " + client.clientLastName;
                if (projectMembersSet.contains(clientFullName)) {
                    connectedClientsInProject.add(client);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connectedClientsInProject;
    }

    public ArrayList getOnlineClientsOfProjectArray(String projectName) {
        ArrayList<String> connectedClients = new ArrayList<>();
        StringBuilder fullNameBuilder = new StringBuilder();
        String dataToClient = "";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String query = "SELECT clients.name, clients.surname\n"
                    + "FROM userProjects\n"
                    + "    JOIN clients ON userProjects.email = clients.email\n"
                    + "WHERE\n"
                    + "    userProjects.project_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, projectName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String surname = resultSet.getString("surname");

                fullNameBuilder.append(name).append(" ").append(surname).append(",");
            }

            String[] useres = fullNameBuilder.toString().split(",");
            ArrayList<String> connectedToserver = new ArrayList<>();
            for (int i = 0; i < Server.connectedClients.size(); i++) {
                connectedToserver.add(Server.connectedClients.get(i).clientName + " " + Server.connectedClients.get(i).clientLastName);
            }
            for (int i = 0; i < useres.length; i++) {
                for (int j = 0; j < connectedToserver.size(); j++) {
                    if (useres[i].equals(connectedToserver.get(j))) {
                        dataToClient += useres[i] + ",";
                        connectedClients.add(dataToClient);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connectedClients;
    }

    public void getOnlineClientsOfProject(String projectName) {
        StringBuilder fullNameBuilder = new StringBuilder();
        String dataToClient = "";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String query = "SELECT clients.name, clients.surname\n"
                    + "FROM userProjects\n"
                    + "    JOIN clients ON userProjects.email = clients.email\n"
                    + "WHERE\n"
                    + "    userProjects.project_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, projectName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String surname = resultSet.getString("surname");

                fullNameBuilder.append(name).append(" ").append(surname).append(",");
            }

            String[] useres = fullNameBuilder.toString().split(",");
            ArrayList<String> connectedToserver = new ArrayList<>();
            for (int i = 0; i < Server.connectedClients.size(); i++) {
                connectedToserver.add(Server.connectedClients.get(i).clientName + " " + Server.connectedClients.get(i).clientLastName);
            }
            for (int i = 0; i < useres.length; i++) {
                for (int j = 0; j < connectedToserver.size(); j++) {
                    if (useres[i].equals(connectedToserver.get(j))) {
                        System.out.println("User" + useres[i]);
                        dataToClient += useres[i] + ",";
                    }
                }
            }

            try {
                out.writeUTF("71," + dataToClient);
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList getProjectMembers(String projectName, String email) {
        ArrayList<String> pMembers = new ArrayList<>();
        String dataToSend = "";
        if (getProjectManagerEmail(projectName).equals(email)) {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                String query = "SELECT clients.name, clients.surname\n"
                        + "FROM userProjects\n"
                        + "    JOIN clients ON userProjects.email = clients.email\n"
                        + "WHERE\n"
                        + "    userProjects.project_name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, projectName);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String surname = resultSet.getString("surname");
//                    fullNameBuilder.append(name).append(" ").append(surname).append(",");
                    pMembers.add(name + " " + surname + ",");
                    dataToSend += name + " " + surname + ",";
                }

                try {
                    out.writeUTF("61," + projectName + "," + dataToSend);
                } catch (IOException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                out.writeUTF("60");
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pMembers;
    }

    private String getProjectManagerEmail(String projectName) {
        String query = "SELECT manager_email FROM Projects WHERE project_name = ?";
        String managerEmail = null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, projectName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                managerEmail = resultSet.getString("manager_email");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return managerEmail;
    }

    private void createProject(String email, String projectName) {
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
                addUserToProject(email, projectName);
                messageToserver = "";
                messageToserver += "31,";
                messageToserver += projectName;
                messageToserver += ",";
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

    private void signUp(String name, String lastName, String email, String passwordData) {
        // Sign up logic
        this.client.clientName = name;
        this.client.clientLastName = lastName;
        this.client.cleintEmail = email;
        this.client.in = in;
        this.client.out = out;

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

    private void signIn(String email, String passwordData) {
        // Sign in logic
        this.client.in = in;
        this.client.out = out;
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
                        this.client.clientName = nameTosend;
                        this.client.clientLastName = lastNameToSend;
                        this.client.cleintEmail = emailToSend;

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

    private void getProjectPrivateKey(String email) {
        String toSend = "";
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String sql = "SELECT project_name, project_key FROM Projects WHERE manager_email = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String projectname = resultSet.getString("project_name");
                String key = resultSet.getString("project_key");
                toSend += projectname + "->" + key + ",";
            }
            try {
                if (!toSend.equals("")) {
                    out.writeUTF("81," + toSend);
                } else {
                    out.writeUTF("80");
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            // Handle any SQL errors
            e.printStackTrace();
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

    private void joinProject(String pName, String projectKey, String email) {
        if (checkProjectKey(pName, projectKey)) {
            try {
                addUserToProject(email, pName);
                System.out.println("The user withe email : " + email + " Join the project " + pName);
                out.writeUTF("41," + pName);
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                out.writeUTF("40");
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void addUserToProject(String email, String pName) {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String insertQuery = "INSERT INTO userProjects (email, project_name) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, email);
                pstmt.setString(2, pName);

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

    public boolean checkProjectKey(String projectName, String projectKey) {
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

    private String getUserProjects(String email) {
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

}
