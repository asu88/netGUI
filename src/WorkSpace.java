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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.io.*;

public class WorkSpace {
	
    private static Component c;

    public static File askForWorkSpace (Component cp)
    {
	c = cp;
	String workDir;

	File f;
	do
	    {
		do 
		    {
			workDir = exploreDirectories ("Open");
		    }while (workDir == null);

		f = checkWorkDirectory(workDir);

	    }while (f==null);
    	
    	return f;
    	
    }

    private static String workSpaceAsk ()
    {
	String workDir;
	String msg="You must type a working directory\n" +
	    "before editing a network with NetGUI.";
	do
	    {
		workDir = (String)JOptionPane.showInputDialog(
							      c,
							      msg,
							      "NetGUI WorkSpace",
							      JOptionPane.INFORMATION_MESSAGE,
							      new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/directory.png"),
							      null,
							      null);
		if (workDir==null)
		    workDir = exploreDirectories("Open");
	    }while (workDir==null || workDir.equalsIgnoreCase(""));
  	  
	return workDir;
    }
	
    public static String saveDirectories (String title, String button)
    {
    	JFileChooser fc = new JFileChooser();
    	fc.setDialogTitle(title);
    	//fc.addChoosableFileFilter(new NKProjectFilter());
    	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	fc.setAcceptAllFileFilterUsed(false);
	UIManager.put("FileChooser.fileNameLabelText", "Enter a new directory for the project:");
	SwingUtilities.updateComponentTreeUI(fc);
	int returnVal = fc.showDialog(c, button);
	if (returnVal == JFileChooser.APPROVE_OPTION)
	    return fc.getSelectedFile().toString();
	else 
	    return null;
    }
    
    public static String exploreDirectories (String label)
    {
    	JFileChooser fc = new JFileChooser();
    	fc.setDialogTitle("Choose project directory");
    	//fc.addChoosableFileFilter(new NKProjectFilter());
    	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	fc.setAcceptAllFileFilterUsed(false);
	//	int returnVal = fc.showOpenDialog(c);
	UIManager.put("FileChooser.fileNameLabelText", "Enter project directory:");
	SwingUtilities.updateComponentTreeUI(fc);
	int returnVal = fc.showDialog(c, label);
	if (returnVal == JFileChooser.APPROVE_OPTION)
	    return fc.getSelectedFile().toString();
	else 
	    return null;
    }

    private static File checkWorkDirectory (String workDir)
    {
    	File d = new File (workDir);

	if (!d.exists())
	    {
    		if (createWorkDirQuestion(d))
		    return d;
    		else return null;
	    }
    	return d;
    }
    
    private static boolean createWorkDirQuestion (File f)
    {
    	boolean created = false;
    	String msg="There is no NetGUI project at specified directory.\nDo you want to create it?";
    	Object[] options = {"create dir", "cancel"};
    	int n = JOptionPane.showOptionDialog(c,
					     msg,
					     "New project",
					     JOptionPane.YES_NO_OPTION,
					     JOptionPane.QUESTION_MESSAGE,
					     new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/question.png"),
					     options,
					     options[0]);
    	if (n == 0)
	    {
    		f.mkdir();
    		created = true;
	    }
    	return created;
    }
		
}
