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

public class NodeTelnetCommunicator2 {
    //asocia a cada nodo un conexi�n TCP a trav�s de telnet para comunicarse con �l

    private static HashMap<NKSystem, Telnet> openConnections = new HashMap<NKSystem, Telnet>();
    private static Random portFactory = new Random();

    public synchronized static void addConnection(NKSystem node, Telnet tp) {
        openConnections.put(node, tp);
    }

    public synchronized static boolean removeConnection(NKSystem node) {
        if (!openConnections.containsKey(node)) {
            return false;
        }
        try {
            (openConnections.get(node)).disconnect();
            openConnections.remove(node);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public synchronized static Telnet getTelnetSocket(NKSystem node) {
        if (!openConnections.containsKey(node)) {
            return null;
        }
        return openConnections.get(node);
    }

    public synchronized static void print() {
        System.out.println(openConnections);
    }

    public synchronized static int getNewPort() {
        int port;
        boolean exit = false;
        do {
            port = Math.abs(portFactory.nextInt(9999));
            if (port <= 1000) {
                port += 1001;
            }
            exit = portIsUsed(port);

        } while (exit);

        return port;
    }

    private synchronized static boolean portIsUsed(int port) {
        boolean isUsed = false;
        if (!openConnections.isEmpty()) {
            Iterator i = openConnections.values().iterator();
            while ((!isUsed) && (i.hasNext())) {
                isUsed = (port == ((Telnet) i.next()).getPort());
            }
        }
        return isUsed;
    }
}//Fin NodeTelnetCommunicator

