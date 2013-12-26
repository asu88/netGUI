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

import java.io.*;
import java.util.Scanner;

public class UtilNetGUI {

    private static File currentWorkSpace;
    private static int netCount = 1;
    private static int systemCount = 1;
    private static int routerCount = 1;
    private static int switchCount = 1;
    private static int hubCount = 1;
    private static final String hubName = "hub";
    private static final String idNet = "NET";
    private static final String idSystem = "pc";
    private static final String idRouter = "r";
    private static final String idSwitch = "s";
    private static boolean statusChanged = false;
    private static boolean ipv4AddrShow = true;
    private static boolean ipv6AddrShow = false;

    /**
     * **************************************
     * Cambia el directorio de trabajo actual (nuevo proyecto, cargar
     * proyecto...) **************************************
     */
    public static void setCurrentWorkSpace(File in) {
        currentWorkSpace = in;
    }

    public static File getCurrentWorkSpace() {
        return currentWorkSpace;
    }

    /**
     * *******************************************
     * Devuelve true si el archivo fileName existe dentro del directorio de
     * trabajo *******************************************
     */
    public static boolean fileExists(String fileName) {
        File f = new File(currentWorkSpace.getAbsolutePath() + "/" + fileName);
        return f.exists();
    }

    /**
     * *******************************************
     * Si existe el fichero .disk asociado al nodo lo borra y devuelve true
     * *******************************************
     */
    public static boolean deleteFileNodeDisk(String nodeName) {
        File f = new File(currentWorkSpace.getAbsolutePath() + "/" + nodeName + ".disk");
        if (f.exists()) {
            f.delete();
        }
        return f.exists();
    }

    /**
     * **************************************
     * M�todo de clase que devuelve un nombre para una nueva red
     * **************************************
     */
    public static String getNetName() {
        return idNet + netCount;
    }

    /**
     * **************************************
     * M�todo de clase que devuelve un nombre para una nuevo terminal
     * **************************************
     */
    public static String getPcName() {
        return idSystem + systemCount;
    }

    /**
     * **************************************
     * M�todo de clase que devuelve un nombre para una nuevo router
     * **************************************
     */
    public static String getRouterName() {
        return idRouter + routerCount;
    }

    /**
     * **************************************
     * M�todo de clase que devuelve un nombre para una nuevo switch
     * **************************************
     */
    public static String getSwitchName() {
        return idSwitch + switchCount;
    }

    /**
     * **************************************
     * M�todo de clase que devuelve un nombre para el nuevo hub
     * **************************************
     */
    public static String getHubName() {
        return hubName + hubCount;
    }

    public static void reset() {
        netCount = 1;
        hubCount = 1;
        routerCount = 1;
        systemCount = 1;
        //System.out.println("Reset: RouterCount=" + routerCount + ", SystemCount=" + 
        //systemCount + ", NetCount=" + netCount);
    }

    public static boolean aplicationChanged() {
        return statusChanged;
    }

    public static void setAplicationStatusChange(boolean change) {
        statusChanged = change;
    }

    public static void IncrementHubCount() {
        hubCount++;
    }

    public static void IncrementRouterCount() {
        routerCount++;
    }

    public static void IncrementSwitchCount() {
        switchCount++;
    }

    public static void IncrementPcCount() {
        systemCount++;
    }

    public static void IncrementNetCount() {
        netCount++;
    }

    public static void displayUserManual() {
        try {
            Process proc;
            proc = Runtime.getRuntime().exec("firefox "
                    + System.getProperty("NETLAB_HOME")
                    + "html/manual.html", null);
        } catch (Exception ex) {
            System.out.println("Error " + ex);
        }
    }

    public static void readNetGUIConf() {
        File f = new File(currentWorkSpace.getAbsolutePath() + "/" + "netgui.conf");
        if (!f.exists()) {
            return;
        }

        try {
            FileReader reader = new FileReader(f);
            Scanner scanner = new Scanner(reader);
            try {
                while (scanner.hasNextLine()) {
                    processLine(scanner.nextLine());
                }
            } finally {
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void processLine(String aLine) {
        Scanner scanner = new Scanner(aLine);
        scanner.useDelimiter("=");
        if (scanner.hasNext()) {
            String name = (scanner.next()).trim();
            String value = (scanner.next()).trim();
// 		System.out.println("Variable=" + name.trim()); 
// 		System.out.println("Valor=" + value.trim()); 
            if (name.equals("IPv4AddrShow")) {
                if (value.equals("n")) {
                    ipv4AddrShow = false;
                }
            } else if (name.equals("IPv6AddrShow")) {
                if (value.equals("y")) {
                    ipv6AddrShow = true;
                }
            }
        }
// 	    System.out.println("IPv4AddrShowBoolean=" + ipv4AddrShow + 
// 	                       "     IPv6AddrShowBoolean=" + ipv6AddrShow ); 

    }

    public static boolean getIPv4AddrShowVar() {
        return ipv4AddrShow;
    }

    public static boolean getIPv6AddrShowVar() {
        return ipv6AddrShow;
    }

    public static void setIpv4AddrShow(boolean ipv4AddrShow) {
        UtilNetGUI.ipv4AddrShow = ipv4AddrShow;
    }

    public static void setIpv6AddrShow(boolean ipv6AddrShow) {
        UtilNetGUI.ipv6AddrShow = ipv6AddrShow;
    }
    
    
}
