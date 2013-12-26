
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fran
 */
public class DisableRouterProtocolEventHandler extends Thread implements ActionListener{
    
    private NKNode node;
    private int STATE;
    private static final int BGP = 0, OSPF = 1, RIP = 2;
    
    public DisableRouterProtocolEventHandler(String protocol, NKNode node) {
        this.node = node;

        if (protocol.equals("BGP")) {
            STATE = BGP;
        } else if (protocol.equals("OSPF")) {
            STATE = OSPF;
        } else if (protocol.equals("RIP")) {
            STATE = RIP;
        }
    }
    
    @Override
    public void run() {
        Telnet telnet = NodeTelnetCommunicator2.getTelnetSocket((NKSystem) node);
        switch (STATE) {
            case BGP:
                telnet.sendCommand("sed -i.bak '22c\\bgpd=no' /etc/zebra/daemons");
                telnet.sendCommand("/etc/init.d/zebra stop");
                break;
            case OSPF:
                telnet.sendCommand("sed -i.bak '23c\\ospfd=no' /etc/zebra/daemons");
                telnet.sendCommand("/etc/init.d/zebra stop");
                break;
            case RIP:
                telnet.sendCommand("sed -i.bak '25c\\ripd=no' /etc/zebra/daemons");
                telnet.sendCommand("/etc/init.d/zebra stop");
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        start();
    }
    
}
