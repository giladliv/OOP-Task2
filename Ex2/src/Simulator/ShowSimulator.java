package Simulator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class ShowSimulator extends JFrame {

    public static final int LEN = 50;
    private JFrame frame;
    private JPanel panel;
    private JButton button1;
    private ImageIcon circle;
    private HashMap<Integer, JLabel> shpeNode;
    private HashMap<Integer, JLabel> labelNode;
    private Canvas canvas;



    public ShowSimulator()
    {
        frame = new JFrame("HELLO");
        circle = new ImageIcon("pics/circleS.png");
        shpeNode = new HashMap<>();
        labelNode = new HashMap<>();
        panel = new JPanel();

        frame.setExtendedState(MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        canvas = new Canvas(frame);

        button1 = new JButton();
        button1.setText("Click");
        button1.setFont(new Font(button1.getFont().getFontName(), button1.getFont().getStyle(), 12));
        button1.setFocusPainted(false);

        makeVer(1, 200, 400, LEN);
        makeVer(2, 400, 100, LEN);

        //setArrow(shpeNode.get(1), shpeNode.get(2));
        canvas.setArrow(shpeNode.get(1), shpeNode.get(2));
        canvas.setVisible(true);
        canvas.repaint();

        button1.setBounds(100, 100, 60, 60);
        //button1.setRota
        panel.setLayout(null);


        //button1.
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println(e.toString());
            }

        });

        panel.add(button1);
        frame.add(panel);
        //panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.pack();
        panel.add(canvas);

        frame.setExtendedState(MAXIMIZED_BOTH);
        frame.repaint();
    }

    public void makeVer(int num, int x, int y, int len)
    {
        String numStr = "" + num;
        ImageIcon partOf = new ImageIcon(circle.getImage().getScaledInstance(LEN, LEN, Image.SCALE_SMOOTH));

        JLabel shape = new JLabel(partOf);
        shape.setBounds(x, y, len, len);
        //shape.setBorder(new LineBorder(Color.RED, 1));

        JLabel lbOnShape = new JLabel(numStr, SwingConstants.CENTER);
        lbOnShape.setBounds(x, y, len, len);

        MouseAdapterLabel mouseAdapterLabel = new MouseAdapterLabel(shape, lbOnShape, canvas);
        shape.addMouseListener(mouseAdapterLabel);
        shape.addMouseMotionListener(mouseAdapterLabel);
        MouseAdapterLabel.canMove = true;

        shape.setName(numStr);
        lbOnShape.setName(numStr);

        this.shpeNode.put(num, shape);
        this.labelNode.put(num, lbOnShape);
        canvas.add(lbOnShape);
        canvas.add(shape);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        ShowSimulator dialog = new ShowSimulator();
        //System.exit(0);
    }


}

class MouseAdapterLabel extends MouseAdapter
{
    private static boolean inDrag = false;
    private Component _cmp;
    private Component _scndCmp;
    private Canvas _canvas;
    private static int startX = -1, startY = -1;
    public static boolean canMove = false;

    public MouseAdapterLabel(Component cmp, Component second, Canvas canvas, boolean isCanMove)
    {
        _cmp = cmp;
        _scndCmp = second;
        _canvas = canvas;
        canMove = isCanMove;

    }

    public MouseAdapterLabel(Component cmp, Component second, Canvas canvas)
    {
        this(cmp, second, canvas, false);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!canMove)
        {
            inDrag = false;
            return;
        }
        Point p = MouseInfo.getPointerInfo().getLocation();
        System.out.println("mousePressed at " + p);
        startX = p.x - _cmp.getLocation().x;
        startY = p.y - _cmp.getLocation().y;
        inDrag = true;
    }

    /** Called when the mouse has been released. */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        inDrag = false;
    }

    // And two methods from MouseMotionListener:
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (!canMove)
        {
            inDrag = false;
            return;
        }

        Point p = MouseInfo.getPointerInfo().getLocation();
        p.translate(-startX, -startY);
        //System.out.println("mouse drag to " + p);
        if (inDrag)
        {
            _cmp.setLocation(p);
            _scndCmp.setLocation(p);
            _canvas.repaint();
        }
    }
}

