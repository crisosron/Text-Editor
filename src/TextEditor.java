//TODO: Create an update method that tracks if a change was made or not
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.List;

public class TextEditor extends JFrame implements ActionListener {

    /*UI Fields*/
    private JTextArea mainTextArea;
    private JMenuBar menuBar;
    private JScrollPane mainTextAreaScroll;
    private Font mainTextAreaFont;

    /*Constants for sizes of components - All are somehow related back to FRAME_WIDTH and FRAME_HEIGHT*/
    public static final int FRAME_WIDTH = 1000;
    public static final int FRAME_HEIGHT = 800;
    public static final int MAIN_TEXT_AREA_WIDTH = FRAME_WIDTH;
    public static final int MAIN_TEXT_AREA_HEIGHT = FRAME_HEIGHT;

    /*Collections for UI*/
    private Set<String> menuNames, menuItemNames, checkBoxMenuItemNames, fileMenuItemNames, editMenuItemNames, formatMenuItemNames, graphicsMenuItemNames;
    private Map<String, JMenu> menuMap; //Use this map to gain access to menus
    private Map<String, JMenuItem> menuItemsMap; //Use this map to gain access to menu items
    private Map<String, JCheckBoxMenuItem> checkBoxMenuItemsMap = new HashMap<>(); //Use this map to gain access to check box menu items

    /*Other fields*/
    private boolean isWrapping = false; //Wrapping of text area is set to false by default
    private boolean lightThemeActive = true; //Light theme on by default
    private boolean darkThemeActive = false;
    private boolean fileExists = false; //Used for saving
    public static final Font DEFAULT_FONT = new Font("Sans-Serif", Font.PLAIN, 20);
    public static FontWindow fontWindow;
    public static TextEditor textEditor;

    /*For file management*/
    private String openedFileName = "";
    private String openedFileNamePath = "";
    private boolean hasOpenedFile = false;
    private static boolean changesMade = true;

    /**
     * Constructor - Initialises UI components and collections
     */
    public TextEditor(){

        /*Making the UI use the OS aesthetics*/
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){e.printStackTrace();}

        /* ---- Initializing some fields ---- */
        mainTextArea = new JTextArea();
        mainTextAreaScroll = new JScrollPane(mainTextArea);
        menuBar = new JMenuBar();
        menuItemNames = new HashSet<>();
        menuMap = new HashMap<>();
        menuItemsMap = new HashMap<>();
        mainTextAreaFont = DEFAULT_FONT;

        /* ---- Setting up the collections ---- */
        /*Set that stores the names of all the menus in the editor*/
        menuNames = new HashSet<>(Arrays.asList("File", "Edit", "Format", "Graphics"));

        /*Sets that stores the menu items within each respective menu*/
        fileMenuItemNames = new HashSet<>(Arrays.asList("New", "Open", "Save", "Save As...", "Exit"));
        editMenuItemNames = new HashSet<>(Arrays.asList("Undo", "Cut", "Copy", "Paste"));
        formatMenuItemNames = new HashSet<>(Arrays.asList("Font", "Word Wrap", "Light Theme", "Dark Theme"));
        graphicsMenuItemNames = new HashSet<>(Arrays.asList("New Graphics Window", "Open Graphics In Current Window"));

        /*Seperate set for JCheckBoxMenuItem objects*/
        checkBoxMenuItemNames = new HashSet<>(Arrays.asList("Word Wrap", "Light Theme", "Dark Theme"));

        /*Adding all menu item names into one set*/
        menuItemNames.addAll(fileMenuItemNames);
        menuItemNames.addAll(editMenuItemNames);
        menuItemNames.addAll(formatMenuItemNames);
        menuItemNames.addAll(graphicsMenuItemNames);

        /*Creating all menus and adding to map*/
        for(String menuName : menuNames){
            menuMap.put(menuName, new JMenu(menuName));
        }

        /*Creating all JMenuItem objects and placing them into the map*/
        for(String menuItemName : menuItemNames){
            menuItemsMap.put(menuItemName, new JMenuItem(menuItemName));
        }

        /*Creating JCheckBoxMenuItem objects and placing them into the map*/
        for(String checkBoxMenuItemName : checkBoxMenuItemNames){
            checkBoxMenuItemsMap.put(checkBoxMenuItemName, new JCheckBoxMenuItem(checkBoxMenuItemName));
        }

        /*Setting action commands for all the menu items*/
        for(Map.Entry<String, JMenuItem> entryMenuItem : menuItemsMap.entrySet()){
            entryMenuItem.getValue().setActionCommand(entryMenuItem.getKey()); // The action command is the same as the reference key
            entryMenuItem.getValue().addActionListener(this); //Adding this action listener
        }

        /*Setting action commands for all the check box menu items*/
        for(Map.Entry<String, JCheckBoxMenuItem> entryCheckBoxMenuItem : checkBoxMenuItemsMap.entrySet()){
            entryCheckBoxMenuItem.getValue().setActionCommand(entryCheckBoxMenuItem.getKey());
            entryCheckBoxMenuItem.getValue().addActionListener(this);
        }

        /*Overrides the exit operation on the frame closing button - This ensures that the user is prompted to save
        * any changes made before exiting*/
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if(changesMade){
                    saveCheck(0);
                }
            }
        });

        /*Setting up the GUI*/
        setupGUI();
    }

    /**
     * Sets up the user interface
     */
    public void setupGUI(){

        /* ---- Setting up the frame(this) ---- */
        setLayout(new BorderLayout());
        setTitle("Text Editor");
        setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false); //Disables resizability

        /* ---- Setting up the menus ---- */
        /*Adding menus to menu bar*/
        List<String> tempMenuList = new ArrayList<>(Arrays.asList("File", "Edit", "Format", "Graphics")); //Menus will be added to menu bar in this order
        for(String menuName : tempMenuList){
            menuBar.add(menuMap.get(menuName));
        }

        /*Adding menu items to their respective menu items*/
        for(Map.Entry<String, JMenuItem> menuItemEntry : menuItemsMap.entrySet()){
            if(fileMenuItemNames.contains(menuItemEntry.getKey()) && !checkBoxMenuItemsMap.containsKey(menuItemEntry.getKey())) menuMap.get("File").add(menuItemEntry.getValue()); //If element belongs to File menu, add it to file menu
            else if(editMenuItemNames.contains(menuItemEntry.getKey()) && !checkBoxMenuItemsMap.containsKey(menuItemEntry.getKey())) menuMap.get("Edit").add(menuItemEntry.getValue()); //If element belongs to Edit menu, add it to edit menu
            else if(formatMenuItemNames.contains(menuItemEntry.getKey()) && !checkBoxMenuItemsMap.containsKey(menuItemEntry.getKey())) menuMap.get("Format").add(menuItemEntry.getValue()); //If element belongs to Format menu, add it to format menu
            else if(graphicsMenuItemNames.contains(menuItemEntry.getKey()) && !checkBoxMenuItemsMap.containsKey(menuItemEntry.getKey())) menuMap.get("Graphics").add(menuItemEntry.getValue()); //If element belongs to Graphics menu, add it to graphics menu
        }

        /*Adding check box menu items to their respective menus*/
        for (Map.Entry<String, JCheckBoxMenuItem> entryCheckBox : checkBoxMenuItemsMap.entrySet()){
            if(fileMenuItemNames.contains(entryCheckBox.getKey())) menuMap.get("File").add(entryCheckBox.getValue());
            else if(editMenuItemNames.contains(entryCheckBox.getKey())) menuMap.get("Edit").add(entryCheckBox.getValue());
            else if(formatMenuItemNames.contains(entryCheckBox.getKey())) menuMap.get("Format").add(entryCheckBox.getValue());
            else if(graphicsMenuItemNames.contains(entryCheckBox.getKey())) menuMap.get("Graphics").add(entryCheckBox.getValue());
            System.out.println("Added " + entryCheckBox.getKey());
        }

        /*Setting this checkbox menu item to true since the light theme is on by default*/
        checkBoxMenuItemsMap.get("Light Theme").setSelected(true);

        /* ---- Setting up the main text area scroll panel (and in turn the main text area itself) ---- */
        mainTextArea.setFont(mainTextAreaFont);
        mainTextArea.setMargin(new Insets(5, 5, 5, 5));
        mainTextAreaScroll.setBackground(Color.white);
        mainTextAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainTextAreaScroll.setPreferredSize(new Dimension(MAIN_TEXT_AREA_WIDTH, MAIN_TEXT_AREA_HEIGHT));

        /*Adding components to the frame*/
        add(menuBar, BorderLayout.NORTH);
        add(mainTextAreaScroll);

        setVisible(true);
    }

    /**
     * actionPerformed method that handles all the interaction in the menu
     */
    public void actionPerformed(ActionEvent event){
        String action = event.getActionCommand();

        /*File menu actions*/
        if(action.equals("New"))newDocument();
        else if(action.equals("Open"))openFile();
        else if(action.equals("Save"))saveFile();
        else if(action.equals("Save As..."))saveFileAs();
        else if(action.equals("Exit"))exit();

        /*Edit menu actions*/
        else if(action.equals("Cut")) cut();
        else if(action.equals("Paste")) paste();
        else if(action.equals("Copy")) copy();

        /*Format menu actions*/
        else if(action.equals("Word Wrap")) setWordWrap();
        else if(action.equals("Dark Theme")) enableDarkTheme();
        else if(action.equals("Light Theme")) enableLightTheme();
        else if(action.equals("Font")) {fontWindow = new FontWindow();}
    }

    public void openFile(){
        try{

            /*Prompting user to open a file using file choosers*/
            JFileChooser openFileChooser = new JFileChooser();
            openFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir"))); //Gets current working directory
            int status = openFileChooser.showOpenDialog(null); //Prompting user to open a file
            if(status != JFileChooser.APPROVE_OPTION)JOptionPane.showMessageDialog(null, "No file selected!");
            else{

                /*Getting the text in the opened file and transferring it onto the
                 * mainTextArea component*/
                File openedFile = openFileChooser.getSelectedFile();
                openedFileName = openFileChooser.getSelectedFile().getName();
                openedFileNamePath = openedFile.getAbsolutePath(); //Used for saving to an existing file
                hasOpenedFile = true;
                System.out.println(hasOpenedFile);
                Scanner scan = new Scanner(openedFile);
                String textToDisplay = "";
                while(scan.hasNext()) textToDisplay += scan.nextLine() + "\n";
                mainTextArea.setText(textToDisplay);
                setTitle(openedFileName); //Setting the title of the frame to the name of the opened text file
            }

        }catch(Exception e){e.printStackTrace();}
    }

    /**
     * Called when the Save menu item in the File menu. This method differs from saveFileAs method since
     * it first checks if the user is trying to save to an existing file. If not, call the saveFileAs method
     */
    public void saveFile(){
        try{

            /*If saving to existing file*/
            if(hasOpenedFile){
                File openedFile = new File(openedFileNamePath);
                FileWriter writeToOpenedFile = new FileWriter(openedFile);
                mainTextArea.write(writeToOpenedFile);
                JOptionPane.showMessageDialog(null, "File Saved!");
                changesMade = false;
                return;
            }

            /*saveFileAs method is called if the user is trying to save a brand new document*/
            saveFileAs();

        }catch(Exception e) {e.printStackTrace();}
    }

    /**
     *  Method called when the 'Save As ....' menu item is clicked. This will force the showSaveDialog method to happen
     *  unlike the saveFile method which does not force the showSaveDialog if the file being saved is a file that already
     *  exists.
     */
    public void saveFileAs(){
        try {
            /*Operations to conduct when saving to a brand new file*/
            JFileChooser saveFileChooser = new JFileChooser();
            int status = saveFileChooser.showSaveDialog(null);
            if (status != JFileChooser.APPROVE_OPTION) JOptionPane.showMessageDialog(null, "Save cancelled!");
            else {
                File fileToSave = saveFileChooser.getSelectedFile(); //Creates a new file with a title based on the user's input
                FileWriter writer = new FileWriter(fileToSave); //FileWriter object to write to the newly created file
                mainTextArea.write(writer); //Gets the text in the mainTextArea component and writes it to the newly created file
                setTitle(fileToSave.getName());
                JOptionPane.showMessageDialog(null, "File saved as: " + fileToSave.getName());
            }
            hasOpenedFile = true;
            changesMade = false;

        }catch(Exception e){e.printStackTrace();}

    }

    /**
     * Method called when the user attempts to create a new document or exit the program without saving
     * changes made to the current document
     *
     * Parameter sourceID notation: 0 = User clicked on exit button on the window
     *                              1 = User clicked on Exit JMenuItem in the File menu
     *                              2 = User clicked on New JMenuItem in the File menu without saving current changes
     */
    public void saveCheck(int sourceID){
        int optionInput = JOptionPane.showConfirmDialog(null, "Would you like to save changes made? ");
        if(optionInput == JOptionPane.YES_OPTION) saveFile();
        else if(optionInput == JOptionPane.CANCEL_OPTION) return;
        else{
            /*If the user clicks exit on the window*/
            if(sourceID ==  0 || sourceID == 1) {
                System.exit(0);
            }
        }
    }

    /**
     * Creates a new untitled document
     */
    public void newDocument(){

        /*Checking if the user wants to save an unsaved changes*/
        if(changesMade) {
            saveCheck(2);
        }

        /*Resetting some things*/
        hasOpenedFile = false;
        mainTextArea.setText("");
        openedFileNamePath = "";
        openedFileName = "";
    }

    /**
     * Forces a hard exit -Called when the Exit menu item in the File menu is clicked
     */
    public void exit(){
        saveCheck(1);
        System.exit(EXIT_ON_CLOSE);
    }

    /**
     * Changes the editor to use a dark theme
     */
    public void enableDarkTheme(){

        /*Exits the method if the dark theme is already active*/
        if(darkThemeActive) return;

        /*Operations to convert to dark theme*/
        darkThemeActive = true;
        lightThemeActive = false;
        checkBoxMenuItemsMap.get("Dark Theme").setSelected(true); //Enables the tick for the dark theme menu item
        checkBoxMenuItemsMap.get("Light Theme").setSelected(false); //Disables the tick for the light theme menu item
        mainTextArea.setCaretColor(Color.white);
        mainTextArea.setForeground(Color.white);
        mainTextArea.setBackground(new Color(42, 42, 42));
    }

    /**
     * Changes the editor to use a light theme
     */
    public void enableLightTheme(){

        /*Exits the method if the light theme is already active*/
        if(lightThemeActive) return;

        /*Operations to convert to light theme*/
        lightThemeActive = true;
        darkThemeActive = false;
        checkBoxMenuItemsMap.get("Dark Theme").setSelected(false); //Disables the tick for the dark theme menu item
        checkBoxMenuItemsMap.get("Light Theme").setSelected(true); //Enables the tick for the light theme menu item
        mainTextArea.setCaretColor(Color.black);
        mainTextArea.setForeground(Color.black);
        mainTextArea.setBackground(Color.white);
    }

    /**
     * Activates/Deactivates the word wrap functionality around the mainTextArea component
     */
    public void setWordWrap(){
        isWrapping = !isWrapping;
        mainTextArea.setLineWrap(isWrapping);
    }

    /**
     * Method that copies text and removes it from the mainTextArea component
     */
    public void cut(){

        /*Text to cut is the selected text in the mainTextArea component*/
        String textToCut = mainTextArea.getSelectedText();

        /*Clipboard object to store the highlighted text in the mainTextArea component*/
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        /*StringSelection object that holds the highlighted text in the mainTextArea*/
        StringSelection selectedText = new StringSelection(textToCut);

        /*Storing selected text into system clipboard*/
        clipboard.setContents(selectedText, selectedText);

        /*Operations to remove the selected text from the mainTextArea component*/
        mainTextArea.replaceSelection("");

    }

    /**
     * Method that takes the text stored in the system clipboard and pastes it to
     * the mainTextArea component
     */
    public void paste() {
        try {
            String textToPaste = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            mainTextArea.insert(textToPaste, mainTextArea.getCaretPosition()); //Inserts the text in the clipboard in the current position of the caret
        }catch(Exception e){e.printStackTrace();}
    }

    /**
     * Copies selected text in mainTextArea into system clipboard without removing the text from the mainTextArea component
     */
    public void copy(){
        StringSelection textToCopy = new StringSelection(mainTextArea.getSelectedText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(textToCopy, textToCopy);
    }

    /**
     * Undo's most recent action
     */
    public void undo(){
        //TODO: Implement this using a string stack

    }

    /**
     * Sets the font based on what font the user selects
     */
    public void setNewFont(String fontFamily, String fontStyle, int fontSize){
        mainTextArea.setFont(new Font(fontFamily, fontStyleType(fontStyle), fontSize));
    }

    /**
     * Method that returns the type of font style in string form
     */
    public int fontStyleType(String fontStyle){
        if(fontStyle.contains("Bold") && fontStyle.contains("Italic")) return Font.BOLD|Font.ITALIC;
        else if(fontStyle.contains("Italic")) return Font.ITALIC;
        else if(fontStyle.contains("Bold")) return Font.BOLD;
        return Font.PLAIN;
    }


    public static void main(String[] args){
        textEditor = new TextEditor();
        while(true){
            //TODO: Implement some sort of check here that determines whether or not a change was made to the current doc

        }
    }
}
