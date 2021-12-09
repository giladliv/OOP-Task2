package Simulator;

import api.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static java.awt.Frame.MAXIMIZED_BOTH;

public class GraphZone
{
    public static final int LEN = 35;
    private JPanel panel;
    private ImageIcon circle;
    private HashMap<Integer, JLabel> shpeNode;
    private HashMap<Integer, JLabel> labelNode;
    private Canvas canvas;
    private DirectedWeightedGraphAlgorithms _algorithm;
    private DirectedWeightedGraph _graph;


    public GraphZone(DirectedWeightedGraphAlgorithms algorithm, int x, int y, int w, int h, JPanel panel)
    {
        circle = new ImageIcon("pics/circleS.png");
        shpeNode = new HashMap<>();
        labelNode = new HashMap<>();
        _algorithm = algorithm;
        _graph = algorithm.copy();
        this.panel = panel;

        canvas = new Canvas(x, y, w, h, _graph);

        this.panel.add(canvas);

        paintAllNodesEdges();
        canvas.setVisible(true);
        canvas.repaint();
        MouseAdapterLabel.canMove = false;
    }

    public double performShortestPath(int src, int dest)
    {
        canvas.setPath(_algorithm.shortestPath(src, dest));
        return _algorithm.shortestPathDist(src, dest);
    }

    public void perfornCenter()
    {
        NodeData node = _algorithm.center();
        JLabel circleShape = shpeNode.get(node.getKey());
        JLabel circleLabel = labelNode.get(node.getKey());
        circleShape.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
        circleLabel.setForeground(Color.BLUE);

    }

    public void disableCenter()
    {
        NodeData node = _algorithm.center();
        JLabel circleShape = shpeNode.get(node.getKey());
        JLabel circleLabel = labelNode.get(node.getKey());
        circleShape.setBorder(BorderFactory.createEmptyBorder());
        circleLabel.setForeground(Color.BLACK);

    }

    public void forget()
    {
        panel.remove(canvas);
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


}

class MouseAdapterLabel extends MouseAdapter
{
    private static boolean inDrag = false;
    private Component _cmp;
    private Component _scndCmp;
    private Canvas _canvas;
    private static int startX = -1, startY = -1;
    public static boolean canMove = false;
    public static int needToPick = 2;
    public static boolean isShortest = false;
    public static ArrayList<Component> nodesPicked = new ArrayList<>();

    public MouseAdapterLabel(Component cmp, Component second, Canvas canvas)
    {
        _cmp = cmp;
        _scndCmp = second;
        _canvas = canvas;
    }

    public static void resetAll()
    {
        canMove = false;
        needToPick = 0;
        isShortest = false;
        nodesPicked = new ArrayList<>();
    }

    public static void resetPicked()
    {
        for (int i = 0; i < nodesPicked.size(); i++) {
            nodesPicked.get(i).setForeground(Color.BLACK);
        }
        nodesPicked.clear();
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (needToPick > 0)
        {
            if (nodesPicked.size() == needToPick)
                resetPicked();

            nodesPicked.add(_scndCmp);
            _scndCmp.setForeground(Color.BLUE);

        }
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
        if (canMove)
        {
            try
            {
                int src = Integer.parseInt(_cmp.getName());
                int dest = Integer.parseInt(_scndCmp.getName());
                _canvas.updateLocation(src, _cmp.getLocation());
                _canvas.updateLocation(dest, _scndCmp.getLocation());
                //printArrow(src, dest);
            }
            catch (Exception ex)
            {

            }
        }
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
        if (inDrag)
        {
            _cmp.setLocation(p);
            _scndCmp.setLocation(p);
            _canvas.repaint();
        }
    }
}

