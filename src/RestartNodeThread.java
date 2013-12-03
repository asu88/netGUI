
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestartNodeThread extends Thread {

    String cmd;
    NKSystem device;

    public RestartNodeThread(String cmd, NKSystem device) {
        this.cmd = cmd;
        this.device = device;
    }

    @Override
    public void run() {
        try {
            // Hay que esperar para que no se mate la nueva
            // instancia de la maquina que se esta arrancando
            System.out.println("Aborting r1");
            System.out.println("Waiting 5s for restart " + device.getName() + " ...");
            Thread.sleep(5000);

            System.out.println("Restarting " + device.getName());
            device.startNetKit();
        } catch (InterruptedException ex) {
            Logger.getLogger(RestartNodeThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
