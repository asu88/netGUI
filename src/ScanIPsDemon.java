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
import java.util.*;

public class ScanIPsDemon extends Thread {

    private PLayer nodeLayer;
    // TODO: sleep time should increment iteratively
    // if no changes have been found as this 
    // impacts on the system's CPU heavily
    // Sleep every 10 seconds:
    private static int sleeptime = 10000;

    public ScanIPsDemon(PLayer nodeLayer) {
        this.nodeLayer = nodeLayer;
    }

    @Override
    public void run() {
        Object obj;
        do {
            // Iterator i = Collections.list(Collections.enumeration(nodeLayer.getAllNodes())).iterator();
            Iterator i = (nodeLayer.getAllNodes()).iterator();
            while (i.hasNext()) {
                obj = i.next();
                if (obj instanceof NKSystem) {
                    if (((NKSystem) obj).isStarted()) {
                        //System.out.println("Leer en " + ((NKNode)obj).getName());
                        readIP((NKSystem) obj);
                    }
                }
            }
            try {
                sleep(sleeptime);
            } catch (Exception e) {;
            }

        } while (true);
    }

    // Metodo readIP implementado con la antigua clase Telnet
    
////    private void readIP(NKSystem node) {
////        TelnetSocket ts = NodeTelnetCommunicator.getTelnetSocket(node);
////        if (ts != null) {
////            if (ts.send("ifconfig")) {
////                String txt = ts.getOutputResponse();
////                if (txt != null) {
////                    analizeInterfaces(txt, node);
////                }
////            }
////        }
////    }
    
    // Metodo readIP implementado con la nueva clase Telnet
    
    private void readIP(NKSystem node) {
        if (node.isStarted()){
            Telnet ts = NodeTelnetCommunicator2.getTelnetSocket(node);
            if (ts != null) {
                //System.out.println("Mandando comando ifconfig para maquina " + ts.getHost() + ":" + ts.getPort());
                String vmAnswer = ts.sendCommand("ifconfig");
                //System.out.println("Comando enviado, esperando respuesta...");
                //String vmAnswer = ts.read();
                //System.out.println("---> Respuesta recibida: " + vmAnswer);
                if (vmAnswer != null) {
                    analizeInterfaces(vmAnswer, node);
                }  
                
            }
        }
    }

    private void analizeInterfaces(String result, NKSystem node) {
        HashMap<String, String> eth_ip = new HashMap<String, String>();
        String eth = "eth";
        String ethx;
        
        // Hay un problema cuando realizamos las capturas ya que al hacer el
        // ifconfig despues de comenzar la captura recibimos en la respuesta
        // el tdpdump: listening... aunque lo estemos arrancando con &
        // Miramos si en la salida aparece tcpdump y en caso positivo eliminamos
        // esa linea
        
        while (result.indexOf("tcpdump") != -1){
            result = result.substring(result.indexOf('\n') + 1);
        }
        
//                 System.out.println("Resultado de ifconfig " + result + "--------------------");
        while (result.indexOf(eth) != -1 && result.indexOf("Interrupt") != -1) {
            try {
                ethx = result.substring(result.indexOf(eth), result.indexOf("Interrupt"));
                getEthConf(ethx, eth_ip);
                result = result.substring((result.indexOf("Interrupt") + 9), result.length());
            } catch (Exception e) {;
            }
        }
        node.updateEthernets(eth_ip);

    }

    private void getEthConf(String ethx, HashMap<String, String> eth_ip) {
        String eth = "eth";
        String inetaddr = "inet addr:";
        String inet6addr = "inet6 addr: ";
        String scope = "Scope";
        String bcast = "Bcast:";
        String ethernet = "";
        String ip = "";
        String ip6 = "";

//                 System.out.println("Buscando informacion ethernet en " + ethx + "--------------");
        if (ethx.indexOf(eth) != -1) {
            ethernet = ethx.substring(ethx.indexOf(eth), ethx.indexOf(" "));
        }

        if (UtilNetGUI.getIPv4AddrShowVar()) {
            // Direccion IPv4
            if (ethx.indexOf(inetaddr) != -1) {
                ip = ethx.substring(ethx.indexOf(inetaddr) + inetaddr.length(), ethx.length());
                ip = ip.substring(0, ip.indexOf(" "));
            }
        }

        if (UtilNetGUI.getIPv6AddrShowVar()) {
//                     System.out.println("Buscando ipv6 ethernet en " + ethx + "--------------");
            // Direccion IPv6
            while (ethx.indexOf(inet6addr) != -1) {
                ip6 = ethx.substring(ethx.indexOf(inet6addr) + inet6addr.length(), ethx.length());
                ip6 = ip6.substring(0, ip6.indexOf(" "));
//                         System.out.println("Cortado ipv6 " + ip6 + "------" );
                ip = ip + "\n" + ip6;
//                         System.out.println("IP------------- " + ip + "------" );
                ethx = ethx.substring(ethx.indexOf(scope) + scope.length(), ethx.length());
            }
        }
//        System.out.println("Informacion ethernet: Eth =" + ethernet + ";IP =" + ip);
        eth_ip.put(ethernet, ip);
    }
}
