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

import java.io.File;
import java.net.Socket;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;



public class TelnetConnector
{
	private static String LOCALHOST = "localhost";
	
	public TelnetConnector (NKSystem node, String startCMD)
	{
                // Do not start the connector if the host does not 
                // have /usr/sbin/in.telnetd, as the telnet connection
                // would stall
                File telnetd = new File ("/usr/sbin/in.telnetd");
                if ( telnetd.exists() ) 
                {
                    int port = getStartedPort(startCMD);

                    if (port!=0)
                    {
                        ConnectorThread ct = new ConnectorThread(node,port);
                        ct.start();
                    }
                } else {
			System.out.println("Will not start Telnet connector: " + telnetd.getAbsolutePath() + " is not present.");
			node.setStarted(true);
                }
	}
	
	private int getStartedPort (String startCMD)
	{
		String patron1 = "--con1=port:", patron2 = "--new", port;
		int p=0;
		if (startCMD.indexOf(patron2) == -1)
			port = startCMD.substring(startCMD.indexOf(patron1) + patron1.length(),
								startCMD.length());
		else 
			port = startCMD.substring(startCMD.indexOf(patron1) + patron1.length(),
								startCMD.indexOf(patron2) - 1);
		
		try{
			p = Integer.parseInt(port);
		}catch(NumberFormatException e){
			System.out.println("Port parse error " + e );
			}
			
		return p;
	}
		
//Hilo de ejecución que crea un canal TCP para comunicarse con la máquina virtual a través
//del protocolo Telnet.
public class ConnectorThread extends Thread
{
	private NKSystem node;
	private int port;
	
	public ConnectorThread (NKSystem node, int port)
	{
		this.node = node;
		this.port = port;
	}
	
	public void run ()
	{
		String errorMsg = "No se ha podido arrancar la máquina " + node.getName() + ".\n"
				+ "A continuación se reiniciará su ejecución.";
		int MAXATT=100, i = 1;
		boolean connected = false;
		try
		{
			sleep(3000);
			while ((!connected) && (i <= MAXATT))
			{
		                // System.out.println(+ node.getName() + " intentando conectar");
				connected = openTelnetConnection();
				sleep(2000);
				i ++;
			}
		if (connected)
		{
		    System.out.println(node.getName() + " connected to GUI");
		    //Preparamos el socket para leer desde el inductor de la shell
		    NodeTelnetCommunicator.getTelnetSocket(node).getOutputResponse();
		    node.setStarted(true);
		}
		else 
		{
		    System.out.println("Warning: cannot connect with " + node.getName());
		    node.setStarted(true);
		    
// 			JOptionPane.showMessageDialog(null,
// 				      errorMsg,
// 				      node.getName()+":error",
// 				      JOptionPane.ERROR_MESSAGE,
// 				      new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/stop.png"));
// 			node.stopNetKit(false);
// 			//Esperamos a que la máquina virtual finalice de manera ordenada
// 			sleep(18000);
// 			node.startNetKit();
		}

		}catch(InterruptedException e){;}
	}
	
	private boolean openTelnetConnection ()
	{
		try
		{
			//System.out.println("intentando... " + LOCALHOST + ":" + port);
			TelnetState ts = new TelnetState( new Socket( LOCALHOST, port));
			//System.out.println("probando");
			TelnetProtocol tp = new TelnetProtocol( ts );
			NodeTelnetCommunicator.addConnection(node,tp);
			return true;
		}catch (Exception e)
		{
			//System.out.println(e);
			return false;
		}
	}
} //Fin connectorThread
}
