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

    private void enabledDisabled(PInputEvent e, JMenuItem menuItem) {
        if (!((NKSystem) e.getPickedNode()).isStarted()) {
            menuItem.setEnabled(false);
        }
    }

    private void enabledDisabledRouterOptions(NKRouter router, JMenuItem enable, JMenuItem disable, int tag) {
        if (router.isStarted()) {
            switch (tag) {
                case 0:
                    if (!router.isRunningBGP()) {
                        enable.setEnabled(true);
                        disable.setEnabled(false);
                    } else {
                        enable.setEnabled(false);
                        disable.setEnabled(true);
                    }
                    break;
                case 1:
                    if (!router.isRunningOSPF()) {
                        enable.setEnabled(true);
                        disable.setEnabled(false);
                    } else {
                        enable.setEnabled(false);
                        disable.setEnabled(true);
                    }
                    break;
                case 2:
                    if (!router.isRunningRIP()) {
                        enable.setEnabled(true);
                        disable.setEnabled(false);
                    } else {
                        enable.setEnabled(false);
                        disable.setEnabled(true);
                    }
                    break;
            }
        } else {
            enable.setEnabled(false);
            disable.setEnabled(false);
        }
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
        } else if ((e.getPickedNode() instanceof NKSystem) && (e.getButton() == MouseEvent.BUTTON3)) {
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
            menuItem.addActionListener(new NodeOptionsEventHandler(((NKNode) e.getPickedNode())));
            enabledDisabled(e, menuItem);
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
            menuItem.addActionListener(new AddRouteEventHandler(((NKNode) e.getPickedNode())));
            enabledDisabled(e, menuItem);
            addRoute.add(menuItem);
            menuItem = new JMenuItem("To net");
            menuItem.addActionListener(new AddRouteEventHandler(((NKNode) e.getPickedNode())));
            enabledDisabled(e, menuItem);
            addRoute.add(menuItem);
            menuItem = new JMenuItem("Default");
            menuItem.addActionListener(new AddRouteEventHandler(((NKNode) e.getPickedNode())));
            enabledDisabled(e, menuItem);
            addRoute.add(menuItem);

            route.add(addRoute);
//          -------------------------------------------------------------------------------------

            // Delete route feature: We can choose between deleting a host, net
            // or default route
            JMenu deleteRoute = new JMenu("Delete route");
            menuItem = new JMenuItem("To host");
            menuItem.addActionListener(new DeleteRouteEventHandler(((NKNode) e.getPickedNode())));
            enabledDisabled(e, menuItem);
            deleteRoute.add(menuItem);
            menuItem = new JMenuItem("To net");
            menuItem.addActionListener(new DeleteRouteEventHandler(((NKNode) e.getPickedNode())));
            enabledDisabled(e, menuItem);
            deleteRoute.add(menuItem);
            menuItem = new JMenuItem("Default");
            menuItem.addActionListener(new DeleteRouteEventHandler(((NKNode) e.getPickedNode())));
            enabledDisabled(e, menuItem);
            deleteRoute.add(menuItem);

            route.add(deleteRoute);
//          -------------------------------------------------------------------------------------

            // Show route table feature
            menuItem = new JMenuItem("Show route table");
            menuItem.addActionListener(new NodeOptionsEventHandler(((NKNode) e.getPickedNode())));
            enabledDisabled(e, menuItem);
            route.add(menuItem);

            Pmenu.add(route);

            if (e.getPickedNode() instanceof NKRouter) {
                JMenuItem menuItem1 = new JMenuItem("Enable");
                JMenuItem menuItem2 = new JMenuItem("Disable");
                // Fijar 4 JCheckMenuItems para estos 4 casos y para así poder 
                // manejarlos desde otras clases
                JMenu runProtocol = new JMenu("Protocols");
                // Submenu BGP
                JMenu bgp = new JMenu("BGP");
                // Propiedades de BGP
                menuItem1.addActionListener(new EnableRouterProtocolEventHandler("BGP", ((NKNode) e.getPickedNode())));
                bgp.add(menuItem1);
                menuItem2.addActionListener(new DisableRouterProtocolEventHandler("BGP", ((NKNode) e.getPickedNode())));
                bgp.add(menuItem2);
                runProtocol.add(bgp);
                enabledDisabledRouterOptions((NKRouter) e.getPickedNode(), menuItem1, menuItem2, 0);

                // Submenu OSPF
                JMenu ospf = new JMenu("OSPF");
                // Propiedades de ospf
                menuItem1 = new JMenuItem("Enable");
                menuItem2 = new JMenuItem("Disable");
                menuItem1.addActionListener(new EnableRouterProtocolEventHandler("OSPF", ((NKNode) e.getPickedNode())));
                ospf.add(menuItem1);
                menuItem2.addActionListener(new DisableRouterProtocolEventHandler("OSPF", ((NKNode) e.getPickedNode())));
                ospf.add(menuItem2);
                runProtocol.add(ospf);
                enabledDisabledRouterOptions((NKRouter) e.getPickedNode(), menuItem1, menuItem2, 1);

                // Submenu rip
                JMenu rip = new JMenu("RIP");
                // Propiedades de rip
                menuItem1 = new JMenuItem("Enable");
                menuItem2 = new JMenuItem("Disable");
                menuItem1.addActionListener(new EnableRouterProtocolEventHandler("RIP", ((NKNode) e.getPickedNode())));
                rip.add(menuItem1);
                menuItem2.addActionListener(new DisableRouterProtocolEventHandler("RIP", ((NKNode) e.getPickedNode())));
                rip.add(menuItem2);
                runProtocol.add(rip);
                enabledDisabledRouterOptions((NKRouter) e.getPickedNode(), menuItem1, menuItem2, 2);

                Pmenu.add(runProtocol);
            }

            Pmenu.show(canvas, (int) e.getCanvasPosition().getX(), (int) e.getCanvasPosition().getY());

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
        if (e.getPickedNode() instanceof NKSystem) {
            ((NKSystem) e.getPickedNode()).setPos(e.getPosition());
        }
    }
}
