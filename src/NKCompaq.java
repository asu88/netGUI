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
 * Clase que representa un ordenador dentro de la red
 **********************************************************************

 */
import edu.umd.cs.piccolo.nodes.*;
import java.awt.Color;
import java.awt.geom.*;
import java.util.*;

public class NKCompaq extends NKSystem {

    public static final long serialVersionUID = 1L;
    private static final String fileImage = System.getProperty("NETLAB_HOME") + "/images/new2/128x128/system5.png";
    private static final String deleteFileImage = System.getProperty("NETLAB_HOME") + "/images/new2/128x128/systemDel5.png";
    private static final String fileSelectedImage = System.getProperty("NETLAB_HOME") + "/images/new2/128x128/system_selected5.png";
    private static final String fileStartedImage = System.getProperty("NETLAB_HOME") + "/images/new2/128x128/systemStarted5.png";
    private static final String fileSelectedStartedImage = System.getProperty("NETLAB_HOME") + "/images/new2/128x128/systemStartedSelected5.png";
    //especificamos un rectangulo de delimitaci�n (Para calcular intersecciones)
    private static RectangleNodeDelimiter delimiter;
    private static final String eth0 = "eth0";
    private final Ethernet eth = new Ethernet(eth0);
    private NKConection edge;
    private HashMap<String, Ethernet> interfaces;

    public NKCompaq(String name, Point2D position, LayersHandler handler) {
        super(name, fileImage, fileSelectedImage, deleteFileImage, 
                position, handler);
        //formatEthText(eth.getEthName());

        // for big icons
        delimiter = new RectangleNodeDelimiter(104.0,-104.0,78.0,-87.0);

        // for small icons
//        delimiter = new RectangleNodeDelimiter(80.0, -75.0, 60.0, -65.0);
        interfaces = new HashMap<String, Ethernet>();
        interfaces.put("eth0", eth);
    }

    public HashMap<String, Ethernet> getInterfaces() {
        return interfaces;
    }

    /**
     * ***********************************************************
     * A�ade una conexi�n al nodo
	 ************************************************************
     */
    @Override
    public void addEdge(NKConection edge) {
        this.edge = edge;
        handler.showInterfazEth(eth);
        interfaces.put("eth0", eth);
    }

    /**
     * ***********************************************************
     * Elimina una conexi�n con el nodo
	 ************************************************************
     */
    @Override
    public void removeEdge(NKConection edge) {
        if ((this.edge != null) && (this.edge.equals(edge))) {
            this.edge = null;
            handler.notShowInterfazEth(eth);
            interfaces.remove("eth0");
        }
    }

    /**
     * ***********************************************************
     * Actualiza todas las conexiones del nodo para poder representarlas en
     * pantalla cuando el nodo es arrastrado
	 ************************************************************
     */
    @Override
    public void updateEdges() {
        if (edge != null) {
            edge.updateEdge();
        }
    }

    /**
     * ***********************************************************
     * Devuelve todas las conexiones del nodo
	 ************************************************************
     */
    public NKConection getEdge() {
        return edge;
    }

    @Override
    public void startNetKit() {
        if (!isStarted()) {
            String cmd = vstartCmdGen();

            try {
                Process proc;
                //System.out.println("Antes del exec, cmd=" + cmd);
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
        eth.setIp(null);
    }

    /**
     * ***************************************************************
     * Genera el comando vstart con los par�metros necesarios para arrancar
     * netkit
	 ****************************************************************
     */
    private String vstartCmdGen() {
        String cmd;
        if (edge == null) {
            cmd = "vstart -l " + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + " " + getName();
        } else {
            cmd = "vstart -l " + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + " " + getName()
                    + " --" + eth0 + "=" + getNetName();
        }

        return processStartCmd(cmd);

    }

    /**
     * ***************************************************************
     * Los ordenadores se conectan a la red a trav�s de un hub o de un switch.
     * El nodo accede a �l para obtener el nombre de red.
	 ****************************************************************
     */
    private String getNetName() {


        // Si es un NKDirectConection es porque estan el host conectado 
        // conectado directamente a un switch
        if (edge instanceof NKDirectConection) {
            return ((NKDirectConection) edge).getNetName();
        } else if (edge.getNode1().getName().equalsIgnoreCase(getName())) {
            return ((NKHub) edge.getNode2()).getNetName();
        } else {
            return ((NKHub) edge.getNode1()).getNetName();
        }
    }

    @Override
    protected void updateEthLocation(NKConection edge) {
        if (edge != null) {
            Point2D p = NetKitGeom.getGlobalIntersect(delimiter, this, edge);
            //localToGlobal(p);
            eth.centerFullBoundsOnPoint(p.getX(), p.getY());
        }
    }

    @Override
    public void updateEthernets(HashMap eth_ip) {
        String ip = (String) eth_ip.get(eth0);
        eth.setIp(ip);
    }

    /**
     * ***********************************************************
     * Posiciona el nombre del nodo en el icono
	 ************************************************************
     */
    @Override
    protected void ShowDisplayName(String name) {
        double height = this.getHeight(), width = this.getWidth();
        PText tNode = new PText(name.toUpperCase());
        tNode.setTextPaint(Color.WHITE);
        tNode.centerFullBoundsOnPoint((width / 2.2), (height / 1.8));
        tNode.setPickable(false);
        tNode.setScale((float) 1.5);
        this.addChild(tNode);
    }
}
