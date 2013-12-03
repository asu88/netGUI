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


/*********************************************************************
 * Delimita un nodo a través de las rectas x1,x2,y1,y2
 *
 *		   y2
 *	     _______________ 
 *	    |		    |
 *	    |		    |
 *	x2  |    Center     | x1
 *	    |		    |
 *	    |_______________|  	
 *		   y1
 *
 *
 *********************************************************************/
public class RectangleNodeDelimiter
{
	double x1, x2, y1, y2;
	public RectangleNodeDelimiter (double x1, double x2, double y1, double y2)
	{
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}
	
	double getX1 (){return x1;}
	double getX2 (){return x2;}
	double getY1 (){return y1;}
	double getY2 (){return y2;}
}
