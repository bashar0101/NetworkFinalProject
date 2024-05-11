/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package computernetworks.finalprojectnetwork;

import java.util.ArrayList;

/**
 *
 * @author basha
 */
public class Project {

    String projectName;
    String projectManager;
    String projectServerKey;
    ArrayList<Client> projectMembers;

    public Project(String projectName, String projectManager) {
        this.projectName = projectName;
        this.projectManager = projectManager;
        this.projectMembers = new ArrayList<>();
    }

    public void addmember() {

    }

}
