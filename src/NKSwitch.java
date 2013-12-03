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
 * Clase que representa un switch dentro de la red
 **********************************************************************

 */
import edu.umd.cs.piccolo.nodes.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;

public class NKSwitch extends NKSystem {

    public static final long serialVersionUID = 1L;
    private static final String fileImage = System.getProperty("NETLAB_HOME") + "/images/128x128/switch.png";
    private static final String deleteFileImage = System.getProperty("NETLAB_HOME") + "/images/128x128/switchDel.png";
    private static final String fileSelectedImage = System.getProperty("NETLAB_HOME") + "/images/128x128/switch_selected.png";
    private static final String fileStartedImage = System.getProperty("NETLAB_HOME") + "/images/128x128/switchStarted.png";
    private static final String fileSelectedStartedImage = System.getProperty("NETLAB_HOME") + "/images/128x128/switchStartedSelected.png";
    private static final String eth = "eth";
    //Almacenaremos un array con todas las referencias de las conexiones que mantenga
    //abiertas este nodo.
    private ArrayList<NKConection> conections;
    //Almacenaremos las tuplas [red, interfaz] -> [String, Ethernet]
    private HashMap<String, Ethernet> interfaces;
    //especificamos un rectangulo de delimitaci�n (Para calcular intersecciones)
    private final RectangleNodeDelimiter delimiter;

    public NKSwitch(String name, LayersHandler handler) {
        super(name, fileImage, fileSelectedImage, deleteFileImage, handler);
        conections = new ArrayList<NKConection>();
        interfaces = new HashMap<String, Ethernet>();
        delimiter = new RectangleNodeDelimiter(61.0, -68.0, 35.0, -44.0);
    }

    /**
     * ***********************************************************
     * A�ade una conexi�n al nodo
	 ************************************************************
     */
    @Override
    public void addEdge(NKConection edge) {
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
	 ************************************************************
     */
    @Override
    public void removeEdge(NKConection edge) {
        if (conections.contains(edge)) {
            conections.remove(conections.indexOf(edge));
            handler.notShowInterfazEth(interfaces.get(getNetName(edge)));
            interfaces.remove(getNetName(edge));
        }
    }

    public HashMap<String, Ethernet> getInterfaces() {
        return interfaces;
    }
    
    /**
     * ***********************************************************
     * Devuelve todas las conexiones del nodo
	 ************************************************************
     */
    public ArrayList getEdges() {
        return conections;
    }

    /**
     * ***********************************************************
     * Actualiza todas las conexiones del nodo para poder representarlas en
     * pantalla cuando el nodo es arrastrado
	 ************************************************************
     */
    @Override
    public void updateEdges() {
        for (int i = 0; i < conections.size(); i++) {
            //NetKitGUI.this.updateEdge((PPath) edges.get(i));
            (conections.get(i)).updateEdge();
        }
    }

    protected void CreateStartupSwitchFile() {
        // Se crea un fichero de arranque para el switch para que se configure
        // con brctl
        String homeEnvStr = System.getenv("HOME");
        File fich = new File(UtilNetGUI.getCurrentWorkSpace().getAbsolutePath()
                + "/" + getName() + ".startup");

        if (homeEnvStr.equals(UtilNetGUI.getCurrentWorkSpace())) {
            // si se esta usando el directorio por defecto .netkitgui
            // se borra el startup porque puede ser de una configuracion antigua
            if (!(fich.exists())) {
                fich.delete();
            }
        }

        if (!(fich.exists())) {
            System.out.println("Creando fichero="
                    + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath()
                    + "/" + getName() + ".startup");
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(
                        UtilNetGUI.getCurrentWorkSpace().getAbsolutePath()
                        + "/" + getName() + ".startup"));

                bw.write("echo -e '\\e[1;32mConfiguring " + getName() + "\\e[0m'");
                bw.newLine();
                bw.write("echo 0 > /proc/sys/net/ipv4/ip_forward");
                bw.newLine();
                for (int i = 0; i < conections.size(); i++) {
                    bw.write("ifconfig " + "eth" + i + " up");
                    bw.newLine();
                }
                bw.write("brctl addbr " + getName());
                bw.newLine();
                for (int i = 0; i < conections.size(); i++) {
                    bw.write("echo -e '     \\e[1;32mAdding eth" + i + " interface\\e[0m'");
                    bw.newLine();
                    bw.write("brctl addif " + getName() + " " + "eth" + i);
                    bw.newLine();
                }
                bw.write("ifconfig " + getName() + " up");
                bw.newLine();
                bw.write("echo -e '\\e[1;32mSwitch " + getName() + " is configured\\e[0m'");
                bw.newLine();

                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startNetKit() {
        if (!isStarted()) {
            CreateStartupSwitchFile();

            String cmd = vstartCmdGen();
            // System.out.println("CMD SWITCH=" + cmd);
            try {
                Process proc;
                proc = Runtime.getRuntime().exec(cmd, null);
                //setStarted(true);
                updateStartedImages();
                TelnetConnector tc = new TelnetConnector(this, cmd);
            } catch (Exception ex) {
                System.out.println("Error " + ex);
            }
        }
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
	 ****************************************************************
     */
    private String getNetName(NKConection edge) {
        //Si es un NKDirectConection es porque estan dos switches conectados directamente
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
     * netkit
	 ****************************************************************
     */
    private String vstartCmdGen() {
        if (conections.isEmpty()) {
            return processStartCmd("vstart -l " + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + " " + getName());
        }
        Iterator i = conections.iterator();
        String s = "vstart -l " + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + " " + getName();
        String key;
        while (i.hasNext()) {
            key = getNetName((NKConection) i.next());
            s += " --" + (interfaces.get(key)).getEthName().getText() + "=" + key;
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

    private boolean ethIsUsed(String ethId) {
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
    protected void updateEthLocation(NKConection edge) {
        if (conections.contains(edge)) {
            Point2D p = NetKitGeom.getGlobalIntersect(delimiter, this, edge);
            Ethernet ethNode = interfaces.get(getNetName(edge));
            ethNode.centerFullBoundsOnPoint(p.getX(), p.getY());
        }
    }

    @Override
    public void updateEthernets(HashMap eth_ip) {
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

    /**
     * ***********************************************************
     * Posiciona el nombre del nodo en el icono
	 ************************************************************
     */
    @Override
    protected void ShowDisplayName(String name) {
        double height = this.getHeight(), width = this.getWidth();
        PText tNode = new PText(name);
        tNode.centerFullBoundsOnPoint((width / 2) - 10, height);
        tNode.setPickable(false);
        tNode.setScale((float) 1.5);
        this.addChild(tNode);
    }
}
