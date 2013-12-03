
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Fran
 */
public class Log extends JScrollPane {

    public static final long serialVersionUID = 1L;
    JEditorPane editor = new JEditorPane();
    public final int log_width = 200;
    public final int log_height = 600;

    public Log() {
        setLayout(new ScrollPaneLayout());
        setPreferredSize(new Dimension(225, 600));
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        Date date = new Date();
        getViewport().add(editor);
        editor.setEditable(false);
        editor.setBounds(0, 0, log_width, log_height);
        editor.setBackground(Color.BLACK);
        editor.setForeground(Color.GREEN);
        String intro = "=====================\n"
                +       "| " + date.toString() + " |\n"
                +      "=====================\n" ;
        editor.setText(intro);
    }

    public synchronized void addText(String text) {
        editor.setText(editor.getText() + text + '\n');
        goDown();
    }
    
    public String getText(){
        return editor.getText();
    }
    
    public void delete(String node){
        addText("> " + node + " disconnected from GUI");
    }

    private void goDown() {
        getViewport().setViewPosition(new Point(0, editor.getSize().height));
    }
}
