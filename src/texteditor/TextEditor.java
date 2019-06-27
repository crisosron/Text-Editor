package texteditor;

import texteditor.menu.items.FileMenuItem;
import texteditor.menu.items.MenuItem;
import texteditor.menu.items.NoCustomShortcutException;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class TextEditor extends JFrame implements ActionListener, KeyListener , UndoableEditListener{

    /*UI Fields*/
    private JTextArea mainTextArea;
    private JMenuBar menuBar;
    private JScrollPane mainTextAreaScroll;
    private Font mainTextAreaFont;
    private JPopupMenu rightClickMenu;

    /*Constants for sizes of components - All are somehow related back to FRAME_WIDTH and FRAME_HEIGHT*/
    public static final int FRAME_WIDTH = 1000;
    public static final int FRAME_HEIGHT = 800;
    private static final int MAIN_TEXT_AREA_WIDTH = FRAME_WIDTH;
    private static final int MAIN_TEXT_AREA_HEIGHT = FRAME_HEIGHT;

    /*Collections for UI*/
    private List<String> fileMenuItemNames, editMenuItemNames, formatMenuItemNames, paintMenuItemNames, rightClickMenuItemNames, checkBoxMenuItemNames, menuItemsWithSeparators;
    private Map<String, JMenu> menuMap; //Use this map to gain access to menus
    private List<JMenuItem> menuItemsList; //Use this map to gain access to menu items
    private List<JCheckBoxMenuItem> checkBoxMenuItemsList; //Use this map to gain access to check box menu items

    /*Sets that are used for keyboard shortcuts*/
    private Set<String> menuItemsWithBasicShortcuts, menuItemsWithCustomShortCuts, allMenuItemsWithShortcuts, menuItemsWithShiftShortCuts;

    /*Other fields*/
    private UndoManager undoManager;
    private ActionController actionController;

    /*Acts as an instance id, used to check what instance the user tries to close*/
    private int instanceNum;

    private ArrayList<TextEditor> instanceList;

    /**
     * Constructor - Initialises UI components and collections
     */
    public TextEditor(int instanceNum, ArrayList<TextEditor> instanceList){

        /*Making the UI use the OS aesthetics*/
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){e.printStackTrace();}

        this.instanceNum = instanceNum;
        this.instanceList = instanceList;

        /* ---- Initializing some fields ---- */
        mainTextArea = new JTextArea();
        rightClickMenu = new JPopupMenu();
        mainTextAreaScroll = new JScrollPane(mainTextArea);
        checkBoxMenuItemsList = new ArrayList<>();
        menuBar = new JMenuBar();
        menuMap = new HashMap<>();
        menuItemsList = new ArrayList<>();
        undoManager = new UndoManager();
        mainTextArea.getDocument().addUndoableEditListener(this); //Adds the undoable edit listener to the mainTextArea
        actionController = new ActionController(this);

        /*Loading set defaults*/
        loadDefaults(); //In this method, the setDefaultFont method is called

        /* ---- Setting up the collections ---- */
        /*Set that stores the names of all the menus in the editor*/
        Set<String> menuNames = new HashSet<>(Arrays.asList("File", "Edit", "Format"));

        /*Sets that stores the menu items within each respective menu*/
        fileMenuItemNames = new ArrayList<>(Arrays.asList("New", "Open", "Save", "Save As...", "Exit", "New Window"));
        editMenuItemNames = new ArrayList<>(Arrays.asList("Cut", "Copy", "Paste", "Undo", "Redo", "Insert Point", "Insert Sub Point", "Insert Date"));
        formatMenuItemNames = new ArrayList<>(Arrays.asList("Font", "Word Wrap", "Light Theme", "Dark Theme"));
        menuItemsWithSeparators = Arrays.asList("Save", "Save As...", "Paste", "Redo", "Font", "Insert Sub Point");
        //paintMenuItemNames = new ArrayList<>(Arrays.asList("New Paint Window", "Open Paint In Current Window"));
        rightClickMenuItemNames  = new ArrayList<>(Arrays.asList("Undo", "Cut", "Copy", "Paste", "Redo"));
        checkBoxMenuItemNames = new ArrayList<>(Arrays.asList("Word Wrap", "Light Theme", "Dark Theme"));

        /*Separate set for JCheckBoxMenuItem objects*/
        List<String> checkBoxMenuItemNames = new ArrayList<>(Arrays.asList("Word Wrap", "Light Theme", "Dark Theme"));

        /*Explanation of the 3 Collections:
        *   menuItemsWithBasic is a collection of menu items whose shortcut is CTRL + [first character of the menu item name]
        *   menuItemsWithCustomShortCuts is a collection of menu items whose shortcut does not use the first character of the menu item name
        *   menuItemsWithShiftShortcuts is a collection of menu items whose shortcut is CTRL + SHIFT + [first character of the menu item name]
        */
        menuItemsWithBasicShortcuts = new HashSet<>(Arrays.asList("New", "Open", "Save", "Copy", "Font", "Insert Point"));
        menuItemsWithCustomShortCuts = new HashSet<>(Arrays.asList("Undo", "Cut", "Paste", "Exit", "Redo", "Insert Date"));
        menuItemsWithShiftShortCuts = new HashSet<>(Arrays.asList("Save As...", "New Window", "Insert Sub Point"));

        //Creating menu items and adding them to the corresponding menu
        for(String menuName : menuNames) menuMap.put(menuName, new JMenu(menuName));
        createMenuItems(fileMenuItemNames, "File");
        createMenuItems(editMenuItemNames, "Edit");
        createMenuItems(formatMenuItemNames, "Format");

        /*Creating JCheckBoxMenuItem objects and placing them into the map*/
        for(String checkBoxMenuItemName : checkBoxMenuItemNames){
            JCheckBoxMenuItem newJCheckBoxMenuItem = new JCheckBoxMenuItem(checkBoxMenuItemName);
            newJCheckBoxMenuItem.setName(checkBoxMenuItemName);
            checkBoxMenuItemsList.add(newJCheckBoxMenuItem);
        }

        /*Setting action commands for all the check box menu items*/
        for(JCheckBoxMenuItem checkBoxMenuItem : checkBoxMenuItemsList){
            checkBoxMenuItem.setActionCommand(checkBoxMenuItem.getName());
            checkBoxMenuItem.addActionListener(this);
        }

        /*Overrides the exit operation on the frame closing button - This ensures that the user is prompted to save
        * any changes made before exiting*/
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if(actionController.hasChangesMade()){
                    actionController.saveCheck(0);
                }else{

                    /*Checks the number of instances and sees if the instance should just dispose or close the
                    * program entirely*/
                    if(instanceList.size() - 1 <= 0) System.exit(0);
                    else {
                        removeInstanceFromList(instanceNum);
                        dispose();
                    }
                }
            }
        });

        //Setting up the gui
        setupGUI();

        //Enabling macOS full screen ability
        macOSFullscreen();
    }

    /**
     * Determines the custom shortcut for a menu item with the supplied name
     * @param menuItemName Name of the menu item associated with the shortcut
     * @return An integer representing the shortcut key
     */
    private int findCustomShortcut(String menuItemName){
        try {
            switch (menuItemName) {
                case "Undo": return KeyEvent.VK_Z;
                case "Cut": return KeyEvent.VK_X;
                case "Paste": return KeyEvent.VK_V;
                case "Exit": return KeyEvent.VK_W;
                case "Redo": return KeyEvent.VK_Y;
                case "Insert Date": return KeyEvent.VK_D;
                default: throw new NoCustomShortcutException(menuItemName);
            }
        }catch(NoCustomShortcutException e){throw new Error();}
    }

    /**
     * Creates the menu items and establishes their link to their menus
     * @param menuItemNames List of menu item names
     * @param associatedMenuName Name of the menu that the menu items are associated with
     */
    private void createMenuItems(List<String> menuItemNames, String associatedMenuName){
        JMenu associatedMenu = menuMap.get(associatedMenuName);
        menuItemNames.stream().forEach(menuItemName -> {
            JMenuItem menuItem;

            //Determines short cut info of the menu item
            if(menuItemsWithBasicShortcuts.contains(menuItemName)) menuItem = new MenuItem(menuItemName, true, false);//Menu item with short cut but no shift or no custom
            else if(menuItemsWithShiftShortCuts.contains(menuItemName)) menuItem = new MenuItem(menuItemName, true, true);
            else if(menuItemsWithCustomShortCuts.contains(menuItemName)) menuItem = new MenuItem(menuItemName, true, true, findCustomShortcut(menuItemName));
            else menuItem = new MenuItem(menuItemName); //Menu item with no shortcut

            //Adding the menu item to its menu
            associatedMenu.add(menuItem);
            menuItemsList.add(menuItem);

            //Setting up the action listener
            menuItem.addActionListener(this); //TODO: Explore alternatives to this - eg by making every sub type of MenuItem implementing its own action controller for methods that are associated with the menu?

            //Adding separator to menu on exact menu items
            if(menuItemsWithSeparators.contains(menuItemName)) associatedMenu.addSeparator();
        });

    }

    /**
     * Sets up the user interface
     */
    private void setupGUI(){

        /* ---- Setting up the frame(this) ---- */
        setLayout(new BorderLayout());
        setTitle("Text Editor");
        setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(true); //Enables Resizability

        /* ---- Setting up the menus ---- */
        /*Adding menus to menu bar*/
        List<String> tempMenuList = new ArrayList<>(Arrays.asList("File", "Edit", "Format")); //Menus will be added to menu bar in this order
        for(String menuName : tempMenuList){
            menuBar.add(menuMap.get(menuName));
        }

        /*Setting up the menus*/
        setupMenus();

        /*Setting this checkbox menu item to true since the light theme is on by default*/
        for(JCheckBoxMenuItem checkBoxMenuItem : checkBoxMenuItemsList){
            if(checkBoxMenuItem.getName().equals("Light Theme")) checkBoxMenuItem.setSelected(true);
        }

        /* ---- Setting up the main text area scroll pane (and in turn the main text area itself) ---- */
        mainTextArea.setFocusable(true); //For key events
        mainTextArea.addKeyListener(this); //For key events
        mainTextArea.setWrapStyleWord(true);
        mainTextArea.setFont(mainTextAreaFont);
        mainTextArea.setMargin(new Insets(5, 5, 5, 5));
        mainTextAreaScroll.setBackground(Color.white);
        mainTextAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainTextAreaScroll.setPreferredSize(new Dimension(MAIN_TEXT_AREA_WIDTH, MAIN_TEXT_AREA_HEIGHT));

        /*Other setup*/
        setupBasicMouseListener();

        /*Adding components to the frame*/
        add(menuBar, BorderLayout.NORTH);
        setJMenuBar(menuBar);
        add(mainTextAreaScroll);

        setVisible(true);
    }

    //TODO: MAY NO LONGER NEED THIS METHOD
    /**
     * Adding menu items to their respective menus
     */
    private void setupMenus(){

        /*Adding check box menu items to their respective menus*/
        for (JCheckBoxMenuItem checkBoxMenuItem : checkBoxMenuItemsList){
            String checkBoxMenuItemName = checkBoxMenuItem.getName();
            if(fileMenuItemNames.contains(checkBoxMenuItemName)) menuMap.get("File").add(checkBoxMenuItem);
            else if(editMenuItemNames.contains(checkBoxMenuItemName)) menuMap.get("Edit").add(checkBoxMenuItem);
            else if(formatMenuItemNames.contains(checkBoxMenuItemName)) menuMap.get("Format").add(checkBoxMenuItem);
        }
    }

    /**
     * Adding a basic mouselistener to mainTextArea handle only mouseReleased events for right click
     */
    private void setupBasicMouseListener(){
        mainTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                /*Checking if right click*/
                if(SwingUtilities.isRightMouseButton(e)){
                    rightClickMenu = new JPopupMenu(); //JPopupMenu to show at the point clicked

                    /*Creating JMenuItems and placing into rightClickMenu*/
                    for(JMenuItem menuItem: menuItemsList){
                        String menuItemName = menuItem.getName();
                        if(rightClickMenuItemNames.contains(menuItemName)){
                            JMenuItem newMenuItem = new JMenuItem(menuItemName);
                            newMenuItem.setActionCommand(newMenuItem.getName());
                            newMenuItem.addActionListener(TextEditor.this::actionPerformed);
                            rightClickMenu.add(newMenuItem);
                        }
                    }

                    /*Showing the menu at the place clicked*/
                    rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Loading the defaults of the program using config.properties file
     */
    private void loadDefaults() {

        try {

            /*Setting up InputStream and Properties objects*/
            Properties properties = new Properties();
            InputStream inputStream = new FileInputStream("config.properties");

            /*Loading config.properties using InputStream*/
            if (inputStream != null) properties.load(inputStream);
            else throw new FileNotFoundException("File config.properties cannot be found");

            /*Getting the default font settings*/
            String defFontStyle = properties.getProperty("font-style");
            String defFontSize = properties.getProperty("font-size");

            /*Loading the default font (for startup) and closing inputStream*/
            setDefaultFont(defFontStyle, fontStyleType(defFontStyle), Integer.parseInt(defFontSize));
            inputStream.close();

        }catch(IOException e){e.printStackTrace();}
    }

    /**
     * actionPerformed method that handles all the interaction in the menu
     */
    public void actionPerformed(ActionEvent event){
        String action = event.getActionCommand();

        /*File menu actions*/
        if(action.equals("New"))actionController.newDocument();
        else if(action.equals("Open"))actionController.openFile();
        else if(action.equals("Save"))actionController.saveFile();
        else if(action.equals("Save As..."))actionController.saveFileAs();
        else if(action.equals("Exit"))actionController.exit();
        else if(action.equals("New Window")) {
            instanceNum++;
            ArrayList<TextEditor> newInstanceList = instanceList;
            TextEditor newTextEditorInstance = new TextEditor(instanceNum, newInstanceList);
            newTextEditorInstance.getInstanceList().add(newTextEditorInstance);
        }

        /*Edit menu actions*/
        else if(action.equals("Cut")) actionController.cut();
        else if(action.equals("Paste")) actionController.paste();
        else if(action.equals("Copy")) actionController.copy();
        else if (action.equals("Undo")) actionController.undo();
        else if(action.equals("Redo")) actionController.redo();
        else if(action.equals("Insert Point")) actionController.insertPoint();
        else if(action.equals("Insert Sub Point")) actionController.insertSubPoint();
        else if(action.equals("Insert Date")) actionController.insertDate();

        /*Format menu actions*/
        else if(action.equals("Word Wrap")) actionController.setWordWrap();
        else if(action.equals("Dark Theme")) actionController.changeTheme(action);
        else if(action.equals("Light Theme")) actionController.changeTheme(action);
        else if(action.equals("Font")) {
            FontWindow fontWindow = new FontWindow(this);}

        /*Paint menu actions
        else if(action.equals("New Paint Window")) {
            paint.PaintWindow paintWindow = new paint.PaintWindow();
        }
        */
    }

    /**
     * Comes from the UndoableEditListener interface - Adds undoable actions to the UndoManager object
     */
    public void undoableEditHappened(UndoableEditEvent undoableEditEvent){
        undoManager.addEdit(undoableEditEvent.getEdit());
    }

    /**
     * Sets the font based on what font the user selects
     */
    public void setNewFont(String fontStyle, int fontSize){
        mainTextAreaFont = new Font(fontStyle, fontStyleType(fontStyle), fontSize);
        mainTextArea.setFont(mainTextAreaFont);
    }

    /**
     * Method that returns the type of font style in string form
     */
    public static int fontStyleType(String fontStyle){
        if(fontStyle.contains("Bold") && fontStyle.contains("Italic")) return Font.BOLD|Font.ITALIC;
        else if(fontStyle.contains("Italic")) return Font.ITALIC;
        else if(fontStyle.contains("Bold")) return Font.BOLD;
        return Font.PLAIN;
    }

    /**
     * For KeyListener - Sets changesMade to true
     */
    public void keyPressed(KeyEvent ke){
        int keyShortCutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        /*Checking if the ctrl key is being pressed with another key, unless that other key is v (ie checking
        if the user is trying to perform a keyboard shortcut, if that keyboard shortcut is anything other than
        pasting, do not register a change)*/
        if(((ke.getModifiers() & keyShortCutMask) == keyShortCutMask) && ke.getKeyCode() != KeyEvent.VK_V) return;

        if(Character.isAlphabetic(ke.getKeyChar()) || ke.getKeyCode() == KeyEvent.VK_SPACE || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE || ke.getKeyCode() == KeyEvent.VK_ENTER){
            if(!actionController.hasChangesMade()) actionController.setChangesMadeTrue();
        }
    }

    /**
     * For KeyListener - Sets changesMade to false if the mainTextArea component is empty and the user has not opened a file
     */
    public void keyReleased(KeyEvent ke){
        if(mainTextArea.getText().equals("") && !actionController.openedFileExists()) actionController.setChangesMadeFalse();
    }

    public void keyTyped(KeyEvent ke){ }

    /**
     * Enables native fullscreen for macOS
     */
    private void macOSFullscreen(){
        this.getRootPane().putClientProperty("apple.awt.fullscreenable", true);
    }

    /**
     * Sets up the default font
     * @param fontStyle is the default font style
     * @param fontFamily is the default font family eg Italic, Plain
     * @param fontSize is the default font size
     */
    private void setDefaultFont(String fontStyle, int fontFamily, int fontSize){
        mainTextAreaFont = new Font(fontStyle, fontFamily, fontSize);
    }

    public void removeInstanceFromList(int instanceIDToRemove){
        ArrayList<TextEditor> tempList = instanceList;
        for(int i=0; i<tempList.size(); i++){
            if(tempList.get(i).getInstanceNum() == instanceIDToRemove) {
                instanceList.remove(i);
                break;
            }
        }
    }

    /*Getters*/
    public JTextArea getMainTextArea(){return mainTextArea;}
    public List<JCheckBoxMenuItem> getCheckBoxMenuItemsList(){return checkBoxMenuItemsList;}
    public UndoManager getUndoManager(){return undoManager;}
    public int getInstanceNum() {return instanceNum; }
    public Font getMainTextAreaFont(){return mainTextAreaFont;}
    public ArrayList<TextEditor> getInstanceList(){return instanceList;}

    public static void main(String[] args){

        /*Allows the program to use MacOSX menu bar - Note this has to be at the top of the main method
         * to work */
        if(System.getProperty("os.name").contains("Mac")) System.setProperty("apple.laf.useScreenMenuBar", "true");

        TextEditor textEditor = new TextEditor(0, new ArrayList<>());
        textEditor.getInstanceList().add(textEditor);
    }
}
