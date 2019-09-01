package texteditor;

import texteditor.FontJListCellRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

public class FontWindow extends JFrame implements ActionListener, ListSelectionListener {

    //Some collections
    private List<String> availableFontFamilyNames;
    private Font[] allAvailableFonts;
    private  Set<String> availableFontStylesForSelectedFont;
    private Map<String, JScrollPane> scrollPaneMap;

    //Constants
    private static final int FONT_WINDOW_WIDTH = 700;
    private static final int FONT_WINDOW_HEIGHT = 600;
    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_HEIGHT = 200;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 40;

    //Components
    private JTextArea sampleTextArea;
    private JScrollPane sampleTextAreaScroll;
    private JPanel fontFamilyPanel, fontStylePanel, fontSizePanel, samplePanel;

    //List models and JList objects
    private JList fontFamilyList, fontStyleList;
    private JList<Integer> fontSizeList;
    private DefaultListModel listModelFontFamily, listModelFontStyle, listModelFontSize;

    //Selected items
    private String selectedFontFamily, selectedFontStyle;
    private int selectedFontSize;

    private TextEditor textEditor;

    /**
     * Contsructs a FontWindow object for a TextEditor instance
     * @param textEditor TextEditor object associated with this FontWindow
     */
    public FontWindow(TextEditor textEditor) {
        this.textEditor = textEditor;

        //Initialzing panels
        fontFamilyPanel = new JPanel();
        fontStylePanel = new JPanel();
        fontSizePanel = new JPanel();
        samplePanel = new JPanel();

        scrollPaneMap = new HashMap<>();
        availableFontStylesForSelectedFont = new HashSet<>();

        setUpFontWindowUI();
    }

    /**
     * Sets up the UI of this FontWindow
     */
    private void setUpFontWindowUI(){

        //Setting up the frame
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); //Gets rid of the this frame but the application stays open (therefore main frame stays)
        setLayout(null);
        setTitle("Configure Font");
        setSize(new Dimension(FONT_WINDOW_WIDTH, FONT_WINDOW_HEIGHT));
        setResizable(false);
        setVisible(true);

        //Setting up the panels
        panelSetup();

        //Setting up the lists/
        setupJLists();

        //Setting up the buttons
        setupButtons();

        //Setting up the sample text area
        setupSampleTextArea();
    }

    /**
     * Setting up the JPanel objects - This includes positioning of the panel within the frame
     */
    private void panelSetup(){

        //Getting available fonts and storing into an array
        String[] tempAvailableFontFamilyNamesArray = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(); //Names of fonts
        allAvailableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts(); //Actual font objects
        availableFontFamilyNames = new ArrayList<>();

        //Adding all available font family names into a list
        for(int i = 0; i < tempAvailableFontFamilyNamesArray.length; i++){
            availableFontFamilyNames.add(tempAvailableFontFamilyNamesArray[i]);
        }

        //Coordinates of the top left of the panel bounding box
        int x = 20;
        int y = 20;

        //Absolutely positioning the panels in the frame
        fontFamilyPanel.setBounds(x, y, PANEL_WIDTH, PANEL_HEIGHT); //setBounds(x, y, width, height); - Used for absolute positioning
        x += PANEL_WIDTH + 20;
        fontStylePanel.setBounds(x, y, PANEL_WIDTH, PANEL_HEIGHT);
        x+=PANEL_WIDTH + 20;
        fontSizePanel.setBounds(x, y, PANEL_WIDTH, PANEL_HEIGHT);
        y+=PANEL_HEIGHT + 20;
        x = 20;
        samplePanel.setBounds(x, y, PANEL_WIDTH * 3 + (20*2), PANEL_HEIGHT);

        //Adding panels to the frame
        add(fontFamilyPanel);
        add(fontStylePanel);
        add(fontSizePanel);
        add(samplePanel);
    }

    /**
     * Setting up the buttons for this frame
     */
    private void setupButtons(){
		
		//Creating the JButton objects
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
        JButton setFontAsDefaultButton = new JButton("Set Default");
		
		// X and Y coordiantes of the top left of the bounding rectangle for the buttons
        int x = 430;
        int y = 480;
        int fontDefaultButtonX = 20;
        int fontDefaultButtonY = 480;
		
		//Absolutely positioning the buttons
        confirmButton.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        cancelButton.setBounds(x + BUTTON_WIDTH+15, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        setFontAsDefaultButton.setBounds(fontDefaultButtonX, fontDefaultButtonY, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		//Adding the buttons to this frame
        add(confirmButton);
        add(cancelButton);
        add(setFontAsDefaultButton);
		
		//Adding action listener and setting action commands for each button
        confirmButton.setActionCommand("Confirm");
        cancelButton.setActionCommand("Cancel");
        setFontAsDefaultButton.setActionCommand("Set Default Font");
        confirmButton.addActionListener(this);
        cancelButton.addActionListener(this);
        setFontAsDefaultButton.addActionListener(this);

    }

    /**
     * Setting up the JList objects - This includes filling up the list with values
     */
    private void setupJLists(){
		
		//Custom ListCellRenderer to render the fonts correctly for the JList objects
        FontJListCellRenderer customListCellRenderer = new FontJListCellRenderer();
		
		//Setting up the font family list
        listModelFontFamily = new DefaultListModel<>(); //DefaultListModel object to make adding elements easier
        for(String fontFamilyName : availableFontFamilyNames){
            listModelFontFamily.addElement(fontFamilyName);
        }
        fontFamilyList = new JList(listModelFontFamily);
        fontFamilyList.setCellRenderer(customListCellRenderer); //Uses custom cell renderer to customize each individual cell in this list
        String firstFontFamily = fontFamilyList.getModel().getElementAt(0).toString(); //Gets the first font family in the list
        //setAvailableFontStylesForSelectedFont(firstFontFamily); //TODO: Put this in highlightSelectedFontProperties

        /*Setting up the font style list*/
        listModelFontStyle = new DefaultListModel<>(); //DefaultListModel object to make adding elements easier
        for(String fontStyle : availableFontStylesForSelectedFont){
            listModelFontStyle.addElement(fontStyle);
        }
        fontStyleList = new JList(listModelFontStyle);
        fontStyleList.setCellRenderer(customListCellRenderer); //Uses custom cell renderer to customize each individual cell in this list
        //fontStyleList.setSelectedIndex(0);

        /*Setting up the font size list*/
        listModelFontSize = new DefaultListModel<>(); //Used in conjunction with JList since JList class does not have a class that can take integer arrays
        int fontVal = 2;
        for(int i = 0; i < 21; i++){
            listModelFontSize.addElement(fontVal);
            fontVal+=2;
        }
        fontSizeList = new JList<>(listModelFontSize);
        //fontSizeList.setSelectedIndex(9);
        //TODO: Develop highlightSelectedFontProperties();
        highlightSelectedFontProperties();

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
     * Method that processes the properties of the selected font for highlighting
     */
    private void highlightSelectedFontProperties(){

        /*The font and its properties*/
        Font currentFont = textEditor.getMainTextAreaFont();
        String currentFontFamily = currentFont.getFamily();
        String currentFontStyle = currentFont.getName();
        String currentFontSize = Integer.toString(currentFont.getSize());

        /*Calling the method that processes JLists to highlight the font property*/
        setSelectedIndices(fontFamilyList, currentFontFamily);
        setAvailableFontStylesForSelectedFont(currentFontFamily);
        updateFontStylesList();
        setSelectedIndices(fontStyleList, currentFontStyle);
        setSelectedIndices(fontSizeList, currentFontSize);
    }


    /**
     * Highlights the item in the list that matches the string itemToMatch
     * @param list - List that is iterated through to search for the item
     * @param itemToMatch - Item to search for in the list parameter
     */
    private void setSelectedIndices(JList list, String itemToMatch){
        for(int i = 0; i<list.getModel().getSize(); i++){
            String itemInIteration = list.getModel().getElementAt(i).toString();
            if(itemInIteration.equals(itemToMatch)) list.setSelectedIndex(i);
        }
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
        sampleTextArea.setFont(new Font(initFontFamily, TextEditor.fontStyleType(initFontStyle), initFontSize));
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
        else if(action.equals("Set Default Font")) writeDefaultFontToConfig();
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
        Object fontStyle = fontStyleList.getSelectedValue();
        int fontSize = fontSizeList.getSelectedValue();
        sampleTextArea.setFont(new Font(fontStyle.toString(), TextEditor.fontStyleType(fontStyle.toString()), fontSize));
    }

    /**
     * Edits the config file to load the default font the next time the program is launched
     */
    private void writeDefaultFontToConfig(){
        try{

            /*Font details*/
            String newDefFontFamily = fontFamilyList.getSelectedValue().toString();
            String newDefFontStyle = fontStyleList.getSelectedValue().toString();
            int newDefFontSize = fontSizeList.getSelectedValue();

            /*Confirming if the default font should be reset to a new font*/
            int optionInput = JOptionPane.showConfirmDialog(null, "Set the following selection as default font? " +
                    "\nFont Family: " + newDefFontFamily +
                    "\nFont Style: " + fontStyleList.getSelectedValue().toString() +
                    "\nFont Size: " + newDefFontSize);

            /*Exiting method if cancel or no is clicked in the confirm dialog*/
            if(optionInput != JOptionPane.YES_OPTION) return;

            /*Properties and outputStream objects to set some values in config.properties file*/
            Properties properties = new Properties();
            OutputStream outputStream = new FileOutputStream("config.properties");

            /*Setting new values*/
            properties.setProperty("font-family", newDefFontFamily);
            properties.setProperty("font-style", newDefFontStyle);
            properties.setProperty("font-size", Integer.toString(newDefFontSize));

            /*Storing changes and closing outputStream*/
            properties.store(outputStream, null);
            outputStream.close();
            JOptionPane.showMessageDialog(null, "Default font set!");

        }catch(IOException e){e.printStackTrace();}
    }

    /**
     * Sends the selected combination through and sets the new font
     */
    private void confirm(){
        /*Reminder that the actual font is equal to the name of the font style, font family is just used
         * group the different font styles in the same family*/
        textEditor.setNewFont(selectedFontStyle, selectedFontSize);
        dispose(); //Closes the font window
        JOptionPane.showMessageDialog(null, "Updated Font!");
    }
}
