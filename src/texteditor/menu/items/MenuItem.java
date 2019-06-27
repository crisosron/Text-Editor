package texteditor.menu.items;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
//TODO: Split action listening to be restricted to each type of menu item - eg EditMenuItem implements EditActionController?

public class MenuItem extends JMenuItem {
    protected String menuItemName;
    protected boolean isCheckBoxItem;
    public static final int SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    protected boolean isShiftKeyShortcut;
    protected boolean hasShortcut;

    /**
     * Basic constructor for menu items with no shortcuts
     * @param menuItemName Name corresponding to the menu item
     */
    public MenuItem(String menuItemName){
        super(menuItemName);
        this.menuItemName = menuItemName;
        isCheckBoxItem = false;
        isShiftKeyShortcut = false;
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
        setActionCommand(menuItemName); //For click events
    }

    public MenuItem(String menuItemName, boolean hasShortcut, boolean isShiftKeyShortcut, int customShortcut){
        super(menuItemName);
        this.menuItemName = menuItemName;
        this.hasShortcut = hasShortcut;
        this.isShiftKeyShortcut = isShiftKeyShortcut;
        isCheckBoxItem = false;
        setActionCommand(menuItemName); //For click events
    }

    /**
     * Constructor for menu items that are check box menu items
     * */
    public MenuItem(String menuItemName, boolean isCheckBoxItem){

    }


    public String getMenuItemName() {
        return menuItemName;
    }

    public boolean isCheckBoxItem() {
        return isCheckBoxItem;
    }

    public static int getShortcutKeyMask() {
        return SHORTCUT_KEY_MASK;
    }

    public boolean isShiftKeyShortcut() {
        return isShiftKeyShortcut;
    }

    public boolean isHasShortcut() {
        return hasShortcut;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public void setCheckBoxItem(boolean checkBoxItem) {
        isCheckBoxItem = checkBoxItem;
    }

    public void setShiftKeyShortcut(boolean shiftKeyShortcut) {
        isShiftKeyShortcut = shiftKeyShortcut;
    }

    public void setHasShortcut(boolean hasShortcut) {
        this.hasShortcut = hasShortcut;
    }
}
