package texteditor.menu.items;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
//TODO: Split action listening to be restricted to each type of menu item - eg EditMenuItem implements EditActionController?

public class MenuItem extends JMenuItem {
    private String menuItemName;
    private boolean isCheckBoxItem;
    private final int SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    private boolean isShiftKeyShortcut;
    private boolean hasShortcut;
    private JCheckBoxMenuItem checkBoxMenuItem;

    /**
     * Basic constructor for menu items with no shortcuts
     * @param menuItemName Name corresponding to the menu item
     */
    public MenuItem(String menuItemName){
        super(menuItemName);
        this.menuItemName = menuItemName;
        isCheckBoxItem = false;
        isShiftKeyShortcut = false;
        checkBoxMenuItem = null;
        setActionCommand(menuItemName); //For click events

    }

    /**
     * Constructor for menu items with short cuts - By default, menu items shortcut key is the first letter of the name
     * @param menuItemName Name corresponding to the menu item
     * @param hasShortcut Boolean indicating that the menu item has a short cut
     * @param isShiftKeyShortcut Boolean indicating that the menu item has a short cut with the shift key
     */
    public MenuItem(String menuItemName, boolean hasShortcut, boolean isShiftKeyShortcut){
        super(menuItemName);
        this.menuItemName = menuItemName;
        this.hasShortcut = hasShortcut;
        this.isShiftKeyShortcut = isShiftKeyShortcut;
        isCheckBoxItem = false;
        checkBoxMenuItem = null;
        setActionCommand(menuItemName); //For click events
        setupShortcut(getKeyEventForChar(menuItemName.charAt(0)));
    }

    /**
     * Constructor for menu items with a custom short cut
     * @param menuItemName Name corresponding to the menu item
     * @param hasShortcut Boolean indicating that the menu item has a short cut
     * @param isShiftKeyShortcut Boolean indicating that the menu item has a short cut with the shift key
     * @param customShortcut Custom short cut key for this menu item (as opposed to being the first character of the menu item name)
     */
    public MenuItem(String menuItemName, boolean hasShortcut, boolean isShiftKeyShortcut, int customShortcut){
        super(menuItemName);
        this.menuItemName = menuItemName;
        this.hasShortcut = hasShortcut;
        this.isShiftKeyShortcut = isShiftKeyShortcut;
        isCheckBoxItem = false;
        checkBoxMenuItem = null;
        setActionCommand(menuItemName); //For click events
        setupShortcut(customShortcut);
    }
    
    /**
     * Constructor for MenuItem objects that are checkbox menu items
     * @param menuItemName Name of the menu item
     * @param isCheckBoxItem Boolean indicating that this MenuItem is a check box item
     * @param actionListener ActionListener object for the check box menu item
     */
    public MenuItem(String menuItemName, boolean isCheckBoxItem, ActionListener actionListener){

        //There has to be a better way to do this....... Since both JMenuItem and JCheckBoxMenuItem
        //Are separate classes, there's no easy way to make  this class inherit from both of them!
        //For now.... this works.....
        checkBoxMenuItem = new JCheckBoxMenuItem(menuItemName);
        checkBoxMenuItem.setName(menuItemName);
        checkBoxMenuItem.addActionListener(actionListener);
        setActionCommand(menuItemName);
        this.isCheckBoxItem = isCheckBoxItem;
    }

    /**
     * Sets up the required keys for the short cut of this menu item
     * @param keyCode Integer that corresponds with the short cut key of this menu item
     */
    private void setupShortcut(int keyCode){
        if(isShiftKeyShortcut) setAccelerator(KeyStroke.getKeyStroke(keyCode,SHORTCUT_KEY_MASK | ActionEvent.SHIFT_MASK));
        else setAccelerator(KeyStroke.getKeyStroke(keyCode, SHORTCUT_KEY_MASK));
    }

    /**
     * Returns the KeyEvent constant that corresponds to the character passed
     * into the method
     * @param character is the character that needs to be processed into a KeyEvent constant
     * @return An integer that corresponds to the key event of the supplied character
     */
    private int getKeyEventForChar(char character){
        try {
            switch (character) {
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
                default: throw new IllegalArgumentException("Argument needs to be a character between A-Z. Found: " + character);
            }
        }catch(IllegalArgumentException e){throw new Error(e.getMessage());}
    }

    public JCheckBoxMenuItem getCheckBoxMenuItem(){
        return checkBoxMenuItem;
    }
}
