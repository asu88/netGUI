/*
 * Copyright (C) 2005, 2006 
 * Santiago Carot Nemesio
 *
 * This file is part of NetGUI.
 *
 * NetGUI is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * NetGUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with NetGUI; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *  
 */

/*

 **********************************************************************
 * Clase que representa un router dentro de la red
 **********************************************************************

 */
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import java.awt.geom.*;
import java.util.*;

public class NKRouter extends NKSystem {

    public static final long serialVersionUID = 1L;
//    private static final String fileStartedImageRIPD = System.getProperty("NETLAB_HOME") + "/images/128x128/router_ripd.png";
//    private static final String fileStartedImageOSPFD = System.getProperty("NETLAB_HOME") + "/images/128x128/router_ospfd.png";
//    private static final String fileStartedSelectedImageRIPD = System.getProperty("NETLAB_HOME") + "/images/128x128/routerSelected_ripd.png";
//    private static final String fileStartedSelectedOSPFD = System.getProperty("NETLAB_HOME") + "/images/128x128/routerSelected_ospfd.png";
    private static final String fileImage = System.getProperty("NETLAB_HOME") + "/images/new2/128x128/router2.png";
    private static final String deleteFileImage = System.getProperty("NETLAB_HOME") + "/images/new2/128x128/routerDel2.png";
    private static final String fileSelectedImage = System.getProperty("NETLAB_HOME") + "/images/new2/128x128/router_selected2.png";
    private static final String fileStartedImage = System.getProperty("NETLAB_HOME") + "/images/new2/128x128/routerStarted2.png";
    private static final String fileSelectedStartedImage = System.getProperty("NETLAB_HOME") + "/images/new2/128x128/routerStartedSelected2.png";
    private static final String eth = "eth";
    //Almacenaremos un array con todas las referencias de las conexiones que mantenga
    //abiertas este nodo.
    private ArrayList<NKConection> conections;
    //Almacenaremos las tuplas [red, interfaz] -> [String, Ethernet]
    private HashMap<String, Ethernet> interfaces;
    //especificamos un rectangulo de delimitaci�n (Para calcular intersecciones)
    private final RectangleNodeDelimiter delimiter;
    // especificamos 3 imagenes que se haran visibles o no en funcion del protocolo
    // de encaminamiento que funcione en el router
    PImage ospfd = new PImage(System.getProperty("NETLAB_HOME") + "/images/new2/marks/o.png");
    PImage bgpd = new PImage(System.getProperty("NETLAB_HOME") + "/images/new2/marks/b.png");
    PImage ripd = new PImage(System.getProperty("NETLAB_HOME") + "/images/new2/marks/r.png");
    
    private boolean runningBGP = false;
    private boolean runningOSPF = false;
    private boolean runningRIP = false;

    public NKRouter(String name, Point2D position, LayersHandler handler) {
        super(name, fileImage, fileSelectedImage, deleteFileImage,
                position, handler);
        conections = new ArrayList<NKConection>();
        interfaces = new HashMap<String, Ethernet>();

        //big icons
        //delimiter = new RectangleNodeDelimiter(79.0,-85.0,54.0,-71.0);

        //small icons
        delimiter = new RectangleNodeDelimiter(75.0, -70.0, 50.0, -50.0);

        // Fijamos el campo para el demonio
        CreateDemonField();
        InitZebraImages();
    }

    public boolean isRunningBGP() {
        return runningBGP;
    }

    public boolean isRunningOSPF() {
        return runningOSPF;
    }

    public boolean isRunningRIP() {
        return runningRIP;
    }
    
    

    /**
     * ***********************************************************
     * A�ade una conexi�n al nodo
     * ***********************************************************
     */
    @Override
    public synchronized void addEdge(NKConection edge) {
        if (!conections.contains(edge)) {
            //No exist�a la conexi�n
            conections.add(edge);
            Ethernet ethX = new Ethernet(getNewEthName());
            //formatEthText(ethX);
            handler.showInterfazEth(ethX);
            interfaces.put(getNetName(edge), ethX);
        }
    }

    /**
     * ***********************************************************
     * Elimina una conexi�n con el nodo
     * ***********************************************************
     */
    @Override
    public synchronized void removeEdge(NKConection edge) {
        if (conections.contains(edge)) {
            conections.remove(conections.indexOf(edge));
            handler.notShowInterfazEth(interfaces.get(getNetName(edge)));
            interfaces.remove(getNetName(edge));
        }
    }

    public synchronized HashMap<String, Ethernet> getInterfaces() {
        return interfaces;
    }

    /**
     * ***********************************************************
     * Devuelve todas las conexiones del nodo
     * ***********************************************************
     */
    public ArrayList getEdges() {
        return conections;
    }

    /**
     * ***********************************************************
     * Actualiza todas las conexiones del nodo para poder representarlas en
     * pantalla cuando el nodo es arrastrado
     * ***********************************************************
     */
    @Override
    public void updateEdges() {
        for (int i = 0; i < conections.size(); i++) {
            //NetKitGUI.this.updateEdge((PPath) edges.get(i));
            (conections.get(i)).updateEdge();
        }
    }

    @Override
    public void startNetKit() {
        if (!isStarted()) {
            String cmd = vstartCmdGen();
            try {
                Process proc;
                proc = Runtime.getRuntime().exec(cmd, null);
                //setStarted(true);
                updateStartedImages();
                TelnetConnector tc = new TelnetConnector(this, cmd);
                //TCPConnector tc = new TCPConnector(this, cmd);
            } catch (Exception ex) {
                System.out.println("Error " + ex);
            }
        }
    }
//////    // Metodos para variar la imagen cuando corra algun demonio en router
//////    
//////    protected void updateRIPDImages(){
//////        changeSelectedImage(fileStartedSelectedImageRIPD); // Crear imagen para seleccionado
//////        changeNormalImage(fileStartedImageRIPD);
//////    }
//////    
//////    protected void updateOSPFDImages(){
//////        changeSelectedImage(fileStartedSelectedOSPFD); // Crear imagen para seleccionado
//////        changeNormalImage(fileStartedImageOSPFD);
//////    }
    // Metodo para variar una etiqueta en lugar de la imagen completa
    private PText tNode;

    private void CreateDemonField() {
        double height = this.getHeight(), width = this.getWidth();
        tNode = new PText();
        tNode.centerFullBoundsOnPoint((width / 8), (height / 2));
        tNode.setPickable(false);
        tNode.setScale((float) 1.5);
        this.addChild(tNode);
    }

    private void InitZebraImages() {
        bgpd.centerFullBoundsOnPoint(20, 20);
        bgpd.setPickable(false);
        this.addChild(bgpd);
        disableBGP();

        ospfd.centerFullBoundsOnPoint(this.getWidth() - 20, 20);
        ospfd.setPickable(false);
        this.addChild(ospfd);
        disableOSPF();

//        ripd.centerFullBoundsOnPoint(20, this.getHeight() - 20);
        ripd.centerFullBoundsOnPoint(this.getWidth() / 2, 20);
        ripd.setPickable(false);
        this.addChild(ripd);
        disableRIP();
    }
    
    public void enableBGP(){
        bgpd.setVisible(true);
        runningBGP = true;
        disableOSPF();
        disableRIP();
    }
    
    public void enableOSPF(){
        ospfd.setVisible(true);
        runningOSPF = true;
        disableBGP();
        disableRIP();
    }
    
    public void enableRIP(){
        ripd.setVisible(true);
        runningRIP = true;
        disableBGP();
        disableOSPF();
    }
    
    private void disableBGP(){
        bgpd.setVisible(false);
        runningBGP = false;
    }
    
    private void disableOSPF(){
        ospfd.setVisible(false);
        runningOSPF = false;
    }
    
    private void disableRIP(){
        ripd.setVisible(false);
        runningRIP = false;
    }
    
    public void disableAll(){
        bgpd.setVisible(false);
        ospfd.setVisible(false);
        ripd.setVisible(false);
        
        runningBGP = false;
        runningOSPF = false;
        runningRIP = false;
    }

    protected void SetDemonName(String name) {

        tNode.setText(name);

    }

    @Override
    protected void updateStartedImages() {
        changeSelectedImage(fileSelectedStartedImage);
        changeNormalImage(fileStartedImage);
    }

    @Override
    protected void updateNormalImages() {
        changeSelectedImage(fileSelectedImage);
        changeNormalImage(fileImage);
        updateEthernets(new HashMap());
    }

    /**
     * ***************************************************************
     * Los nodos se conectan a la red �nicamente a trav�s de hubs El nodo accede
     * a ellos para obtener el nombre de red.
     * ***************************************************************
     */
    private String getNetName(NKConection edge) {
        //Si es un NKDirectConection es porque estan dos routers conectados directamente
        if (edge instanceof NKDirectConection) {
            return ((NKDirectConection) edge).getNetName();
        } else if (edge.getNode1().getName().equalsIgnoreCase(getName())) {
            return ((NKHub) edge.getNode2()).getNetName();
        } else {
            return ((NKHub) edge.getNode1()).getNetName();
        }
    }

    /**
     * ***************************************************************
     * Genera el comando vstart con los par�metros necesarios para arrancar
     * netkit ***************************************************************
     */
    private synchronized String vstartCmdGen() {
        if (conections.isEmpty()) {
            return processStartCmd("vstart -l " + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + " " + getName());
        }
        Iterator i = conections.iterator();
        String s = "vstart -l " + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + " " + getName();
        String key;
        while (i.hasNext()) {
            key = getNetName((NKConection) i.next());
            s += " --" + (interfaces.get(key)).getEthName().getText()
                    + "=" + key;
        }
        return processStartCmd(s);
    }

    private String getNewEthName() {
        boolean exit = false;
        String ethId;
        int count = 0;
        do {
            ethId = eth + count;
            count++;
        } while (ethIsUsed(ethId));
        return ethId;
    }

    private synchronized boolean ethIsUsed(String ethId) {
        Iterator i = interfaces.values().iterator();
        boolean used = false;
        Ethernet eAux;
        while (i.hasNext() && !used) {
            eAux = (Ethernet) i.next();
            used = (ethId.equalsIgnoreCase(eAux.getEthName().getText()));
        }
        return used;
    }

    @Override
    protected synchronized void updateEthLocation(NKConection edge) {
        if (conections.contains(edge)) {
            Point2D p = NetKitGeom.getGlobalIntersect(delimiter, this, edge);
            Ethernet ethNode = interfaces.get(getNetName(edge));
            ethNode.centerFullBoundsOnPoint(p.getX(), p.getY());
        }
    }

    @Override
    public synchronized void updateEthernets(HashMap eth_ip) {
        Iterator i = interfaces.keySet().iterator();
        String net, ethX;
        while (i.hasNext()) {
            net = (String) i.next();
            ethX = (interfaces.get(net)).getEthName().getText();
            if (eth_ip.containsKey(ethX)) //actualizamos la ip de la interfaz
            {
                (interfaces.get(net)).setIp((String) eth_ip.get(ethX));
            } else //si no est� significa que la interfaz no tiene ip asignada
            {
                (interfaces.get(net)).setIp(null);
            }
        }
    }

    @Override
    protected void ShowDisplayName(String name) {
        double height = this.getHeight(), width = this.getWidth();
        PText nodeName = new PText(name.toUpperCase());
        nodeName.centerFullBoundsOnPoint((width / 2.5), (height / 1.75));
        nodeName.setPickable(false);
        nodeName.setScale((float) 1.5);
        this.addChild(nodeName);
    }
}
