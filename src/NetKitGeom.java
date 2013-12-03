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

import java.awt.geom.*;
import java.awt.geom.Point2D.*;

public class NetKitGeom
{			 
	public static Point2D getGlobalIntersect (RectangleNodeDelimiter r,
		 NKNode nodoOrigen, NKConection line)
	// ecuation rect: y = ax+b (b=0); a = d1/d2
	{
		Point2D pg1,p1 = line.getNode1().getFullBoundsReference().getCenter2D();
		Point2D pg2,p2 = line.getNode2().getFullBoundsReference().getCenter2D();
		Point2D og,origen = nodoOrigen.getFullBoundsReference().getCenter2D();
		double xs,ys,m;
				
		//los pasamos al sistema de coordenadas global
		pg1 = line.localToGlobal(p1);
		pg2 = line.localToGlobal(p2);
		og = nodoOrigen.localToGlobal(origen);
		
		if (nodoOrigen.equals(line.getNode1()))
		{
			m = getPendient(p1,p2);
			Point2D pSol = checkLimits(m,pg1,pg2,r);
			if (pSol!=null)
			{
				xs = pSol.getX(); ys = pSol.getY();
				return new Point2D.Double(xs,ys);
			}
		} 
		else
		{
			m = getPendient(p2,p1);
			Point2D pSol = checkLimits(m,pg2,pg1,r);
			if (pSol!=null)
			{
				xs = pSol.getX(); ys = pSol.getY();
				return new Point2D.Double(xs,ys);
			}
		}
		return null;
	}
	
	private static double getPendient (Point2D start, Point2D end)
	{
		
		return (end.getY()-start.getY())/(end.getX()-start.getX());
	}
	
	/***********************************************************
	 * El punto de intersección con la recta y = mx +b 
	 ***********************************************************/
	private static double getIntersectionY (double x,double m)
	{
		return m*x;
	}
	
	/***********************************************************
	 * El punto de intersección con la recta y = mx +b que pasa
	 * el origen de coordenadas
	 ***********************************************************/
	private static double getIntersectionX (double y,double m)
	{
		return y/m;
	}
	
	private static Point2D checkLimits (double m, Point2D origen, Point2D fin,
		RectangleNodeDelimiter r)
	{
		double ys, xs;
		//solucion 1:
		ys = getIntersectionY (r.getX1(),m);
		if ((fin.getX()>=r.getX1()+origen.getX())
			&& checkLimitY(ys,r))
			return new Point2D.Double (r.getX1()+origen.getX(),ys+origen.getY());
		
		//solucion 2:
		ys = getIntersectionY (r.getX2(),m);
		if ((fin.getX()<=r.getX2()+origen.getX())
			&& checkLimitY(ys,r))
			return new Point2D.Double (r.getX2()+origen.getX(),ys+origen.getY());
		
		//solucion 3:
		xs = getIntersectionX (r.getY1(),m);
		if ((fin.getY()>=r.getY1()+origen.getY())
			&& checkLimitX(xs,r))
			return new Point2D.Double (xs+origen.getX(),r.getY1()+origen.getY());
		
		//solucion 4:
		xs = getIntersectionX (r.getY2(),m);
		if ((fin.getY()<=r.getY2()+origen.getY())
			&& checkLimitX(xs,r))
			return new Point2D.Double (xs+origen.getX(),r.getY2()+origen.getY());
			
		//el fin está dentro del rectángilo definido para el icono
		//=>Posicionaremos la interfaz en el final de la recta
		return new Point2D.Double (fin.getX(),fin.getY());
	}
	
	private static boolean checkLimitY (double y, RectangleNodeDelimiter r)
	{
		return ((r.getY1()>=y) && (r.getY2()<=y));
	}
	
	private static boolean checkLimitX (double x, RectangleNodeDelimiter r)
	{
		return ((r.getX1()>=x) && (r.getX2()<=x));
	}
}
