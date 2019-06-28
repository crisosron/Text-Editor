package texteditor.menu.items;

public class FormatMenuItem extends MenuItem {
    public FormatMenuItem(String name){
        super(name);
    }

    public FormatMenuItem(String name, boolean hasShortcut, boolean isShiftKeyShortcut){
        super(name, hasShortcut, isShiftKeyShortcut);
    }

    public FormatMenuItem(String name, boolean hasShortcut, boolean isShiftKeyShortcut, int customShortcut){
        super(name, hasShortcut, isShiftKeyShortcut, customShortcut);
    }
}
