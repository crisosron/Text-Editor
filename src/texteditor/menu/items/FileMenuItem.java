package texteditor.menu.items;

public class FileMenuItem extends MenuItem {
    public FileMenuItem(String name){
        super(name);
    }

    public FileMenuItem(String name, boolean hasShortcut, boolean isShiftKeyShortcut){
        super(name, hasShortcut, isShiftKeyShortcut);
    }

    public FileMenuItem(String name, boolean hasShortcut, boolean isShiftKeyShortcut, int customShortcut){
        super(name, hasShortcut, isShiftKeyShortcut, customShortcut);
    }
}
