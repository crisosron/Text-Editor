package texteditor.menu.items;

public class NoCustomShortcutException extends Exception{
    public NoCustomShortcutException(String menuItemName){
        super(menuItemName + " has no corresponding custom shortcut");
    }
}
