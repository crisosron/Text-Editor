import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class PaintWindow extends JFrame implements MouseListener, MouseMotionListener, ActionListener {

    /*Collections to manage panels*/
    private Map<String, JPanel> panelMap;
    private Set<String> panelNamesSet;

    /*Collections for color buttons*/
    JButton colorButtons[][];
    private Map<String, Color> colorCommandsMap;
    private List<String> availableHues;

    private Set<String> toolCommands;
    /*Rows and columns for colorButtons 2D Array*/
    int numRow = 10;
    int numCol = 8;

    /*Constants*/
    private final int SIDE_PANEL_WIDTH = 250;
    private final int SIDE_PANEL_HEIGHT = 800;
    private final int CANVAS_PANEL_WIDTH = TextEditor.FRAME_WIDTH - SIDE_PANEL_WIDTH;
    private final int CANVAS_PANEL_HEIGHT = TextEditor.FRAME_HEIGHT;
    private final int TOOL_PANEL_WIDTH = 200;
    private final int TOOL_PANEL_HEIGHT = 80;
    private final int COLOR_PANEL_WIDTH = 200;
    private final int COLOR_PANEL_HEIGHT = 250;
    private final int FILL_PANEL_WIDTH = 200;
    private final int FILL_PANEL_HEIGHT = 50;
    private final int OTHER_ACTIONS_PANEL_WIDTH = 200;
    private final int OTHER_ACTIONS_PANEL_HEIGHT = 310;
    private final int COLOR_BUTTON_SIZE = 20;
    private final int TOOL_BUTTON_SIZE = 35;

    public PaintWindow(){

        /*Initializing some collections*/
        panelMap = new HashMap<>();
        colorButtons = new JButton[numRow][numCol];
        availableHues = new ArrayList<>(Arrays.asList("Black", "Red", "Green", "Blue", "Yellow", "Cyan", "Magenta", "Orange", "Violet", "Brown"));
        colorCommandsMap = new HashMap<>();
        toolCommands = new HashSet<>();

        setupPaintWindowUI();
    }

    public void setupPaintWindowUI(){

        /*Setting up the paint window frame*/
        setTitle("Paint");
        setLayout(null);
        setSize(new Dimension(TextEditor.FRAME_WIDTH, TextEditor.FRAME_HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE); //TODO: Change to ignore - need to implement save check at some point
        setVisible(true);
        setResizable(false);

        panelNamesSet = new HashSet<>(Arrays.asList("Menu", "Canvas"));

        /*Creating new panels - Note that the order matters in terms of their creation since absolute positioning is being used*/
        createPanel("Canvas Panel", SIDE_PANEL_WIDTH, 0, CANVAS_PANEL_WIDTH, CANVAS_PANEL_HEIGHT);
        createPanel("Tool", 25, 20, TOOL_PANEL_WIDTH, TOOL_PANEL_HEIGHT);
        createPanel("Color", 25, TOOL_PANEL_HEIGHT + 20, COLOR_PANEL_WIDTH, COLOR_PANEL_HEIGHT);
        createPanel("Fill", 25, TOOL_PANEL_HEIGHT + COLOR_PANEL_HEIGHT + 20, FILL_PANEL_WIDTH, FILL_PANEL_HEIGHT);
        createPanel("Other Actions", 25, TOOL_PANEL_HEIGHT + COLOR_PANEL_HEIGHT + FILL_PANEL_HEIGHT + 20, OTHER_ACTIONS_PANEL_WIDTH, OTHER_ACTIONS_PANEL_HEIGHT);
        createPanel("Side Panel", 0, 0, SIDE_PANEL_WIDTH, SIDE_PANEL_HEIGHT);

        /*Setting the background of all panels to white*/
        for(Map.Entry<String, JPanel> panelEntry : panelMap.entrySet()){
            panelEntry.getValue().setBackground(Color.white);
        }

        /*Setting up the color buttons*/
        setupColorButtons();

        /*Setting up the tool buttons*/
        setupToolButtons();
    }

    /**
     * Method that sets up the color buttons
     */
    public void setupColorButtons(){

        /*Setting the layout of the color panel to null so that the
         * buttons being added can be positioned absolutely*/
        panelMap.get("Color").setLayout(null);

        /*Variables for the buttons*/
        int x = 20;
        int y = 25;
        int r = 0;
        int g = 0;
        int b = 0;
        int hueCount = 0;

        /*Looping through the rows of the colorButtons 2d array*/
        for(int row = 0; row < colorButtons.length; row++){

            /*Getting combination of rgb*/
            List<Integer> selectedColorCombo = determineHue(availableHues.get(hueCount));
            r = selectedColorCombo.get(0);
            g = selectedColorCombo.get(1);
            b = selectedColorCombo.get(2);

            /*Processing the hue for each column in the row - There should be a decrease in
             * vividness as the number of columns within the row increases*/
            for(int col = 0; col < colorButtons[row].length; col++){

                /*Creating the color, color command and the color button and placing into 2d array*/
                Color color = new Color(r, g, b);
                String colorCommand = availableHues.get(hueCount) +  "[" + r + g + b + "]";
                JButton colorButton = createColorButton(color, x, y, colorCommand);
                colorButtons[row][col] = colorButton;
                x += COLOR_BUTTON_SIZE;

                /*Incrementing each of rgb by the same amount to get an overall decrease in saturation*/
                /*Setting r to max if it is about to exceed the max value*/
                if(r + 20 >= 255) r = 255;
                else r += 20;

                /*Setting r to max if it is about to exceed the max value*/
                if(g + 20 >= 255) g = 255;
                else g += 20;

                /*Setting r to max if it is about to exceed the max value*/
                if(b + 20 >= 255) b = 255;
                else b += 20;
            }
            hueCount++;
            x = 20;
            y += COLOR_BUTTON_SIZE;
        }
    }

    /**
     * Sets up the tool buttons
     */
    public void setupToolButtons(){

        /*Setting the Tool panel layout to null so that components within it can be
        * positioned absolutely*/
        panelMap.get("Tool").setLayout(null);

        /*Variables for the buttons*/
        int x = 10;
        int y = 25;

        /*File object and array to get to the icon files*/
        File imagesFolder = new File("Images");
        File[] iconFiles = imagesFolder.listFiles();

        /*Looping through iconFiles array to create a buttons from*/
        for(int i = 0; i < iconFiles.length; i++){

            /*Name of the icon file (not including 'Images' folder directory)*/
            String iconFileName = iconFiles[i].getName();

            /*Removes the 'Icon.gif' part of the icon file and sets just the tool name as part of the command*/
            String toolCommand = "Set Tool " + iconFileName.replace("Icon.gif", "");

            /*Creating the actual tool button*/
            createToolButton(iconFileName, x, y, toolCommand);
            x += TOOL_BUTTON_SIZE;
        }
    }

    /**
     * Method used to determine what hue should be generated for the color buttons (used to determine hue for row)
     * Returns a List with rgb values in order
     */
    public List<Integer> determineHue(String hueToGenerate){
        if(hueToGenerate.equals("Red")) return (new ArrayList<>(Arrays.asList(255, 0, 0)));
        else if(hueToGenerate.equals("Green")) return (new ArrayList<>(Arrays.asList(0, 255, 0)));
        else if (hueToGenerate.equals("Blue")) return (new ArrayList<>(Arrays.asList(0, 0, 255)));
        else if (hueToGenerate.equals("Black")) return (new ArrayList<>(Arrays.asList(0, 0, 0)));
        else if (hueToGenerate.equals("Yellow")) return (new ArrayList<>(Arrays.asList(255, 255, 0)));
        else if (hueToGenerate.equals("Cyan")) return (new ArrayList<>(Arrays.asList(0, 255, 255)));
        else if (hueToGenerate.equals("Magenta")) return (new ArrayList<>(Arrays.asList(255, 0, 255)));
        else if (hueToGenerate.equals("Orange")) return (new ArrayList<>(Arrays.asList(255, 165, 0)));
        else if (hueToGenerate.equals("Violet")) return (new ArrayList<>(Arrays.asList(238, 130, 238)));
        else if (hueToGenerate.equals("Brown")) return (new ArrayList<>(Arrays.asList(165, 42, 42)));
        return null;
    }

    /**
     * Method that creates panels and positions them onto the frame
     */
    public void createPanel(String name, int x, int y, int width, int height){
        JPanel newPanel = new JPanel();
        newPanel.setBounds(x, y, width, height);
        newPanel.setBorder(BorderFactory.createTitledBorder(name));
        panelMap.put(name, newPanel);
        add(newPanel);
    }

    /**
     * Creates a button that represents a color - Used for color selection
     */
    public JButton createColorButton(Color color, int x, int y, String colorCommand){
        JButton colorButton = new JButton();
        colorButton.setContentAreaFilled(false); //Removes default fill of button
        colorButton.setOpaque(true); //Allows for custom fill
        colorButton.setBackground(color);
        colorButton.setForeground(color);
        colorButton.setBounds(x, y, COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE);
        colorButton.setActionCommand(colorCommand);
        colorCommandsMap.put(colorCommand, color);
        panelMap.get("Color").add(colorButton);
        colorButton.addActionListener(this);
        return colorButton;
    }

    /**
     * Creates a button that represents a tool that the user can use
     */
    public void createToolButton(String iconFileName, int x, int y, String toolCommand){

        /*Creating the icon for the image*/
        Icon buttonIcon = new ImageIcon("Images/"+iconFileName);

        /*Creating the JButton object and positioning and placing into panel*/
        JButton toolButton = new JButton();
        toolButton.setIcon(buttonIcon);
        toolButton.setBounds(x, y, TOOL_BUTTON_SIZE, TOOL_BUTTON_SIZE);
        panelMap.get("Tool").add(toolButton);

        /*Setup of the button for the ActionListener*/
        toolCommands.add(toolCommand);
        toolButton.setActionCommand(toolCommand);
        toolButton.addActionListener(this);

        /*Validating the icons*/
        validate();
    }

    /*For MouseListener*/
    public void mouseClicked(MouseEvent mouseEvent){}
    public void mousePressed(MouseEvent mouseEvent){}
    public void mouseReleased(MouseEvent mouseEvent){}
    public void mouseEntered(MouseEvent mouseEvent){}
    public void mouseExited(MouseEvent mouseEvent){}

    /*For MouseMotionListener*/
    public void mouseDragged(MouseEvent mouseEvent){}
    public void mouseMoved(MouseEvent mouseEvent){}

    /*For ActionListener*/
    public void actionPerformed(ActionEvent actionEvent){
        String action = actionEvent.getActionCommand();
        if(colorCommandsMap.containsKey(action))panelMap.get("Canvas Panel").setBackground(colorCommandsMap.get(action)); //TODO: This is temporary
        else if(toolCommands.contains(action)){
            System.out.println("Tool Selected");
        }
    }
}
