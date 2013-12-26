
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fran
 */
public class PanEventHandler extends PPanEventHandler{
    
    PCanvas canvas;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Point hotSpot = new Point(0,0);
    // Imagenes de mano abierta y cerrada
    Image open = toolkit.getImage(System.getProperty("NETLAB_HOME") + "/images/32x32/open_hand.png");    
    Image close = toolkit.getImage(System.getProperty("NETLAB_HOME") + "/images/32x32/close_hand.png");
    Cursor cursorOpen = toolkit.createCustomCursor(open, hotSpot, "openHand");    
    Cursor cursorClose = toolkit.createCustomCursor(close, hotSpot, "closeHand");
    
    public PanEventHandler(PCanvas canvas){
        this.canvas = canvas;
    }
    
    @Override
    protected void startDrag(PInputEvent e){
        super.startDrag(e);
        canvas.setBackground(Color.LIGHT_GRAY);
        canvas.setCursor(cursorClose);
    }
    
    @Override
    protected void drag(PInputEvent e){
        super.drag(e);
        canvas.setBackground(Color.LIGHT_GRAY);        
        canvas.setCursor(cursorClose);
        
    }
    
    @Override
    protected void endDrag(PInputEvent e){
        super.endDrag(e);
        canvas.setBackground(Color.WHITE);        
        canvas.setCursor(cursorOpen);
    }
    
}
