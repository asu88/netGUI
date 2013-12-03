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

import java.util.*; 
import java.lang.*;
import java.net.Socket;

public class TelnetSocket 
{

	private final static String CRLF = "\r\n";
	
	NKSystem node;
	TelnetProtocol tp;

	public TelnetSocket (NKSystem node, TelnetProtocol tp)
	{
		this.node=node;
		this.tp = tp;
	}
	
	/*******************************************
	 * Envia un comando a través de la conexión
	 *******************************************/
	public boolean send(String toSend )
  	{
		try
		{
    			tp.writeString( toSend + CRLF);
			return true;
		}catch(Exception e){return false;}
  	}
	
	/**********************************************************
	 * Obtiene el resultado proporcionado por la ejecución del
	 * comando, si no se envió ninguno se quedará bloqueado
	 * esperando a leer el inductor de shell.
	 **********************************************************/
	public String getOutputResponse()
	{
		String aux ="",text = null, TOEXPECT = node.getName() +":~#";
		try
		{
			do
			{
				text = tp.readText();
				if (text != null)
					aux += text;
			}
			while ( text.indexOf( TOEXPECT ) == -1 );
			return aux;
		}catch(Exception e){return null;}
	}
}
