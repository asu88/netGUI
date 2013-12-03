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
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class NormalDragEventHandler extends PDragEventHandler {

    private PCanvas canvas;

    public NormalDragEventHandler(PCanvas aCanvas) {
        PInputEventFilter filter = new PInputEventFilter();
        filter.setOrMask(InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK);
        canvas = aCanvas;
        setEventFilter(filter);
    }

    @Override
    public void mouseClicked(PInputEvent e) {
        super.mouseClicked(e);

        if (e.getButton() == MouseEvent.BUTTON2) {

            if (e.getPickedNode() instanceof NKSystem) {
                if (((NKSystem) e.getPickedNode()).isStarted()) {
                    ((NKSystem) e.getPickedNode()).stopNetKit(false);
                } else {
                    ((NKSystem) e.getPickedNode()).startNetKit();
                }
            }
        } else if (e.getButton() == MouseEvent.BUTTON3){
            // Create a new popup menu
            JPopupMenu Pmenu;
            JMenuItem menuItem;
            Pmenu = new JPopupMenu();
            
            // Introduce the node's name as title
            
            Pmenu.add(new JLabel("  " + ((NKSystem) e.getPickedNode()).getName()));
            Pmenu.addSeparator();
            
            // Create and include a new option in popup that will be enabled
            // only if the node is started: Change IP
            menuItem = new JMenuItem("Change IP");
            menuItem.addActionListener(new NodeOptionsEventHandler(((NKNode)e.getPickedNode())));
            if (!((NKSystem)e.getPickedNode()).isStarted()){
                menuItem.setEnabled(false);
            }
            Pmenu.add(menuItem);
            
            // Create a new menu for route options
            JMenu route;
            route = new JMenu("Routes");
            
            // Create and include new options in routes submenu that will be
            // enabled only when the node is started
            
            // Add route feature menu: We can choose between adding a host, net
            // or default route
            JMenu addRoute = new JMenu("Add route");
            menuItem = new JMenuItem("To host");
            menuItem.addActionListener(new AddRouteEventHandler(((NKNode)e.getPickedNode())));
            addRoute.add(menuItem);
            menuItem = new JMenuItem("To net");
            menuItem.addActionListener(new AddRouteEventHandler(((NKNode)e.getPickedNode())));
            addRoute.add(menuItem);
            menuItem = new JMenuItem("Default");
            menuItem.addActionListener(new AddRouteEventHandler(((NKNode)e.getPickedNode())));
            addRoute.add(menuItem);

            route.add(addRoute);
//          -------------------------------------------------------------------------------------
            
            // Delete route feature: We can choose between deleting a host, net
            // or default route
            JMenu deleteRoute = new JMenu("Delete route");
            menuItem = new JMenuItem("To host");
            menuItem.addActionListener(new DeleteRouteEventHandler(((NKNode)e.getPickedNode())));
            deleteRoute.add(menuItem);
            menuItem = new JMenuItem("To net");
            menuItem.addActionListener(new DeleteRouteEventHandler(((NKNode)e.getPickedNode())));
            deleteRoute.add(menuItem);
            menuItem = new JMenuItem("Default");
            menuItem.addActionListener(new DeleteRouteEventHandler(((NKNode)e.getPickedNode())));
            deleteRoute.add(menuItem);

            route.add(deleteRoute);
//          -------------------------------------------------------------------------------------
            
            // Show route table feature
            menuItem = new JMenuItem("Show route table");
            menuItem.addActionListener(new NodeOptionsEventHandler(((NKNode)e.getPickedNode())));
            if (!((NKSystem)e.getPickedNode()).isStarted()){
                menuItem.setEnabled(false);
            }
            route.add(menuItem);
            
//            Pmenu.add(menuItem);
            // Add route menu to the popup and configure the position in screen
            Pmenu.add(route);
            Pmenu.show(((Component)e.getComponent()), (int)e.getPosition().getX(), (int)e.getPosition().getY());
//            Pmenu.show(((Component)e.getComponent()), ((Component)e.getComponent()).getX(), ((Component)e.getComponent()).getY());
//            System.out.println((int)e.getPosition().getX() + "," + (int)e.getPosition().getY());
            
        } else if (e.getButton() == MouseEvent.BUTTON1) {
            if (e.getPickedNode() instanceof NKSystem) {
                if (((NKSystem) e.getPickedNode()).isStarted()) {
                    if (e.getClickCount() == 2) {
                        try {
                            Process proc;
                            proc = Runtime.getRuntime().exec("xwit "
                                    + "-focus -pop -warp 10 20 -names "
                                    + ((NKSystem) e.getPickedNode()).getName(), null);
                        } catch (Exception ex) {
                            System.out.println("Error " + ex);
                        }

                    } else {
                        ((NKSystem) e.getPickedNode()).startNetKit();
                    }
                }

            }
        }
    }

    @Override
    public void mouseEntered(PInputEvent e) {
        super.mouseEntered(e);
        //canvas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (e.getButton() == MouseEvent.NOBUTTON) {
            if (e.getPickedNode() instanceof NKNode) {
                ((NKNode) e.getPickedNode()).setSelectedImage();
            }
        }
    }

    @Override
    public void mouseExited(PInputEvent e) {
        super.mouseExited(e);
        if (e.getButton() == MouseEvent.NOBUTTON) {
            if (e.getPickedNode() instanceof NKNode) {
                ((NKNode) e.getPickedNode()).setNormalImage();
            }
        }
    }

    @Override
    protected void startDrag(PInputEvent e) {
        super.startDrag(e);
        e.setHandled(true);
        e.getPickedNode().moveToFront();
        //Se ha producido un cambio en el estado de la aplicaci�n:
        UtilNetGUI.setAplicationStatusChange(true);
    }

    @Override
    protected void drag(PInputEvent e) {
        super.drag(e);
        NKNode pNode = (NKNode) e.getPickedNode();
        pNode.updateEdges();
    }
}
