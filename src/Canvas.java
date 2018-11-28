import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.PrintStream;
import java.util.*;
import java.util.List;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener {
    private int x, y, width, height;
    float eraserWidth, eraserHeight;
    private List<ShapeItem> shapeItems, whiteEraserCircleShapeItems, lineShapes; //shapeItems is the master list, lineShapes is used as a checker if a shape is a line
    private Point shapeStart, shapeEnd;

    public Canvas(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        shapeItems = new ArrayList<>();
        whiteEraserCircleShapeItems = new ArrayList<>();
        lineShapes = new ArrayList<>();
        eraserWidth = 50;
        eraserHeight = 50;
        setupCanvas();
    }

    public void setupCanvas(){
        setBounds(x, y, width, height);
        setBorder(BorderFactory.createTitledBorder("Canvas"));
        setBackground(Color.white);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    /**
     * Handles all the drawing of shapes
     */
    public void paintComponent(Graphics g){
        super.paintComponent(g); //Setting the parent of the Graphics object
        Graphics2D graphics2D = (Graphics2D)g; //Using 2d graphics library to gain access to more advanced operations

        /*Removes visual artifacts (jagged edges)*/
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        /*Drawing all the shapes*/
        for(ShapeItem shapeItem : shapeItems){
            graphics2D.setColor(shapeItem.getShapeColor());
            if(!shapeItem.isFilled() || lineShapes.contains(shapeItem)) {
                graphics2D.draw(shapeItem.getShape());
            }else{
                graphics2D.fill(shapeItem.getShape());
            }
        }

        /*Shape being drawn when the mouse is being dragged*/
        if(shapeEnd != null && shapeStart != null){
            if(TextEditor.paintWindow.selectedTool.equals("line")){
                Line2D.Float guideLine = new Line2D.Float(shapeStart.x, shapeStart.y, shapeEnd.x, shapeEnd.y);
                graphics2D.draw(guideLine);

            }else if(TextEditor.paintWindow.selectedTool.equals("rectangle")){
                Rectangle2D.Float guideLine = new Rectangle2D.Float(Math.min(shapeStart.x,shapeEnd.x), Math.min(shapeStart.y, shapeEnd.y), Math.abs(shapeStart.x - shapeEnd.x), Math.abs(shapeStart.y - shapeEnd.y));
                graphics2D.draw(guideLine);


            }else if(TextEditor.paintWindow.selectedTool.equals("ellipse")){
                Ellipse2D.Float guideLine = new Ellipse2D.Float(Math.min(shapeStart.x,shapeEnd.x), Math.min(shapeStart.y, shapeEnd.y), Math.abs(shapeStart.x - shapeEnd.x), Math.abs(shapeStart.y - shapeEnd.y));
                graphics2D.draw(guideLine);

            }else if(TextEditor.paintWindow.selectedTool.equals("erase")){
                //TODO: Develop this
                /*
                Ellipse2D.Float guideEraser = new Ellipse2D.Float(shapeStart.x - eraserWidth/2, shapeStart.y - eraserHeight/2, eraserWidth, eraserHeight);
                ShapeItem guideEraserShapeItem = new ShapeItem(guideEraser, Color.black);
                shapeItems.add(guideEraserShapeItem);
                whiteEraserCircleShapeItems.add(guideEraserShapeItem);
                */
            }
        }
    }

    /**
     * Creates a rectangle shape and adds to list
     */
    public void createRect(int startX, int startY, int endX, int endY, boolean hasFill, Color color){
        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int width = Math.abs(endX - startX);
        int height = Math.abs(endY - startY);
        Rectangle2D.Float newRect = new Rectangle2D.Float(x, y, width, height);
        Point firstPoint = new Point(startX, startY);
        Point secondPoint = new Point(endX, endY);
        ShapeItem shapeItem = new ShapeItem(newRect, color, hasFill, firstPoint, secondPoint, "Rectangle");
        shapeItems.add(shapeItem);
    }

    /**
     * Creates a line shape and adds to list
     */
    public void createLine(int startX, int startY, int endX, int endY, Color color){
        Line2D.Float newLine = new Line2D.Float(startX, startY, endX, endY);
        Point firstPoint = new Point(startX, startY);
        Point secondPoint = new Point(endX, endY);
        ShapeItem shapeItem = new ShapeItem(newLine, color, false, firstPoint, secondPoint, "Line"); //Line has no fill by default
        shapeItems.add(shapeItem);
        lineShapes.add(shapeItem);
     }

    /**
     * Creates an oval shape and adds to list
     */
    public void createOval(int startX, int startY, int endX, int endY, boolean hasFill, Color color){
        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int width = Math.abs(endX - startX);
        int height = Math.abs(endY - startY);
        Ellipse2D.Float newOval = new Ellipse2D.Float(x, y, width, height);
        Point firstPoint = new Point(startX, startY);
        Point secondPoint = new Point(endX, endY);
        ShapeItem shapeItem = new ShapeItem(newOval, color, hasFill, firstPoint, secondPoint, "Oval");
        shapeItems.add(shapeItem);
    }

    /**
     * Creates a white circle to emulate erasing
     */
    /*
    public void createEraseCircle(){
        float x = shapeStart.x - eraserWidth/2;
        float y = shapeStart.y - eraserHeight/2;
        Ellipse2D.Float whiteEraseShape = new Ellipse2D.Float(x, y, eraserWidth, eraserHeight);
        ShapeItem shapeItem = new ShapeItem(whiteEraseShape, Color.white, true);
        shapeItems.add(shapeItem);
        whiteEraserCircleShapeItems.add(shapeItem);
    }
    */

    public void mouseClicked(MouseEvent mouseEvent){}

    /**
     * Starts a new start point for the shape
     */
    public void mousePressed(MouseEvent mouseEvent){
        shapeStart = new Point(mouseEvent.getX(), mouseEvent.getY());
        shapeEnd = shapeStart;
        repaint();
    }

    /**
     * When the mouse is released, that is when the shape is added to the shapes list for drawing
     */
    public void mouseReleased(MouseEvent mouseEvent){
        if(TextEditor.paintWindow.selectedTool.equals("line")) createLine(shapeStart.x, shapeStart.y, shapeEnd.x, shapeEnd.y, TextEditor.paintWindow.selectedColor);
        else if(TextEditor.paintWindow.selectedTool.equals("rectangle")) createRect(shapeStart.x, shapeStart.y, shapeEnd.x, shapeEnd.y, TextEditor.paintWindow.filling, TextEditor.paintWindow.selectedColor);
        else if(TextEditor.paintWindow.selectedTool.equals("ellipse")) createOval(shapeStart.x, shapeStart.y, shapeEnd.x, shapeEnd.y, TextEditor.paintWindow.filling, TextEditor.paintWindow.selectedColor);
        repaint();
    }

    public void mouseEntered(MouseEvent mouseEvent){}
    public void mouseExited(MouseEvent mouseEvent){}

    /*Setting the end point of the shape being drawn*/
    public void mouseDragged(MouseEvent mouseEvent){
        shapeEnd = new Point(mouseEvent.getX(), mouseEvent.getY());
        repaint();
    }

    public void mouseMoved(MouseEvent mouseEvent){
    }

    public void createNew(){}

    /**
     * Saves all shapes to a file
     */
    public void save(){
       try{

           /*File object to write to and PrintStream object to write to the file*/
           File saveToFile = new File("paint.txt");
           PrintStream printStreamWriter = new PrintStream(saveToFile);
           //TODO: Make it so that the save as dialog appears and allows the user to name their file

           /*Looping through shapeItems to write each shape's info to the file*/
           for(ShapeItem shapeItem : shapeItems){
               String shapeType = shapeItem.getShapeType();
               double x1 = shapeItem.getPointOne().getX();
               double y1 = shapeItem.getPointOne().getY();
               double x2 = shapeItem.getPointTwo().getX();
               double y2 = shapeItem.getPointTwo().getY();
               Color shapeColor = shapeItem.getShapeColor();
               int red = shapeColor.getRed();
               int blue = shapeColor.getBlue();
               int green = shapeColor.getGreen();
               boolean hasFill = shapeItem.isFilled();
               printStreamWriter.println(shapeType + " " + red + " " + green + " " + blue + " " + hasFill + " " + x1 + " " + y1 + " " + x2 + " " + y2);
           }
       } catch(Exception e){e.printStackTrace();}
    }

    public void clear(){
        shapeItems.clear();
        Graphics g = getGraphics();
        super.paintComponent(g); //Clears the JPanel
    }

    /**
     * Open's a file with shape info
     */
    //TODO: Make it check if the file is a valid shapes file or not (maybe write to the file that the file is indeed a shapes file and check if the apppears here?)
    public void open(){
        clear();
        try{

            /*Prompting the user to open a file using a JFileChooser object*/
            JFileChooser openFileChooser = new JFileChooser();
            int status = openFileChooser.showOpenDialog(null);
            if(status != JFileChooser.APPROVE_OPTION){
                JOptionPane.showMessageDialog(null, "No File Selected!");
                return;
            }
            File selectedFile = openFileChooser.getSelectedFile();
            Scanner scanFile = new Scanner(selectedFile);

            /*Scanning the file to get the shapes to display*/
            while(scanFile.hasNext()){

                /*Type of shape*/
                String shapeType = scanFile.next();

                /*RGB values of the shape color*/
                int red = scanFile.nextInt();
                int green = scanFile.nextInt();
                int blue = scanFile.nextInt();

                /*Boolean that dictates if the shape is filled or not*/
                boolean shapeIsFilled = scanFile.nextBoolean();

                /*X and Y values of the shape*/
                int x1 = (int)scanFile.nextDouble();
                int y1 = (int)scanFile.nextDouble();
                int x2 = (int)scanFile.nextDouble();
                int y2 = (int)scanFile.nextDouble();

                /*Creating the color of the shape*/
                Color shapeColor = new Color(red, green, blue);

                /*Creating the shape based on the type of shape*/
                if(shapeType.equals("Line")) createLine(x1, y1, x2, y2, shapeColor);
                else if(shapeType.equals("Oval")) createOval(x1, y1, x2, y2, shapeIsFilled, shapeColor);
                else if(shapeType.equals("Rectangle")) createRect(x1, y1, x2, y2, shapeIsFilled, shapeColor);
                repaint();

            }

        }catch(Exception e){e.printStackTrace();}

    }
}
