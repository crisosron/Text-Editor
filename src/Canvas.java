//TODO: Use ShapeItem instead of Shape objects so that the shapes being drawn can be tied to a color
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener {
    private int x, y, width, height;
    private List<Shape> shapes;
    private Point shapeStart, shapeEnd;

    public Canvas(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        shapes = new ArrayList<>();
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
        for(Shape shape : shapes){
            int r = (int)(Math.random()*256);
            int gr = (int)(Math.random()*256);
            int b = (int)(Math.random()*256);
            graphics2D.setColor(new Color(r, gr, b));
            //graphics2D.setColor(TextEditor.paintWindow.selectedColor);
            graphics2D.draw(shape);
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
            }

        }
    }

    /**
     * Creates a rectangle shape and adds to list
     */
    public void createRect(int startX, int startY, int endX, int endY){
        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int width = Math.abs(endX - startX);
        int height = Math.abs(endY - startY);
        Rectangle2D.Float newRect = new Rectangle2D.Float(x, y, width, height);
        shapes.add(newRect);
    }

    /**
     * Creates a line shape and adds to list
     */
    public void createLine(int startX, int startY, int endX, int endY){
        Line2D.Float newLine = new Line2D.Float(startX, startY, endX, endY);
        shapes.add(newLine);
    }

    /**
     * Creates an oval shape and adds to list
     */
    public void createOval(int startX, int startY, int endX, int endY){
        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int width = Math.abs(endX - startX);
        int height = Math.abs(endY - startY);
        Ellipse2D.Float newOval = new Ellipse2D.Float(x, y, width, height);
        shapes.add(newOval);
    }

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
        if(TextEditor.paintWindow.selectedTool.equals("line")) createLine(shapeStart.x, shapeStart.y, shapeEnd.x, shapeEnd.y);
        else if(TextEditor.paintWindow.selectedTool.equals("rectangle")) createRect(shapeStart.x, shapeStart.y, shapeEnd.x, shapeEnd.y);
        else if(TextEditor.paintWindow.selectedTool.equals("ellipse")) createOval(shapeStart.x, shapeStart.y, shapeEnd.x, shapeEnd.y);
        repaint();
    }

    public void mouseEntered(MouseEvent mouseEvent){}
    public void mouseExited(MouseEvent mouseEvent){}

    /*Setting the end point of the shape being drawn*/
    public void mouseDragged(MouseEvent mouseEvent){
        shapeEnd = new Point(mouseEvent.getX(), mouseEvent.getY());
        repaint();
    }
    public void mouseMoved(MouseEvent mouseEvent){}



}
