/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package computernetworks.finalprojectnetwork;

import static computernetworks.finalprojectnetwork.MainFrm.project;
import static computernetworks.finalprojectnetwork.ProjectFrm.projeConnectedClientsModel;
//import static computernetworks.finalprojectnetwork.ProjectFrm.projectmembersListModel;
import static computernetworks.finalprojectnetwork.SignInFrm.client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author basha
 */
public class Client extends Thread {

    // data sending part
    // first we wnat to send the sign in data to check in the database
//    static String siginInData = "";
    String serverResponse = "";
    ///

    Socket socket;

    DataInputStream in;
    DataOutputStream out;
    // server adresi ip address
    String serverIp;
    // port numarası
    int serverPort;
    boolean isListening = true;

    public String clientName;
    public String clientLastName;
    public String cleintEmail;

    /// ////////////////
    static SignInFrm signInFrm;
    static SignUpFrm signUpFrm;
    static MainFrm mainFrm;
    static ProjectFrm projectFrm;

    // file sending
    File file;

    public Client(String serverIp, int port) {
        this.serverIp = serverIp;
        this.serverPort = port;
    }

    public boolean ConnectToServer() {
        try {

            socket = new Socket(this.serverIp, this.serverPort);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            System.out.println("Connection accepted with server -> " + socket.getInetAddress() + ":" + socket.getPort());

        } catch (Exception err) {
            System.out.println("Error connecting to server: " + err);
        }
        return true;
    }

    @Override
    public void run() {
        while (isListening) {
            try {
                serverResponse = "";
                serverResponse = in.readUTF();
                System.out.println("Response form server : " + serverResponse);

                String[] dataFromServer = client.serverResponse.split(",");
                // if response is 11 means that the user in the database
                if (dataFromServer[0].equals("11")) {
                    signInCorrect(dataFromServer);
                    // if email in the database but password wrong response will be 10
                } else if (dataFromServer[0].equals("10")) {
                    // if eamil true and password wrong server send (10) , we can reset password for
                    // this
                    System.out.println("email true but password wrong");
                    JOptionPane.showMessageDialog(signInFrm, "reset password");
                    // if response if 0 no user in the database
                } else if (dataFromServer[0].equals("0")) {
                    // becuse no email registerd in the database server send (0), we can create a
                    // new account or cancel
                    System.out.println("Email not found");
                    JOptionPane.showMessageDialog(signInFrm, "Email not registerd in the database!!");

                }
                // if response is 111 measns that user added to databse clients table
                if (dataFromServer[0].equals("111")) {
                    SignInFrm signIn = Client.signInFrm;
                    signUpFrm.setVisible(false);
                    signIn.setVisible(true);
                    // if response is 000 email is use in the database
                } else if (dataFromServer[0].equals("000")) {
                    JOptionPane.showMessageDialog(signUpFrm, "Email is used use new email!!");

                }
                if (dataFromServer[0].equals("31")) {
                    project = new Project(dataFromServer[1], client.clientName);
                    String serverKey = dataFromServer[2];
                    project.projectServerKey = serverKey;
                    MainFrm.projectListModel.addElement(dataFromServer[1]);
                    JOptionPane.showMessageDialog(mainFrm, "project created!");
                    System.out.println("project created done!!");
                } else if (dataFromServer[0].equals("30")) {
                    JOptionPane.showMessageDialog(mainFrm, "This project name is already used!");
                }

                if (dataFromServer[0].equals("41")) {

                    MainFrm.projectListModel.addElement(dataFromServer[1]);
//            Project.connectedToPRojectClients.add(client.socket);
                    JOptionPane.showMessageDialog(mainFrm, "Join the project succefully!");
                } else if (dataFromServer[0].equals("40")) {
                    JOptionPane.showMessageDialog(mainFrm, "Did not joined the project!");
                }
                // get the projects keys 
                if (dataFromServer[0].equals("81")) {
                    String t = "";
                    for (int i = 1; i < dataFromServer.length; i++) {
                        t += dataFromServer[i] + "\n";
                    }
                    JOptionPane.showMessageDialog(mainFrm, t);
                } else if (dataFromServer[0].equals("80")) {
                    JOptionPane.showMessageDialog(mainFrm, "No project for you");
                }
                if (dataFromServer[0].equals("61")) {
                    String t = "";
                    String pName = dataFromServer[1];
                    for (int i = 2; i < dataFromServer.length; i++) {
//                        projectmembersListModel.addElement(dataFromServer[i]);
                        t += dataFromServer[i] + "\n";
                    }
                    JOptionPane.showMessageDialog(projectFrm, "Members of projetct " + pName + "\n" + t);
                } else if (dataFromServer[0].equals("60")) {
                    JOptionPane.showMessageDialog(projectFrm, "Only Manager of project can see the project members!");
                }
                if (dataFromServer[0].equals("71")) {
                    for (int i = 1; i < dataFromServer.length; i++) {
                        projeConnectedClientsModel.addElement(dataFromServer[i]);
                    }
                }
                if (dataFromServer[0].equals("51")) {
                    ProjectFrm.comingMessagesListModel.addElement(dataFromServer[1] + " " + dataFromServer[2] + " : " + dataFromServer[3]);
                }
                if (dataFromServer[0].equals("91")) {
                    ProjectFrm.comingMessagesListModel.addElement(dataFromServer[1] + " " + dataFromServer[2] + " : " + dataFromServer[3] + "(Solo message)");
                }

            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void signInCorrect(String[] dataFromServer) {
        // if email true and password true we know from the server message (11) , go to
        // main page of the program
        client.clientName = dataFromServer[1];
        client.clientLastName = dataFromServer[2];
        client.cleintEmail = dataFromServer[3];

        MainFrm mainFrm = new MainFrm();
        for (int i = 4; i < dataFromServer.length; i++) {
            MainFrm.projectListModel.addElement(dataFromServer[i]);
        }
        System.out.println("email and passowrd true");

        signInFrm.setVisible(false);
        mainFrm.setVisible(true);
    }

    public void disconnectClientFromServer(String data) {
        try {
            out.writeUTF(data);
            System.out.println("data send to server is :" + data);
            serverResponse = in.readUTF();
            System.out.println("result of checking in db : " + serverResponse);
            out.flush();
//            checkDBServerResult = "";

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

//    public void sendFile(String filePath) throws FileNotFoundException, IOException {
//        byte[] buffer = new byte[8192]; // Buffer size
//        int bytesRead;
//        FileInputStream fileInputStream = new FileInputStream(filePath);
//        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
//            out.write(buffer, 0, bytesRead);
//        }
//    }

    public void sendMessageSolo(String message, String name, String lastName) {
        try {
            out.writeUTF("91," + name + "," + lastName + "," + message);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(String message, String name, String lastName) {
        try {
            out.writeUTF("51," + name + "," + lastName + "," + message);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendDataToServer(String data) {
        try {
            out.writeUTF(data);
            System.out.println("data send to server is :" + data);

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Listen() {
        this.isListening = true;
        this.start();

    }

    public void disconnect() {
        try {
            //tüm nesneleri kapatıyoruz
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }

            if (socket != null) {
                socket.close();
            }

        } catch (Exception err) {
            System.out.println(err.getMessage());
        }

    }
}
