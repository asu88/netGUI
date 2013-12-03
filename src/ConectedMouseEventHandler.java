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
import edu.umd.cs.piccolo.nodes.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class ConectedMouseEventHandler extends PBasicInputEventHandler {

    private LayersHandler handler;
    private PCanvas canvas;
    private boolean iconClicked = false;
    private boolean block = false;
    private PPath line;
    private NKNode node1, node2;

    public ConectedMouseEventHandler(PCanvas aCanvas, LayersHandler lHandler) {
        super();
        canvas = aCanvas;
        handler = lHandler;
    }

    @Override
    public void mouseClicked(PInputEvent e) {
        super.mouseClicked(e);
        if (e.getButton() == MouseEvent.BUTTON1) {
            processClick(e);
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            cancelCurrentConexion();
        }
    }

    @Override
    public void mouseMoved(PInputEvent e) {
        super.mouseMoved(e);
        if (iconClicked && !block) {
            updateLine(e);
        }
    }

    @Override
    public void mouseEntered(PInputEvent e) {
        super.mouseEntered(e);
        //canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        if (e.getPickedNode() instanceof NKNode) {
            if (iconClicked) {
                processCurrentSituation(e, false);
            } else {
                ((NKNode) e.getPickedNode()).setSelectedImage();
            }
        }

    }

    @Override
    public void mouseExited(PInputEvent e) {
        super.mouseExited(e);
        if ((!iconClicked) && (e.getPickedNode() instanceof NKNode)) {
            ((NKNode) e.getPickedNode()).setNormalImage();
        }
        if ((iconClicked) && (e.getPickedNode() instanceof NKNode)) {
            if (!node1.equals((NKNode) e.getPickedNode())) {
                ((NKNode) e.getPickedNode()).setNormalImage();
                updateLine(e);
                block = false;
            }
        }
    }

    public void cancelCurrentConexion() {
        if (iconClicked) {
            canvas.getLayer().removeChild(line);
            node1.setNormalImage();
            line = null;
            iconClicked = false;
            block = false;
        }
    }

    private void updateLine(PInputEvent aEvent) {
        Point2D start = node1.getFullBoundsReference().getCenter2D();
        Point2D end = aEvent.getPosition();
        line.reset();
        line.moveTo((float) start.getX(), (float) start.getY());
        line.lineTo((float) end.getX(), (float) end.getY());
    }

    private void updateNodeLine(PInputEvent aEvent) {
        Point2D start = node1.getFullBoundsReference().getCenter2D();
        Point2D end = ((NKNode) aEvent.getPickedNode()).getFullBoundsReference().getCenter2D();
        line.reset();
        line.moveTo((float) start.getX(), (float) start.getY());
        line.lineTo((float) end.getX(), (float) end.getY());
    }

    private void firstClickSate(PInputEvent event) {
        node1 = (NKNode) event.getPickedNode();
        iconClicked = true;
        line = new PPath();
        updateLine(event);
        canvas.getLayer().addChild(line);
    }

    private void secondClickSate(PInputEvent event) {
        node2 = (NKNode) event.getPickedNode();
        canvas.getLayer().removeChild(line);
        line = null;
        iconClicked = false;
        block = false;
    }

    private void processClick(PInputEvent aEvent) {
        if (aEvent.getPickedNode() instanceof NKNode) {
            if (!iconClicked) //Primer click
            {
                firstClickSate(aEvent);
            } else //segundo click (creamos una conexion) 
            {
                processCurrentSituation(aEvent, true);
            }
        }
    }

    private void processConnectNormalSituation(PInputEvent aEvent, boolean click) {
        // sólo se permite conexión de un terminal a un hub o un switch
        if ((aEvent.getPickedNode() instanceof NKHub) || (aEvent.getPickedNode() instanceof NKSwitch)) {
            if (!click) {
                ((NKNode) aEvent.getPickedNode()).setSelectedImage();
                blockLine(aEvent);
            } else {
                secondClickSate(aEvent);
                handler.addConnection(node1, node2);
            }
        }
    }

    private void processRoutersConnectSituation(PInputEvent aEvent, boolean click) {
        // conexion desde un router: solo se permite a hub, router y switch
        if (aEvent.getPickedNode() instanceof NKRouter) {
            if (!click) {
                ((NKNode) aEvent.getPickedNode()).setSelectedImage();
                blockLine(aEvent);
            } else {
                secondClickSate(aEvent);
                handler.addConnection(node1, node2);
            }
        } else {
            processConnectNormalSituation(aEvent, click);
        }
    }

    private void processCurrentSituation(PInputEvent aEvent, boolean click) {
        if (!node1.equals((NKNode) aEvent.getPickedNode())) //El nodo seleccionado es distinto del que se ha "pinchado" anteriormente
        {
            if (node1 instanceof NKCompaq) //Es un terminal, en tal caso s�o se permite su conexion a un hub y switch
            {
                processConnectNormalSituation(aEvent, click);
            }
        }
        if (node1 instanceof NKRouter) //Los routers pueden conectarse tanto a hubs como a otros routers y switch
        {
            processRoutersConnectSituation(aEvent, click);
        } else if (node1 instanceof NKHub) {
            //El elemento seleccionado anteriormente es un Hub, en tal caso s�o se
            //permite su conexion a un router, switch o a un terminal
            if ((aEvent.getPickedNode() instanceof NKRouter)
                    || (aEvent.getPickedNode() instanceof NKCompaq)
                    || (aEvent.getPickedNode() instanceof NKSwitch)) {
                if (!click) {
                    blockLine(aEvent);
                    ((NKNode) aEvent.getPickedNode()).setSelectedImage();
                } else {
                    secondClickSate(aEvent);
                    handler.addConnection(node1, node2);
                }
            }
        } else if (node1 instanceof NKSwitch) {
            //permite su conexion con todo
            if ((aEvent.getPickedNode() instanceof NKRouter)
                    || (aEvent.getPickedNode() instanceof NKCompaq)
                    || (aEvent.getPickedNode() instanceof NKHub)
                    || (aEvent.getPickedNode() instanceof NKSwitch)) {
                if (!click) {
                    blockLine(aEvent);
                    ((NKNode) aEvent.getPickedNode()).setSelectedImage();
                } else {
                    secondClickSate(aEvent);
                    handler.addConnection(node1, node2);
                }
            }
        }

    }

    private void blockLine(PInputEvent aEvent) {
        block = true;
        updateNodeLine(aEvent);
    }
}
