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
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.util.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


import java.lang.*;
import java.util.*;




import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class NetKitViewer extends JFrame {
	
    public static final long serialVersionUID = 1L;
    NetKitGUI netkitWindow;
    JToolBar toolBar;
	
    JMenuItem showLayer = new JMenuItem("show Nodelayer");
    JMenuItem hideLayer = new JMenuItem("hide Nodelayer");

    public class NoneSelectedButtonGroup extends ButtonGroup {
        public static final long serialVersionUID = 1L;

        public void setSelected(ButtonModel model, boolean selected) {
            if (selected) {
                super.setSelected(model, selected);
            } else {
                 clearSelection();
            }
        }
    }
            


    public NetKitViewer() 
    {
	setJMenuBar(createMenuBar());
	toolBar = new JToolBar("NetKitBar");
	addButtons(toolBar);
	getContentPane().add(toolBar, BorderLayout.PAGE_START);
				

	setTitle("NetGUI");
	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
	JPanel mainPanel = new JPanel(new GridBagLayout());
    	//mainPanel.setPreferredSize(new Dimension(280, 100));
    	netkitWindow = new NetKitGUI(700, 600);
	getContentPane().add(netkitWindow);
	pack();
	setVisible(true);

	JOptionPane.showMessageDialog
	    (this,
	     "GSyC/Mobiquo, Grupo de Sistemas y Comunicaciones\n" +
	     "Universidad Rey Juan Carlos (URJC)\n"+
             "Author: Santiago Carot Nemesio\n"+
	     "GNU GPL v2 License",
	     "NetGUI version 0.4.10",
	     JOptionPane.INFORMATION_MESSAGE,
	     new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/other/gsyc.png"));
	
	
	
	
	//obtenemos el directorio de trabajo
	initWorkSpaceForNetKit ();
	
	
    }
    
    
    private void selectActionPerformed (ActionEvent evt)
    {
    	netkitWindow.enableSelectMode ();
    }
    
    private void editCutActionPerformed(ActionEvent evt)
    {
    	netkitWindow.deleteElement ();
    }
    
    private void addNewTerminalActionPerformed(ActionEvent evt)
    {
    	netkitWindow.addTerminal ();
    }
    
    private void addNewRouterActionPerformed(ActionEvent evt)
    {
    	netkitWindow.addRouter ();
    }
    
    private void addNewSwitchActionPerformed(ActionEvent evt)
    {
    	netkitWindow.addSwitch ();
    }
 
    private void addNewHubActionPerformed(ActionEvent evt)
    {
    	netkitWindow.addHub();
    }
    
    private void addNewConectionActionPerformed(ActionEvent evt)
    {
    	netkitWindow.addConexion ();
    }
    
    private void addButtons(JToolBar toolBar) 
    {    	
        NoneSelectedButtonGroup group = new NoneSelectedButtonGroup();

        //JButton addTerminal = new JButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/system.png"));
        JToggleButton addTerminal = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/system.png"));
        //addTerminal.setSelectedButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/system_selected.png"));
        toolBar.add(addTerminal);
       


        JToggleButton addRouter = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/router.png"));
        toolBar.add(addRouter);


        JToggleButton addSwitch = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/switch.png"));
        toolBar.add(addSwitch);

        JToggleButton addHub = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/hub.png"));
        toolBar.add(addHub);

        JToggleButton addConexion = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/rj45_cable.png"));
        toolBar.add(addConexion);

        group.add(addTerminal);
        group.add(addRouter);
        group.add(addSwitch);
        group.add(addHub);
        group.add(addConexion);

        toolBar.addSeparator();
        toolBar.addSeparator();

        JToggleButton editCutMode = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/editcut.png"));
        toolBar.add(editCutMode);

        JToggleButton selectMode = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/arrow.png"));
        toolBar.add(selectMode);

        addTerminal.setToolTipText("new host");
        addRouter.setToolTipText("new router");
        addSwitch.setToolTipText("new switch");
        addHub.setToolTipText("new hub");
        addConexion.setToolTipText("connect...");
        editCutMode.setToolTipText("delete");
        selectMode.setToolTipText("select");

        selectMode.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    selectActionPerformed(evt);
		}
	    });
        
        editCutMode.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    editCutActionPerformed(evt);
		}
	    });
        
        addTerminal.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    addNewTerminalActionPerformed(evt);
		}
	    });
        
        addRouter.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    addNewRouterActionPerformed(evt);
		}
	    });
        
        addSwitch.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    addNewSwitchActionPerformed(evt);
		}
	    });
        
        addHub.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    addNewHubActionPerformed(evt);
		}
	    });
        
        addConexion.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    addNewConectionActionPerformed(evt);
		}
	    });
	
        toolBar.addSeparator();
        toolBar.addSeparator();
        
        JToggleButton runButton = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/run.png"));
        runButton.setToolTipText("start host/router/switch");
        toolBar.add(runButton);
        
        runButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    startNetKitActionPerformed(evt);
		}
	    });

        JToggleButton stopButton = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/exit.png"));
        stopButton.setToolTipText("stop host/router/switch");
        toolBar.add(stopButton);
        
        stopButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    stopNetKitActionPerformed(evt);
		}
	    });

        JToggleButton restartButton = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/restart.png"));
        restartButton.setToolTipText("restart host/router/switch");
        toolBar.add(restartButton);
        
        restartButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    restartNetKitActionPerformed(evt);
		}
	    });


        toolBar.addSeparator();
        toolBar.addSeparator();

	JToggleButton centerButton = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/center.png"));
	centerButton.setToolTipText("center view");
        toolBar.add(centerButton);

	centerButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    centerNetKitActionPerformed(evt);
		}
	    });

        group.add(editCutMode);
        group.add(selectMode);
        group.add(runButton);
        group.add(stopButton);
        group.add(restartButton);
        group.add(centerButton);

    }
    

    private void saveAsActionPerformed(ActionEvent evt)
    {

	String currentPath = UtilNetGUI.getCurrentWorkSpace().getAbsolutePath();
	
	
	    String newPath = WorkSpace.exploreDirectories("Save as");
	    if (newPath == null)
		return;
	    
	    File oldDir = new File (currentPath);
	    File newDir = new File (newPath);
	    
	    if (!newDir.exists())	    
		{
		    newDir.mkdir();
		}

	   try {
                 Process proc;
		 String cmd ="tar  -Scf "+newPath+"/.tmpnk  --directory "+currentPath + "/ .";
		 String cmd2="tar xf "+newPath+"/.tmpnk  --directory "+newPath +"/";
                 proc = Runtime.getRuntime().exec(cmd,null);
		 proc.waitFor();
                 proc = Runtime.getRuntime().exec(cmd2,null);
		 proc.waitFor();
                proc = Runtime.getRuntime().exec("rm " + newPath + "/.tmpnk", null);

	   } catch (Exception ex)
             {System.out.println("Error " + ex);}

	    
	    setTitle("NetGUI: " + newDir.getName());
	    UtilNetGUI.setCurrentWorkSpace(newDir);
	    
	    
	
	
	    netkitWindow.saveProject(new File(
					      UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + "/" +
					      "netgui.nkp"));
	    
	    UtilNetGUI.setAplicationStatusChange(false);
	    
	    JOptionPane.showMessageDialog(this,
					  "Changes saved." ,
					  "NetGUI",
					  JOptionPane.INFORMATION_MESSAGE);
	
    }

    private void saveActionPerformed(ActionEvent evt)
    {

	boolean salir = false;
	String currentPath = UtilNetGUI.getCurrentWorkSpace().getAbsolutePath();
	String tempPath = System.getProperty("user.home") +"/.netkitgui";
	
	File oldDir, newDir;
	
	
 	// System.out.println("Dir0 currentPath: " + currentPath);
	
	if (currentPath.equals(tempPath))
	{

	    do {
		String newPath = WorkSpace.saveDirectories("Save project", "Save");

		if (newPath == null)
		    return;
	    
		if (newPath.indexOf(' ') != -1) {
		    showErrorMsg("Error", "Whitespaces are not allowed in pathname.");
		} else {

		    oldDir = new File (tempPath);
		    newDir = new File (newPath);
		    
		    if (!newDir.exists())	    
			{
			    oldDir.renameTo(newDir);
			    setTitle("NetGUI: " + newDir.getName());
			    UtilNetGUI.setCurrentWorkSpace(newDir);
			    salir = true;
			}
		    else	    
			{
			    showErrorMsg ("Error", "Directory already exists.\n"
				  + "Enter a new directory for saving the project");
			}
		}
	    }while(!salir);
	    
	}
	
	
	if (UtilNetGUI.aplicationChanged()) {
	    netkitWindow.saveProject(new File(
					      UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + "/" +
					      "netgui.nkp"));
	    
	    UtilNetGUI.setAplicationStatusChange(false);
	    
	    JOptionPane.showMessageDialog(this,
					  "Changes saved." ,
					  "NetGUI",
					  JOptionPane.INFORMATION_MESSAGE);
	}
	else
	    JOptionPane.showMessageDialog(this,
					  "No changes from last saved project" ,
					  "NetGUI",
					  JOptionPane.INFORMATION_MESSAGE);
	
    }


    private void openActionPerformed(ActionEvent evt)
    {
    	if (UtilNetGUI.aplicationChanged ())
	    {
    		if (advertiseExitWindow() == 0)
		    open();
	    }
    	else 
	    open();
    		
    }		
    
    private void newActionPerformed(ActionEvent evt)
    {
    	if (UtilNetGUI.aplicationChanged ()) 
	    {   	
    		if (advertiseExitWindow() == 0)
		    {

    			if (getNewWorkSpace()==-1) 
			    return;
    			deleteCurrentProyect ();
    			UtilNetGUI.setAplicationStatusChange(false);
		    }
	    }
    	else
	    {
		if (getNewWorkSpace() == -1)
		    return;
		deleteCurrentProyect ();
		UtilNetGUI.setAplicationStatusChange(false);
	    }
    }
    
    private void deleteCurrentProyect ()
    {
    	//Finalizar la ejecución de todos los NetKit arrancados
    	netkitWindow.stopNetKit();
    	getContentPane().remove(netkitWindow);
    	netkitWindow = new NetKitGUI(700,600);
    	getContentPane().add(netkitWindow);
    	pack();
    }
    
    
    private int getNewWorkSpace()
    {
	boolean salir = false;
	File d;
    	//obtenemos el nueevo directorio de trabajo
    	do
	    {
		String workDir = WorkSpace.saveDirectories ("New project", "Create");
		if (workDir==null)
		    return -1;

		d = new File(workDir);
		if (d.exists())
		    showErrorMsg ("Error", "Directory already exists.\n"
				  + "Enter a new directory for the new project.");
		else if (workDir.indexOf(' ') != -1) 
		    {
			showErrorMsg("Error", "Whitespaces are not allowed in pathname.");
		    }
		else
		    {
			d.mkdir();
			setTitle("NetGUI: " + d.getName());
			UtilNetGUI.setCurrentWorkSpace(d);
			salir = true;
		    }
		
	    }while(!salir);
			
	//No existe: Creamos el fichero de configuración
	netkitWindow.saveProject
	    (new File(UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() 
		      + "/" + "netgui.nkp"));
	UtilNetGUI.setAplicationStatusChange(false);
	netkitWindow.repaint();
	
	return 0;
    }
    
    private void setOpenWorkSpace(File fileSelected)
    {
    	String newWorkSpace = fileSelected.getAbsolutePath();
    	newWorkSpace = newWorkSpace.substring(0,newWorkSpace.lastIndexOf('/'));
    	UtilNetGUI.setCurrentWorkSpace(new File(newWorkSpace));
    }
    

    private void open ()
    {

	String newPath = WorkSpace.exploreDirectories("Open");

	if (!(newPath==null))
	    {
		File d = new File(newPath);
		File f = new File(newPath + "/" + "netgui.nkp");

		// Compatibilidad con versiones antiguas de NetGUI donde
		// se utilizaba otro nombre para el .nkp
		if (!f.exists())
		    f = new File(newPath + "/" + d.getName() + ".nkp");

		if (f.exists())
		    {
    	                String newWorkSpace = f.getAbsolutePath();

                        if (newWorkSpace.indexOf(' ') != -1 ) { 
		               showErrorMsg("Error", "Whitespaces are not allowed in the pathname.");
                               return;
                        }
			deleteCurrentProyect ();
			setTitle("NetGUI: " + d.getName());
			setOpenWorkSpace(f);
			netkitWindow.openProject(f);
			UtilNetGUI.setAplicationStatusChange(false);
			UtilNetGUI.readNetGUIConf();
		    }
		else 
		    showErrorMsg("Error", "There is no NetGUI project in the selected directory.");
	    }
    }


    
    private int advertiseExitWindow()
	//si el valor devuelto = 0 -> El usuario ignora la advertencia de guardar 
	//valor = 1 -> El usuario se arrepiente 
    {
    	String msg="WARNING: There are changes that are not saved.\n" + 
	    "Do you want to exit without saving or cancel?";
    	Object[] options = {"Exit without saving", "Cancel"};
    	int n = JOptionPane.showOptionDialog
	    (this,
	     msg,
	     "ATENTION",
	     JOptionPane.YES_NO_OPTION,
	     JOptionPane.QUESTION_MESSAGE,
	     new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/info.png"),
	     options,
	     options[1]);
    	return n;
    }
    
    private void startNetKitActionPerformed(ActionEvent evt)
    {
    	// netkitWindow.enableStartNode ();
    	netkitWindow.startNode ();
    }
    
    private void stopNetKitActionPerformed(ActionEvent evt)
    {
    	netkitWindow.stopNode ();
    	//if (netkitWindow.stopNetKit() == 0)
	    //No había máquinas paradas
	//    showErrorMsg("Apagando nodos","No existen nodos que apagar");
    }

    private void restartNetKitActionPerformed(ActionEvent evt)
    {
    	// netkitWindow.enableStartNode ();
    	netkitWindow.restartNode ();
    }
 
    private void centerNetKitActionPerformed(ActionEvent evt)
    {
	netkitWindow.centerView();
    }

    
    private void showErrorMsg (String tittleMsg, String msg)
    {
	JOptionPane.showMessageDialog
	    (this,
	     msg,
	     tittleMsg,
	     JOptionPane.ERROR_MESSAGE,
	     new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/stop.png"));
    }
    
    /*******************************************
     * Crea el menu principal de la aplicación
     *******************************************/ 
    private JMenuBar createMenuBar() 
    {
    	//Crear menu bar.
        JMenuBar menuBar = new JMenuBar();
        //menuBar.setPreferredSize(new Dimension(250, 20));
        
        //*************************************************************
	//Creamos el menu Herramientas
	//*************************************************************
	JMenu archivoMenu = new JMenu("File");
        menuBar.add(archivoMenu);
        
	
	
        //-------------------------------------------------------------
        //Creamos un submenu para Nuevo
        //-------------------------------------------------------------
        JMenuItem nuevo = new JMenuItem("New Project");
	archivoMenu.add(nuevo);
        nuevo.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    newActionPerformed(evt);
		}
	    });
        //-------------------------------------------------------------
        //Creamos un submenu para Abrir proyecto
        //-------------------------------------------------------------
        JMenuItem abrir = new JMenuItem("Open");
	archivoMenu.add(abrir);
        abrir.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    openActionPerformed(evt);
		}
	    });
        //-------------------------------------------------------------
        //Creamos un submenu para Guardar proyecto
        //-------------------------------------------------------------
        JMenuItem guardar = new JMenuItem("Save");
	archivoMenu.add(guardar);
        guardar.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent evt) {
        		    saveActionPerformed(evt);
        		}
        	    });

        //-------------------------------------------------------------
        //Creamos un submenu para Guardar como ... 
        //-------------------------------------------------------------
        //JMenuItem guardarComo = new JMenuItem("Save as...");
	//archivoMenu.add(guardarComo);
        //guardarComo.addActionListener(new ActionListener() {
	//	public void actionPerformed(ActionEvent evt) {
	//	    saveAsActionPerformed(evt);
	//	}
	//    });


        //-------------------------------------------------------------
        //Creamos un submenu para el salir
        //-------------------------------------------------------------
        JMenuItem salir = new JMenuItem("Exit");
	archivoMenu.add(salir);
		
	salir.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
    		    if (UtilNetGUI.aplicationChanged ()){
			if (advertiseExitWindow() == 0){
			    netkitWindow.stopNetKit(); 
			    System.exit(0);
			}
                    }
		    else{
			netkitWindow.stopNetKit(); 
			System.exit(0);
		    }
			
		}
	    });
	
        
        //*************************************************************
	//Creamos el menu de Ayuda
	//*************************************************************
	JMenu helpMenu = new JMenu("Help");
	//        helpMenu.setIcon(new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/16x16/help.png"));
        menuBar.add(helpMenu);
        
        //-------------------------------------------------------------
        //Creamos un submenu para el archivoMenu
        //-------------------------------------------------------------
        JMenuItem aboutAs = new JMenuItem("About NetGUI");
	helpMenu.add(aboutAs);

	aboutAs.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    JOptionPane.showMessageDialog
			(SwingUtilities.getRoot((Component)evt.getSource()),
	                 "GSyC/Mobiquo, Grupo de Sistemas y Comunicaciones\n" +
	                 "Universidad Rey Juan Carlos (URJC)\n"+
                         "Author: Santiago Carot Nemesio\n"+
	                 "GNU GPL v2 License",
	                 "NetGUI version 0.4.10",
			 JOptionPane.INFORMATION_MESSAGE,
			 new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/other/gsyc.png"));


		}
	    });	
			

	// JMenuItem clean = new JMenuItem("Forzar la parada de nodos");
	// helpMenu.add(clean);
		
	// clean.addActionListener(new ActionListener() {
	// 	public void actionPerformed(ActionEvent evt) {
    	// String msg="ATENCION: Forzar la parada puede hacerle perder cambios\n" +            "El proceso de parada forzada dura 30 segundos durante\n"+
 	//     "los cuales NetGUI no interacciona con el usuario\n"+
	//     "¿Desea forzar la parada de los nodos arrancados, o prefiere cancelar?";
    	// Object[] options = {"Forzar la parada", "Cancelar"};
    	// int n = JOptionPane.showOptionDialog
	//   (SwingUtilities.getRoot((Component)evt.getSource()),
	//      msg,
	//      "ATENCION",
	//      JOptionPane.YES_NO_OPTION,
	//      JOptionPane.QUESTION_MESSAGE,
	//      new ImageIcon(System.getProperty("NETLAB_HOME")+"/images/48x48/info.png"),
	//      options,
	//      options[1]);
	//  if (n==0){
	//    try {
        //          Process proc;
    	//          if (netkitWindow.stopNetKit() != 0)
  	// 	    Thread.sleep(20000);

	// 	 String cmd ="vclean -K";
        //          proc = Runtime.getRuntime().exec(cmd,null);

	//    } catch (Exception ex)
        //      {System.out.println("Error " + ex);}
		   
	//  }
	// 	}
	//     });

	JMenuItem userMan = new JMenuItem("User Manual");
	helpMenu.add(userMan);
		
	userMan.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    UtilNetGUI.displayUserManual();
		}
	    });
        
        return menuBar;
    }
    
    

    
    // Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    }

    private void initWorkSpaceForNetKit ()
    {


	String homeDir = System.getProperty("user.home");


	File d = new File (homeDir + "/.netkitgui");
	deleteDir(d);
	d.mkdir();
	
    
	UtilNetGUI.setCurrentWorkSpace(d);
		
	// Creamos el fichero de configuración
	netkitWindow.saveProject(new File(
					  UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + "/" +
					  "netgui.nkp"));
	UtilNetGUI.setAplicationStatusChange(false);
	netkitWindow.repaint();
    }

    
    public static void main(String args[]) {
	Locale.setDefault(Locale.US);
	new NetKitViewer();
    }
}
