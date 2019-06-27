package texteditor.menu.items;

import java.awt.event.KeyEvent;

//TODO: Split action listening to be restricted to each type of menu item?
public class EditMenuItem extends MenuItem{
    public EditMenuItem(String name){
        super(name);
    }

    public EditMenuItem(String name, boolean hasShortcut, boolean isShiftKeyShortcut){
        super(name, hasShortcut, isShiftKeyShortcut);
    }

    public EditMenuItem(String name, boolean hasShortcut, boolean isShiftKeyShortcut, int customShortcut){
        super(name, hasShortcut, isShiftKeyShortcut, customShortcut);
    }
}

