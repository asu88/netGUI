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

import javax.swing.*;
import java.util.*;
import java.io.*;

public class NKProjectWriter 
{
	private final String extensionNKP = ".nkp";
	private final String beginNodeSection = "<nodes>";
	private final String beginLink = "<link>";
	private final String beginLinkSection = "<connections>";
	private final String endNodeSection = "<\\nodes>";
	private final String endLink = "<\\link>";
	private final String endLinkSection = "<\\connections>";
	private PLayer nLayer, eLayer;
	
	public NKProjectWriter (PLayer nodeLayer, PLayer edgeLayer)
	{
		nLayer = nodeLayer;
		eLayer = edgeLayer;
	}
	
	public void save (String fileName)
	{
		String contenido;
		contenido = getNodes();
		contenido += getEdges();
		try{
			writeFile(setExtensionNKP(fileName),contenido);
		}catch(IOException e){System.out.println("Error en la grabación");}
	}
	
	private String getNodes ()
	{
		Collection cNodes = nLayer.getAllNodes();
		Iterator i = cNodes.iterator();
		String s =beginNodeSection + "\n";
		while (i.hasNext())
		{
			Object o = i.next();
			if (o instanceof NKCompaq)
				s += "position("+
					((NKNode)o).getFullBounds().getCenter2D().getX()+"," +
					((NKNode)o).getFullBounds().getCenter2D().getY()+
					"); NKCompaq(\""+((NKNode)o).getName()+"\")\n";
			else if (o instanceof NKRouter)
				s += "position("+
					((NKNode)o).getFullBounds().getCenter2D().getX()+"," +
					((NKNode)o).getFullBounds().getCenter2D().getY()+
					"); NKRouter(\""+((NKNode)o).getName()+"\")\n";
			else if (o instanceof NKSwitch)
				s += "position("+
					((NKNode)o).getFullBounds().getCenter2D().getX()+"," +
					((NKNode)o).getFullBounds().getCenter2D().getY()+
					"); NKSwitch(\""+((NKNode)o).getName()+"\")\n";
			else if (o instanceof NKHub)
				s += "position("+
					((NKNode)o).getFullBounds().getCenter2D().getX()+"," +
					((NKNode)o).getFullBounds().getCenter2D().getY()+
					"); NKHub(\""+((NKNode)o).getName()+"\")\n";
		}
		s += endNodeSection+ "\n";
		return s;
	}
	
	private String getEdges ()
	{
		Collection eEdges = eLayer.getAllNodes();
		Iterator i = eEdges.iterator();
		String s = beginLinkSection+ "\n";
		while (i.hasNext())
		{
			Object o = i.next();
			if (o instanceof NKConection)
			{
				s += beginLink + "\n"; 
				s += "Connect(\"" +
					((NKConection)o).getNode1().getName()+ "\")\n" +
					"To(\"" + ((NKConection)o).getNode2().getName() + "\")\n";
				s += endLink + "\n";
			}
		}
		
		s += endLinkSection+ "\n";
		return s;
	}
	
	private void writeFile (String fileName, String contenido)
		throws IOException 
	{
		BufferedWriter output =
			new BufferedWriter (
				new FileWriter (fileName));
		output.write(contenido,0,contenido.length());
		output.close();
	}
	
	private String setExtensionNKP (String fileName)
	{
		if (!fileName.endsWith(extensionNKP))
			return fileName+extensionNKP;
		else return fileName;
	}
}
