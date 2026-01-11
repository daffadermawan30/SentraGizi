package com.sentragizi.modules.inspector.ui;

import com.sentragizi.modules.inspector.ui.wizard.PanelQueue;
import com.sentragizi.modules.inspector.ui.wizard.PanelStage1;
import java.awt.CardLayout;
import javax.swing.JPanel;

public class InspectorMainFrame extends javax.swing.JFrame {

    public CardLayout cards;
    public JPanel container; 

    public InspectorMainFrame() {
        initComponents();
        
        this.setTitle("Inspector Dashboard - SentraGizi");
        this.setSize(637, 460); 
        this.setLocationRelativeTo(null); 
        
        container = new JPanel(new CardLayout());
        this.setContentPane(container); 
        
        cards = (CardLayout) container.getLayout();
        
        
        
        container.add(new PanelQueue(this), "QUEUE");   
        container.add(new PanelStage1(), "STAGE1"); 
        
        cards.show(container, "QUEUE"); 
    
    }
    
    
    public void showPage(String pageName) {
        cards.show(container, pageName);
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.CardLayout());

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
