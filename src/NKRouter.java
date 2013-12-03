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
import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

import java.util.*;
import java.lang.*;
import java.awt.geom.*;
import java.awt.*;
import java.awt.geom.Point2D.*;

public class NKRouter extends NKSystem
{
        public static final long serialVersionUID = 1L;
	private static final String fileImage = System.getProperty("NETLAB_HOME")+"/images/128x128/router2.png";
	private static final String deleteFileImage = System.getProperty("NETLAB_HOME")+"/images/128x128/routerDel2.png";
	private static final String fileSelectedImage = System.getProperty("NETLAB_HOME")+"/images/128x128/router_selected2.png";
	private static final String fileStartedImage = System.getProperty("NETLAB_HOME")+"/images/128x128/routerStarted2.png";
	private static final String fileSelectedStartedImage = System.getProperty("NETLAB_HOME")+"/images/128x128/routerStartedSelected2.png";
	private static final String eth = "eth";
	//Almacenaremos un array con todas las referencias de las conexiones que mantenga
	//abiertas este nodo.
	private ArrayList<NKConection> conections;
	
	//Almacenaremos las tuplas [red, interfaz] -> [String, Ethernet]
        private HashMap<String,Ethernet> interfaces;
	
	//especificamos un rectangulo de delimitación (Para calcular intersecciones)
	private final RectangleNodeDelimiter delimiter;
	
	public NKRouter (String name, LayersHandler handler)
	{
		super(name, fileImage, fileSelectedImage, deleteFileImage, handler);
		conections = new ArrayList<NKConection>();
		interfaces = new HashMap<String,Ethernet>();

		//big icons
		//delimiter = new RectangleNodeDelimiter(79.0,-85.0,54.0,-71.0);

		//small icons
		delimiter = new RectangleNodeDelimiter(75.0,-70.0,50.0,-50.0);
	}
	
	/*************************************************************
	 * Añade una conexión al nodo
	 *************************************************************/
	public void addEdge (NKConection edge)
	{
		if (!conections.contains(edge))
		{
			//No existía la conexión
			conections.add(edge);
			Ethernet ethX = new Ethernet(getNewEthName());
			//formatEthText(ethX);
			handler.showInterfazEth(ethX);
			interfaces.put(getNetName(edge),ethX);			
		}
	}
	
	/*************************************************************
	 * Elimina una conexión con el nodo
	 *************************************************************/
	public void removeEdge (NKConection edge)
	{
		if (conections.contains(edge))
		{
			conections.remove(conections.indexOf(edge));
			handler.notShowInterfazEth(interfaces.get(getNetName(edge)));
			interfaces.remove(getNetName(edge));
		}
	}
	
	/*************************************************************
	 * Devuelve todas las conexiones del nodo
	 *************************************************************/
	public ArrayList getEdges ()
	{
		return conections;
	}
	
	/*************************************************************
	 * Actualiza todas las conexiones del nodo para poder
	 * representarlas en pantalla cuando el nodo es arrastrado
	 *************************************************************/
	public void updateEdges ()
	{
		for (int i = 0; i < conections.size(); i++)
		{
			//NetKitGUI.this.updateEdge((PPath) edges.get(i));
			(conections.get(i)).updateEdge();			
		}
	}
	
	public void startNetKit()
	{
		if (!isStarted())
		{
			String cmd = vstartCmdGen();
			try {
				Process proc;
				proc = Runtime.getRuntime().exec(cmd, null);
				//setStarted(true);
				updateStartedImages();
				TelnetConnector tc = new TelnetConnector(this, cmd);
			} catch (Exception ex)
				{System.out.println("Error " + ex);}
		}
	}
	
	protected void updateStartedImages ()
	{
		changeSelectedImage (fileSelectedStartedImage);
		changeNormalImage (fileStartedImage);
	}
	
	protected void updateNormalImages ()
	{
		changeSelectedImage (fileSelectedImage);
		changeNormalImage (fileImage);
		updateEthernets(new HashMap());
	}
	
	/*****************************************************************
	 * Los nodos se conectan a la red únicamente a través de hubs
	 * El nodo accede a ellos para obtener el nombre de red.
	 *****************************************************************/
	private String getNetName(NKConection edge)
	{
		//Si es un NKDirectConection es porque estan dos routers conectados directamente
		if (edge instanceof NKDirectConection)
			return ((NKDirectConection)edge).getNetName ();
		else if (edge.getNode1().getName().equalsIgnoreCase(getName()))
			return ((NKHub)edge.getNode2()).getNetName();
		else return ((NKHub)edge.getNode1()).getNetName();
	}
	
	/*****************************************************************
	 * Genera el comando vstart con los parámetros necesarios para
	 * arrancar netkit
	 *****************************************************************/
	private String vstartCmdGen()
	{
		if (conections.isEmpty())
			return processStartCmd("vstart -l " + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + " " + getName());
		Iterator i = conections.iterator();
		String s = "vstart -l " + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + " " + getName();
		String key;
		while (i.hasNext())
		{
			key = getNetName((NKConection)i.next());
			s += " --" + (interfaces.get(key)).getEthName ().getText() +
				"=" + key;
		}
		return processStartCmd(s);
	}
	
	private String getNewEthName()
	{
		boolean exit = false;
		String ethId;
		int count = 0;
		do
		{
			ethId = eth + count;
			count ++;
		}while(ethIsUsed(ethId));
		return ethId;			
	}
	
	private boolean ethIsUsed(String ethId)
	{
		Iterator i = interfaces.values().iterator();
		boolean used = false;
		Ethernet eAux;
		while (i.hasNext() && !used)
		{
			eAux = (Ethernet)i.next();
			used = (ethId.equalsIgnoreCase(eAux.getEthName ().getText()));
		}
		return used;
	}
	
	protected void updateEthLocation(NKConection edge)
	{
		if (conections.contains(edge))
		{			
			Point2D p = NetKitGeom.getGlobalIntersect(delimiter,this, edge);
			Ethernet ethNode = interfaces.get(getNetName(edge));
			ethNode.centerFullBoundsOnPoint(p.getX(),p.getY());
		}
	}
	
	public void updateEthernets (HashMap eth_ip)
	{
		Iterator i = interfaces.keySet().iterator();
		String net,ethX;
		while (i.hasNext())
		{
			net = (String)i.next();
			ethX = (interfaces.get(net)).getEthName().getText();
			if (eth_ip.containsKey(ethX))
				//actualizamos la ip de la interfaz
				(interfaces.get(net)).setIp((String)eth_ip.get(ethX));
			else
				//si no está significa que la interfaz no tiene ip asignada
				(interfaces.get(net)).setIp(null);
		}
	}
}
