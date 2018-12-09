import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class FontWindow extends JFrame implements ActionListener, ListSelectionListener {

    /*Standard Collections*/
    private List<String> availableFontFamilyNames;
    private Font[] allAvailableFonts;
    private  Set<String> availableFontStylesForSelectedFont;
    private Map<String, JScrollPane> scrollPaneMap;

    /*Constants*/
    private static final int FONT_WINDOW_WIDTH = 700;
    private static final int FONT_WINDOW_HEIGHT = 600;
    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_HEIGHT = 200;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 40;

    /*Various components*/
    private JTextArea sampleTextArea;
    private JScrollPane sampleTextAreaScroll;
    private JPanel fontFamilyPanel, fontStylePanel, fontSizePanel, samplePanel;

    /*JLists and DefaultListModel objects*/
    private JList fontFamilyList, fontStyleList;
    private JList<Integer> fontSizeList;
    private DefaultListModel listModelFontFamily, listModelFontStyle, listModelFontSize;

    /*Other variables*/
    private String selectedFontFamily, selectedFontStyle;
    private int selectedFontSize;

    public FontWindow() {

        /*Initialising panels*/
        fontFamilyPanel = new JPanel();
        fontStylePanel = new JPanel();
        fontSizePanel = new JPanel();
        samplePanel = new JPanel();

        scrollPaneMap = new HashMap<>();
        availableFontStylesForSelectedFont = new HashSet<>();

        setUpFontWindowUI();
    }
    private void setUpFontWindowUI(){

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

        /*Setting up the sample text area*/
        setupSampleTextArea();

    }

    /**
     * Setting up the JPanel objects - This includes positioning of the panel within the frame
     */
    private void panelSetup(){

        /*Getting available fonts in machine and storing into array*/
        String[] tempAvailableFontFamilyNamesArray = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(); //Names of fonts
        allAvailableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts(); //Actual font objects
        availableFontFamilyNames = new ArrayList<>();

        /*Adding all font family names into availableFontFamilyNames set*/
        for(int i = 0; i < tempAvailableFontFamilyNamesArray.length; i++){
            availableFontFamilyNames.add(tempAvailableFontFamilyNamesArray[i]);
        }

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

    /**
     * Setting up the buttons for this frame
     */
    private void setupButtons(){

        /*Creating JButton objects*/
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        /*x and y coordinates of the top left of the bounding rectangle for the buttons*/
        int x = 430;
        int y = 480;

        /*Absolutely positioning the buttons*/
        confirmButton.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        cancelButton.setBounds(x + BUTTON_WIDTH+15, y, BUTTON_WIDTH, BUTTON_HEIGHT);

        /*Adding the buttons to the this frame*/
        add(confirmButton);
        add(cancelButton);

        /*Adding action listener and setting action commands for each button*/
        confirmButton.setActionCommand("Confirm");
        cancelButton.setActionCommand("Cancel");
        confirmButton.addActionListener(this);
        cancelButton.addActionListener(this);
    }

    /**
     * Setting up the JList objects - This includes filling up the list with values
     */
    private void setupJLists(){

        /*Custom ListCellRenderer object*/
        FontJListCellRenderer customListCellRenderer = new FontJListCellRenderer();

        /*  Setting up the font family list  */
        listModelFontFamily = new DefaultListModel<>(); //DefaultListModel object to make adding elements easier
        for(String fontFamilyName : availableFontFamilyNames){
            listModelFontFamily.addElement(fontFamilyName);
        }
        fontFamilyList = new JList(listModelFontFamily);
        fontFamilyList.setCellRenderer(customListCellRenderer); //Uses custom cell renderer to customize each individual cell in this list
        String firstFontFamily = fontFamilyList.getModel().getElementAt(0).toString(); //Gets the first font family in the list
        setAvailableFontStylesForSelectedFont(firstFontFamily);
        fontFamilyList.setSelectedIndex(0);

        /*Setting up the font style list*/
        listModelFontStyle = new DefaultListModel<>(); //DefaultListModel object to make adding elements easier
        for(String fontStyle : availableFontStylesForSelectedFont){
            listModelFontStyle.addElement(fontStyle);
        }
        fontStyleList = new JList(listModelFontStyle);
        fontStyleList.setCellRenderer(customListCellRenderer); //Uses custom cell renderer to customize each individual cell in this list
        fontStyleList.setSelectedIndex(0);

        /*Setting up the font size list*/
        listModelFontSize = new DefaultListModel<>(); //Used in conjunction with JList since JList class does not have a class that can take integer arrays
        int fontVal = 2;
        for(int i = 0; i < 21; i++){
            listModelFontSize.addElement(fontVal);
            fontVal+=2;
        }
        fontSizeList = new JList<>(listModelFontSize);
        fontSizeList.setSelectedIndex(9);

        /*Creating the JScrollPane objects to hold the JList objects*/
        createScrollPane(fontFamilyList, "Font Family", PANEL_WIDTH, PANEL_HEIGHT, false);
        createScrollPane(fontStyleList, "Font Style", PANEL_WIDTH, PANEL_HEIGHT, true);
        createScrollPane(fontSizeList, "Font Size", PANEL_WIDTH, PANEL_HEIGHT, false);

        /*Setting the initial selected font combination*/
        selectedFontFamily = fontFamilyList.getSelectedValue().toString();
        selectedFontStyle = fontStyleList.getSelectedValue().toString();
        selectedFontSize = fontSizeList.getSelectedValue();

        /*Adding list selection listeners to the lists*/
        fontFamilyList.addListSelectionListener(this);
        fontStyleList.addListSelectionListener(this);
        fontSizeList.addListSelectionListener(this);

        /*adding the scroll panes of each list to the corresponding panel*/
        fontFamilyPanel.add(scrollPaneMap.get("Font Family"));
        fontStylePanel.add(scrollPaneMap.get("Font Style"));
        fontSizePanel.add(scrollPaneMap.get("Font Size"));
        System.out.println("Finished setting up JLists");
    }

    /**
     * Creates a scroll pane for a JList object and adds it to the map
     */
    private void createScrollPane(JList list, String nameOfScrollPane, int scrollPaneX , int scrollPaneY, boolean alwaysScroll){
        JScrollPane scrollPane= new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(scrollPaneX, scrollPaneY));
        scrollPane.setBorder(BorderFactory.createTitledBorder(nameOfScrollPane));
        if(alwaysScroll) scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneMap.put(nameOfScrollPane, scrollPane);
    }

    /**
     * Method that sets up the sample text area - This includes the first font,
     * absolute positioning and the sample text contained within the text area
     */
    private void setupSampleTextArea(){

        String initFontFamily = fontFamilyList.getSelectedValue().toString();
        String initFontStyle = fontStyleList.getSelectedValue().toString();
        int initFontSize = fontSizeList.getSelectedValue();

        /*Setting initial font that the sample should be displayed in*/
        sampleTextArea = new JTextArea();
        sampleTextArea.setFont(new Font(initFontFamily, fontStyleType(initFontStyle), initFontSize));
        sampleTextArea.setBorder(BorderFactory.createTitledBorder("Sample"));
        sampleTextArea.setText("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz");
        sampleTextArea.setLineWrap(true);
        sampleTextAreaScroll = new JScrollPane(sampleTextArea);
        sampleTextAreaScroll.setPreferredSize(new Dimension(PANEL_WIDTH * 3 + (20*2), PANEL_HEIGHT));
        sampleTextAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        samplePanel.add(sampleTextAreaScroll);

    }

    /**
     * Adding all available font styles for the selected font to the set
     */
    private void setAvailableFontStylesForSelectedFont(String fontFamily){
        if(!availableFontStylesForSelectedFont.isEmpty()) availableFontStylesForSelectedFont.clear();
        for(Font font : allAvailableFonts){
            String fontStyle = font.getName(); //Name of the font is the style of the font
            if(font.getFamily().equals(fontFamily)) {
                availableFontStylesForSelectedFont.add(fontStyle);
            }
        }
    }

    /**
     * Handles button clicking events
     */
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

            /*If change was within the fontFamilyList JList object - other conditions are needed to handle any special circumstances to prevent errors*/
            if((listModelFontFamily.contains(list.getSelectedValue()) && listModelFontStyle.contains(list.getSelectedValue()) && listModelFontStyle.size() == 1)
            || listModelFontFamily.contains(list.getSelectedValue()) && listModelFontStyle.contains(list.getSelectedValue())){
                selectedFontFamily = list.getSelectedValue().toString();
                selectedFontStyle = list.getSelectedValue().toString();
                updateSample();

            /*If change was within the fontFamilyList JList object*/
            }else if(listModelFontFamily.contains(list.getSelectedValue())){
                selectedFontFamily = list.getSelectedValue().toString();
                setAvailableFontStylesForSelectedFont(selectedFontFamily);
                updateFontStylesList();
                updateSample();

            /*If the change was within the fontStyleList JList object*/
            }else if(listModelFontStyle.contains(list.getSelectedValue())){
                selectedFontStyle = list.getSelectedValue().toString();
                updateSample();

            /*If the change was within the fontSizeList JList object*/
            }else if(listModelFontSize.contains(list.getSelectedValue())){
                selectedFontSize = (int)list.getSelectedValue();
                updateSample();
            }
        }
    }

    /**
     * Updates the list model that the fontStyleList JList object uses and in turn
     * updates the actual fontStyleList JList object
     */
    private void updateFontStylesList(){
        listModelFontStyle.clear();
        for(String fontStyle : availableFontStylesForSelectedFont){
            listModelFontStyle.addElement(fontStyle);
        }
        fontStyleList.setModel(listModelFontStyle); //Updates fontStyleList with new model
        fontStyleList.setSelectedIndex(0);
    }

    /**
     * Updates the sample text area font
     */
    private void updateSample(){
        String fontFamily = fontFamilyList.getSelectedValue().toString();
        String fontStyle = fontStyleList.getSelectedValue().toString();
        int fontSize = fontSizeList.getSelectedValue();
        sampleTextArea.setFont(new Font(fontFamily, fontStyleType(fontStyle), fontSize));
    }

    /**
     * Method that returns the type of font style in string form
     */
    private int fontStyleType(String fontStyle){
        if(fontStyle.contains("Bold") && fontStyle.contains("Italic")) return Font.BOLD|Font.ITALIC;
        else if(fontStyle.contains("Italic")) return Font.ITALIC;
        else if(fontStyle.contains("Bold")) return Font.BOLD;
        else return Font.PLAIN;
    }

    /**
     * Sends the selected combination through and sets the new font
     */
    private void confirm(){
        TextEditor.textEditor.setNewFont(selectedFontFamily, selectedFontStyle, selectedFontSize);
        dispose(); //Closes the font window
        JOptionPane.showMessageDialog(null, "Updated Font!");
    }
}
