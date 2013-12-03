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

public class NormalDragEventHandler extends PDragEventHandler
{
	private PCanvas canvas;
	
	public NormalDragEventHandler (PCanvas aCanvas)
		{
			PInputEventFilter filter = new PInputEventFilter();
			filter.setOrMask(InputEvent.BUTTON1_MASK | InputEvent.BUTTON3_MASK);
			canvas = aCanvas;
			setEventFilter(filter);
		}
	
	public void mouseClicked (PInputEvent e)
		{
			super.mouseClicked(e);

			if (e.getButton() == MouseEvent.BUTTON3) {

				if (e.getPickedNode() instanceof NKSystem)
					if (((NKSystem)e.getPickedNode()).isStarted())
						((NKSystem)e.getPickedNode()).stopNetKit(false);
					else
						((NKSystem)e.getPickedNode()).startNetKit();
			}
			else if (e.getButton() == MouseEvent.BUTTON1) { 
			    if (e.getPickedNode() instanceof NKSystem){
				if (((NKSystem)e.getPickedNode()).isStarted())
				    if(e.getClickCount() == 2) { 
					try {
					    Process proc;
					    proc = Runtime.getRuntime().exec
						("xwit "+
						 "-focus -pop -warp 10 20 -names " + 
						 ((NKSystem)e.getPickedNode()).getName(), null);
					} 
					catch (Exception ex){
					    System.out.println("Error " + ex);
					}
					
				    }
				    else
					((NKSystem)e.getPickedNode()).startNetKit();
				
			    }
			}
		}
			
	public void mouseEntered(PInputEvent e) 
		{
			super.mouseEntered(e);
			//canvas.setCursor(new Cursor(Cursor.HAND_CURSOR));
			if (e.getButton() == MouseEvent.NOBUTTON) {
				if (e.getPickedNode() instanceof NKNode)
					((NKNode)e.getPickedNode()).setSelectedImage();
			}
		}
			
	public void mouseExited(PInputEvent e)
		{
			super.mouseExited(e);
			if (e.getButton() == MouseEvent.NOBUTTON) {
				if (e.getPickedNode() instanceof NKNode)
					((NKNode)e.getPickedNode()).setNormalImage();
			}
		}
						
	protected void startDrag(PInputEvent e) 
		{
			super.startDrag(e);
			e.setHandled(true);
			e.getPickedNode().moveToFront();
			//Se ha producido un cambio en el estado de la aplicación:
			UtilNetGUI.setAplicationStatusChange(true);
		}
			
	protected void drag(PInputEvent e) 
		{
			super.drag(e);
			NKNode pNode = (NKNode)e.getPickedNode();
			pNode.updateEdges();
		}
		
	
}
