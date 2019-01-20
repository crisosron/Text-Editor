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
    private List<String> fileMenuItemNames, editMenuItemNames, formatMenuItemNames, paintMenuItemNames, rightClickMenuItemNames, checkBoxMenuItemNames;
    private Map<String, JMenu> menuMap; //Use this map to gain access to menus
    private List<JMenuItem> menuItemsList; //Use this map to gain access to menu items
    private List<JCheckBoxMenuItem> checkBoxMenuItemsList; //Use this map to gain access to check box menu items

    /*Sets that are used for keyboard shortcuts*/
    private Set<String> menuItemsWithBasicShortcuts, menuItemsWithCustomShortCuts, allMenuItemsWithShortcuts, menuItemsWithShiftShortCuts;

    /*Other fields*/
    private static FontWindow fontWindow;
    public static TextEditor textEditor;
    private static PaintWindow paintWindow;
    private UndoManager undoManager;
    private ActionController actionController;

    /*Acts as an instance id, used to check what instance the user tries to close*/
    private int instanceNum;

    /**
     * Constructor - Initialises UI components and collections
     */
    public TextEditor(int instanceNum){

        /*Making the UI use the OS aesthetics*/
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){e.printStackTrace();}

        this.instanceNum = instanceNum;

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
        paintMenuItemNames = new ArrayList<>(Arrays.asList("New Paint Window", "Open Paint In Current Window"));
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

        /*Adding all the menu items with a keyboard shortcut to a single set*/
        allMenuItemsWithShortcuts = new HashSet<>();
        allMenuItemsWithShortcuts.addAll(menuItemsWithBasicShortcuts);
        allMenuItemsWithShortcuts.addAll(menuItemsWithShiftShortCuts);
        allMenuItemsWithShortcuts.addAll(menuItemsWithCustomShortCuts);

        /*Adding all menu item names into one set*/
        List<String> menuItemNames = new ArrayList<>();
        Stream.of(fileMenuItemNames, editMenuItemNames, formatMenuItemNames, paintMenuItemNames).forEach(menuItemNames::addAll);

        /*Creating all menus and adding to map*/
        for(String menuName : menuNames){
            menuMap.put(menuName, new JMenu(menuName));
        }

        /*Creating all JMenuItem objects and placing them into the map*/
        for(String menuItemName : menuItemNames){
            JMenuItem newMenuItem = new JMenuItem(menuItemName);
            newMenuItem.setName(menuItemName);
            if(fileMenuItemNames.contains(menuItemName) || editMenuItemNames.contains(menuItemName)) {
                if(fileMenuItemNames.contains(menuItemName))newMenuItem.setPreferredSize(new Dimension(200, 25));
                else if(editMenuItemNames.contains(menuItemName)) newMenuItem.setPreferredSize(new Dimension(160, 25));
                menuItemsList.add(newMenuItem);
            }else menuItemsList.add(newMenuItem);
        }

        /*Creating JCheckBoxMenuItem objects and placing them into the map*/
        for(String checkBoxMenuItemName : checkBoxMenuItemNames){
            JCheckBoxMenuItem newJCheckBoxMenuItem = new JCheckBoxMenuItem(checkBoxMenuItemName);
            newJCheckBoxMenuItem.setName(checkBoxMenuItemName);
            checkBoxMenuItemsList.add(newJCheckBoxMenuItem);
        }

        /*Setting action commands for all the menu items*/
        for(JMenuItem menuItem : menuItemsList){
            menuItem.setActionCommand(menuItem.getName());
            menuItem.addActionListener(this);
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

                    /*Checks if the program should close or not depending on the
                    * instance the user is trying to close. If the first instance is being closed,
                    * the whole program ends*/
                    if(instanceNum != 0) dispose();
                    else System.exit(0);
                }
            }
        });

        /*Setting up the GUI*/
        setupGUI();
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
        mainTextArea.setFont(mainTextAreaFont);
        mainTextArea.setMargin(new Insets(5, 5, 5, 5));
        mainTextAreaScroll.setBackground(Color.white);
        mainTextAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainTextAreaScroll.setPreferredSize(new Dimension(MAIN_TEXT_AREA_WIDTH, MAIN_TEXT_AREA_HEIGHT));

        /*Other setup*/
        setupBasicMouseListener();
        setupHotKeys();

        /*Adding components to the frame*/
        add(menuBar, BorderLayout.NORTH);
        setJMenuBar(menuBar);
        add(mainTextAreaScroll);

        setVisible(true);
    }

    /**
     * Adding menu items to their respective menus
     */
    private void setupMenus(){

        /*Adding menu items to their respective  menus using the menuItemsMap and menuMap*/
        for(JMenuItem menuItem : menuItemsList){
            String menuItemName = menuItem.getName();

            /*If menu item is within the file menu*/
            if(fileMenuItemNames.contains(menuItemName) && !checkBoxMenuItemNames.contains(menuItemName)) {
                menuMap.get("File").add(menuItem);
               if(menuItemName.equals("Save As...") || menuItemName.equals("Save")) menuMap.get("File").addSeparator(); //Adding separator after certain menu items
            }

            /*If menu item is within the edit menu*/
            else if(editMenuItemNames.contains(menuItemName) && !checkBoxMenuItemNames.contains(menuItemName)) {
                menuMap.get("Edit").add(menuItem);
                if(menuItemName.equals("Paste") || menuItemName.equals("Redo")) menuMap.get("Edit").addSeparator();
            }

            /*If menu item is within the format menu*/
            else if(formatMenuItemNames.contains(menuItemName) && !checkBoxMenuItemNames.contains(menuItemName)) {
                menuMap.get("Format").add(menuItem);
                if(menuItemName.equals("Font") || menuItemName.equals("Light Theme")) menuMap.get("Format").addSeparator();
            }

            /*---------------------------------------------------------- ENABLES PAINT FUNCTIONALITY ----------------------------------------------------------*/
            //else if(paintMenuItemNames.contains(menuItemEntry.getKey()) && !checkBoxMenuItemsList.containsKey(menuItemEntry.getKey())) menuMap.get("Graphics").add(menuItemEntry.getValue());
        }

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
     * Handles all the keyboard shortcuts
     */
    private void setupHotKeys(){

        /*Will either be control key if Windows or Linux, or command key for MacOSX*/
        int shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        /*Looping through all the menu items with key shortcuts and setting the shortcuts*/
        for(String menuItemWithKeyShortCut : allMenuItemsWithShortcuts){
            JMenuItem currentMenuItem = new JMenuItem();

            /*Finding the correct JMenuItem object for the outer for loop iteration*/
            for(JMenuItem menuItem : menuItemsList){
                if(menuItem.getName().equals(menuItemWithKeyShortCut)) {
                    currentMenuItem = menuItem;
                    break;
                }
            }

            char firstChar = menuItemWithKeyShortCut.charAt(0);

            /*'CTRL + [First character of menu item name]'*/
            if (menuItemsWithBasicShortcuts.contains(menuItemWithKeyShortCut))
                currentMenuItem.setAccelerator(KeyStroke.getKeyStroke(getKeyEventForChar(firstChar), shortcutKeyMask));

                /*'CTRL + SHIFT + [First character of menu item name]'*/
            else if (menuItemsWithShiftShortCuts.contains(menuItemWithKeyShortCut))
                currentMenuItem.setAccelerator(KeyStroke.getKeyStroke(getKeyEventForChar(firstChar), shortcutKeyMask | ActionEvent.SHIFT_MASK));

                /*Menu items that have short cuts that adhere to the established standard (instead of using the first character of the menu item name)*/
            else if (menuItemsWithCustomShortCuts.contains(menuItemWithKeyShortCut)) {
                if (menuItemWithKeyShortCut.equals("Undo")) currentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcutKeyMask));
                else if (menuItemWithKeyShortCut.equals("Cut")) currentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, shortcutKeyMask));
                else if (menuItemWithKeyShortCut.equals("Paste")) currentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, shortcutKeyMask));
                else if (menuItemWithKeyShortCut.equals("Exit")) currentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, shortcutKeyMask));
                else if (menuItemWithKeyShortCut.equals("Redo")) currentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, shortcutKeyMask));
                else if (menuItemWithKeyShortCut.equals("Insert Date")) currentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, shortcutKeyMask));
            }
        }
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
            new TextEditor(instanceNum);
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
        else if(action.equals("Font")) {fontWindow = new FontWindow();}

        /*Paint menu actions*/
        else if(action.equals("New Paint Window")) {paintWindow = new PaintWindow();}
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
        mainTextArea.setFont(new Font(fontStyle, fontStyleType(fontStyle), fontSize));
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
        if(Character.isAlphabetic(ke.getKeyChar())){
            if(!actionController.hasChangesMade()){
                int keyShortCutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

                /*Checks if the key shortcut mask is being combined with any other key, and if not, register a change*/
                if((ke.getModifiers() & keyShortCutMask) != keyShortCutMask){
                    actionController.setChangesMadeTrue();
                }
            }
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
     * Returns the KeyEvent constant that corresponds to the character passed
     * into the method
     * @param character is the character that needs to be processed into a KeyEvent constant
     */
    private int getKeyEventForChar(char character){
        switch(character){
            case 'A': return KeyEvent.VK_A;
            case 'B': return KeyEvent.VK_B;
            case 'C': return KeyEvent.VK_C;
            case 'D': return KeyEvent.VK_D;
            case 'E': return KeyEvent.VK_E;
            case 'F': return KeyEvent.VK_F;
            case 'G': return KeyEvent.VK_G;
            case 'H': return KeyEvent.VK_H;
            case 'I': return KeyEvent.VK_I;
            case 'J': return KeyEvent.VK_J;
            case 'K': return KeyEvent.VK_K;
            case 'L': return KeyEvent.VK_L;
            case 'M': return KeyEvent.VK_M;
            case 'N': return KeyEvent.VK_N;
            case 'O': return KeyEvent.VK_O;
            case 'P': return KeyEvent.VK_P;
            case 'Q': return KeyEvent.VK_Q;
            case 'R': return KeyEvent.VK_R;
            case 'S': return KeyEvent.VK_S;
            case 'T': return KeyEvent.VK_T;
            case 'U': return KeyEvent.VK_U;
            case 'V': return KeyEvent.VK_V;
            case 'W': return KeyEvent.VK_W;
            case 'X': return KeyEvent.VK_X;
            case 'Y': return KeyEvent.VK_Y;
            case 'Z': return KeyEvent.VK_Z;
        }

        return 0;
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

    /*Getters*/
    public PaintWindow getPaintWindow(){return paintWindow;}
    public FontWindow getFontWindow(){return fontWindow;}
    public JTextArea getMainTextArea(){return mainTextArea;}
    public List<JCheckBoxMenuItem> getCheckBoxMenuItemsList(){return checkBoxMenuItemsList;}
    public UndoManager getUndoManager(){return undoManager;}
    public int getInstanceNum() {return instanceNum; }

    public static void main(String[] args){

        /*Allows the program to use MacOSX menu bar - Note this has to be at the top of the main method
         * to work */
        if(System.getProperty("os.name").contains("Mac")) System.setProperty("apple.laf.useScreenMenuBar", "true");

        textEditor = new TextEditor(0);
        textEditor.getRootPane().putClientProperty("apple.awt.fullscreenable", Boolean.valueOf(true));
    }
}
