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



import java.util.*; 
import java.lang.*;
import java.net.Socket;

public class NodeTelnetCommunicator
{
	//asocia a cada nodo un conexión TCP a través de telnet para comunicarse con él
        private static HashMap<NKSystem,TelnetProtocol> openConnections = new HashMap<NKSystem,TelnetProtocol>();
	private static Random portFactory = new Random();
	
	public synchronized static void addConnection (NKSystem node, TelnetProtocol tp)
	{
		openConnections.put(node,tp);
	}

	public synchronized static boolean removeConnection (NKSystem node)
	{	
		if (!openConnections.containsKey(node))
			return false;
		try
		{
			(openConnections.get(node)).close();
			openConnections.remove(node);
			return true;
		}catch(Exception e){return false;}
	}
	
	public synchronized static TelnetSocket getTelnetSocket (NKSystem node)
	{
		if (!openConnections.containsKey(node))
			return null;
		TelnetProtocol tp = (openConnections.get(node));
		return new TelnetSocket(node, tp);
	}

	public synchronized static void print ()
	{
		System.out.println(openConnections);
	}
	public synchronized static int getNewPort ()
	{
		int port;
		boolean exit = false;
		do
		{ 
			port = Math.abs(portFactory.nextInt(9999));
			if (port <= 1000)
				port += 1001;
			exit = portIsUsed(port);
			
		}while (exit);
		
		return port;
	}
	
	private synchronized static boolean portIsUsed (int port)
	{
		boolean isUsed = false;
		if (openConnections.size() != 0)
		{
			Iterator i = openConnections.values().iterator();
			while ((!isUsed)&&(i.hasNext()))
				isUsed = (port == ((TelnetProtocol)i.next()).getConnectedPort ());			
		}
		return isUsed;
	}

}//Fin NodeTelnetCommunicator

