package edu.cmu.hcii.paint;

public class Main {
    public static void main(String[] args) {
        // Run on the Event Dispatch Thread (EDT) as it's a Swing application
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Initialize the PaintWindow with an initial width and height
                new PaintWindow(800, 600);
            }
        });
    }
}
