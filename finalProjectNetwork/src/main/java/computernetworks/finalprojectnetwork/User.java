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
public class User {

    Client c = SignInFrm.client;
    String name;
    String lastName;
    String email;
    ArrayList< Project> userProjects;

    public User(String name, String lastName, String email) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        userProjects = new ArrayList<>();
    }
    
    

}
