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
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

import java.awt.geom.Point2D;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class NKProjectReader 
{
	private final String beginNodeSection = "<nodes>";
	private final String beginLink = "<link>";
	private final String beginLinkSection = "<connections>";
	private final String endNodeSection = "<\\nodes>";
	private final String endLink = "<\\link>";
	private final String endLinkSection = "<\\connections>";
	private LayersHandler handler;
	
	public NKProjectReader (LayersHandler lHandler)
	{
		handler = lHandler;
	}
	
	public void load (String fileName)
	{		
		try{
			loadFile(fileName);
		}catch(IOException e){System.out.println("Error en la grabación");}
	}
	
	private void loadFile (String fileName)
		throws IOException 
	{
		String line;
		BufferedReader input =
			new BufferedReader (
				new FileReader (fileName));
		while ((line = input.readLine()) != null)
		{
			if (line.equalsIgnoreCase(beginNodeSection))
				processNodes(input);
			if (line.equalsIgnoreCase(beginLinkSection))
				processConnections(input);
		}
		input.close();
	}
	
	private void processNodes (BufferedReader input)
	{
		try {
			String aux,line = input.readLine();
			Point2D p;
			NKNode n;
			StringTokenizer st;
			while ((line != null) && (!line.equalsIgnoreCase(endNodeSection)))
			{
				st = new StringTokenizer(line,";");
				aux = st.nextToken();
				p = getLocation(aux);
				n = getNode(line.substring(aux.length()+2,line.length()));
				if ((p != null) && (n != null))
					insertNodeAtPosition(n,p);
				else System.out.println("Error");
				line = input.readLine();
			}
			
			if (line == null) System.out.println("Error");
			
		}catch(IOException e){System.out.println(e);}
	}
	
	private void processConnections(BufferedReader input)
	{
		try {
			String aux,line = input.readLine();
			while ((line != null) && (!line.equalsIgnoreCase(endLinkSection)))
			{
				if (line.equals(beginLink))
					processLink(input);
				line = input.readLine();
			}
			
			if (line == null) System.out.println("Error");
		} catch (IOException e){System.out.println(e);}
	}
	
	private void processLink (BufferedReader input)
	{
		NKNode n1 = new NKHub(null,null), n2=new NKHub(null,null);
		boolean error=false;
		try {
			String aux,line = input.readLine();
			while ((line != null) && (!line.equalsIgnoreCase(endLink)))
			{
				if (line.startsWith("Connect(\""))
					n1 = getNodeName(line, "Connect(\"");
				else if (line.startsWith("To(\""))
					n2 = getNodeName(line, "To(\"");
				else
					{
					error = true; 
					break;
					}
				line = input.readLine();
			}
			
			if ((line != null) || (!error))
					addConnection(n1,n2);
		} catch (IOException e){System.out.println(e);}
	}
	
	private Point2D getLocation (String input)
	{
		String init = "position(";
		String xs,ys;
		if ((input.startsWith(init)) && (input.endsWith(")")))
			{
			if (input.indexOf(',') != -1){
				xs=input.substring(init.length(),input.indexOf(','));
				ys=input.substring(input.indexOf(',')+1,input.length()-1);
				return getLocation(xs,ys);
				}
			else return null;
			}
		else return null;
	}
	
	private NKNode getNode (String input)
	{
		String type, name;
		if ((input.indexOf("(")!=-1) && input.endsWith(")"))
		{
			type = input.substring(0,input.indexOf("("));
			name = input.substring(input.indexOf("(")+2,input.length()-2);
			return nodeType(type, name);
		}
		else return null;
	}
	
	private NKNode nodeType (String nType, String name)
	{
		if (nType.equalsIgnoreCase("NKCompaq"))
			return new NKCompaq(name, handler);
		else if (nType.equalsIgnoreCase("NKRouter"))
			return new NKRouter(name, handler);
		else if (nType.equalsIgnoreCase("NKSwitch"))
			return new NKSwitch(name, handler);
		else if (nType.equalsIgnoreCase("NKHub"))
			return new NKHub(name,null);
		else return null;
	}
	
	private Point2D getLocation (String xs, String ys)
	{
		try{
			return new Point2D.Double(Double.parseDouble(xs), Double.parseDouble(ys));
		}catch(NumberFormatException e){
			//Error: Devolvemos null
			return null;}
	}
	
	private NKNode getNodeName(String line, String beginLine)
	{
		String nodeName="", s = beginLine;
		if (line.endsWith("\")"))
		{
			nodeName = line.substring(s.length(),line.length()-2);
			return handler.searchNode(nodeName);
		}
		else return null;
	}
	
	private void addConnection (NKNode n1, NKNode n2)
	{
		handler.addConnection(n1,n2);
	}
	
	private void insertNodeAtPosition(NKNode n, Point2D p)
	{
		handler.addNewNode(p,n);
	}
}
