
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fran
 */
public class TCPConnector {
    
    public TCPConnector(NKSystem node, String startCMD){
        int port = getStartedPort(startCMD);
        if (port != 0){
            TCPConnectorThread conn = new TCPConnectorThread(node, port);
            conn.start();
        }
        //node.setStarted(true);
    }
    
    private int getStartedPort(String startCMD) {
        String patron1 = "--con1=port:", patron2 = "--new", port;
        int p = 0;
        if (startCMD.indexOf(patron2) == -1) {
            port = startCMD.substring(startCMD.indexOf(patron1) + patron1.length(),
                    startCMD.length());
        } else {
            port = startCMD.substring(startCMD.indexOf(patron1) + patron1.length(),
                    startCMD.indexOf(patron2) - 1);
        }

        try {
            p = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            System.out.println("Port parse error " + e);
        }

        return p;
    }
    
public class TCPConnectorThread extends Thread{
    private NKSystem node;
    private int port;
    
    public TCPConnectorThread(NKSystem node, int port){
        this.node = node;
        this.port = port;
    }
    
    @Override
    public void run(){
        try {
            boolean connected = false;
            int attempt = 0;
            final int maxAttempt = 100;
            
            Thread.sleep(3000);
            while((!connected) && (attempt <= maxAttempt)){
                connected = openTCPConnection();
                Thread.sleep(2000);
                attempt++;
            }
            
            if (connected){
                JOptionPane.showMessageDialog(null, node.getName() + " connected to GUI", "Conexión exitosa", JOptionPane.INFORMATION_MESSAGE);
                node.setStarted(connected);
            } else {
                JOptionPane.showMessageDialog(null, "ERROR! " + node.getName() + " cannot be connected to GUI", "Error en conexión", JOptionPane.ERROR_MESSAGE);
                node.setStarted(connected);
            }
            
        } catch (InterruptedException ex) {
            Logger.getLogger(TCPConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean openTCPConnection(){
        TCPRequest req = new TCPRequest("20.0.0.2", "echo hola");
        req.setTCPRequestCmd();
        req.send();
        String reqAnswer = req.getAnswer();
        System.out.println("Respuesta en openTCPConnection: /" + reqAnswer + "/");
        
        try{
            if (reqAnswer.equals("hola" + "\n")){
                return true;
            } else {
                return false;
            }
        }catch (NullPointerException ex){
            return false;
        }
    }
    
}

}