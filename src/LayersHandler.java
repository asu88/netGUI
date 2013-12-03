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

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.nodes.*;
import java.awt.geom.Point2D;
import java.util.*;

public class LayersHandler {

    PLayer nodeLayer;
    PLayer edgeLayer;
    PLayer ethLayer;

    public LayersHandler(PLayer nLayer, PLayer eLayer, PLayer ethLayer) {
        nodeLayer = nLayer;
        edgeLayer = eLayer;
        this.ethLayer = ethLayer;
    }

    public void showInterfazEth(Ethernet eth) {
        ethLayer.addChild(eth);        
    }

    public void notShowInterfazEth(Ethernet eth) {
        ethLayer.removeChild(eth);
    }
//---------------------------------------------------------------------------------

    public void showInterfazEth(PText eth) {
        ethLayer.addChild(eth);
    }

    public void notShowInterfazEth(PText eth) {
        ethLayer.removeChild(eth);
    }
//---------------------------------------------------------------------------------

    public void deleteConnection(NKConection edge) {
        if (edge != null) {
            edge.getNode1().removeEdge(edge);
            edge.getNode2().removeEdge(edge);
            edgeLayer.removeChild(edge);
            UtilNetGUI.setAplicationStatusChange(true);
        }
    }

    public void deleteNode(NKNode n) {
        if (n instanceof NKCompaq) {
            deleteConnection(((NKCompaq) n).getEdge());
        }
        if (n instanceof NKRouter) {
            deleteAllConections(((NKRouter) n).getEdges());
        }
        if (n instanceof NKSwitch) {
            deleteAllConections(((NKSwitch) n).getEdges());
        }
        if (n instanceof NKHub) {
            deleteAllConections(((NKHub) n).getEdges());
        }
        if (n instanceof NKSystem) {
            //borramos el nodo con vcrash
            ((NKSystem) n).stopNetKit(true);
            //Eliminamos su *.disk si existe
            UtilNetGUI.deleteFileNodeDisk(n.getName());
        }
        nodeLayer.removeChild(n);
        UtilNetGUI.setAplicationStatusChange(true);
    }

    public void addNewNode(Point2D globalPoint2D, NKNode n) {
        if (n instanceof NKHub) {
            addSavedHub(globalPoint2D, n.getName());
        } else if (n instanceof NKCompaq) {
            addNewTerminal(globalPoint2D, n.getName());
        } else if (n instanceof NKRouter) {
            addNewRouter(globalPoint2D, n.getName());
        } else if (n instanceof NKSwitch) {
            addNewSwitch(globalPoint2D, n.getName());
        }
        UtilNetGUI.setAplicationStatusChange(true);
    }

    public void addNewTerminal(Point2D globalPoint2D, String terminalName) {
        if (!nodeNameExists(terminalName)) {
            NKCompaq terminal = new NKCompaq(terminalName, this);
            terminal.centerFullBoundsOnPoint(globalPoint2D.getX(), globalPoint2D.getY());
            nodeLayer.addChild(terminal);
            UtilNetGUI.setAplicationStatusChange(true);
        } else {
            System.out.println("El terminal: " + terminalName + " ya existe");
        }
    }

    public void addNewRouter(Point2D globalPoint2D, String routerName) {
        NKRouter router = new NKRouter(routerName, this);
        router.centerFullBoundsOnPoint(globalPoint2D.getX(), globalPoint2D.getY());
        nodeLayer.addChild(router);
        UtilNetGUI.setAplicationStatusChange(true);
    }

    public void addNewSwitch(Point2D globalPoint2D, String switchName) {
        NKSwitch s = new NKSwitch(switchName, this);
        s.centerFullBoundsOnPoint(globalPoint2D.getX(), globalPoint2D.getY());
        nodeLayer.addChild(s);
        UtilNetGUI.setAplicationStatusChange(true);
    }

    public void addNewHub(Point2D globalPoint2D) {
        NKHub hub = new NKHub(getUnusedHubName(), getUnusedNetName());
        hub.centerFullBoundsOnPoint(globalPoint2D.getX(), globalPoint2D.getY());
        nodeLayer.addChild(hub);
        UtilNetGUI.setAplicationStatusChange(true);
    }

    private void addSavedHub(Point2D globalPoint2D, String hubName) {
        NKHub hub = new NKHub(hubName, getUnusedNetName());
        hub.centerFullBoundsOnPoint(globalPoint2D.getX(), globalPoint2D.getY());
        nodeLayer.addChild(hub);
    }

    public String getUnusedTerminalName() {
        String pcName = UtilNetGUI.getPcName();
        while (nodeNameExists(pcName)) {
            UtilNetGUI.IncrementPcCount();
            pcName = UtilNetGUI.getPcName();
        }
        return pcName;
    }

    public String getUnusedRouterName() {
        String rName = UtilNetGUI.getRouterName();
        while (nodeNameExists(rName)) {
            UtilNetGUI.IncrementRouterCount();
            rName = UtilNetGUI.getRouterName();
        }
        return rName;
    }

    public String getUnusedSwitchName() {
        String sName = UtilNetGUI.getSwitchName();
        while (nodeNameExists(sName)) {
            UtilNetGUI.IncrementSwitchCount();
            sName = UtilNetGUI.getSwitchName();
        }
        return sName;
    }

    public boolean nodeNameExists(String name) {
        Collection cNodes = nodeLayer.getAllNodes();
        Iterator i = cNodes.iterator();
        boolean exist = false;
        while (i.hasNext() && !exist) {
            Object obj = i.next();
            if (obj instanceof NKNode) {
                exist = (name.equalsIgnoreCase(((NKNode) obj).getName()));
            }
        }
        return exist;
    }

    public NKNode searchNode(String name) {
        Object obj;
        Collection cNodes = nodeLayer.getAllNodes();
        Iterator i = cNodes.iterator();
        while (i.hasNext()) {
            obj = i.next();
            if (obj instanceof NKNode) {
                if (name.equalsIgnoreCase(((NKNode) obj).getName())) {
                    return (NKNode) obj;
                }
            }
        }

        return null;
    }

    private String getUnusedHubName() {
        String hName = UtilNetGUI.getHubName();
        while (nodeNameExists(hName)) {
            UtilNetGUI.IncrementHubCount();
            hName = UtilNetGUI.getHubName();
        }
        return hName;
    }

    private String getUnusedNetName() {
        String netName = UtilNetGUI.getNetName();
        while (netNameExists(netName)) {
            UtilNetGUI.IncrementNetCount();
            netName = UtilNetGUI.getNetName();
        }
        return netName;
    }

    public boolean netNameExists(String name) {
        return (searchNetNameInHubs(name) || searchNetNameInDirectsNets(name));
    }

    private boolean searchNetNameInHubs(String name) {
        Collection cNodes = nodeLayer.getAllNodes();
        Iterator i = cNodes.iterator();
        boolean exist = false;
        while (i.hasNext() && !exist) {
            Object obj = i.next();
            if (obj instanceof NKHub) {
                exist = (name.equalsIgnoreCase(((NKHub) obj).getNetName()));
            }
        }
        return exist;
    }

    private boolean searchNetNameInDirectsNets(String name) {
        Collection cEdges = edgeLayer.getAllNodes();
        Iterator i = cEdges.iterator();
        boolean exist = false;
        while (i.hasNext() && !exist) {
            Object obj = i.next();
            if (obj instanceof NKDirectConection) {
                exist = (name.equalsIgnoreCase(((NKDirectConection) obj).getNetName()));
            }
        }
        return exist;
    }

    private void deleteAllConections(ArrayList connections) {
        while (!connections.isEmpty()) {
            deleteConnection((NKConection) connections.get(0));
        }
    }

//-------------------------------------------------------------------------------------------	
    public void addConnection(NKNode node1, NKNode node2) {
        NKConection edge;
        //if ((node1 instanceof NKRouter) && (node2 instanceof NKRouter)) 
        if (((node1 instanceof NKRouter) && (node2 instanceof NKRouter))
                || ((node1 instanceof NKSwitch) && (node2 instanceof NKSwitch))
                || ((node1 instanceof NKRouter) && (node2 instanceof NKSwitch))
                || ((node1 instanceof NKSwitch) && (node2 instanceof NKRouter))
                || ((node1 instanceof NKSwitch) && (node2 instanceof NKCompaq))
                || ((node1 instanceof NKCompaq) && (node2 instanceof NKSwitch))) {
            edge = new NKDirectConection(node1, node2, getUnusedNetName());
        } else {
            edge = new NKConection(node1, node2);
        }

        if (!edgeLayer.getAllNodes().contains(edge)) {
            if (node1 instanceof NKCompaq) {
                removeOldConexion((NKCompaq) node1);
            }
            if (node2 instanceof NKCompaq) {
                removeOldConexion((NKCompaq) node2);
            }
            node1.addEdge(edge);
            node2.addEdge(edge);
            edgeLayer.addChild(edge);
            edge.updateEdge();
            UtilNetGUI.setAplicationStatusChange(true);
        } else {
            System.out.println("ya existe la conexion!!");
        }

        node1.setNormalImage();
        node2.setNormalImage();
    }
    /*	
     public void addConnection (NKNode n1, NKNode n2)
     {			
     NKConection edge = new NKConection(n1,n2);
				
     if (!edgeLayer.getAllNodes().contains(edge))
     {
     n1.addEdge(edge);
     n2.addEdge(edge);
     edgeLayer.addChild(edge);
     edge.updateEdge();
     UtilNetGUI.setAplicationStatusChange(true);
     }
     else System.out.println("ya existe la conexion!!");
     }
     */

    /**
     * ***********************************************************
     * Los terminales s�o se conectan a trav� de una interfaz, en caso de que ya
     * estuviera conectado, se borra su antiga conexi� y se a�de la nueva
	 ************************************************************
     */
    private void removeOldConexion(NKCompaq n) {
        NKConection edge = n.getEdge();
        if (edge != null) {
            if (n.equals(edge.getNode1())) {
                edge.getNode2().removeEdge(edge);
            } else {
                edge.getNode1().removeEdge(edge);
            }
            n.removeEdge(edge);
            edgeLayer.removeChild(edge);
        }
    }
}
