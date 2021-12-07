package Simulator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ShowSimulator extends JFrame {

    private JFrame frame;
    private JPanel panel;
    private JButton button1;
    private ImageIcon circle;
    private HashMap<Integer, JLabel> shpeNode;
    private HashMap<Integer, JLabel> labelNode;

    private final int LEN = 50;

    public ShowSimulator()
    {
        frame = new JFrame("HELLO");
        circle = new ImageIcon("pics/circleS.png");
        shpeNode = new HashMap<>();
        labelNode = new HashMap<>();
        //String imagePath = "pics/circle.png";
        //BufferedImage myPicture = ImageIO.read(new File(imagePath));

        panel = new JPanel();
        button1 = new JButton();
        button1.setText("Click");
        button1.setFont(new Font(button1.getFont().getFontName(), button1.getFont().getStyle(), 12));
        button1.setFocusPainted(false);

        makeVer(1, 200, 400, LEN);
        makeVer(2, 400, 100, LEN);

        setArrow(shpeNode.get(1), shpeNode.get(2));

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
        //panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setExtendedState(MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    public void setArrow(JLabel node1, JLabel node2)
    {
        Point center1 = node1.getLocation();
        int r = LEN / 2;
        center1.translate(node1.getWidth()/2, node1.getHeight()/2);
        Point center2 = node2.getLocation();
        center2.translate(node2.getWidth()/2, node2.getHeight()/2);

        double dist = center1.distance(center2);

        Point p3 = new Point(center1);
        p3.translate((int)((r/dist)*(center2.x - center1.x)), (int)((r/dist)*(center2.y - center1.y)));
        Point p4 = new Point(center1);
        p4.translate((int)((1 - r/dist)*(center2.x - center1.x)), (int)((1 - r/dist)*(center2.y - center1.y)));

        int x = Math.min(p3.x, p4.x);
        int y = Math.min(p3.y, p4.y);
        int width = Math.abs(p3.x - p4.x);
        int height = Math.abs(p3.y - p4.y);

        ImageIcon arrow = new ImageIcon("pics/RightUp.png");

        ImageIcon partOf = new ImageIcon(arrow.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        JLabel shape = new JLabel(partOf);
        shape.setBounds(x, y, width, height);
        panel.add(shape);
    }

    public void makeVer(int num, int x, int y, int len)
    {
        String numStr = "" + num;
        ImageIcon partOf = new ImageIcon(circle.getImage().getScaledInstance(LEN, LEN, Image.SCALE_SMOOTH));

        JLabel shape = new JLabel(partOf);
        shape.setBounds(x, y, len, len);

        JLabel lbOnShape = new JLabel(numStr, SwingConstants.CENTER);
        lbOnShape.setBounds(x, y, len, len);

        MouseAdapterLabel mouseAdapterLabel = new MouseAdapterLabel(shape, lbOnShape);
        shape.addMouseListener(mouseAdapterLabel);
        shape.addMouseMotionListener(mouseAdapterLabel);

        shape.setName(numStr);
        lbOnShape.setName(numStr);

        this.shpeNode.put(num, shape);
        this.labelNode.put(num, lbOnShape);
        panel.add(lbOnShape);
        panel.add(shape);
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
    private static int startX = -1, startY = -1;

    public MouseAdapterLabel(Component cmp, Component second)
    {
        _cmp = cmp;
        _scndCmp = second;
    }

    @Override
    public void mousePressed(MouseEvent e) {
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
    public void mouseDragged(MouseEvent e) {
        Point p = MouseInfo.getPointerInfo().getLocation();
        p.translate(-startX, -startY);
        //System.out.println("mouse drag to " + p);
        if (inDrag)
        {
            _cmp.setLocation(p);
            _scndCmp.setLocation(p);
        }
    }
}

