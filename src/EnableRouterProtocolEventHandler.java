
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
public class EnableRouterProtocolEventHandler extends Thread implements ActionListener {

    private NKNode node;
    private int STATE;
    private static final int BGP = 0, OSPF = 1, RIP = 2;

    public EnableRouterProtocolEventHandler(String protocol, NKNode node) {
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
                telnet.sendCommand("sed -i.bak '22c\\bgpd=yes' /etc/zebra/daemons");
                telnet.sendCommand("sed -i.bak '23c\\ospfd=no' /etc/zebra/daemons");
                telnet.sendCommand("sed -i.bak '25c\\ripd=no' /etc/zebra/daemons");
                telnet.sendCommand("/etc/init.d/zebra restart");
                break;
            case OSPF:
                telnet.sendCommand("sed -i.bak '22c\\bgpd=no' /etc/zebra/daemons");
                telnet.sendCommand("sed -i.bak '23c\\ospfd=yes' /etc/zebra/daemons");
                telnet.sendCommand("sed -i.bak '25c\\ripd=no' /etc/zebra/daemons");
                telnet.sendCommand("/etc/init.d/zebra restart");
                break;
            case RIP:
                telnet.sendCommand("sed -i.bak '22c\\bgpd=no' /etc/zebra/daemons");
                telnet.sendCommand("sed -i.bak '23c\\ospfd=no' /etc/zebra/daemons");
                telnet.sendCommand("sed -i.bak '25c\\ripd=yes' /etc/zebra/daemons");
                telnet.sendCommand("/etc/init.d/zebra restart");
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        start();
    }
}
