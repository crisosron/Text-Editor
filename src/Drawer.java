//TODO: Need to make draw rect and draw ellipse work for any pivot point (eg drawing rect from bottom right instead of top left)
import java.awt.*;

public class Drawer {
    private Graphics2D graphics2D;

    public Drawer(Graphics g){
        graphics2D = (Graphics2D)g; //Casting to Graphics2D object so that the more advanced operations Graphics2D has over Graphics can be accessed
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //Improves visual artifacts (jagged edges)
    }

    /**
     * Draws a new line on the canvas using values determined by mouse position
     */
    public void drawNewLine(int startX, int startY, int endX, int endY){
        graphics2D.setColor(TextEditor.paintWindow.selectedColor);
        graphics2D.drawLine(startX, startY, endX, endY);
    }

    /**
     * Draws a new rectangle on the canvas using values determined by mouse position
     */
    public void drawNewRectangle(int startX, int startY, int endX, int endY){
        graphics2D.setColor(TextEditor.paintWindow.selectedColor);
        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int width = Math.abs(endX - startX);
        int height = Math.abs(endY - startY);
        graphics2D.drawRect(x, y, width, height);
    }

    /**
     * Draws a new ellipse on the canvas using values determined by mouse position
     */
    public void drawNewOval(int startX, int startY, int endX, int endY){
        graphics2D.setColor(TextEditor.paintWindow.selectedColor);
        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int width = Math.abs(endX - startX);
        int height = Math.abs(endY - startY);
        graphics2D.drawOval(x, y, width, height);
    }
}
