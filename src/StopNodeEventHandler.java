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

public class StopNodeEventHandler extends PBasicInputEventHandler
{
	private LayersHandler handler;
	
	public StopNodeEventHandler (LayersHandler lHandler)
		{
                    super();
                    handler = lHandler;

		}
	


	public void mouseClicked (PInputEvent e)
		{
			super.mouseClicked(e);

			if (e.getButton() == MouseEvent.BUTTON1) {

				if (e.getPickedNode() instanceof NKSystem)
					if (((NKSystem)e.getPickedNode()).isStarted())
						((NKSystem)e.getPickedNode()).stopNetKit(false);
					//else
					//	((NKSystem)e.getPickedNode()).startNetKit();
			}
		}
			
}
