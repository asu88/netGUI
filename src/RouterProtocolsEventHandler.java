
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Fran
 */
public class RouterProtocolsEventHandler extends Thread implements ActionListener {

    private NKNode node;
    private Telnet telnet;
    private String iden;
    private JCheckBoxMenuItem item;

    public RouterProtocolsEventHandler(NKNode node) {
        this.node = node;
    }
    
    @Override
    public void run(){
        telnet = NodeTelnetCommunicator2.getTelnetSocket((NKSystem) node);

        if (iden.equals("Zebra")) {
            telnet.sendCommand("/etc/init.d/zebra start");
        } else if (iden.contains("BGP")) {
            telnet.sendCommand("sed -i.bak '22c\\bgpd=yes' /etc/zebra/daemons");
            telnet.sendCommand("sed -i.bak '23c\\ospfd=no' /etc/zebra/daemons");
            telnet.sendCommand("sed -i.bak '25c\\ripd=no' /etc/zebra/daemons");
            telnet.sendCommand("/etc/init.d/zebra restart");
        } else if (iden.contains("OSPF")) {
            telnet.sendCommand("sed -i.bak '22c\\bgpd=no' /etc/zebra/daemons");
            telnet.sendCommand("sed -i.bak '23c\\ospfd=yes' /etc/zebra/daemons");
            telnet.sendCommand("sed -i.bak '25c\\ripd=no' /etc/zebra/daemons");
            telnet.sendCommand("/etc/init.d/zebra restart");
        } else if (iden.contains("RIP")) {
            telnet.sendCommand("sed -i.bak '22c\\bgpd=no' /etc/zebra/daemons");
            telnet.sendCommand("sed -i.bak '23c\\ospfd=no' /etc/zebra/daemons");
            telnet.sendCommand("sed -i.bak '25c\\ripd=yes' /etc/zebra/daemons");
            telnet.sendCommand("/etc/init.d/zebra restart");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        iden = e.getActionCommand();
        item = (JCheckBoxMenuItem)e.getSource();
        item.setState(!item.getState());
        start();
    }
}
