import java.awt.*;

/**
 * This class makes it easier to form relationships between shapes and colors
 */
public class ShapeItem {
    private Shape shape;
    private Color color;
    public ShapeItem(Shape shape, Color color){
        this.shape = shape;
        this.color = color;
    }
    public Shape getShape(){ return shape;}
    public Color getShapeColor(){return color;}
}
