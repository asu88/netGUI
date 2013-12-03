
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fran
 */
public class DeleteRouteEventHandler implements ActionListener{
    
    NKNode node;
    
    public DeleteRouteEventHandler(NKNode node){
        this.node = node;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ModifierWindow mod = new ModifierWindow(node);
        mod.setDelDialog(e.getActionCommand());
    }
    
    class ModifierWindow{
        
        String nodeName;
        
        public ModifierWindow(NKNode node){
            this.nodeName = node.getName();
        }
        
        public void setDelDialog(String type){
            
            JTextField IP = new JTextField(10);
            JTextField net = new JTextField(10);
            JTextField netmask = new JTextField(10);
            
            String routeTo;

            JPanel myPanel = new JPanel();
            
            if (type.indexOf("host") != -1){
                myPanel.add(new JLabel("IP "));
                myPanel.add(IP);
                routeTo = "host";
                int result = JOptionPane.showConfirmDialog(null, 
                        myPanel, 
                        nodeName + " : Delete " + routeTo + " route" , 
                        JOptionPane.OK_CANCEL_OPTION, 
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String ip = IP.getText();
                    Telnet telnet = NodeTelnetCommunicator2.getTelnetSocket((NKSystem)node);
                    String delAnswer = telnet.sendCommand("route del -host " + ip);
                    if (delAnswer.contains("No such process")){
                        JOptionPane.showMessageDialog(null, 
                                "It seems like we had a problem", 
                                "Error deleting route", 
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        NetKitViewer.log.addText("> " + nodeName + ": " + "deleted " + routeTo + " route to " + ip);
                    }
                }
            } else if (type.indexOf("net") != -1){
                myPanel.add(new JLabel("NET "));
                myPanel.add(net);
                myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                myPanel.add(new JLabel("Netmask"));
                myPanel.add(netmask);
                routeTo = "net";
                int result = JOptionPane.showConfirmDialog(null, 
                        myPanel, 
                        nodeName + " : Delete " + routeTo + " route" , 
                        JOptionPane.OK_CANCEL_OPTION, 
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String net2 = net.getText();
                    String netm = netmask.getText();
                    Telnet telnet = NodeTelnetCommunicator2.getTelnetSocket((NKSystem)node);
                    String delAnswer = telnet.sendCommand("route del -net " + net2 + " netmask " + netm);
                    if (delAnswer.contains("No such process")){
                        JOptionPane.showMessageDialog(null, 
                                "It seems like we had a problem", 
                                "Error deleting route", 
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        NetKitViewer.log.addText("> " + nodeName + ": " + "deleted " + routeTo + " route to " + net2);
                    }
                }
            } else if (type.indexOf("Default") != -1){
                myPanel.add(new JLabel("Default route deleted"));
                routeTo = "default";
                int result = JOptionPane.showConfirmDialog(null, 
                        myPanel, 
                        nodeName + " : Delete " + routeTo + " route" , 
                        JOptionPane.OK_CANCEL_OPTION, 
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    Telnet telnet = NodeTelnetCommunicator2.getTelnetSocket((NKSystem)node);
                    String delAnswer = telnet.sendCommand("route del default");
                    if (delAnswer.contains("No such process")){
                        JOptionPane.showMessageDialog(null, 
                                "It seems like we had a problem", 
                                "Error deleting route", 
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        NetKitViewer.log.addText("> " + nodeName + ": " + "deleted " + routeTo + " route");
                    }
                }
                
            }
            
            
            
        }

    }
    
}