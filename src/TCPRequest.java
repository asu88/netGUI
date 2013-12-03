
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fran
 */
public class TCPRequest {
    
    private String answer;
    private String[] request;
    private String IP;
    private String command;
    
    public TCPRequest(String IP, String command){
        this.IP = IP;
        this.command = command;
    }
    
    public void setTCPRequestCmd(){
        String request0 = "python";
        String request1 = System.getProperty("NETLAB_HOME") + "bin/TCPClient.py";
        String request2 = IP;
        String request3 = command;
        request = new String[4];
        request[0] = request0;
        request[1] = request1;
        request[2] = request2;
        request[3] = request3;
    }
    
    public synchronized void send(){
        try {
//            for (int i=0; i<request.length; i++){
//                System.out.print(request[i] + " ");
//            }
//            System.out.println();
            //System.out.println("LANZAMOS " + request);
            answer = "";
            Process process = Runtime.getRuntime().exec(request, null);
            process.waitFor();
            InputStream inputStream = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            
            String aux = br.readLine();
            while (aux != null){
                answer += aux + "\n";
                aux = br.readLine();
            }
            
        } catch (IOException ex) {
            Logger.getLogger(TCPRequest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex){
            Logger.getLogger(TCPRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getAnswer() {
        return answer;
    }
    
}
