import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PaintWindow extends JFrame implements MouseListener, MouseMotionListener, ActionListener {

    /*Collections to manage panels*/
    private Map<String, JPanel> panelMap;
    private Set<String> panelNamesSet;

    /*Constants*/
    private final int SIDE_PANEL_WIDTH = 250;
    private final int SIDE_PANEL_HEIGHT = 800;
    private final int CANVAS_PANEL_WIDTH = TextEditor.FRAME_WIDTH - SIDE_PANEL_WIDTH;
    private final int CANVAS_PANEL_HEIGHT = TextEditor.FRAME_HEIGHT;

    public PaintWindow(){

        setupPaintWindowUI();
    }

    public void setupPaintWindowUI(){

        /*Setting up the paint window frame*/
        setTitle("Paint");
        setLayout(null);
        setSize(new Dimension(TextEditor.FRAME_WIDTH, TextEditor.FRAME_HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE); //TODO: Change to ignore - need to implement save check at some point
        setVisible(true);
        setResizable(false);

        panelNamesSet = new HashSet<>(Arrays.asList("Menu", "Canvas"));

        /*Creating new panels*/
        createPanel("Side Panel", 0, 0, SIDE_PANEL_WIDTH, SIDE_PANEL_HEIGHT);
        createPanel("Canvas Panel", SIDE_PANEL_WIDTH, 0, CANVAS_PANEL_WIDTH, CANVAS_PANEL_HEIGHT);
    }

    /**
     * Method that creates panels and positions them onto the frame
     */
    public void createPanel(String name, int x, int y, int width, int height){
        JPanel newPanel = new JPanel();
        newPanel.setBounds(x, y, width, height);
        newPanel.setBorder(BorderFactory.createTitledBorder(name));
        add(newPanel);
    }

    /*For MouseListener*/
    public void mouseClicked(MouseEvent mouseEvent){}
    public void mousePressed(MouseEvent mouseEvent){}
    public void mouseReleased(MouseEvent mouseEvent){}
    public void mouseEntered(MouseEvent mouseEvent){}
    public void mouseExited(MouseEvent mouseEvent){}

    /*For MouseMotionListener*/
    public void mouseDragged(MouseEvent mouseEvent){}
    public void mouseMoved(MouseEvent mouseEvent){}

    /*For ActionListener*/
    public void actionPerformed(ActionEvent actionEvent){}

}
