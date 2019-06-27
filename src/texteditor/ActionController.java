package texteditor;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class ActionController {
    private TextEditor textEditor;

    /*For file management*/
    private boolean changesMade = false;
    private boolean cancelClose = false;
    private boolean cancelOpenFile;
    private boolean cancelNewFile;
    private File openedFile;

    /*Format menu booleans*/
    private boolean isWrapping = false; //Wrapping of text area is set to false by default
    private boolean lightThemeActive = true; //Light theme on by default
    private boolean darkThemeActive = false;

    public ActionController(TextEditor textEditor){this.textEditor = textEditor;}
    public void openFile(){
        try{

            /*Performs save check*/
            if(changesMade) saveCheck(3);

            /*If the saveAs method is called during saveCheck method, and the user cancels the save as dialog, assume
            * that the user wants to cancel the entire operation of opening a new file altogether - This prevents the user
            * from being spammed a whole bunch of dialog boxes*/
            if(cancelOpenFile) {
                cancelOpenFile = false;
                return;
            }

            /*Prompting user to open a file using file choosers*/
            JFileChooser openFileChooser = new JFileChooser();
            openFileChooser.setCurrentDirectory(new File(System.getProperty("user.home"))); //Gets current working directory
            int status = openFileChooser.showOpenDialog(null); //Prompting user to open a file
            if(status != JFileChooser.APPROVE_OPTION){
                JOptionPane.showMessageDialog(null, "No file selected!");
                return;
            }
            else{

                /*Getting the text in the opened file and transferring it onto the
                 * mainTextArea component*/
                openedFile = openFileChooser.getSelectedFile();
                String openedFileName = openFileChooser.getSelectedFile().getName();
                Scanner scan = new Scanner(openedFile);
                String textToDisplay = "";
                while(scan.hasNext()) textToDisplay += scan.nextLine() + "\n";
                textEditor.getMainTextArea().setText(textToDisplay);
                textEditor.setTitle(openedFileName);
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
            if(!changesMade)return;

            /*If saving to existing file*/
            if(openedFile != null){
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
                openedFile = fileToSave;
            }
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
     * 3 = User clicked on open file when changes were made to the current file
     */
    public void saveCheck(int sourceID){
        int optionInput = JOptionPane.showConfirmDialog(null, "Would you like to save changes made? ");
        if(optionInput == JOptionPane.YES_OPTION) {
            saveFile();

            /*If the saveFileAs method is called within saveFile and the user clicks cancel or closes
            * the save as dialog, cancelClose is set to true*/
            if(cancelClose) {
                if(sourceID == 2) cancelNewFile = true;
                if(sourceID == 3) cancelOpenFile = true;
                return;
            }

            if(sourceID == 0 || sourceID == 1) {
                if(textEditor.getInstanceNum() != 0) textEditor.dispose();
                System.exit(0);
            }
        }
        else if(optionInput == JOptionPane.CANCEL_OPTION) return;
        else{
            /*If the user clicks exit on the window*/
            if(sourceID ==  0 || sourceID == 1) {
                if(textEditor.getInstanceNum() != 0) textEditor.dispose();
                else System.exit(0);
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

        if(cancelNewFile){
            cancelNewFile = false;
            return;
        }

        /*Resetting some things*/
        openedFile = null;
        textEditor.getMainTextArea().setText("");
        changesMade = false;
        textEditor.setTitle("texteditor.TextEditor");
    }

    /**
     * Forces a hard exit -Called when the Exit menu item in the File menu is clicked
     */
    public void exit(){
        if(changesMade)saveCheck(1);

        /*Closes the instance only if there is more than one instance of texteditor.TextEditor running
        * Ends the program completely otherwise*/
        if(textEditor.getInstanceList().size() - 1 <= 0) System.exit(0);
        else{
            textEditor.removeInstanceFromList(textEditor.getInstanceNum());
            textEditor.dispose();
        }
    }

    /**
     * Changes the theme to the specified string parameter
     * @param changeThemeTo Specifies what theme the editor should change to
     */
    public void changeTheme(String changeThemeTo){

        JTextArea mainTextArea = textEditor.getMainTextArea();
        List<JCheckBoxMenuItem> checkBoxMenuItemList = textEditor.getCheckBoxMenuItemsList();

        /*Getting the menu items for theme selection*/
        JCheckBoxMenuItem lightThemeMenuItem = new JCheckBoxMenuItem();
        JCheckBoxMenuItem darkThemeMenuItem = new JCheckBoxMenuItem();
        for(JCheckBoxMenuItem checkBoxMenuItem : checkBoxMenuItemList){
            if(checkBoxMenuItem.getName().equals("Dark Theme")) darkThemeMenuItem = checkBoxMenuItem;
            else if(checkBoxMenuItem.getName().equals("Light Theme")) lightThemeMenuItem = checkBoxMenuItem;
        }

        if(changeThemeTo.equals("Dark Theme")){

            /*If the theme is already active, re-toggle selected on the menu item and returns*/
            if(darkThemeActive) {
                darkThemeMenuItem.setSelected(true);
                return;
            }

            /*Setting booleans and selection logic*/
            darkThemeActive = true;
            lightThemeActive = false;
            lightThemeMenuItem.setSelected(false);
            darkThemeMenuItem.setSelected(true);

            /*Setting mainTextArea colors*/
            mainTextArea.setCaretColor(Color.white);
            mainTextArea.setForeground(Color.white);
            mainTextArea.setBackground(new Color(42, 42, 42));

        }else if(changeThemeTo.equals("Light Theme")){

            if(lightThemeActive){
                lightThemeMenuItem.setSelected(true);
                return;
            }

            lightThemeActive = true;
            darkThemeActive = false;
            lightThemeMenuItem.setSelected(true);
            darkThemeMenuItem.setSelected(false);

            mainTextArea.setCaretColor(Color.black);
            mainTextArea.setForeground(Color.black);
            mainTextArea.setBackground(Color.white);
        }
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

    /**
     * Creates a new line and creates 4 spaces and a hyphen - Acts as a bullet point generator - Generated at caret position
     */
    public void insertPoint(){
        textEditor.getMainTextArea().insert("\n" + "    " + "-", textEditor.getMainTextArea().getCaretPosition());
    }

    /**
     * Serves the purpose of being a sub point for a created point at the caret position
     */
    public void insertSubPoint(){
        textEditor.getMainTextArea().insert("\n" + "    " + "    " + "-", textEditor.getMainTextArea().getCaretPosition());
    }

    /**
     * Inserts the current date and day of the week into the text area
     */
    public void insertDate(){

        /*Getting the current date in the format dd/mm/yyyyy*/
        Date date = new Date();
        String dateFormatString= "dd/MM/yyyy"; //Capital MM important to differentiate from mm (milliseconds)
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        String formattedDate = dateFormat.format(date);

        /*Using calendar to get day of the week*/
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String dayOfTheWeek = "";

        /*Switch statement to determine what day of the week it is*/
        switch(day){
            case Calendar.SUNDAY: dayOfTheWeek = "Sunday"; break;
            case Calendar.MONDAY: dayOfTheWeek = "Monday"; break;
            case Calendar.TUESDAY: dayOfTheWeek = "Tuesday"; break;
            case Calendar.WEDNESDAY: dayOfTheWeek = "Wednesday"; break;
            case Calendar.THURSDAY: dayOfTheWeek = "Thursday"; break;
            case Calendar.FRIDAY: dayOfTheWeek = "Friday"; break;
            case Calendar.SATURDAY: dayOfTheWeek = "Saturday"; break;
        }

        /*Outputs text at the caret position*/
        textEditor.getMainTextArea().insert(dayOfTheWeek + " - " + formattedDate, textEditor.getMainTextArea().getCaretPosition());
    }

    /*Getters*/
    public boolean hasChangesMade(){return changesMade;}
    public boolean openedFileExists(){return openedFile != null;}

    /*Setters*/
    public void setChangesMadeTrue(){changesMade = true;}
    public void setChangesMadeFalse(){changesMade = false;}
}