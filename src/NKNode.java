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

 *******************************************************************
 * NKNode es una clase que establece todas las propiedades de los 
 * nodos que se definen sobre NetKit en Picolo.
 *******************************************************************

 */
import edu.umd.cs.piccolo.nodes.*;

public abstract class NKNode extends PImage {

    private String name;
    private PImage image;
    private PImage selected_image;
    private PImage deleted_image;

    /**
     * ***********************************************************
     * Constructor de la clase
	 ************************************************************
     */
    public NKNode(String name, String imgFile, String img_selectedFile, String img_deletedFile) {
        super(imgFile);
        this.name = name;
        image = new PImage(imgFile);
        selected_image = new PImage(img_selectedFile);
        deleted_image = new PImage(img_deletedFile);
        ShowDisplayName(name);
    }

    /**
     * ******************************************
     * Devuelve el nombre del nodo
	 *******************************************
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * ***********************************************************
     * A�ade una conexi�n al nodo
	 ************************************************************
     */
    public abstract void addEdge(NKConection edge);

    /**
     * ***********************************************************
     * Elimina una conexi�n del nodo
	 ************************************************************
     */
    public abstract void removeEdge(NKConection edge);

    /**
     * ***********************************************************
     * Compara este nodo con otro (Devuelve true si tienen el mismo nombre)
	 ************************************************************
     */
    public boolean equals(NKNode n) {
        return name.equalsIgnoreCase(n.getName());
    }

    /**
     * ***********************************************************
     * Actualiza todas las conexiones del nodo para poder representarlas en
     * pantalla cuando el nodo es arrastrado
	 ************************************************************
     */
    public abstract void updateEdges();

    /**
     * ***********************************************************
     * establece la imagen para el icono cuando es seleccionado
	 ************************************************************
     */
    public void setSelectedImage() {
        setImage(selected_image.getImage());
    }

    /**
     * ***********************************************************
     * establece la imagen para el icono cuando deja de ser seleccionado
	 ************************************************************
     */
    public void setNormalImage() {
        setImage(image.getImage());
    }

    /**
     * ***********************************************************
     * establece la imagen para el icono cuando va a ser eliminado
	 ************************************************************
     */
    public void setDeleteImage() {
        setImage(deleted_image.getImage());
    }
    
    

    /**
     * ***********************************************************
     * cambia la imagen para el icono cuando es seleccionado
	 ************************************************************
     */
    protected void changeSelectedImage(String newImage) {
        selected_image = new PImage(newImage);
        setSelectedImage();
    }

    /**
     * ***********************************************************
     * cambia la imagen para el icono cuando deja de ser seleccionado
	 ************************************************************
     */
    protected void changeNormalImage(String newImage) {
        image = new PImage(newImage);
        setNormalImage();
    }

    /**
     * ***********************************************************
     * Posiciona el nombre del nodo en el icono
	 ************************************************************
     */
    protected abstract void ShowDisplayName(String name);
}
