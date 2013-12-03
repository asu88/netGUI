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

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

import java.lang.*;
import java.util.*;

import java.awt.geom.*;
import java.awt.*;
import java.awt.geom.Point2D.*;


public class NKCompaq extends NKSystem
{
        public static final long serialVersionUID = 1L;
	private static final String fileImage = System.getProperty("NETLAB_HOME")+"/images/128x128/system2.png";
	private static final String deleteFileImage = System.getProperty("NETLAB_HOME")+"/images/128x128/systemDel2.png";
	private static final String fileSelectedImage = System.getProperty("NETLAB_HOME")+"/images/128x128/system_selected2.png";
	private static final String fileStartedImage = System.getProperty("NETLAB_HOME")+"/images/128x128/systemStarted2.png";
	private static final String fileSelectedStartedImage = System.getProperty("NETLAB_HOME")+"/images/128x128/systemStartedSelected2.png";
	
	//especificamos un rectangulo de delimitación (Para calcular intersecciones)
	private static RectangleNodeDelimiter delimiter;
	private static final String eth0 = "eth0";
	private final Ethernet eth = new Ethernet (eth0);
	
	private NKConection edge;
	
	public NKCompaq (String name, LayersHandler handler){
		super(name, fileImage, fileSelectedImage, deleteFileImage, handler);
		//formatEthText(eth.getEthName());

		// for big icons
		//delimiter = new RectangleNodeDelimiter(104.0,-104.0,78.0,-87.0);

		// for small icons
		delimiter = new RectangleNodeDelimiter(80.0,-75.0,60.0,-65.0);
	}
	
	/*************************************************************
	 * Añade una conexión al nodo
	 *************************************************************/
	public void addEdge (NKConection edge)
	{
		this.edge = edge;
		handler.showInterfazEth(eth);
	}
	
	/*************************************************************
	 * Elimina una conexión con el nodo
	 *************************************************************/
	public void removeEdge (NKConection edge)
	{
		if ((this.edge!=null)&&(this.edge.equals(edge)))
		{
			this.edge = null;
			handler.notShowInterfazEth(eth);
		}
	}
	
	/*************************************************************
	 * Actualiza todas las conexiones del nodo para poder
	 * representarlas en pantalla cuando el nodo es arrastrado
	 *************************************************************/
	public void updateEdges ()
	{
		if (edge != null)
			edge.updateEdge();
	}
	
	/*************************************************************
	 * Devuelve todas las conexiones del nodo
	 *************************************************************/
	public NKConection getEdge ()
	{
		return edge;
	}
	
	public void startNetKit ()
	{ 
		if (!isStarted())
		{
			String cmd = vstartCmdGen();

			try {
				Process proc;
				//System.out.println("Antes del exec, cmd=" + cmd);
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
		eth.setIp(null);
	}
	
	/*****************************************************************
	 * Genera el comando vstart con los parámetros necesarios para
	 * arrancar netkit
	 *****************************************************************/
	private String vstartCmdGen()
	{
		String cmd;
		if (edge == null)
			cmd = "vstart -l " + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + " " + getName();
		else cmd = "vstart -l " + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + " " + getName() +
						" --" + eth0 + "=" + getNetName();
		
		return processStartCmd(cmd);
		
	}
	
	/*****************************************************************
	 * Los ordenadores se conectan a la red a través de un hub o de un 
	 * switch. El nodo accede a él para obtener el nombre de red.
	 *****************************************************************/
	private String getNetName()
	{


		// Si es un NKDirectConection es porque estan el host conectado 
	        // conectado directamente a un switch
		if (edge instanceof NKDirectConection)
			return ((NKDirectConection)edge).getNetName ();
		else if (edge.getNode1().getName().equalsIgnoreCase(getName()))
			return ((NKHub)edge.getNode2()).getNetName();
		else return ((NKHub)edge.getNode1()).getNetName();
	}
	
	protected void updateEthLocation(NKConection edge)
	{
		if (edge!=null)
		{
			Point2D p = NetKitGeom.getGlobalIntersect(delimiter,this, edge);
			//localToGlobal(p);
			eth.centerFullBoundsOnPoint(p.getX(),p.getY());
		}
	}

	public void updateEthernets (HashMap eth_ip)
	{
		String ip = (String)eth_ip.get(eth0);
		eth.setIp(ip);
	}


	/*************************************************************
	 * Posiciona el nombre del nodo en el icono
	 *************************************************************/
	protected void ShowDisplayName (String name)
	{
		double height = this.getHeight(), width= this.getWidth();
		PText tNode = new PText(name);
		tNode.centerFullBoundsOnPoint((width/2)-10,height);
		tNode.setPickable(false);
		tNode.setScale((float)1.5);
		this.addChild(tNode);
	}

}
