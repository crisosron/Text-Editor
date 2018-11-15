import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FontWindow extends JFrame {
    private String[] availableFontFamilyNamesArray;
    private Font[] allAvailableFonts;
    private static final int FONT_WINDOW_WIDTH = 500;
    private static final int FONT_WINDOW_HEIGHT = 500;
    private static final int FONT_FAMILY_PANEL_WIDTH = 100;
    private static final int FONT_FAMILY_PANEL_HEIGHT = 100;
    private static final int FONT_STYLE_PANEL_WIDTH = 100;
    private static final int FONT_STYLE_PANEL_HEIGHT = 100;
    private static final int FONT_SIZE_PANEL_WIDTH = 100;
    private static final int FONT_SIZE_PANEL_HEIGHT = 100;
    private JPanel fontFamilyPanel, fontStylePanel, fontSizePanel;
    private JList fontFamilyList, fontStyleList, fontSizeList;
    public FontWindow() {

        /*Getting available fonts in machine and storing into array*/
        availableFontFamilyNamesArray = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(); //Names of fonts
        allAvailableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts(); //Actual font objects

        /*Initialising panels*/
        fontFamilyPanel = new JPanel();
        fontStylePanel = new JPanel();
        fontSizePanel = new JPanel();

        /*Setting up the font configuration options in the JList objects*/
        fontFamilyList = new JList(availableFontFamilyNamesArray);
        String[] fontStyleTempArray = new String[]{"Regular", "Italic", "Bold"}; //Initializing with 3 values
        fontStyleList = new JList(fontStyleTempArray);

        /*Filling up the fontSize array and then filling up the fontSizeList JList object*/
        int[] fontSizeTempArray = new int[21]; //Initializing temp array with 20 empty spots
        int fontVal = 2;
        for(int i = 0; i < fontSizeTempArray.length; i++){
            fontSizeTempArray[i] = fontVal;
            fontVal += 2;
        }
        fontSizeList = new JList(fontStyleTempArray);

        setUpFontWindowUI();
    }
    public void setUpFontWindowUI(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); //Gets rid of the this frame but the application stays open (therefore main frame stays)
        setLayout(new BorderLayout());
        setTitle("Configure Font");
        setSize(new Dimension(FONT_WINDOW_WIDTH, FONT_WINDOW_HEIGHT));
        setResizable(false);
        setVisible(true);
    }
}
