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



import java.awt.*;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;

import javax.swing.*;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.util.*;

public class DeleteNodeEventHandler extends PBasicInputEventHandler
{
	private LayersHandler handler;
	
	public DeleteNodeEventHandler (LayersHandler lHandler)
	{
		super();
		handler = lHandler;
	}
	
	public void mouseClicked (PInputEvent e)
	{
		super.mouseClicked(e);
		if (e.getButton() == MouseEvent.BUTTON1)
				handler.deleteNode((NKNode)e.getPickedNode());			
	}
	
	public void mouseEntered(PInputEvent e) 
	{
		super.mouseEntered(e);
		if (e.getButton() == MouseEvent.NOBUTTON) 
			selectedAllNodesConnections((NKNode)e.getPickedNode());
	}
	
	public void mouseExited(PInputEvent e)
	{
		super.mouseExited(e);
		if (e.getButton() == MouseEvent.NOBUTTON)
			unselectedAllNodesConnections((NKNode)e.getPickedNode());
	}
	
	private void selectedAllNodesConnections (NKNode n)
	{
		n.setDeleteImage();
		if (n instanceof NKCompaq)
		{
			if (((NKCompaq)n).getEdge() != null)
				((NKCompaq)n).getEdge().setDeletePaint();
		}
		else if (n instanceof NKHub)
			conectionSetToRed(((NKHub)n).getEdges());
		else if (n instanceof NKRouter)
			conectionSetToRed(((NKRouter)n).getEdges());	
	}
	
	private void unselectedAllNodesConnections (NKNode n)
	{
		n.setNormalImage();
		if (n instanceof NKCompaq)
		{
			if (((NKCompaq)n).getEdge() != null)
				((NKCompaq)n).getEdge().setNormalPaint();
		}
		else if (n instanceof NKHub)
			conectionSetToBlack(((NKHub)n).getEdges());
		else if (n instanceof NKRouter)
			conectionSetToBlack(((NKRouter)n).getEdges());
	}
	
	private void conectionSetToRed (ArrayList connections)
	{
		for (int i = 0; i<connections.size(); i++)
			((NKConection)connections.get(i)).setDeletePaint();
	}
	
	private void conectionSetToBlack (ArrayList connections)
	{
		for (int i = 0; i<connections.size(); i++)
			((NKConection)connections.get(i)).setNormalPaint();
	}
	
}
