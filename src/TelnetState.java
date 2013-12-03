/*
 * Copyright (C) 2000, 2005, 2006 Dave Jarvis (dj@joot.com), 
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


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import java.net.Socket;

/**
 * This class contains information used by the TelnetProtocol class.
 */
public class TelnetState
{
  private Socket mySocket = null;
  private BufferedInputStream myBis = null;
  private BufferedOutputStream myBos = null;

  // Ready for doing the Telnet protocol, by default.
  //  
  private boolean amReady = true;

  /**
   * @param s - The socket whose in/out streams are supposed to conform to
   * the Telnet protocol.
   */
  public TelnetState( Socket s )
  {
    mySocket = s;
    
    try
    {
      myBis = new BufferedInputStream( s.getInputStream() );
      myBos = new BufferedOutputStream( s.getOutputStream() );;
    }
    catch( Exception e )
    {
      notReady();
    }
  }
  
  //Metodo incluido para utilizarlo con NetKitGUI
  public int getConnectedPort ()
  {
  	return mySocket.getPort();
  }
  
  public BufferedInputStream getInStream()
  {
    return myBis;
  }
  
  public BufferedOutputStream getOutStream()
  {
    return myBos;
  }
  
  public boolean isReady()
  {
    return amReady;
  }

  /**
   * Once not ready, everything must start a-fresh.
   */  
  protected void notReady()
  {
    amReady = false;
  }
  
  protected void close()
    throws IOException
  {
    mySocket.close();
  }
}














