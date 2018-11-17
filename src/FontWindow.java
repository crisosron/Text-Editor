import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FontWindow extends JFrame implements ActionListener {
    private String[] availableFontFamilyNamesArray;
    private Font[] allAvailableFonts;
    private static final int FONT_WINDOW_WIDTH = 700;
    private static final int FONT_WINDOW_HEIGHT = 600;
    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_HEIGHT = 200;
    private JPanel fontFamilyPanel, fontStylePanel, fontSizePanel, samplePanel;

    public FontWindow() {

        /*Initialising panels*/
        fontFamilyPanel = new JPanel();
        fontStylePanel = new JPanel();
        fontSizePanel = new JPanel();
        samplePanel = new JPanel();

        setUpFontWindowUI();
    }
    public void setUpFontWindowUI(){

        /*Setting up the frame*/
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); //Gets rid of the this frame but the application stays open (therefore main frame stays)
        setLayout(null);
        setTitle("Configure Font");
        setSize(new Dimension(FONT_WINDOW_WIDTH, FONT_WINDOW_HEIGHT));
        setResizable(false);
        setVisible(true);

        /*Setting up the panels*/
        panelSetup();

        /*Setting up the lists*/
        setupJLists();

        /*Setting up the buttons*/
        setupButtons();

    }

    /**
     * Setting up the JPanel objects - This includes positioning of the panel within the frame
     */
    public void panelSetup(){

        /*Creating titled border for the sample panel (other titled borders are set within JScrollPane objects
        that accompanies the JList objects)*/
        samplePanel.setBorder(BorderFactory.createTitledBorder("Sample"));

        /*x and y coordinates of the top left of corner of the bounding rectangle for the panels*/
        int x = 20;
        int y = 20;

        /*Absolutely positioning the panels in the frame*/
        fontFamilyPanel.setBounds(x, y, PANEL_WIDTH, PANEL_HEIGHT); //setBounds(x, y, width, height); - Used for absolute positioning
        x += PANEL_WIDTH + 20;
        fontStylePanel.setBounds(x, y, PANEL_WIDTH, PANEL_HEIGHT);
        x+=PANEL_WIDTH + 20;
        fontSizePanel.setBounds(x, y, PANEL_WIDTH, PANEL_HEIGHT);
        y+=PANEL_HEIGHT + 20;
        x = 20;
        samplePanel.setBounds(x, y, PANEL_WIDTH * 3 + (20*2), PANEL_HEIGHT);

        /*Adding the panels to the lists*/
        add(fontFamilyPanel);
        add(fontStylePanel);
        add(fontSizePanel);
        add(samplePanel);

    }

    public void setupButtons(){
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
        confirmButton.setActionCommand("Confirm");
        cancelButton.setActionCommand("Cancel");
    }

    /**
     * Setting up the JList objects - This includes filling up the list with values
     */
    public void setupJLists(){

        /*Getting available fonts in machine and storing into array*/
        availableFontFamilyNamesArray = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(); //Names of fonts
        allAvailableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts(); //Actual font objects

        /* ---- Setting up the font family list ---- */
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for(int i = 0; i < availableFontFamilyNamesArray.length; i++){ //Adding all font names to default list model
            listModel.addElement(availableFontFamilyNamesArray[i]);
        }

        /*Custom cell renderer for fontFamilyList JList object - makes it so that every individual cell that
        * represents a font is written in the font that it represents*/
        //TODO: Put this into its own class so that the fontStyleList can also take advantage of this (parameterize)
        ListCellRenderer listCellRenderer = new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                JLabel label = new JLabel(); //The selected element is a JLabel object
                label.setOpaque(true); //Allows the selection indication to function
                label.setText(value.toString());
                label.setFont(new Font(value.toString(), Font.PLAIN, 12));

                /*Setting the background for the selected element*/
                if(isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                }else{
                    label.setBackground(list.getBackground());
                    label.setForeground(list.getForeground());
                }
                return label;
            }
        };

        JList fontFamilyList = new JList(listModel);
        fontFamilyList.setCellRenderer(listCellRenderer);
        JScrollPane fontFamilyListScroll = new JScrollPane(fontFamilyList);
        fontFamilyListScroll.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        fontFamilyListScroll.setBorder(BorderFactory.createTitledBorder("Font Family"));

        /*Setting up the font style list*/
        String[] fontStyleTempArray = new String[]{"Regular", "Italic", "Bold"}; //Initializing with 3 values
        JList fontStyleList = new JList(fontStyleTempArray);
        JScrollPane fontStyleListScroll = new JScrollPane(fontStyleList);
        fontStyleListScroll.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        fontStyleListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        fontStyleListScroll.setBorder(BorderFactory.createTitledBorder("Font Style"));

        /*Setting up the font size list*/
        DefaultListModel<Integer> dataModel = new DefaultListModel<>(); //Used in conjunction with JList since JList class does not have a class that can take integer arrays
        JList<Integer> fontSizeList = new JList<>(dataModel);
        int fontVal = 2;
        for(int i = 0; i < 21; i++){
            dataModel.addElement(fontVal);
            fontVal+=2;
        }
        JScrollPane fontSizeListScroll = new JScrollPane(fontSizeList);
        fontSizeListScroll.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        fontSizeListScroll.setBorder(BorderFactory.createTitledBorder("Font Size"));

        fontFamilyPanel.add(fontFamilyListScroll);
        fontStylePanel.add(fontStyleListScroll);
        fontSizePanel.add(fontSizeListScroll);
    }

    public void actionPerformed(ActionEvent e){
        String action = e.getActionCommand();
        if(action.equals("Confirm")) confirm();
        else if(action.equals("Cancel"))dispose();

    }

    public void updateSample(){
        //TODO: Develop this
    }

    public void confirm(){
        //TODO: Add functionality here
    }
}
