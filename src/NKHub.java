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
* Clase que representa un hub dentro de la red
**********************************************************************

*/

import java.util.*;
import java.lang.*;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.util.*;

public class NKHub extends NKNode
{
        public static final long serialVersionUID = 1L;
	private static final String fileImage = System.getProperty("NETLAB_HOME")+"/images/128x128/hub.png";
	private static final String deleteFileImage = System.getProperty("NETLAB_HOME")+"/images/128x128/hubDel.png";
	private static final String fileSelectedImage = System.getProperty("NETLAB_HOME")+"/images/128x128/hub_selected.png";
	
	private String netName;
	private ArrayList<NKConection> conections;
	
	
	/*****************************************************
	 * Crea un Hub
	 *****************************************************/
	public NKHub (String hubName, String netName)
	{
		super(hubName,fileImage, fileSelectedImage, deleteFileImage);
		this.netName = netName;
		conections = new ArrayList<NKConection>();
	}
	
	/*************************************************************
	 * Añade una conexión al nodo
	 *************************************************************/
	public void addEdge (NKConection edge)
	{
		if (!conections.contains(edge))
			//No existía la conexión
			conections.add(edge);
	}
	
	/*************************************************************
	 * Elimina una conexión con el nodo
	 *************************************************************/
	public void removeEdge (NKConection edge)
	{
		conections.remove(conections.indexOf(edge));
	}
	
	/*************************************************************
	 * Devuelve todas las conexiones del nodo
	 *************************************************************/
	public ArrayList getEdges ()
	{
		return conections;
	}
	
	/*************************************************************
	 * Devuelve el nombre de red asignado al Hub (util cuando
	 * arrancamos un nodo con parámetros UML para netkit)
	 *************************************************************/
	public String getNetName()
	{
		return netName;
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
	
	/*************************************************************
	 * Posiciona el nombre del nodo en el icono
	 *************************************************************/
	protected void ShowDisplayName (String name)
	{
		double height = this.getHeight(), width= this.getWidth();
		PText tNode = new PText(name);
		tNode.centerFullBoundsOnPoint((width/2)-10,height+8);
		tNode.setPickable(false);
		tNode.setScale((float)1.5);
		this.addChild(tNode);
	}
	
}
