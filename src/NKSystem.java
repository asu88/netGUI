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

/**
 * ********************************************************************
 * Crea una subclase que encapsula a todos los nodos de la red capaces de
 * ejecutar un kernel (Compaq, routers...)
**********************************************************************
 */
import edu.umd.cs.piccolo.nodes.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

public abstract class NKSystem extends NKNode {

    private boolean started;
    protected LayersHandler handler;
    private Point2D position;

    public NKSystem(String name, String fileImage, String fileSelectedImage,
            String deleteFileImage, Point2D position, LayersHandler handler) {
        super(name, fileImage, fileSelectedImage, deleteFileImage);
        started = false;
        this.position = position;
        this.handler = handler;
    }
    
    @Override
    public double getX(){
        return position.getX();
    }
    
    @Override
    public double getY(){
        return position.getY();
    }
    
    public void setPos(Point2D position){
        this.position = position;
    }

    protected void setStarted(boolean run) {
        started = run;
    }

    public boolean isStarted() {
        return started;
    }

    /**
     * ******************************************************************
     * Establece el nuevo icono cuando el nodo arranca un kernel-netkit 
	 *******************************************************************
     */
    protected abstract void updateStartedImages();

    /**
     * ******************************************************************
     * Establece el nuevo icono cuando el nodo arranca un kernel-netkit 
	 *******************************************************************
     */
    protected abstract void updateNormalImages();

    /**
     * ***********************************************************
     * Posiciona el nombre del nodo en el icono
	 ************************************************************
     */
    @Override
    protected void ShowDisplayName(String name) {
        double height = this.getHeight(), width = this.getWidth();
        PText tNode = new PText(name);
        tNode.centerFullBoundsOnPoint((width / 2), height);
        tNode.setPickable(false);
        tNode.setScale((float) 1.5);
        this.addChild(tNode);
    }

    protected void formatEthText(PText ethNode) {
        ethNode.setScale((float) 1.0);
        ethNode.setFont(new Font("Dialog", Font.BOLD, 14));
        ethNode.setTextPaint(Color.BLUE);
    }
    
//    public void setProgressBar (String nodeName, int progress){
//        
//        String advance = nodeName;
//        switch (progress){
//            case 1:
//                advance += " <=         > 10 %\n";
//                break;
//            case 2:
//                advance += " <==        > 20 %\n";
//                break;
//            case 3:
//                advance += " <===       > 30 %\n";
//                break;
//            case 4:
//                advance += " <====      > 40 %\n";
//                break;
//            case 5:
//                advance += " <=====     > 50 %\n";
//                break;
//            case 6:
//                advance += " <======    > 60 %\n";
//                break;
//            case 7:
//                advance += " <=======   > 70 %\n";
//                break;
//            case 8:
//                advance += " <========  > 80 %\n";
//                break;
//            case 9:
//                advance += " <========= > 90 %\n";
//                break;
//            case 10:
//                advance += " <==========> 100%\n";
//                break;
//        }
//        String editor = NetKitViewer.log.getText();
//        
//        int barLocation = editor.indexOf(nodeName + " <");
//        if (barLocation != -1){
//            String part1 = editor.substring(0, barLocation);
//            String part2 = editor.substring(barLocation + nodeName.length() + 19);
//            NetKitViewer.log.refreshAll(part1 + advance + part2);
//        } else {
//            NetKitViewer.log.addText(advance);
//        }
//    }

    /**
     * *************************
     * Arranca un kernel-netkit 
	 **************************
     */
    public abstract void startNetKit();

    /**
     * *************************
     * Para Netkit (vrash -r si crash=true; vhalt en otro caso)
	 **************************
     */
    public void stopNetKit(boolean crash) {
        String cmd = "";
        if (isStarted()) {
            if (crash) {
                cmd = vcrashCmdGen();
            } else {
                cmd = vhaltCmdGen();
            }
            //System.out.println(cmd);

            try {
                NodeTelnetCommunicator2.removeConnection(this);
                Process proc;
                proc = Runtime.getRuntime().exec(cmd, null);
                setStarted(false);
                updateNormalImages();
            } catch (Exception ex) {
                System.out.println("Error " + ex);
            }
        }
    }

    /**
     * ***************************************************************
     * Genera el comando vhalt para parar la ejecuci�n de la m�quina virtual
     * arrancada sobre este nodo
	 ****************************************************************
     */
    private String vhaltCmdGen() {
        NetKitViewer.log.delete(getName());
        return "vhalt " + getName();

    }

    /**
     * ***************************************************************
     * Genera el comando vcrash para parar la ejecuci�n de la m�quina virtual y
     * borrar su archivo de configuraci�n
	 ****************************************************************
     */
    private String vcrashCmdGen() {
        return "vcrash -r " + getName();

    }

    /**
     * ************************************************************
     * M�todo invocado sobre el nodo cuando cambia el estado de su conexi�n para
     * que actualice la representaci�n de sus interfaces
	 *************************************************************
     */
    protected abstract void updateEthLocation(NKConection edge);

    /**
     * ***************************************************************
     * A�ade los par�metros --no-log -f /workspace/nodeName.disk [--new] si
     * nodeName.disk no existe.(es la primera vez que se arranca un nodo)
	 ****************************************************************
     */
    protected String processStartCmd(String cmd) {
        cmd += " -f " + UtilNetGUI.getCurrentWorkSpace().getAbsolutePath()
                + "/" + getName() + ".disk ";
        // Modificación para poder arrancar una maquina con acceso a internet
        //cmd += "--eth0=tap,20.0.0.1,20.0.0.2 ";  //Donde X es un valor dentro de la misma subred
        cmd += "--con1=port:" + NodeTelnetCommunicator2.getNewPort();
        //System.out.println(cmd);
        return cmd;
    }

    /**
     * ******************************************************************
     * Obtiene un hastmap con el string ethX como clave y su respectiva IP
     * tambi�n como String que contiene todas las interfaces a actualizar con su
     * ip asociada. Si una interfaz no aparece dentro del hasmap significar� que
     * ya no tiene ip asociada. 
	 *******************************************************************
     */
    public abstract void updateEthernets(HashMap eth_ip);
}
