package edu.cmu.hcii.paint;

import java.awt.*;

public class LinePaint extends PaintObject {

    Point[] points;
    
    public LinePaint() {
    }
    
    public double getStartX() { return points[0].getX(); }
    public double getStartY() { return points[0].getY(); }
    public double getEndX() { return points[points.length - 1].getX(); }
    public double getEndY() { return points[points.length - 1].getY(); }
    
    public void define(Point[] points) {
        this.points = points;
    }
    
    public Rectangle getBoundingBox() {
        int minX = Math.min((int)getStartX(), (int)getEndX()) - thickness / 2;
        int maxX = Math.max((int)getStartX(), (int)getEndX()) + thickness / 2;
        int minY = Math.min((int)getStartY(), (int)getEndY()) - thickness / 2;
        int maxY = Math.max((int)getStartY(), (int)getEndY()) + thickness / 2;
        
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }
    
    public void paint(Graphics2D g) {
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(thickness));
        g.setColor(color);
        
        g.drawLine((int)getStartX(), (int)getStartY(), (int)getEndX(), (int)getEndY());            
        
        g.setStroke(oldStroke);        
    }
}
