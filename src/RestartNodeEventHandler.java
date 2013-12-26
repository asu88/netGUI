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

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.*;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

public class RestartNodeEventHandler extends PBasicInputEventHandler {

    private PCanvas canvas;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Point hotSpot = new Point(0,0);
    // Imagenes de mano abierta y cerrada
    Image restart = toolkit.getImage(System.getProperty("NETLAB_HOME") + "/images/32x32/restart_cursor.png");    
    Cursor restartCursor = toolkit.createCustomCursor(restart, hotSpot, "run");
    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    public RestartNodeEventHandler(PCanvas canvas) {
        super();
        this.canvas = canvas;
    }

    @Override
    public void mouseClicked(PInputEvent e) {
        //Process proc;
        String cmd = "";
        NKSystem device;
        super.mouseClicked(e);

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (e.getPickedNode() instanceof NKSystem) {
                device = (NKSystem) e.getPickedNode();
                try {
                    cmd = "clean-vm.sh " + device.getName();
                    Runtime.getRuntime().exec(cmd, null);

                    device.setStarted(false);
                    device.updateNormalImages();
                    RestartNodeThread restart = new RestartNodeThread(cmd, device);
                    restart.start();

                } catch (Exception ex) {
                    System.out.println("Error " + ex);
                }
            }
        }
    }
    
    @Override
    public void mouseEntered(PInputEvent e) {
        super.mouseEntered(e);
        //canvas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (e.getButton() == MouseEvent.NOBUTTON) {
            if (e.getPickedNode() instanceof NKSystem) {
                ((NKSystem) e.getPickedNode()).setSelectedImage();
                canvas.setCursor(restartCursor);
            }
        }
    }

    @Override
    public void mouseExited(PInputEvent e) {
        super.mouseExited(e);
        if (e.getButton() == MouseEvent.NOBUTTON) {
            if (e.getPickedNode() instanceof NKSystem) {
                ((NKSystem) e.getPickedNode()).setNormalImage();
                canvas.setCursor(defaultCursor);
            }
        }
    }
}
