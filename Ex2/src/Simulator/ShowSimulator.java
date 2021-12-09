package Simulator;

import api.*;

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
import java.util.HashSet;
import java.util.Iterator;

public class ShowSimulator extends JFrame
{
    public static final int LEN = 35;
    private JFrame frame;
    private JPanel panel;
    private JButton button1;
    private ImageIcon circle;
    private HashMap<Integer, JLabel> shpeNode;
    private HashMap<Integer, JLabel> labelNode;
    private Canvas canvas;
    private DirectedWeightedGraphAlgorithms _algorithm;
    private DirectedWeightedGraph _graph;

    public ShowSimulator(DirectedWeightedGraphAlgorithms algorithm)
    {
        frame = new JFrame("HELLO");
        circle = new ImageIcon("pics/circleS.png");
        shpeNode = new HashMap<>();
        labelNode = new HashMap<>();
        panel = new JPanel();
        _algorithm = algorithm;
        _graph = algorithm.copy();

        frame.setExtendedState(MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //canvas = new Canvas(frame, _graph);

        button1 = new JButton();
        button1.setText("Click");
        button1.setFont(new Font(button1.getFont().getFontName(), button1.getFont().getStyle(), 12));
        button1.setFocusPainted(false);

        //makeVer(1, 200, 400, LEN);
        //makeVer(2, 400, 100, LEN);
        //makeVer(3, 0, 0, LEN);

        //setArrow(shpeNode.get(1), shpeNode.get(2));
        //canvas.setArrow(shpeNode.get(1), shpeNode.get(2));
        paintAllNodesEdges();
        canvas.setVisible(true);
        canvas.repaint();
        //button1.setRota
        panel.setLayout(null);
        MouseAdapterLabel.canMove = true;

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

    public void paintAllNodesEdges()
    {
        this.shpeNode.clear();
        this.labelNode.clear();
        Iterator<NodeData> itNode = _graph.nodeIter();
        while (itNode.hasNext())
        {
            makeVer(itNode.next(), LEN);
        }
        Iterator<EdgeData> itEdge = _graph.edgeIter();

        while (itEdge.hasNext())
        {
            EdgeData edge = itEdge.next();
            canvas.setArrow(shpeNode.get(edge.getSrc()), shpeNode.get(edge.getDest()), edge.getWeight());
        }
        canvas.repaint();
    }
    public void makeVer(NodeData node, int len)
    {
        Point p = canvas.getPointFromGeo(node.getLocation());
        makeVer(node.getKey(), p.x, p.y, len);
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


        shape.setName(numStr);
        lbOnShape.setName(numStr);

        this.shpeNode.put(num, shape);
        this.labelNode.put(num, lbOnShape);
        canvas.add(lbOnShape);
        canvas.add(shape);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        DirectedWeightedGraphAlgorithms algorithm = new GraphAlgorithms();

        if (algorithm.load("data/G1.json"))
        {
            System.out.println(algorithm.shortestPathDist(1, 5));
            ShowSimulator dialog = new ShowSimulator(algorithm);
            dialog.canvas.setPath(algorithm.shortestPath(1, 5));
            //
        }

        //System.exit(0);
    }

}