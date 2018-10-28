import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class TextEditor extends JFrame implements ActionListener {

    /*UI Fields*/
    //Container container;
    private JTextArea mainTextArea;
    private JMenuBar menuBar;
    private JScrollPane mainTextAreaScroll;

    /*Constants for sizes of components - All are somehow related back to FRAME_WIDTH and FRAME_HEIGHT*/
    public static final int FRAME_WIDTH = 1000;
    public static final int FRAME_HEIGHT = 800;
    public static final int MAIN_TEXT_AREA_WIDTH = FRAME_WIDTH;
    public static final int MAIN_TEXT_AREA_HEIGHT = FRAME_HEIGHT;

    /*Collections for UI*/
    private Set<String> menuNames;
    private Set<String> menuItemNames;
    private Set<String> fileMenuItemNames;
    private Set<String> editMenuItemNames;
    private Set<String> formatMenuItemNames;
    private Map<String, JMenu> menuMap; //Use this map to gain access to menus
    private Map<String, JMenuItem> menuItemsMap; //Use this map to gain access to menu items

    /**
     * Constructor - Initialises UI components and collections
     */
    public TextEditor(){

        /* ---- Initializing some fields ---- */
        mainTextArea = new JTextArea();
        mainTextAreaScroll = new JScrollPane(mainTextArea);
        menuBar = new JMenuBar();
        menuItemNames = new HashSet<>();
        menuMap = new HashMap<>();
        menuItemsMap = new HashMap<>();

        /* ---- Setting up the collections ---- */
        /*Set that stores the names of all the menus in the editor*/
        menuNames = new HashSet<>(Arrays.asList("File", "Edit", "Format"));

        /*Sets that stores the menu items within each respective menu*/
        fileMenuItemNames = new HashSet<>(Arrays.asList("Open", "Save", "Exit"));
        editMenuItemNames = new HashSet<>(Arrays.asList("Undo", "Cut", "Paste"));
        formatMenuItemNames = new HashSet<>(Arrays.asList("Word Wrap", "Light Theme", "Dark Theme", "Font"));

        /*Adding all menu item names into one set*/
        menuItemNames.addAll(fileMenuItemNames);
        menuItemNames.addAll(editMenuItemNames);
        menuItemNames.addAll(formatMenuItemNames);

        /*Creating all menus and adding to map*/
        for(String menuName : menuNames){
            menuMap.put(menuName, new JMenu(menuName));
        }

        /*Creating all menu items and adding to map*/
        for(String menuItemName : menuItemNames){
            menuItemsMap.put(menuItemName, new JMenuItem(menuItemName));
        }

        /*Setting action commands for all the menu items*/
        for(Map.Entry<String, JMenuItem> entryMenuItem : menuItemsMap.entrySet()){
            entryMenuItem.getValue().setActionCommand(entryMenuItem.getKey()); // The action command is the same as the reference key
        }

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
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false); //Disables resizability

        /* ---- Setting up the menus ---- */
        /*Adding menus to menu bar*/
        for(Map.Entry<String, JMenu> menuEntry : menuMap.entrySet()){
            menuBar.add(menuEntry.getValue());
        }

        /*Adding menu items to their respective menu items*/
        for(Map.Entry<String, JMenuItem> menuItemEntry : menuItemsMap.entrySet()){
            if(fileMenuItemNames.contains(menuItemEntry.getKey()))menuMap.get("File").add(menuItemEntry.getValue()); //If element belongs to File menu, add it to file menu
            else if(editMenuItemNames.contains(menuItemEntry.getKey()))menuMap.get("Edit").add(menuItemEntry.getValue()); //If element belongs to Edit menu, add it to edit menu
            else if(formatMenuItemNames.contains(menuItemEntry.getKey()))menuMap.get("Format").add(menuItemEntry.getValue()); //If element belongs to Format menu, add it to format menu
        }

        /* ---- Setting up the main text area scroll panel (and in turn the main text area itself) ---- */
        mainTextAreaScroll.setBackground(Color.white);
        mainTextAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainTextAreaScroll.setPreferredSize(new Dimension(MAIN_TEXT_AREA_WIDTH, MAIN_TEXT_AREA_HEIGHT));

        /*Adding components to the frame*/
        add(menuBar, BorderLayout.NORTH);
        add(mainTextAreaScroll);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent event){
        String action = event.getActionCommand();
        //TODO: Start implementing functionality here
    }

    public static void main(String[] args){
        TextEditor textEditor = new TextEditor();
    }
}
