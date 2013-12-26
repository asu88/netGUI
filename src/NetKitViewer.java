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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

public class NetKitViewer {

    public static final long serialVersionUID = 1L;
    NetKitGUI netkitWindow;
    JToolBar toolBar;
    JMenuItem showLayer = new JMenuItem("show Nodelayer");
    JMenuItem hideLayer = new JMenuItem("hide Nodelayer");
    public static JFrame netGUIWindow = new JFrame();
    Container cont;
    public static Log log = new Log();
    JSplitPane split;

    public class NoneSelectedButtonGroup extends ButtonGroup {

        public static final long serialVersionUID = 1L;

        @Override
        public void setSelected(ButtonModel model, boolean selected) {
            if (selected) {
                super.setSelected(model, selected);
            } else {
                clearSelection();
            }
        }
    }

    public NetKitViewer() {
        setNimbus();

        cont = netGUIWindow.getContentPane();
        cont.setLayout(new BorderLayout());

        toolBar = new JToolBar("NetKitBar");
        toolBar.setOrientation(JToolBar.VERTICAL);
        cont.add(toolBar, BorderLayout.WEST);


        JScrollPane mainPanel = new JScrollPane();
        mainPanel.setLayout(new ScrollPaneLayout());
        mainPanel.setPreferredSize(new Dimension(1000, 600));
        netkitWindow = new NetKitGUI(1000, 600);
        mainPanel.getViewport().add(netkitWindow);

        addButtons(toolBar);

        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainPanel, log);
        split.setDividerLocation(1000);

        cont.add(split, BorderLayout.CENTER);

        netGUIWindow.setJMenuBar(createMenuBar());
        netGUIWindow.pack();
        netGUIWindow.setTitle("NetGUI");
        netGUIWindow.setVisible(true);
        netGUIWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        String startMessage = "GSyC/Mobiquo, Grupo de Sistemas y Comunicaciones\n"
                + "Universidad Rey Juan Carlos (URJC)\n"
                + "Authors: Santiago Carot Nemesio & Francisco Javier Torcal Gonzalez\n"
                + "GNU GPL v2 License";
        JOptionPane.showMessageDialog(null,
                startMessage,
                "Bienvenido",
                JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/other/gsyc.png"));


        //obtenemos el directorio de trabajo
        initWorkSpaceForNetKit();

    }

    private void selectActionPerformed(ActionEvent evt) {
        netkitWindow.enableSelectMode();
    }

    private void editCutActionPerformed(ActionEvent evt) {
        netkitWindow.deleteElement();
    }

    private void addNewTerminalActionPerformed(ActionEvent evt) {
        netkitWindow.addTerminal();
    }

    private void addNewRouterActionPerformed(ActionEvent evt) {
        netkitWindow.addRouter();
    }

    private void addNewSwitchActionPerformed(ActionEvent evt) {
        netkitWindow.addSwitch();
    }

    private void addNewHubActionPerformed(ActionEvent evt) {
        netkitWindow.addHub();
    }

    private void addNewConectionActionPerformed(ActionEvent evt) {
        netkitWindow.addConexion();
    }

    private void moveActionPerformed(ActionEvent evt) {
        netkitWindow.enableMoveMode();
    }

    private void zoomActionPerformed(ActionEvent evt) {
        netkitWindow.enableZoomMode();
    }

    private void addButtons(JToolBar toolBar) {
        NoneSelectedButtonGroup group = new NoneSelectedButtonGroup();

        // System button 
        JToggleButton addTerminal = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/new2/icons/32x32/system.png"));
        addTerminal.setToolTipText("new host");
        group.add(addTerminal);
        toolBar.add(addTerminal);

        JToggleButton addRouter = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/new2/icons/32x32/router.png"));
        addRouter.setToolTipText("new router");
        group.add(addRouter);
        toolBar.add(addRouter);


        JToggleButton addSwitch = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/32x32/switch.png"));
        addSwitch.setToolTipText("new switch");
        group.add(addSwitch);
        toolBar.add(addSwitch);

        JToggleButton addHub = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/new2/icons/32x32/hub.png"));
        addHub.setToolTipText("new hub");
        group.add(addHub);
        toolBar.add(addHub);

        JToggleButton addConexion = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/32x32/rj45_cable.png"));
        addConexion.setToolTipText("connect...");
        group.add(addConexion);
        toolBar.add(addConexion);


        toolBar.addSeparator();

        JToggleButton editCutMode = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/32x32/editcut.png"));
        editCutMode.setToolTipText("delete");
        group.add(editCutMode);
        toolBar.add(editCutMode);

        JToggleButton selectMode = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/32x32/arrow.png"));
        selectMode.setToolTipText("select");
        group.add(selectMode);
        toolBar.add(selectMode);


        selectMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                selectActionPerformed(evt);
            }
        });

        editCutMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                editCutActionPerformed(evt);
            }
        });

        addTerminal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                addNewTerminalActionPerformed(evt);
            }
        });

        addRouter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                addNewRouterActionPerformed(evt);
            }
        });

        addSwitch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                addNewSwitchActionPerformed(evt);
            }
        });

        addHub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                addNewHubActionPerformed(evt);
            }
        });

        addConexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                addNewConectionActionPerformed(evt);
            }
        });

        toolBar.addSeparator();

        JToggleButton runButton = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/32x32/run.png"));
        runButton.setToolTipText("start host/router/switch");
        toolBar.add(runButton);

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                startNetKitActionPerformed(evt);
            }
        });

        JToggleButton stopButton = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/32x32/exit.png"));
        stopButton.setToolTipText("stop host/router/switch");
        toolBar.add(stopButton);

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopNetKitActionPerformed(evt);
            }
        });

        JToggleButton restartButton = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/32x32/restart.png"));
        restartButton.setToolTipText("restart host/router/switch");
        toolBar.add(restartButton);

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                restartNetKitActionPerformed(evt);
            }
        });

        JToggleButton captureButton = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/32x32/capture.png"));
        captureButton.setToolTipText("traffic capture");
        toolBar.add(captureButton);

        captureButton.addActionListener(new CaptureTrafficEventHandler(netkitWindow));


        toolBar.addSeparator();

        JToggleButton centerButton = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/32x32/center.png"));
        centerButton.setToolTipText("center view");
        toolBar.add(centerButton);

        centerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                centerNetKitActionPerformed(evt);
            }
        });

        JToggleButton moveButton = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/32x32/move.png"));
        moveButton.setToolTipText("move all");
        toolBar.add(moveButton);

        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                moveActionPerformed(evt);
            }
        });

        JToggleButton zoomButton = new JToggleButton(new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/32x32/lupa.png"));
        zoomButton.setToolTipText("zoom");
        toolBar.add(zoomButton);

        zoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                zoomActionPerformed(evt);
            }
        });


        group.add(captureButton);
        group.add(moveButton);
        group.add(zoomButton);
        group.add(runButton);
        group.add(stopButton);
        group.add(restartButton);
        group.add(centerButton);

    }

    private void saveAsActionPerformed(ActionEvent evt) {

        String currentPath = UtilNetGUI.getCurrentWorkSpace().getAbsolutePath();


        String newPath = WorkSpace.exploreDirectories("Save as");
        if (newPath == null) {
            return;
        }

        File oldDir = new File(currentPath);
        File newDir = new File(newPath);

        if (!newDir.exists()) {
            newDir.mkdir();
        }

        try {
            Process proc;
            String cmd = "tar  -Scf " + newPath + "/.tmpnk  --directory " + currentPath + "/ .";
            String cmd2 = "tar xf " + newPath + "/.tmpnk  --directory " + newPath + "/";
            proc = Runtime.getRuntime().exec(cmd, null);
            proc.waitFor();
            proc = Runtime.getRuntime().exec(cmd2, null);
            proc.waitFor();
            proc = Runtime.getRuntime().exec("rm " + newPath + "/.tmpnk", null);

        } catch (Exception ex) {
            System.out.println("Error " + ex);
        }


        netGUIWindow.setTitle("NetGUI: " + newDir.getName());
        UtilNetGUI.setCurrentWorkSpace(newDir);




        netkitWindow.saveProject(new File(
                UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + "/"
                + "netgui.nkp"));

        UtilNetGUI.setAplicationStatusChange(false);

        JOptionPane changesSaved = new JOptionPane("Changes saved.", JOptionPane.INFORMATION_MESSAGE);
        changesSaved.setVisible(true);

    }

    private void saveActionPerformed(ActionEvent evt) {

        boolean salir = false;
        String currentPath = UtilNetGUI.getCurrentWorkSpace().getAbsolutePath();
        String tempPath = System.getProperty("user.home") + "/.netkitgui";

        File oldDir, newDir;


        // System.out.println("Dir0 currentPath: " + currentPath);

        if (currentPath.equals(tempPath)) {

            do {
                String newPath = WorkSpace.saveDirectories("Save project", "Save");

                if (newPath == null) {
                    return;
                }

                if (newPath.indexOf(' ') != -1) {
                    showErrorMsg("Error", "Whitespaces are not allowed in pathname.");
                } else {

                    oldDir = new File(tempPath);
                    newDir = new File(newPath);

                    if (!newDir.exists()) {
                        oldDir.renameTo(newDir);
                        netGUIWindow.setTitle("NetGUI: " + newDir.getName());
                        UtilNetGUI.setCurrentWorkSpace(newDir);
                        salir = true;
                    } else {
                        showErrorMsg("Error", "Directory already exists.\n"
                                + "Enter a new directory for saving the project");
                    }
                }
            } while (!salir);

        }


        if (UtilNetGUI.aplicationChanged()) {
            netkitWindow.saveProject(new File(
                    UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + "/"
                    + "netgui.nkp"));

            UtilNetGUI.setAplicationStatusChange(false);

            JOptionPane changesSaved = new JOptionPane("Changes saved.", JOptionPane.INFORMATION_MESSAGE);
            changesSaved.setVisible(true);
        } else {
            JOptionPane noChanges = new JOptionPane("No changes from last saved project", JOptionPane.INFORMATION_MESSAGE);
            noChanges.setVisible(true);
        }

    }

    private void openActionPerformed(ActionEvent evt) {
        if (UtilNetGUI.aplicationChanged()) {
            if (advertiseExitWindow() == 0) {
                open();
            }
        } else {
            open();
        }

    }

    private void newActionPerformed(ActionEvent evt) {
        if (UtilNetGUI.aplicationChanged()) {
            if (advertiseExitWindow() == 0) {

                if (getNewWorkSpace() == -1) {
                    return;
                }
                deleteCurrentProyect();
                UtilNetGUI.setAplicationStatusChange(false);
            }
        } else {
            if (getNewWorkSpace() == -1) {
                return;
            }
            deleteCurrentProyect();
            UtilNetGUI.setAplicationStatusChange(false);
        }
    }

    private void deleteCurrentProyect() {
        //Finalizar la ejecuci�n de todos los NetKit arrancados
        netkitWindow.stopNetKit();
        netGUIWindow.getContentPane().remove(netkitWindow);
        netkitWindow = new NetKitGUI(700, 600);
        netGUIWindow.getContentPane().add(netkitWindow);
        netGUIWindow.pack();
    }

    private int getNewWorkSpace() {
        boolean salir = false;
        File d;
        //obtenemos el nueevo directorio de trabajo
        do {
            String workDir = WorkSpace.saveDirectories("New project", "Create");
            if (workDir == null) {
                return -1;
            }

            d = new File(workDir);
            if (d.exists()) {
                showErrorMsg("Error", "Directory already exists.\n"
                        + "Enter a new directory for the new project.");
            } else if (workDir.indexOf(' ') != -1) {
                showErrorMsg("Error", "Whitespaces are not allowed in pathname.");
            } else {
                d.mkdir();
                netGUIWindow.setTitle("NetGUI: " + d.getName());
                UtilNetGUI.setCurrentWorkSpace(d);
                salir = true;
            }

        } while (!salir);

        //No existe: Creamos el fichero de configuraci�n
        netkitWindow.saveProject(new File(UtilNetGUI.getCurrentWorkSpace().getAbsolutePath()
                + "/" + "netgui.nkp"));
        UtilNetGUI.setAplicationStatusChange(false);
        netkitWindow.repaint();

        return 0;
    }

    private void setOpenWorkSpace(File fileSelected) {
        String newWorkSpace = fileSelected.getAbsolutePath();
        newWorkSpace = newWorkSpace.substring(0, newWorkSpace.lastIndexOf('/'));
        UtilNetGUI.setCurrentWorkSpace(new File(newWorkSpace));
    }

    private void open() {

        String newPath = WorkSpace.exploreDirectories("Open");

        if (!(newPath == null)) {
            File d = new File(newPath);
            File f = new File(newPath + "/" + "netgui.nkp");

            // Compatibilidad con versiones antiguas de NetGUI donde
            // se utilizaba otro nombre para el .nkp
            if (!f.exists()) {
                f = new File(newPath + "/" + d.getName() + ".nkp");
            }

            if (f.exists()) {
                String newWorkSpace = f.getAbsolutePath();

                if (newWorkSpace.indexOf(' ') != -1) {
                    showErrorMsg("Error", "Whitespaces are not allowed in the pathname.");
                    return;
                }
                deleteCurrentProyect();
                netGUIWindow.setTitle("NetGUI: " + d.getName());
                setOpenWorkSpace(f);
                netkitWindow.openProject(f);
                UtilNetGUI.setAplicationStatusChange(false);
                UtilNetGUI.readNetGUIConf();
            } else {
                showErrorMsg("Error", "There is no NetGUI project in the selected directory.");
            }
        }
    }

    private int advertiseExitWindow() //si el valor devuelto = 0 -> El usuario ignora la advertencia de guardar 
    //valor = 1 -> El usuario se arrepiente 
    {
        String msg = "WARNING: There are changes that are not saved.\n"
                + "Do you want to exit without saving?";

        int n = JOptionPane.showConfirmDialog(null,
                msg,
                "WARNING!!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/48x48/info.png"));

        return n;

//        int n = JOptionPane.showOptionDialog(this,
//                msg,
//                "ATENTION",
//                JOptionPane.YES_NO_OPTION,
//                JOptionPane.QUESTION_MESSAGE,
//                new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/48x48/info.png"),
//                options,
//                options[1]);
        //return n;
    }

    private void startNetKitActionPerformed(ActionEvent evt) {
        // netkitWindow.enableStartNode ();
        netkitWindow.startNode();
    }

    private void stopNetKitActionPerformed(ActionEvent evt) {
        netkitWindow.stopNode();
        //if (netkitWindow.stopNetKit() == 0)
        //No hab�a m�quinas paradas
        //    showErrorMsg("Apagando nodos","No existen nodos que apagar");
    }

    private void restartNetKitActionPerformed(ActionEvent evt) {
        // netkitWindow.enableStartNode ();
        netkitWindow.restartNode();
    }

    private void centerNetKitActionPerformed(ActionEvent evt) {
        netkitWindow.centerView();
    }

    private void showErrorMsg(String tittleMsg, String msg) {
        JOptionPane.showMessageDialog(null,
                msg,
                tittleMsg,
                JOptionPane.ERROR_MESSAGE,
                new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/48x48/stop.png"));
        //showError.setVisible(true);
    }

    /**
     * *****************************************
     * Crea el menu principal de la aplicaci�n
     * *****************************************
     */
    private JMenuBar createMenuBar() {
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
            @Override
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
            @Override
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
            @Override
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
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (UtilNetGUI.aplicationChanged()) {
                    if (advertiseExitWindow() == 0) {
                        netkitWindow.stopNetKit();
                        System.exit(0);
                    }
                } else {
                    netkitWindow.stopNetKit();
                    System.exit(0);
                }

            }
        });


        //*************************************************************
        //Creamos el menu de Preferencias
        //*************************************************************
        JMenu preferences = new JMenu("Preferences");
        menuBar.add(preferences);

        JMenuItem ip = new JMenuItem("IP options");
        ip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] IPCases = {"Show IPv4", "Show IPv6", "Show both"};
                String selectedOption = (String) JOptionPane.showInputDialog(null,
                        "Select one option: ",
                        "IP options",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        IPCases,
                        IPCases[0]);

                if (selectedOption.contains("IPv4")) {
                    UtilNetGUI.setIpv4AddrShow(true);
                    UtilNetGUI.setIpv6AddrShow(false);
                } else if (selectedOption.contains("IPv6")) {
                    UtilNetGUI.setIpv4AddrShow(false);
                    UtilNetGUI.setIpv6AddrShow(true);
                } else if (selectedOption.contains("both")) {
                    UtilNetGUI.setIpv4AddrShow(true);
                    UtilNetGUI.setIpv6AddrShow(true);
                }

            }
        });
        preferences.add(ip);

        JCheckBoxMenuItem logOpt = new JCheckBoxMenuItem("Log panel");
        logOpt.setState(true);

        logOpt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                split.getRightComponent().setVisible(!split.getRightComponent().isVisible());
                if (split.getRightComponent().isVisible()){
                    split.setDividerLocation(0.82);
                } else {
                    split.setDividerLocation(1);
                }
                
            }
        });

        preferences.add(logOpt);
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
            @Override
            public void actionPerformed(ActionEvent evt) {
                JOptionPane.showMessageDialog(SwingUtilities.getRoot((Component) evt.getSource()),
                        "GSyC/Mobiquo, Grupo de Sistemas y Comunicaciones\n"
                        + "Universidad Rey Juan Carlos (URJC)\n"
                        + "Author: Santiago Carot Nemesio & Francisco Javier Torcal González\n"
                        + "GNU GPL v2 License",
                        "NetGUI version 0.4.10",
                        JOptionPane.INFORMATION_MESSAGE,
                        new ImageIcon(System.getProperty("NETLAB_HOME") + "/images/other/gsyc.png"));


            }
        });


        // JMenuItem clean = new JMenuItem("Forzar la parada de nodos");
        // helpMenu.add(clean);

        // clean.addActionListener(new ActionListener() {
        // 	public void actionPerformed(ActionEvent evt) {
        // String msg="ATENCION: Forzar la parada puede hacerle perder cambios\n" +            "El proceso de parada forzada dura 30 segundos durante\n"+
        //     "los cuales NetGUI no interacciona con el usuario\n"+
        //     "�Desea forzar la parada de los nodos arrancados, o prefiere cancelar?";
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
            @Override
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
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    private void initWorkSpaceForNetKit() {


        String homeDir = System.getProperty("user.home");


        File d = new File(homeDir + "/.netkitgui");
        deleteDir(d);
        d.mkdir();


        UtilNetGUI.setCurrentWorkSpace(d);

        // Creamos el fichero de configuraci�n
        netkitWindow.saveProject(new File(
                UtilNetGUI.getCurrentWorkSpace().getAbsolutePath() + "/"
                + "netgui.nkp"));
        UtilNetGUI.setAplicationStatusChange(false);
        netkitWindow.repaint();
    }

    private void setNimbus() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to cross-platform
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                // not worth my time
            }
        }
    }

    public static void main(String args[]) {
        NetKitViewer nkv = new NetKitViewer();


    }
}
