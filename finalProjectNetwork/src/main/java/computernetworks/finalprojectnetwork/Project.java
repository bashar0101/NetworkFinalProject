/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package computernetworks.finalprojectnetwork;

import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author basha
 */
public class Project {

    public static ArrayList<Socket> connectedToPRojectClients;
    String projectName;
    String projectManager;
    String projectServerKey;
    ArrayList<Client> projectMembers;

    public Project(String projectName, String projectManager) {
        this.projectName = projectName;
        this.projectManager = projectManager;
        this.projectMembers = new ArrayList<>();
    }

}
