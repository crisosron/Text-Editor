package paint;

import java.awt.*;

/**
 * This class makes it easier to form relationships between shapes and colors
 */
public class ShapeItem {
    private Shape shape;
    private Color color;
    private boolean hasFill;
    private Point pointOne, pointTwo; //Point objects are used to make it easier to manage x and y values when loading shapes into window from a file, and saving shapes to a file
    private String shapeType;
    public ShapeItem(Shape shape, Color color, boolean hasFill, Point pointOne, Point pointTwo, String shapeType){
        this.shape = shape;
        this.color = color;
        this.hasFill = hasFill;
        this.pointOne = pointOne;
        this.pointTwo = pointTwo;
        this.shapeType = shapeType;
    }
    public Shape getShape(){ return shape;}
    public Color getShapeColor(){return color;}
    public boolean isFilled(){return hasFill;}
    public Point getPointOne(){return pointOne;}
    public Point getPointTwo(){return pointTwo;}
    public String getShapeType(){return shapeType;}

}
