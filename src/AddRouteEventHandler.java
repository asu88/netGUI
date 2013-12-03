
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fran
 */
public class AddRouteEventHandler implements ActionListener{
    
    NKNode node;
    
    public AddRouteEventHandler(NKNode node){
        this.node = node;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ModifierWindow mod = new ModifierWindow(node);
        mod.setAddDialog(e.getActionCommand());
    }
    
    class ModifierWindow{
        
        String nodeName;
        
        public ModifierWindow(NKNode node){
            this.nodeName = node.getName();
        }
        
        public void setAddDialog(String type){
            
            JTextField IP = new JTextField(10);
            JTextField net = new JTextField(10);
            JTextField netmask = new JTextField(10);
            JTextField GW = new JTextField(10);
            
            String routeTo = null;

            JPanel myPanel = new JPanel();
            
            if (type.indexOf("host") != -1){
                myPanel.add(new JLabel("IP "));
                myPanel.add(IP);
                myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                myPanel.add(new JLabel("Gateway"));
                myPanel.add(GW);
                routeTo = "host";
                int result = JOptionPane.showConfirmDialog(null, myPanel, nodeName + " : Add " + routeTo + " route" , JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            
                if (result == JOptionPane.OK_OPTION) {
                    String ip =  IP.getText();
                    String gw =  GW.getText();
                    Telnet telnet = NodeTelnetCommunicator2.getTelnetSocket((NKSystem)node);
                    String cmd = "route add -host " + ip + " gw " + gw;
                    String addAnswer = telnet.sendCommand(cmd);
                    if (addAnswer.contains("No such process")){
                        JOptionPane.showMessageDialog(null, 
                                "It seems like we had a problem", 
                                "Error adding route", 
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        NetKitViewer.log.addText("> " + nodeName + ": " + "added " + routeTo + " route to " + ip);
                    }
                }
            } else if (type.indexOf("net") != -1){
                myPanel.add(new JLabel("NET "));
                myPanel.add(net);
                myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                myPanel.add(new JLabel("Netmask"));
                myPanel.add(netmask);
                myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                myPanel.add(new JLabel("Gateway"));
                myPanel.add(GW);
                routeTo = "net";
                int result = JOptionPane.showConfirmDialog(null, myPanel, nodeName + " : Add " + routeTo + " route" , JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            
                if (result == JOptionPane.OK_OPTION) {
                    String net2 =  net.getText();
                    String netm = netmask.getText();
                    String gw =  GW.getText();
                    Telnet telnet = NodeTelnetCommunicator2.getTelnetSocket((NKSystem)node);
                    String cmd = "route add -net " + net2 + " netmask " + netm + " gw " + gw;
                    String addAnswer = telnet.sendCommand(cmd);
                    if (addAnswer.contains("No such process")){
                        JOptionPane.showMessageDialog(null, 
                                "It seems like we had a problem", 
                                "Error adding route", 
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        NetKitViewer.log.addText("> " + nodeName + ": " + "added " + routeTo + " route to " + net2);
                    }
                }
            } else if (type.indexOf("Default") != -1){
                myPanel.add(new JLabel("Gateway"));
                myPanel.add(GW); 
                routeTo = "default";
                int result = JOptionPane.showConfirmDialog(null, myPanel, nodeName + " : Add " + routeTo + " route" , JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            
                if (result == JOptionPane.OK_OPTION) {
                    String gw =  GW.getText();
                    Telnet telnet = NodeTelnetCommunicator2.getTelnetSocket((NKSystem)node);
                    String cmd = "route add default gw " + gw;
                    String addAnswer = telnet.sendCommand(cmd);
                    if (addAnswer.contains("No such process")){
                        JOptionPane.showMessageDialog(null, 
                                "It seems like we had a problem", 
                                "Error adding route", 
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        NetKitViewer.log.addText("> " + nodeName + ": " + "added " + routeTo + " route");
                    }
                }
            }
            
            
            
        }

    }
    
}
