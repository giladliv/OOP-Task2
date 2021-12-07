package Simulator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RotatingButton {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new RotatingButton().createGUI();
            }
        });
    }

    public void createGUI() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JSlider slider = new JSlider(0, 255, 0);
        frame.add(slider, BorderLayout.PAGE_START);
        final JButton button = new JButton("Button") {

            private static final long serialVersionUID = 1L;

            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                /*
                 * Paint the background
                 */
                g2.setColor(this.getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                /*
                 * Rotate arounf the center
                 */
                g2.rotate(Math.PI * slider.getValue() / 255.0,
                        getWidth() / 2.0, getHeight() / 2.0);
                /*
                 * Compute a scaling
                 */
                double diagonal = Math.sqrt(getWidth() * getWidth()
                        + getHeight() * getHeight());
                double scale = Math.min(getWidth(), getHeight()) / diagonal;
                /*
                 * Apply the scaling from the center
                 */
                g2.translate(getWidth() / 2.0, getHeight() / 2.0);
                g2.scale(scale, scale);
                g2.translate(-getWidth() / 2.0, -getHeight() / 2.0);
                /*
                 * Have the component painted
                 */
                super.paint(g);
            }
        };
        button.setBorder(new TitledBorder("Button"));
        button.setFont(button.getFont().deriveFont(20f));
        button.setPreferredSize(new Dimension(200, 200));
        frame.add(button, BorderLayout.CENTER);
        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent arg0) {
                button.repaint();
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}