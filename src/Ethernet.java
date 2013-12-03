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
 * Clase que representa la interfaz ethernet de conexi�n utilizada por
 * un maquina en la red
 *********************************************************************

 */
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.pswing.PSwing;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.JCheckBox;

class Ethernet extends PPath {

    public static final long serialVersionUID = 1L;
    private static final int OFFSET = 5;
    private String eth;
    private PText ethName;
    private PText ip;
    private PSwing captureCheck;
    public JCheckBox checkbox = new JCheckBox();

    public Ethernet(String ethName) {
        super();
        this.ethName = new PText(ethName);
        this.ip = new PText();
        this.eth = ethName;
        this.captureCheck = new PSwing(checkbox);
        addChild(this.ethName);
        addChild(this.ip);
        addChild(this.captureCheck);
        this.ip.setPickable(false);
        this.ethName.setPickable(false);
        this.captureCheck.setVisible(false);
        formatIpText();
        formatEthText();
//        this.ethName.centerFullBoundsOnPoint(getBounds().getCenter2D().getX(),
//                getBounds().getCenter2D().getY());
//        this.captureCheck.centerFullBoundsOnPoint(getBounds().getCenter2D().getX() + 26,
//                getBounds().getCenter2D().getY());
        updateLocation();
    }
    
    public void showCheck(){
        this.captureCheck.setVisible(true);
    }
    
    public void hideCheck(){
        this.captureCheck.setVisible(false);
    }
    
    public String getIP(){
        return this.ip.getText();
    }

////    public Ethernet(String etherName, String ip) {
////        super();
////        this.ethName = new PText(etherName);
////        this.ip = new PText(ip);
////        addChild(this.ethName);
////        addChild(this.ip);
////        setChildrenPickable(false);
////        formatIpText();
////        formatEthText();
////        updateLocation();
////    }
    public String getEth() {
        return eth;
    }

    public PText getEthName() {
        return ethName;
    }

    public boolean isChecked(){
        return checkbox.isSelected();
    }

    public void setIp(String newIp) {
        ip.setText(newIp);
        updateLocation();
    }

    private void updateLocation() {
        Point2D pd = getBounds().getCenter2D();
        if ((ip.getText() == null) || (ip.getText().equals(""))) {
            ethName.centerFullBoundsOnPoint(pd.getX(), pd.getY());
            captureCheck.centerFullBoundsOnPoint(pd.getX() + 26, pd.getY());
        } else {
            ethName.centerFullBoundsOnPoint(pd.getX(), pd.getY() - OFFSET);
            captureCheck.centerFullBoundsOnPoint(pd.getX(), pd.getY() + 4*OFFSET);
            ip.centerFullBoundsOnPoint(pd.getX(), pd.getY() + OFFSET);
        }
//        Point2D pd = getBounds().getCenter2D();
//        if ((ip.getText() == null) || (ip.getText().equals(""))) {
//            // Nodo no arrancado o arrancado sin ip
//            ethName.centerFullBoundsOnPoint(pd.getX(), pd.getY());
//            captureCheck.setVisible(false);
//        } else {
//            // Arrancado con IP
//            ethName.centerFullBoundsOnPoint(pd.getX(), pd.getY() - OFFSET);
//            captureCheck.setVisible(true);
//            captureCheck.centerFullBoundsOnPoint(pd.getX(), pd.getY() + 4*OFFSET);
//            ip.centerFullBoundsOnPoint(pd.getX(), pd.getY() + OFFSET);
//        }
    }

    private void formatIpText() {
        ip.setFont(new Font("Dialog", Font.BOLD, 14));
        ip.setTextPaint(new Color(0, 0, 210));
        //ip.set(new Color(0,0,210));
    }

    private void formatEthText() {
        ethName.setScale((float) 1.0);
        ethName.setFont(new Font("Dialog", Font.BOLD, 12));
        ethName.setTextPaint(new Color(0, 100, 80));
    }
}
