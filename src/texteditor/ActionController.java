package texteditor;
import texteditor.menu.items.MenuItem;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

class ActionController {
    private TextEditor textEditor;

    //For file management
    private boolean changesMade = false;
    private boolean cancelClose = false;
    private boolean cancelOpenFile;
    private boolean cancelNewFile;
    private File openedFile;

    //Format menu booleans
    private boolean isWrapping = false; //Wrapping of text area is set to false by default
    private boolean lightThemeActive = true; //Light theme on by default
    private boolean darkThemeActive = false;

    /**
     * Constructs an ActionController object which performs the actions that can be performed
     * in the text editor
     * @param textEditor TextEditor instance this ActionController is for
     */
    ActionController(TextEditor textEditor){this.textEditor = textEditor;}

    /**
     * Checks if the supplied file name has .txt extension or has no extension at all
     * @param fileName String file name to check
     */
    private boolean validFileName(String fileName){
	    String fileExtension = fileName.substring(fileName.lastIndexOf("."));
    	return fileExtension.equals(".txt") || fileExtension.equals("");
    }

    /**
     * Opens a file for editing
     */
     void openFile(){
        try{

            //Performs save check
            if(changesMade) saveCheck(SaveCheck.OPEN_FILE);

            /*If the saveAs method is called during saveCheck method, and the user cancels the save as dialog, assume
            * that the user wants to cancel the entire operation of opening a new file altogether - This prevents the user
            * from being spammed a whole bunch of dialog boxes*/
            if(cancelOpenFile) {
                cancelOpenFile = false;
                return;
            }

            //File opening operations
            JFileChooser openFileChooser = new JFileChooser();
            openFileChooser.setCurrentDirectory(new File(System.getProperty("user.home"))); //Gets current working directory
            int status = openFileChooser.showOpenDialog(null); //Prompting user to open a file
            if(status != JFileChooser.APPROVE_OPTION){
                JOptionPane.showMessageDialog(null, "No file selected!");
            }
            else{

                //Getting text in file and placing into mainTextArea
                openedFile = openFileChooser.getSelectedFile();
                String openedFileName = openFileChooser.getSelectedFile().getName();

                //Validates the opened file
                if(!validFileName(openedFileName)){
                    JOptionPane.showMessageDialog(null, "Please open a txt file or a file with no extension!");
                    return;
                }

                Scanner scan = new Scanner(openedFile);
                StringBuilder textToDisplay = new StringBuilder();
                while(scan.hasNext()) textToDisplay.append(scan.nextLine()).append("\n"); //Concatenating the string
                textEditor.getMainTextArea().setText(textToDisplay.toString());
                textEditor.setTitle(openedFileName);
                changesMade = false;
            }

        }catch(FileNotFoundException e){throw new Error("File not found");}
    }

    /**
     * Saves the current file - Calls saveFile if the file does not exist in a directory
     */
     void saveFile(){
        try{

            //If the user tries to save to a newly created file with no changes
            if(!changesMade)return;

            //If saving to an existing file
            if(openedFile != null){
                FileWriter writeToOpenedFile = new FileWriter(openedFile);
                textEditor.getMainTextArea().write(writeToOpenedFile);
                JOptionPane.showMessageDialog(null, "File Saved!");
                changesMade = false;
                return;
            }

            //If the user is trying to save a new file, prompt save as
            saveFileAs();

        }catch(IOException e) {throw new Error("Unable to write to file");}
    }

    /**
     *  Saves the file by using the save dialog
     */
     void saveFileAs(){
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

                //Validates the selected file name to save to
                if(!validFileName(fileToSave.getName())){
                    JOptionPane.showMessageDialog(null, "Please save to a txt file or no file extension!");
                    return;
                }

		        //Writes to the file
                JOptionPane.showMessageDialog(null, fileToSave.getName());
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
     * @param action SaveCheck enum that represents the action that caused the invocation of this method
     */
     void saveCheck(SaveCheck action){
        int optionInput = JOptionPane.showConfirmDialog(null, "Would you like to save changes made? ");
        if(optionInput == JOptionPane.YES_OPTION) {
            saveFile();

            /*If the saveFileAs method is called within saveFile and the user clicks cancel or closes
            * the save as dialog, cancelClose is set to true*/
            if(cancelClose) {
                if(action == SaveCheck.NEW_FILE) cancelNewFile = true;
                if(action == SaveCheck.OPEN_FILE) cancelOpenFile = true;
                return;
            }

            if(action == SaveCheck.EXIT_ON_WINDOW || action == SaveCheck.EXIT_ON_MENU) {
                if(textEditor.getInstanceNum() != 0) textEditor.dispose();
                System.exit(0);
            }
        }
        else{
            //Exits the method if cancel was clicked on dialog
            if(optionInput == JOptionPane.CANCEL_OPTION) return;

            //If the user clicks exit on the window
            if(action == SaveCheck.EXIT_ON_WINDOW || action == SaveCheck.EXIT_ON_MENU) {
                if(textEditor.getInstanceNum() != 0) textEditor.dispose();
                else System.exit(0);
            }
        }
    }

    /**
     * Creates a new untitled document
     */
    void newDocument(){

        //Checks if the user wants to save unsaved changes
        if(changesMade) {
            saveCheck(SaveCheck.NEW_FILE);
        }

        //Exits method if the creation of the new file is cancelled
        if(cancelNewFile){
            cancelNewFile = false;
            return;
        }

        //Resetting some states
        openedFile = null;
        textEditor.getMainTextArea().setText("");
        changesMade = false;
        textEditor.setTitle("TextEditor");
    }

    /**
     * Forces a hard exit -Called when the Exit menu item in the File menu is clicked
     */
     void exit(){
        if(changesMade)saveCheck(SaveCheck.EXIT_ON_MENU);

        //Exits the program if the last TextEditor instance existing is to be closed
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
     void changeTheme(String changeThemeTo){

        JTextArea mainTextArea = textEditor.getMainTextArea();
        Map<String, MenuItem> menuItemsMap = textEditor.getMenuItemsMap();

        /*Getting the menu items for theme selection*/
        JCheckBoxMenuItem lightThemeMenuItem = menuItemsMap.get("Light Theme").getCheckBoxMenuItem();
        JCheckBoxMenuItem darkThemeMenuItem = menuItemsMap.get("Dark Theme").getCheckBoxMenuItem();

        if(changeThemeTo.equals("Dark Theme")){

            //If theme is already active, re-toggle the selection and exit
            if(darkThemeActive) {
                darkThemeMenuItem.setSelected(true);
                return;
            }

            //Setting some booleans and selection logic
            darkThemeActive = true;
            lightThemeActive = false;
            lightThemeMenuItem.setSelected(false);
            darkThemeMenuItem.setSelected(true);

            //Setting colors for the text area
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
     void setWordWrap(){
        isWrapping = !isWrapping;
        textEditor.getMainTextArea().setLineWrap(isWrapping);
    }

    /**
     * Method that copies text and removes it from the mainTextArea component
     */
     void cut(){
        String textToCut = textEditor.getMainTextArea().getSelectedText();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //Clipboard to store the selected text
        StringSelection selectedText = new StringSelection(textToCut); //StringSelection object to store the selected text into the clipboard object
        clipboard.setContents(selectedText, selectedText);
        textEditor.getMainTextArea().replaceSelection(""); //Removing selected text from the text area
    }

    /**
     * Method that takes the text stored in the system clipboard and pastes it to
     * the mainTextArea component
     */
     void paste() {
        try {
            JTextArea mainTextArea = textEditor.getMainTextArea();
            String textToPaste = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            mainTextArea.insert(textToPaste, mainTextArea.getCaretPosition()); //Inserts the text in the clipboard in the current position of the caret
        }catch(Exception e){e.printStackTrace();}
    }

    /**
     * Copies selected text in mainTextArea into system clipboard without removing the text from the mainTextArea component
     */
     void copy(){
        StringSelection textToCopy = new StringSelection(textEditor.getMainTextArea().getSelectedText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(textToCopy, textToCopy);
    }

    /**
     * Undo's most recent action
     */
     void undo(){
        if(textEditor.getUndoManager().canUndo()) textEditor.getUndoManager().undo();
    }

    /**
     * Redo's an undone action
     */
     void redo(){
        if(textEditor.getUndoManager().canRedo()) textEditor.getUndoManager().redo();
    }

    /**
     * Creates a new line and creates 4 spaces and a hyphen - Acts as a bullet point generator - Generated at caret position
     */
     void insertPoint(){
        textEditor.getMainTextArea().insert("\n" + "    " + "-", textEditor.getMainTextArea().getCaretPosition());
    }

    /**
     * Serves the purpose of being a sub point for a created point at the caret position
     */
     void insertSubPoint(){
        textEditor.getMainTextArea().insert("\n" + "    " + "    " + "-", textEditor.getMainTextArea().getCaretPosition());
    }

    /**
     * Inserts the current date and day of the week into the text area
     */
     void insertDate(){

        //Getting current date in dd/mm/yyyy format
        Date date = new Date();
        String dateFormatString= "dd/MM/yyyy"; //Capital MM important to differentiate from mm (milliseconds)
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        String formattedDate = dateFormat.format(date);

        //Using Calendar object to get the day of the week
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String dayOfTheWeek = "";

        //Switch statement to determine the day of the week
        switch(day){
            case Calendar.SUNDAY: dayOfTheWeek = "Sunday"; break;
            case Calendar.MONDAY: dayOfTheWeek = "Monday"; break;
            case Calendar.TUESDAY: dayOfTheWeek = "Tuesday"; break;
            case Calendar.WEDNESDAY: dayOfTheWeek = "Wednesday"; break;
            case Calendar.THURSDAY: dayOfTheWeek = "Thursday"; break;
            case Calendar.FRIDAY: dayOfTheWeek = "Friday"; break;
            case Calendar.SATURDAY: dayOfTheWeek = "Saturday"; break;
        }

        //Outputs date at the caret position in the text area
        textEditor.getMainTextArea().insert(dayOfTheWeek + " - " + formattedDate, textEditor.getMainTextArea().getCaretPosition());
    }

    //Getters
     boolean hasChangesMade(){return changesMade;}
     boolean openedFileExists(){return openedFile != null;}

    //Setters
     void setChangesMadeTrue(){changesMade = true;}
     void setChangesMadeFalse(){changesMade = false;}
}
