
import edu.umd.cs.piccolo.PLayer;
import java.util.Iterator;
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
public class ScanRoutersDemon extends Thread{
    
    private PLayer nodeLayer;
    private String[] protocols = {"ripd", "ospfd", "bgpd"};
    
    public ScanRoutersDemon(PLayer nodeLayer){
        this.nodeLayer = nodeLayer;
    }
    
    @Override
    public void run(){
        Object obj;
        while(true){
            Iterator i = nodeLayer.getAllNodes().iterator();
            while(i.hasNext()){
                obj = i.next();
                if (obj instanceof NKRouter){
                    readPS((NKSystem)obj);
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ScanRoutersDemon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    // Metodo readPS mediante nueva clase Telnet

    private void readPS(NKSystem node) {
//        System.out.println("Entrando a ReadPS");
        if (node.isStarted()){
//            System.out.println("El nodo está arrancado");
            Telnet ts = NodeTelnetCommunicator2.getTelnetSocket(node);
            if (ts != null) {
//                System.out.println("Mandando comando");
                String vmAnswer = ts.sendCommand("ps -efww");
                //System.out.println("Comando enviado, esperando respuesta...");
                //String vmAnswer = ts.read();
//                System.out.println("Respuesta recibida: " + vmAnswer);
                if (vmAnswer != null) {
                    lookFor(vmAnswer, node);
                }
                
            }
        }
    }
    
    // Metodo readPS mediante cliente/servidor TCP
    
////    private void readPS(NKSystem node){
////        if (node.isStarted()){
////            //System.out.println("Tratando de hacer TCPRequest");
////            TCPRequest req = new TCPRequest("20.0.0.2", "ps -efww");
////            req.setTCPRequestCmd();
////            req.send();
////            String reqAnswer = req.getAnswer();
////            //System.out.println("RECIBIMOS: " + reqAnswer);
////            if (reqAnswer != null){
////                lookFor(reqAnswer, node);
////            }
////        }
////    }

    private void lookFor(String vmAnswer, NKSystem node) {
////        for(int i=0 ; i<protocols.length; i++){
////            if (vmAnswer.contains(protocols[i])){
////                ((NKRouter)node).SetDemonName(protocols[i].toUpperCase());
////            }
////        }
        if (vmAnswer.contains("ospfd")){
//	    System.out.println("OSPFD found on router");
            ((NKRouter)node).enableOSPF();
//            ((NKRouter)node).SetDemonName("OSPFD");
            //((NKRouter)node).updateOSPFDImages();
        } else if (vmAnswer.contains("ripd")){
//	    System.out.println("RIPD found on router");
            ((NKRouter)node).enableRIP();
//            ((NKRouter)node).SetDemonName("RIPD");
            //((NKRouter)node).updateRIPDImages();
        }else if (vmAnswer.contains("bgpd")){
//            ((NKRouter)node).SetDemonName("BGPD");
            ((NKRouter)node).enableBGP();
        } else {
            ((NKRouter)node).disableAll();
//	    System.out.println("No daemon found on router");
//            ((NKRouter)node).SetDemonName("");
            //((NKRouter)node).updateStartedImages();
        }
    }   
}
