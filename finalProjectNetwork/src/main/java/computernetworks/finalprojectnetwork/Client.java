/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package computernetworks.finalprojectnetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author basha
 */
public class Client extends Thread {

    // data sending part
    // first we wnat to send the sign in data to check in the database
//    static String siginInData = "";
    String checkDBServerResult = "";
    ///

    Socket socket;

    DataInputStream in;
    DataOutputStream out;
    // server adresi ip address
    String serverIp;
    // port numarası
    int port;
    boolean isListening = false;

    public Client(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    public boolean ConnectToServer() {
        try {
            // Client Soket nesnesi
            socket = new Socket(this.serverIp, this.port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            System.out.println("Connection accepted with server -> " + socket.getInetAddress() + ":" + socket.getPort());

        } catch (Exception err) {
            System.out.println("Error connecting to server: " + err);
        }
        return true;
    }

    public void sendDataToserverToCreateNewAccount(String data) {
        try {
            out.writeUTF(data);
            System.out.println("data send to server is :" + data);
            checkDBServerResult = in.readUTF();
            System.out.println("Server sasy : " + checkDBServerResult);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendDataToCheckInDataBase(String data) {
        try {
            data = SignInFrm.data;
            out.writeUTF(data);
            System.out.println("data send to server is :" + data);
            checkDBServerResult = in.readUTF();
            System.out.println("result of checking in db : " + checkDBServerResult);

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendDataToServer() {
        this.isListening = true;
        this.start();
    }

    @Override
    public void run() {
        while (isListening) {
            try {
//                out.writeUTF(SignInFrm.data);
//                System.out.println("data send to server is :" + SignInFrm.data);
                checkDBServerResult = in.readUTF();
                System.out.println("Server sasy : " + checkDBServerResult);

            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void SendMessage(String data) {
        try {
            out.writeUTF(data);
        } catch (IOException err) {
            System.out.println("Exception writing to server: " + err);
        }
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
