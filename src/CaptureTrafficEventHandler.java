
import edu.umd.cs.piccolo.PLayer;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Fran
 */
class CaptureTrafficEventHandler implements ActionListener {

    private NetKitGUI gui;
    private HashMap<NKNode, ArrayList<String>> activatedInterfaces = new HashMap<NKNode, ArrayList<String>>();
    private HashMap<NKSystem, ArrayList<Listener>> runningProcess = new HashMap<NKSystem, ArrayList<Listener>>();
    private Clock c;
    private PLayer nodeLayer;
    private static final int CHECK = 0;
    private static final int START = 1;
    private static final int FINISH = 2;
    private int STATUS = CHECK;

    public CaptureTrafficEventHandler(NetKitGUI gui) {
        this.gui = gui;
        this.nodeLayer = gui.getNodeLayer();
    }

    private void getActivatedInterfaces() {
        Iterator it = nodeLayer.getAllNodes().iterator();
        while (it.hasNext()) {
            Object node = it.next();
            if (node instanceof NKCompaq) {
                ArrayList<String> ethList = createEthList((NKNode) node, ((NKCompaq) node).getInterfaces().entrySet());
                if (!ethList.isEmpty()) {
                    activatedInterfaces.put((NKNode) node, ethList);
                }
            } else if (node instanceof NKRouter) {
                ArrayList<String> ethList = createEthList((NKNode) node, ((NKRouter) node).getInterfaces().entrySet());
                if (!ethList.isEmpty()) {
                    activatedInterfaces.put((NKNode) node, ethList);
                }
            } else if (node instanceof NKSwitch) {
                ArrayList<String> ethList = createEthList((NKNode) node, ((NKSwitch) node).getInterfaces().entrySet());
                if (!ethList.isEmpty()) {
                    activatedInterfaces.put((NKNode) node, ethList);
                }
            }
        }
    }

    private void showCheck() {
        Iterator it = nodeLayer.getAllNodes().iterator();
        while (it.hasNext()) {
            Object node = it.next();
            if (node instanceof NKCompaq) {
                for (Map.Entry entry : ((NKCompaq) node).getInterfaces().entrySet()) {
                    if ((((NKSystem) node).isStarted()) && !((Ethernet) (entry.getValue())).getIP().equals("")) {
                        ((Ethernet) (entry.getValue())).showCheck();
                    }
                }
            } else if (node instanceof NKRouter) {
                for (Map.Entry entry : ((NKRouter) node).getInterfaces().entrySet()) {
                    if ((((NKSystem) node).isStarted()) && !((Ethernet) (entry.getValue())).getIP().equals("")) {
                        ((Ethernet) (entry.getValue())).showCheck();
                    }
                }
            } else if (node instanceof NKSwitch) {
                for (Map.Entry entry : ((NKSwitch) node).getInterfaces().entrySet()) {
                    if ((((NKSystem) node).isStarted()) && !((Ethernet) (entry.getValue())).getIP().equals("")) {
                        ((Ethernet) (entry.getValue())).showCheck();
                    }
                }
            }
        }
    }

    private void hideCheck() {
        Iterator it = nodeLayer.getAllNodes().iterator();
        while (it.hasNext()) {
            Object node = it.next();
            if (node instanceof NKCompaq) {
                for (Map.Entry entry : ((NKCompaq) node).getInterfaces().entrySet()) {
                    if (((NKSystem) node).isStarted()) {
                        ((Ethernet) (entry.getValue())).hideCheck();
                    }
                }
            } else if (node instanceof NKRouter) {
                for (Map.Entry entry : ((NKRouter) node).getInterfaces().entrySet()) {
                    if (((NKSystem) node).isStarted()) {
                        ((Ethernet) (entry.getValue())).hideCheck();
                    }
                }
            } else if (node instanceof NKSwitch) {
                for (Map.Entry entry : ((NKSwitch) node).getInterfaces().entrySet()) {
                    if (((NKSystem) node).isStarted()) {
                        ((Ethernet) (entry.getValue())).hideCheck();
                    }
                }
            }
        }
    }

    private void setIcon(ActionEvent e, int status) {
        switch (status) {
            case CHECK:
                ((JToggleButton) e.getSource()).setIcon(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/48x48/capture.png"));
                ((JToggleButton) e.getSource()).setToolTipText("stop capture");
                ((JToggleButton) e.getSource()).setSelected(false);
                break;
            case START:
                ((JToggleButton) e.getSource()).setIcon(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/48x48/record.png"));
                ((JToggleButton) e.getSource()).setToolTipText("stop capture");
                ((JToggleButton) e.getSource()).setSelected(false);
                break;
            case FINISH:
                ((JToggleButton) e.getSource()).setIcon(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/48x48/stopRecord.png"));
                ((JToggleButton) e.getSource()).setToolTipText("stop capture");
                ((JToggleButton) e.getSource()).setSelected(false);
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (STATUS) {
            case CHECK:
                STATUS = START;
                showCheck();
                setIcon(e, START);
                break;
            case START:
                getActivatedInterfaces();

                // Insertar una ventana de confirmacion-----------------------------
                String message = "You are attempting to do a capture on the next interfaces: \n";
                for (Map.Entry entry : activatedInterfaces.entrySet()) {
                    String name = ((NKNode) entry.getKey()).getName();
                    message += name + " -> " + entry.getValue() + '\n';
                }
                message += "\nBE SURE ALL THE INTERFACES YOU HAVE SELECTED ARE CORRECTLY CONFIGURED\n\n";
                message += "Do you want to continue?";
                int select = JOptionPane.showConfirmDialog(null,
                        message,
                        "WARNING",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                //------------------------------------------------------------------

                // Recuperamos la respuesta de la ventana de configuracion----------
                if (JOptionPane.YES_OPTION == select) {
                    STATUS = FINISH;
                    System.out.println("Haciendo captura");
                    setIcon(e, FINISH);

                    // Arrancamos el reloj
                    c = new Clock(gui);
                    c.start();

                    Calendar date = Calendar.getInstance();
                    String folder = date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE) + ":" + date.get(Calendar.SECOND);
                    NetKitViewer.log.addText("> New capture : " + folder);
                    // Enviar comando tcpdump -i <INTERFAZ> -s 0 -w /hosthome/cap-<PC>.cap &
                    for (Map.Entry entry : activatedInterfaces.entrySet()) {
                        runAllProcess(folder, entry);
                    }

                }


                break;
            case FINISH:
                STATUS = CHECK;
                c.setStop(true);
                killAllProcess(runningProcess);
                setIcon(e, CHECK);
                hideCheck();

                break;
        }




//        if (!started) {
//            setStarted(true);
//            getActivatedInterfaces();
//
//            // Insertar una ventana de confirmacion-----------------------------
//            String message = "You are attempting to do a capture on the next interfaces: \n";
//            for (Map.Entry entry : activatedInterfaces.entrySet()) {
//                String name = ((NKNode) entry.getKey()).getName();
//                message += name + " -> " + entry.getValue() + '\n';
//            }
//            message += "\nBE SURE ALL THE INTERFACES YOU HAVE SELECTED ARE CORRECTLY CONFIGURED\n\n";
//            message += "Do you want to continue?";
//            int select = JOptionPane.showConfirmDialog(null,
//                    message,
//                    "WARNING",
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.QUESTION_MESSAGE);
//            //------------------------------------------------------------------
//
//            // Recuperamos la respuesta de la ventana de configuracion----------
//            if (JOptionPane.YES_OPTION == select) {
//                System.out.println("Haciendo captura");
//                ((JToggleButton) e.getSource()).setIcon(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/48x48/stopRecord.png"));
//                ((JToggleButton) e.getSource()).setToolTipText("stop capture");
//                ((JToggleButton) e.getSource()).setSelected(false);
//
//                // Arrancamos el reloj
//                c = new Clock(gui);
//                c.start();
//                // Enviar comando tcpdump -i <INTERFAZ> -s 0 -w /hosthome/cap-<PC>.cap &
//                for (Map.Entry entry : activatedInterfaces.entrySet()) {
//                    runAllProcess(entry);
//                }
//
//            } else {
//                ((JToggleButton) e.getSource()).setIcon(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/48x48/record.png"));
//                ((JToggleButton) e.getSource()).setToolTipText("start capture");
//                ((JToggleButton) e.getSource()).setSelected(false);
//                started = false;
//            }
//        } else {
//            setStarted(false);
//            c.setStop(true);
//            killAllProcess(runningProcess);
//            ((JToggleButton) e.getSource()).setIcon(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/48x48/record.png"));
//            ((JToggleButton) e.getSource()).setToolTipText("start capture");
//            ((JToggleButton) e.getSource()).setSelected(false);
//        }

    }

    private void runAllProcess(String folder, Entry entry) {
        Telnet telnet = NodeTelnetCommunicator2.getTelnetSocket((NKSystem) entry.getKey());
        // Creamos una nueva carpeta donde guardaremos los archivos para que no
        // se pisen con otros en el caso de que no recordemos guardarlos bien
        if (!(new File("/hosthome/NetGuiCaptures").exists())) {
            telnet.sendCommand("mkdir " + "/hosthome/NetGuiCaptures");
        }
        String dir = "/hosthome/NetGuiCaptures/" + folder + "/";
        telnet.sendCommand("mkdir " + dir);
        String name = ((NKNode) entry.getKey()).getName();
        ArrayList<Listener> attached = new ArrayList<Listener>();
        for (int i = 0; i < ((ArrayList) entry.getValue()).size(); i++) {
            String eth = (String) ((ArrayList) entry.getValue()).get(i);
            String cmd = "tcpdump -i " + eth + " -s 0 -w " + dir + "cap-" + name + ":" + eth + ".cap &";
            NetKitViewer.log.addText("   + Capturing " + eth + " in " + name);
            String vmAnswer = telnet.sendCommand(cmd);
            String pid = parseVMAnswer(vmAnswer);
//            String pid = vmAnswer.substring(vmAnswer.indexOf("] ") + 2, vmAnswer.lastIndexOf('\n'));
//            System.out.println("PID -- " + pid);
            attached.add(new Listener(eth, pid));
        }
        runningProcess.put((NKSystem) entry.getKey(), attached);
    }

    private String parseVMAnswer(String vmAnswer) {
        String pid = vmAnswer.substring(vmAnswer.indexOf("] ") + 2, vmAnswer.lastIndexOf('\n'));
        System.out.println("PID -- " + pid);

        return pid;
    }

    private void killAllProcess(HashMap<NKSystem, ArrayList<Listener>> runningProcess) {
        for (Map.Entry entry : runningProcess.entrySet()) {
            Telnet telnet = NodeTelnetCommunicator2.getTelnetSocket((NKSystem) entry.getKey());
            for (int i = 0; i < ((ArrayList) entry.getValue()).size(); i++) {
                Listener l = (Listener) ((ArrayList) entry.getValue()).get(i);
                String pid = l.getPid();
                telnet.sendCommand("kill -TERM " + pid);
            }
        }
        runningProcess.clear();
        JOptionPane.showMessageDialog(null,
                "All the captures finish correctly \n"
                + "in " + c.getMinute() + ":" + c.getSecond(),
                null,
                JOptionPane.INFORMATION_MESSAGE);
        NetKitViewer.log.addText("   + Captures finished");
    }

    private ArrayList<String> createEthList(NKNode nkNode, Set<Entry<String, Ethernet>> entrySet) {
        ArrayList<String> ethList = new ArrayList<String>();

        for (Map.Entry entry : entrySet) {
            if (((Ethernet) entry.getValue()).isChecked()) {
                if (((NKSystem) nkNode).isStarted()) {
                    String eth = ((Ethernet) entry.getValue()).getEth();
                    System.out.println(nkNode.getName() + ":" + eth + " is activated");
                    ethList.add(eth);
                } else {
                    JOptionPane.showMessageDialog(null,
                            nkNode.getName() + " is not started. You cannot make capture on his interfaces",
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }


        return ethList;
    }

    class Clock extends Thread {

        private int minute = 0;
        private int second = 0;
        private boolean stop = false;
        private NetKitGUI gui;

        public Clock(NetKitGUI gui) {
            this.gui = gui;
        }

        @Override
        public void run() {
//            JFrame frame = new JFrame();
//            Container cont = frame.getContentPane();
//            cont.setLayout(new FlowLayout());
            JPanel cont;
            cont = new JPanel() {
                public static final long serialVersionUID = 1L;
                ImageIcon imagen = new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/other/captureBox.png");

                @Override
                public void paint(Graphics g) {
                    g.drawImage(imagen.getImage(), 0, 0, getWidth(), getHeight(), this);

                    setOpaque(false);
                    super.paint(g);
                }
            };
            JPanel line = new JPanel() {
                public static final long serialVersionUID = 1L;
                ImageIcon imagen = new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/other/captureLine.png");

                @Override
                public void paint(Graphics g) {
                    g.drawImage(imagen.getImage(), 0, 0, getWidth(), getHeight(), this);

                    setOpaque(false);
                    super.paint(g);
                }
            };
            cont.setLayout(new FlowLayout(FlowLayout.CENTER));
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setString("Capturing...");
            JLabel cam = new JLabel(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/48x48/camera.png"));
            JLabel time = new JLabel();
//            time.setForeground(Color.ORANGE);
            time.setText(represent(minute) + " : " + represent(second));
            cont.add(cam);
            cont.add(time);
            cont.add(bar);
//            cont.setBackground(Color.BLACK);
            JPanel captureTag = new JPanel(new BorderLayout());
            captureTag.add(line, BorderLayout.EAST);
            captureTag.add(cont, BorderLayout.CENTER);
            captureTag.setBounds(425, 0, 200, 100);
            gui.add(captureTag);
            gui.repaint();
            while (!stop) {
                try {
                    sleep(1000);
                    calcule();
                    time.setText(represent(minute) + " : " + represent(second));
                    setBar(bar);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CaptureTrafficEventHandler.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            captureTag.setVisible(false);
            gui.repaint();
        }

        private void setBar(JProgressBar bar) {
            int value = bar.getValue();
            value = value + 10;
            if (value > 100) {
                value = 0;
            }
            bar.setValue(value);
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }

        public synchronized String getMinute() {
            return represent(minute);
        }

        public synchronized String getSecond() {
            return represent(second);
        }

        public void calcule() {
            second++;
            if (second > 59) {
                second = 0;
                minute++;
            }
        }

        public String represent(int value) {
            if (value < 10) {
                return "0" + value;
            } else {
                return Integer.toString(value);
            }
        }
    }
}
