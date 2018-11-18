import javax.swing.*;
import java.awt.*;

public class FontJListCellRenderer extends JLabel implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
        String valueString = value.toString(); //This is the text that the cell holds
        setText(valueString);
        setOpaque(true); //Allows for selection highlighting

        /*Determining the font the text should be displayed in - The font and style should be the same font and style that the text represents*/
        if(!valueString.contains("Bold") && !valueString.contains("Italic"))setFont(new Font(valueString, Font.PLAIN, 12));
        else{
            if(valueString.contains("Italic") && valueString.contains("Bold")) setFont(new Font(valueString, Font.ITALIC|Font.BOLD, 12));
            else if(valueString.contains("Bold")) setFont(new Font(valueString, Font.BOLD, 12));
            else if(valueString.contains("Italic")) setFont(new Font(valueString, Font.ITALIC, 12));
            else{setFont(new Font(valueString, Font.PLAIN, 12));}
        }

        /*Determining selection colors*/
        if(isSelected){
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }else{
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
