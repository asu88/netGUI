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



/**********************************************************************
* Crea una subclase que encapsula a todos los nodos de la red capaces
* de ejecutar un kernel (Compaq, routers...)
***********************************************************************/


import java.io.*;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.util.*;

import java.lang.*;
import java.util.*;

import java.awt.geom.*;
import java.awt.*;
import java.awt.geom.Point2D.*;

public abstract class NKSystem extends NKNode
{
	private boolean started;
	protected LayersHandler handler;
	
	
	public NKSystem (String name, String fileImage, String fileSelectedImage,
		 String deleteFileImage, LayersHandler handler)
	{
		super(name, fileImage, fileSelectedImage, deleteFileImage);
		started = false;
		this.handler = handler;
	}
	
	protected void setStarted (boolean run)
	{
		started = run;
	}
	
	public boolean isStarted () {return started;}	
	
	/********************************************************************
	 * Establece el nuevo icono cuando el nodo arranca un kernel-netkit 
	 ********************************************************************/
	protected abstract void updateStartedImages();
	
	/********************************************************************
	 * Establece el nuevo icono cuando el nodo arranca un kernel-netkit 
	 ********************************************************************/
	protected abstract void updateNormalImages();
	
	/*************************************************************
	 * Posiciona el nombre del nodo en el icono
	 *************************************************************/
	protected void ShowDisplayName (String name)
	{
		double height = this.getHeight(), width= this.getWidth();
		PText tNode = new PText(name);
		tNode.centerFullBoundsOnPoint((width/2),height);
		tNode.setPickable(false);
		tNode.setScale((float)1.5);
		this.addChild(tNode);
	}
	
	protected void formatEthText(PText ethNode)
	{
		ethNode.setScale((float)1.0);
		ethNode.setFont(new Font ("Dialog",Font.BOLD,14));
		ethNode.setTextPaint(Color.BLUE);
	}
	
	/***************************
	 * Arranca un kernel-netkit 
	 ***************************/
	public abstract void startNetKit();
	
	/***************************
	 * Para Netkit (vrash -r si crash=true; vhalt en otro caso)
	 ***************************/
	public void stopNetKit(boolean crash)
	{
		String cmd = "";
		if (isStarted())
		{
			if (crash)
				cmd = vcrashCmdGen();
			else cmd = vhaltCmdGen();
			//System.out.println(cmd);
			
			try {
				NodeTelnetCommunicator.removeConnection(this);
				Process proc;
				proc = Runtime.getRuntime().exec(cmd,null);
				setStarted(false);
				updateNormalImages();
			} catch (Exception ex)
				{System.out.println("Error " + ex);}
		}
	}
	
	/*****************************************************************
	 * Genera el comando vhalt para parar la ejecución de la máquina
	 * virtual arrancada sobre este nodo
	 *****************************************************************/
	private String vhaltCmdGen()
	{
		return "vhalt " + getName();
		
	}
	
	/*****************************************************************
	 * Genera el comando vcrash para parar la ejecución de la máquina
	 * virtual y borrar su archivo de configuración
	 *****************************************************************/
	private String vcrashCmdGen()
	{
		return "vcrash -r " + getName();
		
	}
	
	/**************************************************************
	 * Método invocado sobre el nodo cuando cambia el estado de su
	 * conexión para que actualice la representación de sus interfaces
	 **************************************************************/
	protected abstract void updateEthLocation(NKConection edge);
	
	/*****************************************************************
	 * Añade los parámetros --no-log -f /workspace/nodeName.disk [--new]
	 * si nodeName.disk no existe.(es la primera vez que se arranca un nodo)
	 *****************************************************************/
	 protected String processStartCmd (String cmd)
	 {
	 	cmd += " -f " + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath()
	 			+ "/" + getName() + ".disk ";
	 	cmd += "--con1=port:" + NodeTelnetCommunicator.getNewPort();
		//System.out.println(cmd);
		return cmd;
	 }

	/********************************************************************
	 *Obtiene un hastmap con el string ethX como clave y su respectiva IP
	 *también como String que contiene todas las interfaces a actualizar
	 *con su ip asociada. Si una interfaz no aparece dentro del hasmap
	 *significará que ya no tiene ip asociada. 
	 ********************************************************************/
	public abstract void updateEthernets (HashMap eth_ip);
	
}
