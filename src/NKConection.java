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

import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class NKConection extends PPath {

    public static final long serialVersionUID = 1L;
    private NKNode node1;
    private NKNode node2;

    public NKConection(NKNode node1, NKNode node2) {
        this.node1 = node1;
        this.node2 = node2;

    }

    /**
     * ********************************************************
     * Devuelve true si intervienen los mismos nodos en la conexion
	 *********************************************************
     */
    @Override
    public boolean equals(Object nk) {
        if (nk instanceof NKConection) {
            NKConection nk1 = (NKConection) nk;
            return ((node1.equals(nk1.getNode1())) && (node2.equals(nk1.getNode2())))
                    || ((node1.equals(nk1.getNode2())) && (node2.equals(nk1.getNode1())));
        } else {
            return false;
        }
    }

    public NKNode getNode1() {
        return node1;
    }

    public NKNode getNode2() {
        return node2;
    }

    public void setDeletePaint() {
        setStrokePaint(Color.RED);
    }

    public void setNormalPaint() {
        setStrokePaint(Color.BLACK);
    }

    public void updateEdge() {
        // Note that the node's "FullBounds" must be used (instead of just the "Bound") 
        // because the nodes have non-identity transforms which must be included when
        // determining their position.

        //PNode node1 = (PNode) ((ArrayList)edge.getClientProperty("nodes")).get(0);
        //PNode node2 = (PNode) ((ArrayList)edge.getClientProperty("nodes")).get(1);
        Point2D start = node1.getFullBoundsReference().getCenter2D();
        Point2D end = node2.getFullBoundsReference().getCenter2D();
        reset();
        moveTo((float) start.getX(), (float) start.getY());
        lineTo((float) end.getX(), (float) end.getY());
        updateInterfaces();
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        super.paint(paintContext);
    }

    private void updateInterfaces() {
        if (node1 instanceof NKSystem) {
            ((NKSystem) node1).updateEthLocation(this);
        }
        if (node2 instanceof NKSystem) {
            ((NKSystem) node2).updateEthLocation(this);
        }
    }
}
