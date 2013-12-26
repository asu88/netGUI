/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fran
 */
public class PermanentChanges {

    private static String interfacesDefault = "#MODIFIED FILE\n\n"
            + "auto lo\n"
            + "iface lo inet loopback\n\n";
    
    private static String setInterface(String interf, String IP, String netmask){
        String newInterface = "#" + interf + " START\n"
                + "auto " + interf + "\n"
                + "iface " + interf + " inet static\n"
                + "address " + IP + "\n"
                + "network " + getNet(IP) + "\n"
                + "netmask " + netmask + "\n"
                + "broadcast "+ getBroadcast(IP) + "\n"
                + "#" + interf + " END\n";
        
        return newInterface;
    }
    
    public static void addInterface(Telnet telnet, String interf, String IP, String netmask){
        String newInterface = setInterface(interf, IP, netmask);
        if (alreadyConfigured(telnet, interf)){
            String startTag = "#" + interf + " START";
            String endTag = "#" + interf + " END";
            telnet.sendCommand("sed -i.bak '/^" + startTag + "/,/^" + endTag + "/d' /etc/network/interfaces");
        }
        telnet.sendCommand("echo -e '" + newInterface + "' >> /etc/network/interfaces");
    }
    
//    public static void initializeInterfaces(Telnet telnet){
//        String vmAnswer = telnet.sendCommand("cat /etc/network/interfaces | grep 'MODIFIED'");
//        if (!vmAnswer.contains(" FILE")){
//            System.out.println("Recibido = " + vmAnswer + " -- Reseteando preferencias de red");
//            telnet.sendCommand("echo -e '" + interfacesDefault + "' > /etc/network/interfaces");
//        }
//    }
    
    public static boolean alreadyConfigured (Telnet telnet, String interf){
        String startTag = "#" + interf;
        String vmAnswer = telnet.sendCommand("cat /etc/network/interfaces | grep '" + startTag + "'");
        if ((vmAnswer.contains("START")) && (vmAnswer.contains("END"))){
            return true;
        } else {
            return false;
        }
    }
    
    private static String getNet(String IP){
        String prefix = IP.substring(0, IP.lastIndexOf(".")+1);
        String netSufix = "0";
        
        return prefix.concat(netSufix);
    }
    
    private static String getBroadcast(String IP){
        String prefix = IP.substring(0, IP.lastIndexOf(".")+1);
        String broadcastSufix = "255";
        
        return prefix.concat(broadcastSufix);
    }
    
}
