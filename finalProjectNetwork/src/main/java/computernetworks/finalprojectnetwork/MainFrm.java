/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package computernetworks.finalprojectnetwork;

import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author basha
 */
public class MainFrm extends javax.swing.JFrame {

    /**
     * Creates new form MainFrm
     */
    public static String projectName;
    public static Project project;
    public static DefaultListModel projectListModel = new DefaultListModel();
    Client client;
    static String data = "";

    public MainFrm() {
        initComponents();
        clientProjectsList.setModel(projectListModel);
        Client.mainFrm = this;
        client = SignInFrm.client;
        UserNameSurname.setText("User : " + client.clientName + " " + client.clientLastName);
        UserEmail.setText("User Email : " + client.cleintEmail);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        clientProjectsList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        UserNameSurname = new javax.swing.JLabel();
        UserEmail = new javax.swing.JLabel();
        ceartProjectBtn = new javax.swing.JButton();
        joinProjectButton = new javax.swing.JButton();
        getProjectkeyButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Project Managment");
        setLocation(new java.awt.Point(400, 400));

        clientProjectsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clientProjectsListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(clientProjectsList);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("My Projects");

        UserNameSurname.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        UserNameSurname.setText("jLabel2");

        UserEmail.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        UserEmail.setText("jLabel2");

        ceartProjectBtn.setText("Create Project");
        ceartProjectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ceartProjectBtnActionPerformed(evt);
            }
        });

        joinProjectButton.setText("Join Project");
        joinProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinProjectButtonActionPerformed(evt);
            }
        });

        getProjectkeyButton.setText("Project Key");
        getProjectkeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getProjectkeyButtonActionPerformed(evt);
            }
        });

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(UserNameSurname, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                            .addComponent(UserEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(exitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(ceartProjectBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(joinProjectButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(getProjectkeyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(UserNameSurname)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(UserEmail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(getProjectkeyButton)
                        .addGap(36, 36, 36)
                        .addComponent(joinProjectButton)
                        .addGap(29, 29, 29)
                        .addComponent(ceartProjectBtn))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(exitButton)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ceartProjectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ceartProjectBtnActionPerformed
        // TODO add your handling code here:
        // we will send the request of the client starts with the needed operation
        String pName = JOptionPane.showInputDialog(this, "Enter project Name?");
        data = "";
        data += "3";
        // we will send the name of the project and the manager of theproject 
        data += ",";
        data += client.cleintEmail;
        data += ",";
        data += pName;
        client.sendDataToServer(data);
    }//GEN-LAST:event_ceartProjectBtnActionPerformed

    private void clientProjectsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clientProjectsListMouseClicked
        // TODO add your handling code here:
        projectName = clientProjectsList.getSelectedValue();
        ProjectFrm pFrame = new ProjectFrm();
        pFrame.setVisible(true);

    }//GEN-LAST:event_clientProjectsListMouseClicked

    private void joinProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinProjectButtonActionPerformed
        // TODO add your handling code here:
        String projectName = JOptionPane.showInputDialog(this, "Enter the name of the project!");
        String projectKey = JOptionPane.showInputDialog(this, "Enter the key of the project!");
        // we will send the request of the client starts with the needed operation
        data = "";
        data += "4";
        data += ",";
        data += projectName;
        data += ",";
        data += projectKey;
        data += ",";
        data += client.cleintEmail;
        client.sendDataToServer(data);

//        String[] serverReponses = client.serverResponse.split(",");

    }//GEN-LAST:event_joinProjectButtonActionPerformed

    private void getProjectkeyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getProjectkeyButtonActionPerformed
        // TODO add your handling code here:
        data = "";
        data += "8";
        data += ",";
        data += client.cleintEmail;
        client.sendDataToServer(data);

    }//GEN-LAST:event_getProjectkeyButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        // TODO add your handling code here:
        // if we want to exit we will send exit word to the server
        data = "";
        data += "exit,";
        data += client.cleintEmail;
        data += ",";
        client.sendDataToServer(data);


    }//GEN-LAST:event_exitButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel UserEmail;
    private javax.swing.JLabel UserNameSurname;
    private javax.swing.JButton ceartProjectBtn;
    private javax.swing.JList<String> clientProjectsList;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton getProjectkeyButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton joinProjectButton;
    // End of variables declaration//GEN-END:variables
}
