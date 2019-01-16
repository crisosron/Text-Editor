//TODO: Merge light theme and dark theme methods into one methods
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class ActionController {
    private TextEditor textEditor;

    /*For file management*/
    private String openedFileName = "";
    private String openedFileNamePath = "";
    private boolean hasOpenedFile = false;
    private boolean changesMade = false;
    private boolean cancelClose = false;

    /*Format menu booleans*/
    private boolean isWrapping = false; //Wrapping of text area is set to false by default
    private boolean lightThemeActive = true; //Light theme on by default
    private boolean darkThemeActive = false;

    public ActionController(TextEditor textEditor){this.textEditor = textEditor;}
    public void openFile(){
        try{

            /*Prompting user to open a file using file choosers*/
            JFileChooser openFileChooser = new JFileChooser();
            openFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir"))); //Gets current working directory
            int status = openFileChooser.showOpenDialog(null); //Prompting user to open a file
            if(status != JFileChooser.APPROVE_OPTION){
                JOptionPane.showMessageDialog(null, "No file selected!");
                return;
            }
            else{

                /*Getting the text in the opened file and transferring it onto the
                 * mainTextArea component*/
                File openedFile = openFileChooser.getSelectedFile();
                openedFileName = openFileChooser.getSelectedFile().getName();
                openedFileNamePath = openedFile.getAbsolutePath(); //Used for saving to an existing file
                hasOpenedFile = true;
                Scanner scan = new Scanner(openedFile);
                String textToDisplay = "";
                while(scan.hasNext()) textToDisplay += scan.nextLine() + "\n";
                textEditor.getMainTextArea().setText(textToDisplay);
                textEditor.setTitle(openedFileName); //Setting the title of the frame to the name of the opened text file
                changesMade = false;
            }

        }catch(Exception e){e.printStackTrace();}
    }

    /**
     * Called when the Save menu item in the File menu. This method differs from saveFileAs method since
     * it first checks if the user is trying to save to an existing file. If not, call the saveFileAs method
     */
    public void saveFile(){
        try{

            /*If the user attempts to save a newly opened file with no changes - return*/
            if(!changesMade && hasOpenedFile)return;

            /*If saving to existing file*/
            if(hasOpenedFile){
                File openedFile = new File(openedFileNamePath);
                FileWriter writeToOpenedFile = new FileWriter(openedFile);
                textEditor.getMainTextArea().write(writeToOpenedFile);
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
            if (status != JFileChooser.APPROVE_OPTION){
                JOptionPane.showMessageDialog(null, "Save cancelled!");
                cancelClose = true; //Makes sure the program does not exit if cancel or the exit button is clicked in the save dialog
                return;
            }
            else {
                cancelClose = false;
                File fileToSave = saveFileChooser.getSelectedFile(); //Creates a new file with a title based on the user's input
                FileWriter writer = new FileWriter(fileToSave); //FileWriter object to write to the newly created file
                textEditor.getMainTextArea().write(writer); //Gets the text in the mainTextArea component and writes it to the newly created file
                textEditor.setTitle(fileToSave.getName());
                JOptionPane.showMessageDialog(null, "File saved as: " + fileToSave.getName());
                openedFileNamePath = fileToSave.getAbsolutePath();
            }
            hasOpenedFile = true;
            changesMade = false;

        }catch(Exception e){e.printStackTrace();}

    }

    /**
     * Method called when the user attempts to create a new document or exit the program without saving
     * changes made to the current document
     *
     * Parameter sourceID notation:
     * 0 = User clicked on exit button on the window
     * 1 = User clicked on Exit JMenuItem in the File menu
     * 2 = User clicked on New JMenuItem in the File menu without saving current changes
     */
    public void saveCheck(int sourceID){
        int optionInput = JOptionPane.showConfirmDialog(null, "Would you like to save changes made? ");
        if(optionInput == JOptionPane.YES_OPTION) {
            saveFile();
            if(sourceID == 0 || sourceID == 1 && !cancelClose) System.exit(0);
        }
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
        textEditor.getMainTextArea().setText("");
        openedFileNamePath = "";
        openedFileName = "";
        changesMade = false;
    }

    /**
     * Forces a hard exit -Called when the Exit menu item in the File menu is clicked
     */
    public void exit(){
        if(changesMade)saveCheck(1);
        else System.exit(EXIT_ON_CLOSE);
    }

    /**
     * Changes the editor to use a dark theme
     */
    public void enableDarkTheme(){
        JTextArea mainTextArea = textEditor.getMainTextArea();
        List<JCheckBoxMenuItem> checkBoxMenuItemList = textEditor.getCheckBoxMenuItemsList();

        JCheckBoxMenuItem darkThemeMenuItem = new JCheckBoxMenuItem();
        JCheckBoxMenuItem lightThemeMenuItem = new JCheckBoxMenuItem();
        for(JCheckBoxMenuItem checkBoxMenuItem : checkBoxMenuItemList){
            if(checkBoxMenuItem.getName().equals("Dark Theme")) darkThemeMenuItem = checkBoxMenuItem;
            else if(checkBoxMenuItem.getName().equals("Light Theme")) lightThemeMenuItem = checkBoxMenuItem;
        }

        /*Exits the method if the dark theme is already active*/
        if(darkThemeActive) {
            darkThemeMenuItem.setSelected(true); //Dark theme retains selection appearance
            return;
        }

        /*Operations to convert to dark theme*/
        darkThemeActive = true;
        lightThemeActive = false;
        darkThemeMenuItem.setSelected(true); //Enables the tick for the dark theme menu item
        lightThemeMenuItem.setSelected(false); //Disables the tick for the light theme menu item
        mainTextArea.setCaretColor(Color.white);
        mainTextArea.setForeground(Color.white);
        mainTextArea.setBackground(new Color(42, 42, 42));
    }

    /**
     * Changes the editor to use a light theme
     */
    public void enableLightTheme(){

        JTextArea mainTextArea = textEditor.getMainTextArea();
        List<JCheckBoxMenuItem> checkBoxMenuItemList = textEditor.getCheckBoxMenuItemsList();

        JCheckBoxMenuItem lightThemeMenuItem = new JCheckBoxMenuItem();
        JCheckBoxMenuItem darkThemeMenuItem = new JCheckBoxMenuItem();
        for(JCheckBoxMenuItem checkBoxMenuItem : checkBoxMenuItemList){
            if(checkBoxMenuItem.getName().equals("Dark Theme")) darkThemeMenuItem = checkBoxMenuItem;
            else if(checkBoxMenuItem.getName().equals("Light Theme")) lightThemeMenuItem = checkBoxMenuItem;
        }

        /*Exits the method if the light theme is already active*/
        if(lightThemeActive) {
            lightThemeMenuItem.setSelected(true);
            return;
        }

        /*Operations to convert to light theme*/
        lightThemeActive = true;
        darkThemeActive = false;
        darkThemeMenuItem.setSelected(false); //Disables the tick for the dark theme menu item
        lightThemeMenuItem.setSelected(true); //Enables the tick for the light theme menu item
        mainTextArea.setCaretColor(Color.black);
        mainTextArea.setForeground(Color.black);
        mainTextArea.setBackground(Color.white);
    }

    /**
     * Activates/Deactivates the word wrap functionality around the mainTextArea component
     */
    public void setWordWrap(){
        isWrapping = !isWrapping;
        textEditor.getMainTextArea().setLineWrap(isWrapping);
    }

    /**
     * Method that copies text and removes it from the mainTextArea component
     */
    public void cut(){

        /*Text to cut is the selected text in the mainTextArea component*/
        String textToCut = textEditor.getMainTextArea().getSelectedText();

        /*Clipboard object to store the highlighted text in the mainTextArea component*/
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        /*StringSelection object that holds the highlighted text in the mainTextArea*/
        StringSelection selectedText = new StringSelection(textToCut);

        /*Storing selected text into system clipboard*/
        clipboard.setContents(selectedText, selectedText);

        /*Operations to remove the selected text from the mainTextArea component*/
        textEditor.getMainTextArea().replaceSelection("");

    }

    /**
     * Method that takes the text stored in the system clipboard and pastes it to
     * the mainTextArea component
     */
    public void paste() {
        try {
            JTextArea mainTextArea = textEditor.getMainTextArea();
            String textToPaste = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            mainTextArea.insert(textToPaste, mainTextArea.getCaretPosition()); //Inserts the text in the clipboard in the current position of the caret
        }catch(Exception e){e.printStackTrace();}
    }

    /**
     * Copies selected text in mainTextArea into system clipboard without removing the text from the mainTextArea component
     */
    public void copy(){
        StringSelection textToCopy = new StringSelection(textEditor.getMainTextArea().getSelectedText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(textToCopy, textToCopy);
    }

    /**
     * Undo's most recent action
     */
    public void undo(){
        if(textEditor.getUndoManager().canUndo()) textEditor.getUndoManager().undo();
    }

    /**
     * Redo's an undone action
     */
    public void redo(){
        if(textEditor.getUndoManager().canRedo()) textEditor.getUndoManager().redo();
    }

    /*Getters*/
    public boolean isChangesMade(){return changesMade;}
    public boolean isHasOpenedFile(){return hasOpenedFile;}

    /*Setters*/
    public void setChangesMadeTrue(){changesMade = true;}
    public void setChangesMadeFalse(){changesMade = false;}
}
