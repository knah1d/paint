package edu.cmu.hcii.paint;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PaintCanvas extends JPanel {

    Vector history;
    
    Vector paintObjects;

    private PaintObject temporaryObject;
    private PaintObject hoveringObject;
    
    private int initialWidth;
    private int initialHeight;

    public PaintCanvas(int initialWidth, int initialHeight) {
        
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;
        setPreferredSize(new Dimension(initialWidth, initialHeight));
        
        paintObjects = new Vector();
        
        history = new Vector();
        
    }
    
    @Override
    public Dimension getPreferredSize() {
        int maxX = initialWidth;
        int maxY = initialHeight;
        
        for (Object obj : paintObjects) {
            PaintObject pObj = (PaintObject) obj;
            Rectangle bounds = pObj.getBoundingBox();
            if (bounds.getX() + bounds.getWidth() > maxX) {
                maxX = (int) (bounds.getX() + bounds.getWidth());
            }
            if (bounds.getY() + bounds.getHeight() > maxY) {
                maxY = (int) (bounds.getY() + bounds.getHeight());
            }
        }
        return new Dimension(maxX, maxY);
    }
    
    public void paintComponent(Graphics g) {
        
		((Graphics2D) g).addRenderingHints(
			new java.awt.RenderingHints(
				java.awt.RenderingHints.KEY_ANTIALIASING,
				java.awt.RenderingHints.VALUE_ANTIALIAS_ON));
        
        Rectangle clipBounds = g.getClipBounds();
        g.setColor(Color.white);
        g.fillRect((int)clipBounds.getX(), (int)clipBounds.getY(), 
                    (int)clipBounds.getWidth(), (int)clipBounds.getHeight());
        
        Iterator paintObjectIterator = paintObjects.iterator();
        while(paintObjectIterator.hasNext())
			try {
		        ((PaintObject)paintObjectIterator.next()).paint((Graphics2D)g); 
			} catch(Exception e) { 
				System.err.println("The graphics context isn't a Graphics2D. No anti-aliasing!");
			}
        
        if(temporaryObject != null) temporaryObject.paint((Graphics2D)g);
        
		if(hoveringObject != null) {
			
			Rectangle rect = hoveringObject.getBoundingBox();
			g.setColor(Color.black);
			g.drawRect((int)rect.getX() - 1, (int)rect.getY() - 1, (int)rect.getWidth() + 2, (int)rect.getHeight() + 2);
			hoveringObject.paint((Graphics2D)g);
			
		}
        
    }
    
    public int sizeOfHistory() { return history.size(); }
    
    public void setTemporaryObject(PaintObject temporaryObject) {
        
        this.temporaryObject = temporaryObject;
        repaint();
        
    }
    
    public void setHoveringObject(PaintObject hoveringObject) {
    	
    	this.hoveringObject = hoveringObject;
    	repaint();
    	
    }
    
    public void addPaintObject(PaintObject newObject) {
        
        history.addElement(new Vector(paintObjects));
        paintObjects.addElement(newObject);
        revalidate();
        repaint();
        
    }
    
    public void clear() {
        
        history.addElement(new Vector(paintObjects));
        paintObjects.removeAllElements();
        revalidate();
        repaint();

    }

    public void undo() { 
        
        paintObjects = (Vector)history.lastElement();
        history.removeElement(history.lastElement());
        revalidate();
        repaint();
        
    }


}
