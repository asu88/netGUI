
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.telnet.TelnetClient;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fran
 */
public class Telnet extends Thread{
    
    private InputStream in;
    private PrintStream out;
    private String prompt = "#" + " ";
    private TelnetClient telnet;
    private String host;
    private int port;

    /**
     * @param args the command line arguments
     */
    
    public Telnet(String host, int port){
        this.host = host;
        this.port = port;       
    }
    
    public void initTelnet() throws SocketException, IOException{
            telnet = new TelnetClient();
//            System.out.println("Iniciando telnet en guest " + host + ":" + port);
            telnet.connect(host, port);
//            System.out.println("-- CONECTADO --");
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());
            read();
    }
    
    public String read() {
        try {
            char lastChar = prompt.charAt(prompt.length() - 1);
            StringBuilder sb = new StringBuilder();
            //boolean found = false;
            char ch = (char) in.read();
            while (true) {
                //System.out.println(ch);
                sb.append(ch);
                if (ch == lastChar) {
                    if (sb.toString().endsWith(prompt)) {
                        return sb.toString();
                    }
                }
                ch = (char) in.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(String value) {
        try {
            out.println(value);
            out.flush();
            //System.out.println(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized String sendCommand(String command) {
        try {
            write(command);
            return read();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void disconnect(){
        try {
            telnet.disconnect();
        } catch (IOException ex) {
            Logger.getLogger(Telnet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
    
}
