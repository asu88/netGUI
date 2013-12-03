
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fran
 */
public class NodeOptionsEventHandler implements ActionListener{
    
    private NKNode node;
    
    public NodeOptionsEventHandler(NKNode node){
        this.node = node;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (((NKSystem)node).isStarted()){
            if (e.getActionCommand().equals("Change IP")){
                ModifierWindow mod = new ModifierWindow("Change " + node.getName() + " IP");
                mod.setIPWindow();
            } else if (e.getActionCommand().equals("Show route table")){
                ModifierWindow mod = new ModifierWindow(node.getName() + " route table");
                mod.setRouteWindow();
            }
        } else {
            JOptionPane.showMessageDialog(null, "The node must be started in order to use this feature", "ERROR", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    class ModifierWindow{
        
        final JFrame window;
        
        public ModifierWindow(String title){
            window = new JFrame(title);
            window.setLayout(new GridLayout(2, 1));
        }
        
        public void setIPWindow(){
            JPanel ethPanel = new JPanel();
            ethPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            ethPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Select eth"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            ArrayList<String> interfaces = setInterfaces();
            final JComboBox intList = new JComboBox(interfaces.toArray());
            ethPanel.add(intList);
            JPanel ipPanel = new JPanel();
            ipPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            ipPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Introduce new IP"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            final JTextField ip = new JTextField(10);
            JButton submitIp = new JButton("Submit");
            submitIp.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Telnet telnet = NodeTelnetCommunicator2.getTelnetSocket((NKSystem)node);
                    // Debemos mandar ifconfig <interfaz> <dirIP> netmask <mascara>
                    telnet.sendCommand("ifconfig " + intList.getSelectedItem() + " " + ip.getText() + " netmask 255.255.255.0");
                    telnet.sendCommand("/etc/init.d/networking restart");
                    window.dispose();
                }
            });
            ipPanel.add(ip);
            ipPanel.add(submitIp);
            //JText ip = new JText("Introduce new IP");
            window.add(ethPanel);
            window.add(ipPanel);
            window.pack();
            window.setPreferredSize(new Dimension(100,100));
            window.setLocation(200,200);
            window.setVisible(true);
            window.setResizable(false);
        }
        
        public void setRouteWindow(){
            JPanel routePanel = new JPanel();
            routePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Route table"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            Telnet telnet = NodeTelnetCommunicator2.getTelnetSocket((NKSystem)node);

            String routeResponse = parseRoutes(telnet.sendCommand("route"));
            StringTokenizer st = new StringTokenizer(routeResponse, "\n");
            routePanel.setLayout(new GridLayout(st.countTokens(),8));
            StringTokenizer st2 = new StringTokenizer(routeResponse);
            while (st2.hasMoreTokens()){
                routePanel.add(new JLabel(st2.nextToken()));
            }
            
            JOptionPane.showMessageDialog(null, routePanel, "Route table for " + node.getName(), JOptionPane.PLAIN_MESSAGE);
        }
        
        public String parseRoutes(String out){
            String parsedOut = out.substring(out.indexOf("Destination")-1);
            parsedOut = parsedOut.substring(0, parsedOut.lastIndexOf('\n'));
            
            
            
            
            return parsedOut;
        }
        
        public ArrayList<String> setInterfaces(){
            
            ArrayList<String> interfaces = new ArrayList<String>();
            
            if (node instanceof NKCompaq){
                int aux = 0;
                for (Map.Entry entry : (((NKCompaq)node).getInterfaces()).entrySet()){
                    interfaces.add(((Ethernet)entry.getValue()).getEth());
                    aux++;
                }
            } else if (node instanceof NKRouter) {
                int aux = 0;
                for (Map.Entry entry : (((NKRouter)node).getInterfaces()).entrySet()){
                    interfaces.add(((Ethernet)entry.getValue()).getEth());
                    aux++;
                }
            } else if (node instanceof NKSwitch) {
                int aux = 0;
                for (Map.Entry entry : (((NKSwitch)node).getInterfaces()).entrySet()){
                    interfaces.add(((Ethernet)entry.getValue()).getEth());
                    aux++;
                }
            }
            
            return interfaces;
        }
        
    }
    
}
