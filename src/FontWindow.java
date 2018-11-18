import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class FontWindow extends JFrame implements ActionListener, ListSelectionListener {
    //private String[] availableFontFamilyNamesArray;
    private List<String> availableFontFamilyNames;
    private Font[] allAvailableFonts;
    private static final int FONT_WINDOW_WIDTH = 700;
    private static final int FONT_WINDOW_HEIGHT = 600;
    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_HEIGHT = 200;
    private JPanel fontFamilyPanel, fontStylePanel, fontSizePanel, samplePanel;
    public static Set<String> availableFontStylesForSelectedFont = new HashSet<>();
    private JList fontFamilyList, fontStyleList;
    private JList<Integer> fontSizeList;
    private DefaultListModel listModelFontFamily, listModelFontStyle, listModelFontSize;
    String selectedFontFamily, selectedFontStyle;
    int selectedFontSize;

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

        /*Getting available fonts in machine and storing into array*/
        String[] tempAvailableFontFamilyNamesArray = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(); //Names of fonts
        allAvailableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts(); //Actual font objects
        availableFontFamilyNames = new ArrayList<>();

        /*Adding all font family names into availableFontFamilyNames set*/
        for(int i = 0; i < tempAvailableFontFamilyNamesArray.length; i++){
            availableFontFamilyNames.add(tempAvailableFontFamilyNamesArray[i]);
        }

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
        //TODO: Create a new method that handles the JScrollPane stuff and parameterize for code efficiency

        /* ---- Setting up the font family list ---- */
        listModelFontFamily = new DefaultListModel<>();
        for(String fontFamilyName : availableFontFamilyNames){
            listModelFontFamily.addElement(fontFamilyName);
        }
        fontFamilyList = new JList(listModelFontFamily);
        fontFamilyList.setCellRenderer(new FontJListCellRenderer()); //Uses custom cell renderer to customize each individual cell in this list
        JScrollPane fontFamilyListScroll = new JScrollPane(fontFamilyList);
        fontFamilyListScroll.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        fontFamilyListScroll.setBorder(BorderFactory.createTitledBorder("Font Family"));
        fontFamilyList.setSelectedIndex(0); //Selects the first item in the list
        String selectedFontFamily = fontFamilyList.getModel().getElementAt(fontFamilyList.getSelectedIndex()).toString(); //Gets the selected font family
        setAvailableFontStylesForSelectedFont(selectedFontFamily);

        /*Setting up the font style list*/
        listModelFontStyle = new DefaultListModel<>();
        for(String fontStyle : availableFontStylesForSelectedFont){
            listModelFontStyle.addElement(fontStyle);
        }
        fontStyleList = new JList(listModelFontStyle);
        fontStyleList.setCellRenderer(new FontJListCellRenderer()); //Uses custom cell renderer to customize each individual cell in this list
        JScrollPane fontStyleListScroll = new JScrollPane(fontStyleList);
        fontStyleListScroll.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        fontStyleListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        fontStyleListScroll.setBorder(BorderFactory.createTitledBorder("Font Style"));
        fontStyleList.setSelectedIndex(0);

        /*Setting up the font size list*/
        listModelFontSize = new DefaultListModel<>(); //Used in conjunction with JList since JList class does not have a class that can take integer arrays
        int fontVal = 2;
        for(int i = 0; i < 21; i++){
            listModelFontSize.addElement(fontVal);
            fontVal+=2;
        }
        fontSizeList = new JList<>(listModelFontSize);
        JScrollPane fontSizeListScroll = new JScrollPane(fontSizeList);
        fontSizeListScroll.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        fontSizeListScroll.setBorder(BorderFactory.createTitledBorder("Font Size"));
        fontSizeList.setSelectedIndex(0);

        fontFamilyList.addListSelectionListener(this);
        fontStyleList.addListSelectionListener(this);
        fontSizeList.addListSelectionListener(this);

        fontFamilyPanel.add(fontFamilyListScroll);
        fontStylePanel.add(fontStyleListScroll);
        fontSizePanel.add(fontSizeListScroll);
    }

    /**
     * Adding all available font styles for the selected font to the set
     */
    public void setAvailableFontStylesForSelectedFont(String fontFamily){
        if(!availableFontStylesForSelectedFont.isEmpty()) availableFontStylesForSelectedFont.clear();
        for(Font font : allAvailableFonts){
            String fontStyle = font.getName(); //Name of the font is the style of the font
            if(font.getFamily().equals(fontFamily)) {
                availableFontStylesForSelectedFont.add(fontStyle);
            }
        }
    }

    public void actionPerformed(ActionEvent e){
        String action = e.getActionCommand();
        if(action.equals("Confirm")) confirm();
        else if(action.equals("Cancel"))dispose();
    }

    /**
     * Used for changes in the selected items in the lists
     */
    public void valueChanged(ListSelectionEvent e){
        if(!e.getValueIsAdjusting()){
            JList list = (JList)e.getSource();
            if((availableFontFamilyNames.contains(list.getSelectedValue()) && availableFontStylesForSelectedFont.contains(list.getSelectedValue()) && availableFontStylesForSelectedFont.size() == 1)
            || availableFontFamilyNames.contains(list.getSelectedValue()) && availableFontStylesForSelectedFont.contains(list.getSelectedValue())){
                selectedFontFamily = list.getSelectedValue().toString();
                selectedFontStyle = list.getSelectedValue().toString();

            }else if(availableFontFamilyNames.contains(list.getSelectedValue())){ //TODO: Need to handle when both the font family and the font style are the same
                selectedFontFamily = list.getSelectedValue().toString();
                setAvailableFontStylesForSelectedFont(selectedFontFamily);
                updateFontStylesList();

            }else if(availableFontStylesForSelectedFont.contains(list.getSelectedValue())){
                selectedFontStyle = list.getSelectedValue().toString();

            }else if(listModelFontSize.contains(list.getSelectedValue())){
                selectedFontSize = (int)list.getSelectedValue();
                System.out.println(selectedFontSize);
            }
        }
    }

    /**
     * Updates the list model that the fontStyleList JList object uses and in turn
     * updates the actual fontStyleList JList object
     */
    public void updateFontStylesList(){
        listModelFontStyle.clear();
        for(String fontStyle : availableFontStylesForSelectedFont){
            listModelFontStyle.addElement(fontStyle);
        }
        fontStyleList.setModel(listModelFontStyle);
    }

    public void updateSample(){
        //TODO: Develop this
    }

    public void confirm(){
        //TODO: Add functionality here
        TextEditor.textEditor.setNewFont(selectedFontFamily, selectedFontStyle, selectedFontSize);
    }
}
