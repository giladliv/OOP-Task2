package Simulator;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.GeoLocation;
import api.NodeData;

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
import java.util.Iterator;

public class ShowSimulator extends JFrame {

    public static final int LEN = 50;
    private JFrame frame;
    private JPanel panel;
    private JButton button1;
    private ImageIcon circle;
    private HashMap<Integer, JLabel> shpeNode;
    private HashMap<Integer, JLabel> labelNode;
    private Canvas canvas;
    private DirectedWeightedGraph _graph;

    public ShowSimulator(DirectedWeightedGraph graph)
    {
        frame = new JFrame("HELLO");
        circle = new ImageIcon("pics/circleS.png");
        shpeNode = new HashMap<>();
        labelNode = new HashMap<>();
        panel = new JPanel();
        _graph = graph;

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
        makeVer(3, 0, 0, LEN);

        //setArrow(shpeNode.get(1), shpeNode.get(2));
        canvas.setArrow(shpeNode.get(1), shpeNode.get(2));
        canvas.setVisible(true);
        canvas.repaint();
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

    private double[][] getRangeNodes()
    {
        double[][] range = new double[][] {{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE},
                {Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE}};

        Iterator<NodeData> itNode = _graph.nodeIter();
        while (itNode.hasNext())
        {
            GeoLocation geo = itNode.next().getLocation();
            range[0][0] = Math.min(range[0][0], geo.x());
            range[0][1] = Math.min(range[0][1], geo.y());
            range[0][2] = Math.min(range[0][2], geo.z());

            range[1][0] = Math.max(range[1][0], geo.x());
            range[1][1] = Math.max(range[1][1], geo.y());
            range[1][2] = Math.max(range[1][2], geo.z());
        }

        return range;
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

