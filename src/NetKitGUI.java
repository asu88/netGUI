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
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import java.awt.*;
import java.io.File;
import java.util.*;

public class NetKitGUI extends PSwingCanvas {

    public static final long serialVersionUID = 1L;
    private PLayer nodeLayer, edgeLayer, ethLayer;
    private DisplayManager dm;
    private LayersHandler handler;
    private ScanIPsDemon scan;
    private ConectedMouseEventHandler conectedEventHandler;
    private NormalDragEventHandler normalEventHandler;
    private AddTerminalEventHandler addTerminalEventHandler;
    private AddRouterEventHandler addRouterEventHandler;
    private AddSwitchEventHandler addSwitchEventHandler;
    private AddHubEventHandler addHubEventHandler;
    private DeleteNodeEventHandler deleteNodeEventHandler;
    private DeleteConnectionEventHandler deleteConnectionEventHandler;
    private StartNodeEventHandler startNodeEventHandler;
    private StopNodeEventHandler stopNodeEventHandler;
    private RestartNodeEventHandler restartNodeEventHandler;
    //Manejador para establecer conexiones
    private boolean conectedHandlerdEnabled = false;
    //Manejador para a�adir nodos
    private boolean addTerminalHandlerdEnabled = false;
    private boolean addRouterHandlerdEnabled = false;
    private boolean addSwitchHandlerdEnabled = false;
    private boolean addHubHandlerdEnabled = false;
    private boolean deleteHandlerdEnabled = false;
    private boolean startNodeHandlerEnabled = false;
    private boolean stopNodeHandlerEnabled = false;
    private boolean restartNodeHandlerEnabled = false;
    //Manejador para mover y arrastrar nodos (activado por defecto)
    private boolean normalHandlerEnabled = true;
    
    public ScanRoutersDemon scanRouters;

    public NetKitGUI(int width, int height) {
        //Establecer el tamaño de la ventana y la escala de la cámara
        setPreferredSize(new Dimension(width, height));
        this.getZoomEventHandler().setMaxScale(2.0);
        this.getZoomEventHandler().setMinScale(0.6);

        dm = new DisplayManager(getRoot(), getCamera());

        nodeLayer = new PLayer();
        edgeLayer = new PLayer();
        ethLayer = new PLayer();

        dm.addLayerOnTop(edgeLayer);
        dm.addLayerOnTop(nodeLayer);
        dm.addLayerOnTop(ethLayer);

        UtilNetGUI.reset();

        handler = new LayersHandler(nodeLayer, edgeLayer, ethLayer);

        //Inicializamos los manejadores de eventos
        conectedEventHandler = new ConectedMouseEventHandler(this, handler);
        normalEventHandler = new NormalDragEventHandler(this);
        addTerminalEventHandler = new AddTerminalEventHandler(handler);
        addRouterEventHandler = new AddRouterEventHandler(handler);
        addSwitchEventHandler = new AddSwitchEventHandler(handler);
        addHubEventHandler = new AddHubEventHandler(handler);
        deleteNodeEventHandler = new DeleteNodeEventHandler(handler);
        deleteConnectionEventHandler = new DeleteConnectionEventHandler(handler);
        startNodeEventHandler = new StartNodeEventHandler(handler);
        stopNodeEventHandler = new StopNodeEventHandler(handler);
        restartNodeEventHandler = new RestartNodeEventHandler(handler);
        //inicializamos el threads que se encargar� de actualizar las ips de todas
        //las m�quinas virtuales.
        runDemons();

        //testing display layers on/off
        //getCamera().removeLayer(ethLayer);

        /*
         PCamera c = new PCamera();
         c.setBounds(0, 0, 160, 100);
         c.scaleView(0.26);
         c.addLayer(nodeLayer);
         c.setPaint(Color.yellow);
         getLayer().addChild(c);
         */

        // Crea un manejador de eventos para mover nodos y actualizar las conexiones "codos"
        nodeLayer.addInputEventListener(normalEventHandler);
    }

    public synchronized PLayer getNodeLayer() {
        return nodeLayer;
    }

    private void runDemons(){
        scan = new ScanIPsDemon(nodeLayer);
        scan.start();
        
        scanRouters = new ScanRoutersDemon(nodeLayer);
        scanRouters.start();
    }
    
    /**
     * **************************************************************
     * Guarda el proyecto actual
	 ***************************************************************
     */
    public void saveProject(File file) {
        String fileName = file.getPath();
        NKProjectWriter pWrite = new NKProjectWriter(nodeLayer, edgeLayer);
        pWrite.save(fileName);
    }

    /**
     * **************************************************************
     * Abre un proyecto anterior
	 ***************************************************************
     */
    public void openProject(File file) {
        String fileName = file.getPath();
        NKProjectReader pReader = new NKProjectReader(handler);
        pReader.load(fileName);
    }

    /**
     * **************************************************************
     * Establece el modo selecci�n
	 ***************************************************************
     */
    public void enableSelectMode() {
        if (!normalHandlerEnabled) {
            enableNormalInputEventHandler();
            disableConectedInputEventHandler();
            disableAddTerminalInputEventHandler();
            disableAddRouterInputEventHandler();
            disableAddSwitchInputEventHandler();
            disableAddHubInputEventHandler();
            disableDeleteInputEventHandler();
            disableStartNodeInputEventHandler();
            disableStopNodeInputEventHandler();
            disableRestartNodeInputEventHandler();

        }
    }

    /**
     * **************************************************************
     * Arranca netkit. Devuelve el numero de m�quinas arrancadas
	 ***************************************************************
     */
    public int startNetKit() {
        int started = 0;
        Iterator i = nodeLayer.getAllNodes().iterator();
        Object obj;
        while (i.hasNext()) {
            obj = i.next();
            if (obj instanceof NKSystem) {
                if (!((NKSystem) obj).isStarted()) {
                    ((NKSystem) obj).startNetKit();
                    started++;
                }
            }
        }

        return started;
    }

    /**
     * **************************************************************
     * Detiene la ejecuci�n de netkit. Devuelve el numero de m�quinas paradas
	 ***************************************************************
     */
    public int stopNetKit() {
        int stopped = 0;
        Iterator i = nodeLayer.getAllNodes().iterator();
        Object obj;
        while (i.hasNext()) {
            obj = i.next();
            if (obj instanceof NKSystem) {
                if (((NKSystem) obj).isStarted()) {
                    //Detenemos NetKit con vhalt
                    ((NKSystem) obj).stopNetKit(false);
                    stopped++;
                }
            }
        }
        return stopped;
    }

    /**
     * **************************************************************
     * Inserta un nodo en Player actual
	 ***************************************************************
     */
    public void deleteElement() {
        if (!deleteHandlerdEnabled) {
            enableDeleteInputEventHandler();
            disableAddTerminalInputEventHandler();
            disableConectedInputEventHandler();
            disableNormalInputEventHandler();
            disableAddRouterInputEventHandler();
            disableAddSwitchInputEventHandler();
            disableAddHubInputEventHandler();
            disableStartNodeInputEventHandler();
            disableStopNodeInputEventHandler();
            disableRestartNodeInputEventHandler();
        }
    }

    /**
     * **************************************************************
     * Arranca un nodo en Player actual
	 ***************************************************************
     */
    public void startNode() {
        if (!startNodeHandlerEnabled) {
            enableStartNodeInputEventHandler();
            disableStopNodeInputEventHandler();
            disableRestartNodeInputEventHandler();
            disableAddTerminalInputEventHandler();
            disableConectedInputEventHandler();
            disableNormalInputEventHandler();
            disableAddRouterInputEventHandler();
            disableAddSwitchInputEventHandler();
            disableAddHubInputEventHandler();
            disableDeleteInputEventHandler();
        }
    }

    /**
     * **************************************************************
     * Apaga un nodo en Player actual
	 ***************************************************************
     */
    public void stopNode() {
        if (!stopNodeHandlerEnabled) {
            enableStopNodeInputEventHandler();
            disableStartNodeInputEventHandler();
            disableRestartNodeInputEventHandler();
            disableAddTerminalInputEventHandler();
            disableConectedInputEventHandler();
            disableNormalInputEventHandler();
            disableAddRouterInputEventHandler();
            disableAddSwitchInputEventHandler();
            disableAddHubInputEventHandler();
            disableDeleteInputEventHandler();
        }
    }

    /**
     * **************************************************************
     * Rearranca un nodo en Player actual
	 ***************************************************************
     */
    public void restartNode() {
        if (!restartNodeHandlerEnabled) {
            enableRestartNodeInputEventHandler();
            disableStartNodeInputEventHandler();
            disableStopNodeInputEventHandler();
            disableAddTerminalInputEventHandler();
            disableConectedInputEventHandler();
            disableNormalInputEventHandler();
            disableAddRouterInputEventHandler();
            disableAddSwitchInputEventHandler();
            disableAddHubInputEventHandler();
            disableDeleteInputEventHandler();
        }
    }

    /**
     * **************************************************************
     * Inserta un terminal en la escena
	 ***************************************************************
     */
    public void addTerminal() {
        if (!addTerminalHandlerdEnabled) {
            enableAddTerminalInputEventHandler();
            disableConectedInputEventHandler();
            disableNormalInputEventHandler();
            disableAddRouterInputEventHandler();
            disableAddSwitchInputEventHandler();
            disableAddHubInputEventHandler();
            disableDeleteInputEventHandler();
            disableStartNodeInputEventHandler();
            disableStopNodeInputEventHandler();
            disableRestartNodeInputEventHandler();
        }
    }

    /**
     * **************************************************************
     * Inserta un router en la escena
	 ***************************************************************
     */
    public void addRouter() {
        if (!addRouterHandlerdEnabled) {
            enableAddRouterInputEventHandler();
            disableConectedInputEventHandler();
            disableNormalInputEventHandler();
            disableAddTerminalInputEventHandler();
            disableAddSwitchInputEventHandler();
            disableAddHubInputEventHandler();
            disableDeleteInputEventHandler();
            disableStartNodeInputEventHandler();
            disableStopNodeInputEventHandler();
            disableRestartNodeInputEventHandler();
        }
    }

    /**
     * **************************************************************
     * Inserta un switch en la escena
	 ***************************************************************
     */
    public void addSwitch() {
        if (!addSwitchHandlerdEnabled) {
            enableAddSwitchInputEventHandler();
            disableConectedInputEventHandler();
            disableNormalInputEventHandler();
            disableAddTerminalInputEventHandler();
            disableAddRouterInputEventHandler();
            disableAddHubInputEventHandler();
            disableDeleteInputEventHandler();
            disableStartNodeInputEventHandler();
            disableStopNodeInputEventHandler();
            disableRestartNodeInputEventHandler();
        }
    }

    /**
     * **************************************************************
     * Inserta un hub en la escena
	 ***************************************************************
     */
    public void addHub() {
        if (!addHubHandlerdEnabled) {
            enableAddHubInputEventHandler();
            disableAddTerminalInputEventHandler();
            disableAddRouterInputEventHandler();
            disableAddSwitchInputEventHandler();
            disableConectedInputEventHandler();
            disableNormalInputEventHandler();
            disableDeleteInputEventHandler();
            disableStartNodeInputEventHandler();
            disableStopNodeInputEventHandler();
            disableRestartNodeInputEventHandler();
        }
    }

    /**
     * **************************************************************
     * Crea una conexion entre dos nodos de la escena
	 ***************************************************************
     */
    public void addConexion() {
        if (!conectedHandlerdEnabled) {
            //Activamos el manejador para conectarlos
            enableConectedInputEventHandler();
            //Desconectamos los eventos para arrastrar y mover nodos
            disableNormalInputEventHandler();
            //Desconectamos los eventos para a�adir nodos
            disableAddTerminalInputEventHandler();
            disableAddRouterInputEventHandler();
            disableAddSwitchInputEventHandler();
            disableAddHubInputEventHandler();
            disableDeleteInputEventHandler();
            disableStartNodeInputEventHandler();
            disableStopNodeInputEventHandler();
            disableRestartNodeInputEventHandler();
        }
    }

    /**
     * **************************************************************
     * Crea una conexion entre dos nodos de la escena
	 ***************************************************************
     */
    public void centerView() {
        getCamera().animateViewToCenterBounds(nodeLayer.getUnionOfChildrenBounds(null), false, (long) 1500.0);
    }

    /**
     * **************************************************************
     * Activa un nuevo manejador de eventos para el PCanvas
	 ***************************************************************
     */
    private void enableNormalInputEventHandler() {
        nodeLayer.addInputEventListener(normalEventHandler);
        normalHandlerEnabled = true;
    }

    /**
     * **************************************************************
     * Activa el manejador de eventos para a�adir terminales
	 ***************************************************************
     */
    private void enableAddTerminalInputEventHandler() {
        addInputEventListener(addTerminalEventHandler);
        addTerminalHandlerdEnabled = true;
    }

    /**
     * **************************************************************
     * Activa el manejador de eventos para a�adir routers
	 ***************************************************************
     */
    private void enableAddRouterInputEventHandler() {
        addInputEventListener(addRouterEventHandler);
        addRouterHandlerdEnabled = true;
    }

    /**
     * **************************************************************
     * Activa el manejador de eventos para a�adir switches
	 ***************************************************************
     */
    private void enableAddSwitchInputEventHandler() {
        addInputEventListener(addSwitchEventHandler);
        addSwitchHandlerdEnabled = true;
    }

    /**
     * **************************************************************
     * Activa el manejador de eventos para a�adir hubs
	 ***************************************************************
     */
    private void enableAddHubInputEventHandler() {
        addInputEventListener(addHubEventHandler);
        addHubHandlerdEnabled = true;
    }

    /**
     * **************************************************************
     * Activa un nuevo manejador de eventos para el PCanvas
	 ***************************************************************
     */
    private void enableConectedInputEventHandler() {
        addInputEventListener(conectedEventHandler);
        conectedHandlerdEnabled = true;
    }

    /**
     * **************************************************************
     * Activa el manejador de eventos para eliminar elementos
	 ***************************************************************
     */
    private void enableDeleteInputEventHandler() {
        edgeLayer.addInputEventListener(deleteConnectionEventHandler);
        nodeLayer.addInputEventListener(deleteNodeEventHandler);
        deleteHandlerdEnabled = true;
    }

    /**
     * **************************************************************
     * Activa el manejador de eventos para iniciar nodos
	 ***************************************************************
     */
    private void enableStartNodeInputEventHandler() {
        nodeLayer.addInputEventListener(startNodeEventHandler);
        startNodeHandlerEnabled = true;
    }

    /**
     * **************************************************************
     * Activa el manejador de eventos para parar nodos
	 ***************************************************************
     */
    private void enableStopNodeInputEventHandler() {
        nodeLayer.addInputEventListener(stopNodeEventHandler);
        stopNodeHandlerEnabled = true;
    }

    /**
     * **************************************************************
     * Activa el manejador de eventos para reiniciar nodos
	 ***************************************************************
     */
    private void enableRestartNodeInputEventHandler() {
        nodeLayer.addInputEventListener(restartNodeEventHandler);
        restartNodeHandlerEnabled = true;
    }

    /**
     * **************************************************************
     * Desactiva el manejador de eventos para el nodeLayer
	 ***************************************************************
     */
    private void disableNormalInputEventHandler() {
        nodeLayer.removeInputEventListener(normalEventHandler);
        normalHandlerEnabled = false;
    }

    /**
     * **************************************************************
     * Desactiva un nuevo manejador de eventos para el PCanvas
	 ***************************************************************
     */
    private void disableConectedInputEventHandler() {
        if (conectedHandlerdEnabled) //si hay alguna transacci�n por completar la cancelamos
        {
            conectedEventHandler.cancelCurrentConexion();
        }
        removeInputEventListener(conectedEventHandler);
        conectedHandlerdEnabled = false;
    }

    /**
     * **************************************************************
     * Desactiva el manejador de eventos para a�adir terminales
	 ***************************************************************
     */
    private void disableAddTerminalInputEventHandler() {
        removeInputEventListener(addTerminalEventHandler);
        addTerminalHandlerdEnabled = false;
    }

    /**
     * **************************************************************
     * Desactiva el manejador de eventos para a�adir routers
	 ***************************************************************
     */
    private void disableAddRouterInputEventHandler() {
        removeInputEventListener(addRouterEventHandler);
        addRouterHandlerdEnabled = false;
    }

    /**
     * **************************************************************
     * Desactiva el manejador de eventos para a�adir switches
	 ***************************************************************
     */
    private void disableAddSwitchInputEventHandler() {
        removeInputEventListener(addSwitchEventHandler);
        addSwitchHandlerdEnabled = false;
    }

    /**
     * **************************************************************
     * Desactiva el manejador de eventos para a�adir hubs
	 ***************************************************************
     */
    private void disableAddHubInputEventHandler() {
        removeInputEventListener(addHubEventHandler);
        addHubHandlerdEnabled = false;
    }

    /**
     * **************************************************************
     * Desactiva el manejador de eventos para eliminar elementos
	 ***************************************************************
     */
    private void disableDeleteInputEventHandler() {
        edgeLayer.removeInputEventListener(deleteConnectionEventHandler);
        nodeLayer.removeInputEventListener(deleteNodeEventHandler);
        deleteHandlerdEnabled = false;
    }

    /**
     * **************************************************************
     * Desactiva el manejador de eventos para arrancar nodos
	 ***************************************************************
     */
    private void disableStartNodeInputEventHandler() {
        nodeLayer.removeInputEventListener(startNodeEventHandler);
        startNodeHandlerEnabled = false;
    }

    /**
     * **************************************************************
     * Desactiva el manejador de eventos para parar nodos
	 ***************************************************************
     */
    private void disableStopNodeInputEventHandler() {
        nodeLayer.removeInputEventListener(stopNodeEventHandler);
        stopNodeHandlerEnabled = false;
    }

    /**
     * **************************************************************
     * Desactiva el manejador de eventos para reiniciar nodos
	 ***************************************************************
     */
    private void disableRestartNodeInputEventHandler() {
        nodeLayer.removeInputEventListener(restartNodeEventHandler);
        restartNodeHandlerEnabled = false;
    }
    
    /**
     * **************************************************************
     * METODOS PARA PROBAR DISPLAY MANAGER, BORRAR UNA VEZ PROBADOS
	 ***************************************************************
     */
    public void showNodeLayer() {
        dm.showLayer(nodeLayer);
    }

    public void hideNodeLayer() {
        dm.hideLayer(nodeLayer);
    }

    public void upNodeLayer() {
        dm.upLayer(nodeLayer);
    }

    public void downNodeLayer() {
        dm.downLayer(nodeLayer);
    }

    public void toFrontNodeLayer() {
        dm.toFrontLayer(nodeLayer);
    }

    public void toBottomNodeLayer() {
        dm.toBottomLayer(nodeLayer);
    }
}//Fin de la clase NetKitGui
