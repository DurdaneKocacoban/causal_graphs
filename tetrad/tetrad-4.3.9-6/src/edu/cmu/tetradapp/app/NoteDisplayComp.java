package edu.cmu.tetradapp.app;

import edu.cmu.tetradapp.workbench.DisplayNodeUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Provides a modified appearance for session nodes to be used for notes.
 *
 * @author Joseph Ramsey
 */
public class NoteDisplayComp extends JComponent implements SessionDisplayComp {

    /**
     * States whether the component is selected or not.
     */
    private boolean selected = false;


    /**
     * The colors
     */
    private final static Color BORDER_COLOR = new Color(148, 152, 177);
    private final static Color BACKGROUND_COLOR = new Color(255, 255, 219);



    /**
     * The Jlable that contains the name
     */
    private JLabel name = new JLabel("Note");



    /**
     * Constructs the Node display.
     */
    public NoteDisplayComp() {
        buildComponents();
    }

    //================================ Public Methods ==============================//


    /**
     * Paints the component.
     *
     * @param g
     */
    public void paint(Graphics g) {
        int width = getSize().width;
        int height = getSize().height;
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, width, height);

        int y = this.name.getSize().height;
        y += 2;
        g.setColor(BORDER_COLOR);
        while (y < height) {
            g.drawLine(0, y, width, y);
            y += 5;
        }
        // draw the border.
        g.setColor(BORDER_COLOR);
        g.drawRect(0, 0, width - 1, height - 1);

        super.paint(g);
    }


    /**
     * Sets the acronym of the component.
     *
     * @param acronym
     */
    public void setAcronym(String acronym) {

    }

    /**
     * States whether this comp is selected.
     *
     * @return true iff the display is selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the comp as selected.
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Does nothing.
     *
     * @param b
     */
    public void setHasModel(boolean b) {
        // Ignore.
    }


    public void setName(String name) {
        super.setName(name);
        this.name.setText(name);
        //buildComponents();
    }

    //========================== Private methods ===============================//


    private void buildComponents(){
        removeAll();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setFont(DisplayNodeUtils.getFont());

        Box b = Box.createVerticalBox();
        b.add(Box.createVerticalStrut(2));

        Box b2 = Box.createHorizontalBox();
        b2.add(Box.createHorizontalStrut(5));
        b2.add(name);
        b2.add(Box.createHorizontalStrut(5));
        b2.add(Box.createHorizontalGlue());
        b.add(b2);

        b.add(Box.createVerticalStrut(65));

        add(b, BorderLayout.CENTER);
        revalidate();
        repaint();
    }



}
