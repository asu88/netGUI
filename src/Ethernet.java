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



/*

**********************************************************************
* Clase que representa la interfaz ethernet de conexión utilizada por
* un maquina en la red
*********************************************************************

*/

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

import java.awt.geom.*;
import java.awt.*;
import java.awt.geom.Point2D.*;

class Ethernet extends PPath {
	
        public static final long serialVersionUID = 1L;
	private static final int OFFSET = 5;
	private PText ethName;
	private PText ip;

	public Ethernet (String ethName)
	{
		super();
		this.ethName = new PText(ethName);
		this.ip = new PText();
		addChild(this.ethName);
		addChild(this.ip);
		setChildrenPickable(false);
		formatIpText();
		formatEthText();
		this.ethName.centerFullBoundsOnPoint(getBounds().getCenter2D().getX(),
						getBounds().getCenter2D().getY());
	}

	public Ethernet (String etherName, String ip)
	{
		super();
		this.ethName = new PText(etherName);
		this.ip = new PText(ip);
		addChild(this.ethName);
		addChild(this.ip);
		setChildrenPickable(false);
		formatIpText();
		formatEthText();
		updateLocation();
	}
	
	public PText getEthName ()
	{
		return ethName;
	}

	public void setIp (String newIp)
	{
		ip.setText(newIp);
		updateLocation();
	}

	private void updateLocation () 
	{
		Point2D pd = getBounds().getCenter2D();
		if (ip.getText()==null)
			ethName.centerFullBoundsOnPoint(pd.getX(),pd.getY());
		else
		{
			ethName.centerFullBoundsOnPoint(pd.getX(),pd.getY()-OFFSET);
			ip.centerFullBoundsOnPoint(pd.getX(),pd.getY()+OFFSET);
		}
	}

	private void formatIpText()
	{
		ip.setFont(new Font ("Dialog",Font.BOLD,14));
		ip.setTextPaint(new Color(0, 0, 210));
	}

	
	private void formatEthText()
	{
		ethName.setScale((float)1.0);
		ethName.setFont(new Font ("Dialog",Font.BOLD,12));
		ethName.setTextPaint(new Color (0, 100, 80));
	}

}
