import java.awt.*;

/**
 * This class makes it easier to form relationships between shapes and colors
 */
public class ShapeItem {
    private Shape shape;
    private Color color;
    private boolean hasFill;
    public ShapeItem(Shape shape, Color color, boolean hasFill){
        this.shape = shape;
        this.color = color;
        this.hasFill = hasFill;
    }
    public Shape getShape(){ return shape;}
    public Color getShapeColor(){return color;}
    public boolean isFilled(){return hasFill;}
}
