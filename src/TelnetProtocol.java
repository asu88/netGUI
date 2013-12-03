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


import java.io.*;
import java.net.*;

/**
 * This handles the basic Telnet protocol.  It is tightly coupled with
 * the TelnetState class.  Note that this isn't a full implementation
 * of the Telnet protocol, but rather a bare bones implementation.
 */
public class TelnetProtocol
{
  protected final static int IAC = 255;   // Interpret As Command ...

  // Format: IAC<[WILL|WONT|DO|DONT<option code>]|[IAC/IP|AYT|BRK|NOP|AO]>
  //
  public final static byte DONT = (byte)254; // You are not to use this option.
  public final static byte DO   = (byte)253; // Please do this option?
  public final static byte WONT = (byte)252; // I won't do that option.
  public final static byte WILL = (byte)251; // I will do that option.

  public final static byte SB   = (byte)250; // Subnegotiation of option XYZ
  public final static byte GA   = (byte)249; // Go Ahead
  public final static byte EL   = (byte)248; // Erase Line
  public final static byte EC   = (byte)247; // Erase Character
  public final static byte AYT  = (byte)246; // Are You There?  Huh?  Are ya'?
  public final static byte AO   = (byte)245; // Abort Output
  public final static byte IP   = (byte)244; // Interrupt Process
  public final static byte BRK  = (byte)243; // Break, duh
  public final static byte DM   = (byte)242; // Data Mark
  public final static byte NOP  = (byte)241; // No Operation
  public final static byte SE   = (byte)240; // End Subnegotiation parameters
  public final static byte EOR  = (byte)239; // End of Record
  
  // The TELOPT_ codes for WILL/WONT/DO/DONT commands
  //
  public final static byte TELOPT_SGA = (byte)3;  // Suppress Go Ahead

  // Instead of creating this array each time, create it once and modify
  // the values of its last two indices (command and option).
  //
  protected byte SEND_OPT[] = { (byte)IAC, (byte)0, (byte)0 };
  
  private TelnetState myState = null;

  protected TelnetProtocol() { }

  public TelnetProtocol( TelnetState tState )
  {
    setTelnetState( tState );
    if( !initTelnet() )
      tState.notReady();
  }
  
  protected void setTelnetState( TelnetState tState )
  {
    myState = tState;
  }
  
  //Metodo incluido para utilizarlo con NetKitGUI
  public int getConnectedPort ()
  {
  	return myState.getConnectedPort();
  }
  
  protected TelnetState getTelnetState()
  {
    return myState;
  }

  protected void sendOpt( byte command, byte opt, BufferedOutputStream os )
    throws IOException
  {
    SEND_OPT[1] = command;
    SEND_OPT[2] = opt;
    writeBytes( SEND_OPT, os );
  }

  protected void willOpt( byte opt, BufferedOutputStream os )
    throws IOException
  {
    sendOpt( WILL, opt, os );
  }

  protected void wontOpt( byte opt, BufferedOutputStream os )
    throws IOException
  {
    sendOpt( WONT, opt, os );
  }

  protected void doOpt( byte opt, BufferedOutputStream os )
    throws IOException
  {
    sendOpt( DO, opt, os );
  }

  protected void dontOpt( byte opt, BufferedOutputStream os )
    throws IOException
  {
    sendOpt( DONT, opt, os );
  }

  protected void iacOpt( byte opt, BufferedOutputStream outStream )
    throws IOException
  {
    byte[] SEND_IAC_OPT = { (byte)IAC, opt };
    writeBytes( SEND_IAC_OPT, outStream );
  }

  protected void writeBytes( byte toWrite[], OutputStream outStream )
    throws IOException
  {
    outStream.write( toWrite, 0, toWrite.length );
    outStream.flush();
  }

  /**
   * Parses Telnet codes from the input stream.
   *
   * @return false - the connection closed.
   */
  protected boolean parse()
    throws IOException
  {
    TelnetState tState = getTelnetState();
    BufferedInputStream is = tState.getInStream();
    int byteRead = 0,
        command = 0,
        option = 0;
        
    // Only need to parse if we have something to read ...
    //
    if( bytesAvailable() == 0 )
      return true;    

    // Mark the stream at this point; if the next character isn't
    // IAC, then we have nothing to parse.
    //
    is.mark( 1 );
    if( (byteRead = is.read()) == -1 ) return false;
    
    // While there are Telnet codes to parse ...
    //
    while( byteRead == IAC )
    {
      // Read the character following the IAC character:
      //   DO/DONT/WILL/WONT/IAC/IP/etc.
      //
      if( (command = is.read()) == -1 ) return false;
      
      // Now figure out what it was we were/weren't supposed to do.
      //
      switch( (byte)command )
      {
        // We won't do what was requested, unless it was SGA.
        //
        case DO:
          if( (option = is.read()) == -1 ) return false;
          
          if( option == TELOPT_SGA )
            willOpt( (byte)option, tState.getOutStream() );
          else
            wontOpt( (byte)option, tState.getOutStream() );
          break;

        // We won't do that ... honest!
        //
        case DONT:
          if( (option = is.read()) == -1 ) return false;
          wontOpt( (byte)option, tState.getOutStream() );
          break;

        // Tell the server to go right ahead and do what it will.
        //
        case WILL:
          if( (option = is.read()) == -1 ) return false;
          doOpt( (byte)option, tState.getOutStream() );
          break;

        // Tell the server it doesn't have to do what it won't.
        //
        case WONT:
          if( (option = is.read()) == -1 ) return false;
          dontOpt( (byte)option, tState.getOutStream() );
          break;

        // Request to interrupt process.
        //
        case IP:
          return false;
         
        // An IAC followed by an IAC indicates that the data
        // stream contains a single IAC character; reset the
        // stream to the marked position so it can be read by
        // something else.
        //
        case (byte)IAC:
          is.reset();
          return true;

        // Otherwise, we don't know what to do!
        //
        default:
          return true;
      }

      // If there's nothing more to read, then we've hit the end of the
      // Telnet commands (for now).
      //
      if( is.available() == 0 ) return true;

      // The next character could be another IAC character, so mark the
      // stream in case it isn't.
      //
      is.mark( 1 );

      // Read the next character -- if it is an IAC character, everything
      // is fine.
      //
      if( (byteRead = is.read()) == -1 ) return false;
    }

    // The next character wasn't an IAC character, so reset the stream
    // for somebody else to handle the next character.
    //
    is.reset();
    return true;
  }

  /**
   * Initializes the Telnet session (suppresses Go Ahead).
   *
   * @return false - the connection closed.
   */
  public synchronized boolean initTelnet()
  {
    try
    {
      // Tell the remote side to suppress Go Ahead
      //
      doOpt( TELOPT_SGA, getTelnetState().getOutStream() );
      if( !parse() ) return false;

      // Let the remote side know we will suppress Go Ahead
      //
      willOpt( TELOPT_SGA, getTelnetState().getOutStream() );
      if( !parse() ) return false;
    }
    catch( Exception e )
    {
      return false;
    }
      
    return true;
  }
  
  /**
   * Reads a byte from the input stream.
   *
   * @return The next non-Telnet command character from the stream.
   */  
  public byte readByte()
    throws IOException
  {
    // Parse any Telnet codes.
    //
    parse();

    // Read the byte following any Telnet codes, then return it.
    //    
    int byteRead = getTelnetState().getInStream().read();
    if( byteRead == -1 ) throw new IOException();
    return (byte)byteRead;
  }

  /**
   * @return The number of bytes available to be read ...
   */  
  private int bytesAvailable()
    throws IOException
  {
    return getTelnetState().getInStream().available();
  }

  // Only used by "readText()"
  //  
  private StringBuffer myStringBuffer = new StringBuffer();

  /**
   * Used to read text from the input stream, without having to worry about
   * the Telnet protocol.
   *
   * @return A CR/LF terminated string of characters
   * @return A non-CR/LF terminated string of characters when no more data
   * is available
   * @return "" on any error
   */
  public synchronized String readText()
    throws IOException
  {
    myStringBuffer.setLength( 0 );
    int byteRead = readByte();
    
    while( (byteRead != 0x0A) && (byteRead != -1) )
    {
      myStringBuffer.append( (char)byteRead );
      
      // If the number of bytes available is zero, just return whatever
      // was found to this point.
      //
      if( bytesAvailable() == 0 )
        return myStringBuffer.toString();
      
      byteRead = readByte();
    }
    
    if( byteRead == -1 )
      return "";
    
    myStringBuffer.append( "\r\n" );
    return myStringBuffer.toString();
  }

  // Only used by "writeString( String )"
  //  
  private BufferedWriter myBufferedWriter = null;
  
  /**
   * Writes a string of characters to the output stream.
   *
   * @param s - A CR/LF or LF-terminated string
   */
  public void writeString( String s )
    throws IOException
  {
    // Lazy initialization, as "myBufferedWriter" is only used here.
    //
    if( myBufferedWriter == null )
      myBufferedWriter = new BufferedWriter(
        new OutputStreamWriter( getTelnetState().getOutStream() ) );
        
    myBufferedWriter.write( s, 0, s.length() );
    myBufferedWriter.flush();
  }

  public void close()
    throws IOException
  {
    getTelnetState().close();
  }
}
