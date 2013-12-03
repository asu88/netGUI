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

import edu.umd.cs.piccolo.event.*;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import javax.swing.*;

public class AddTerminalEventHandler extends PBasicInputEventHandler {

    LayersHandler handler;

    public AddTerminalEventHandler(LayersHandler lHandler) {
        PInputEventFilter filter = new PInputEventFilter();
        filter.setOrMask(InputEvent.BUTTON1_MASK);
        setEventFilter(filter);
        handler = lHandler;
    }

    @Override
    public void mouseClicked(PInputEvent e) {
        super.mouseClicked(e);
        displayWindow(e.getPosition());
    }

    private void displayWindow(Point2D globalPoint2D) {
        String systemName;
        boolean cancel = false;
        boolean exit = true;

        do {
            systemName = (String) JOptionPane.showInputDialog(
                    null,
                    "Type a name for this host",
                    "New host",
                    JOptionPane.PLAIN_MESSAGE,
                    new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/48x48/system.png"),
                    null,
                    handler.getUnusedTerminalName());

            cancel = (systemName == null);
            if ((!cancel) && (systemName.length() > 0)) {
                handler.addNewTerminal(globalPoint2D, systemName);
            }
            exit = (cancel || handler.nodeNameExists(systemName));
        } while (!exit);
    }
}
