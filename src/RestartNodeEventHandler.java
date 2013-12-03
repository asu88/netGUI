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


import java.io.*;

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

public class RestartNodeEventHandler extends PBasicInputEventHandler
{
	private LayersHandler handler;
	
	public RestartNodeEventHandler (LayersHandler lHandler)
		{
                    super();
                    handler = lHandler;

		}
	


	public void mouseClicked (PInputEvent e)
		{
			//Process proc;
                        String cmd="";
			NKSystem device;
			super.mouseClicked(e);

			if (e.getButton() == MouseEvent.BUTTON1) {
				if (e.getPickedNode() instanceof NKSystem) {

                                        device = (NKSystem)e.getPickedNode();

                                        try {
                                            //cmd = "vhalt " + device.getName();
					    //Runtime.getRuntime().exec(cmd, null);

                                            cmd = "clean-vm.sh " + device.getName();
					    Runtime.getRuntime().exec(cmd, null);

					    device.setStarted(false);
					    device.updateNormalImages();


                                            // Hay que esperar para que no se mate la nueva
					    // instancia de la maquina que se esta arrancando
                                            System.out.println("Aborting r1");
                                            System.out.println("Waiting 5s for restart "
                                                               +  device.getName() + " ...");
					    Thread.sleep(5000);


                                            System.out.println("Restarting " + device.getName());
					    device.startNetKit();


					} catch (Exception ex)
                                		{System.out.println("Error " + ex);}
				}
			}
		}
			
}
