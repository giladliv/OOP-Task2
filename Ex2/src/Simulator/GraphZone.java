package Simulator;

import api.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
    private int _x, _y, _w, _h;


    public GraphZone(DirectedWeightedGraphAlgorithms algorithm, int x, int y, int w, int h, JPanel panel)
    {
        circle = new ImageIcon(this.getClass().getResource("/Simulator/circleS.png"));

        shpeNode = new HashMap<>();
        labelNode = new HashMap<>();
        _algorithm = algorithm;
        _graph = algorithm.copy();
        this.panel = panel;

        _x = x;
        _y = y;
        _w = w;
        _h = h;

        canvas = new Canvas(_x, _y, _w, _h, _algorithm);


        this.panel.add(canvas);

        paintAllNodesEdges();
        canvas.setVisible(true);
        canvas.repaint();
        MouseAdapterLabel.canMove = false;
    }

    public void paintAllNodesEdges()
    {
        this.shpeNode.clear();
        this.labelNode.clear();
        forget();
        canvas = new Canvas(_x, _y, _w, _h, _algorithm);
        panel.add(canvas);

        Iterator<NodeData> itNode = _algorithm.getGraph().nodeIter();
        while (itNode.hasNext())
        {
            makeVer(itNode.next(), LEN);
        }
        Iterator<EdgeData> itEdge = _algorithm.getGraph().edgeIter();

        while (itEdge.hasNext())
        {
            EdgeData edge = itEdge.next();
            canvas.setArrow(shpeNode.get(edge.getSrc()), shpeNode.get(edge.getDest()), edge.getWeight());
        }
        canvas.repaint();
    }

    public double performShortestPath(int src, int dest)
    {
        List<NodeData> nodes = _algorithm.shortestPath(src, dest);
        canvas.setPath(nodes);

        for (int key: shpeNode.keySet())
        {
            shpeNode.get(key).setVisible(false);
            labelNode.get(key).setVisible(false);
        }
        for (NodeData node: nodes)
        {
            shpeNode.get(node.getKey()).setVisible(true);
            labelNode.get(node.getKey()).setVisible(true);
        }
        return _algorithm.shortestPathDist(src, dest);
    }

    public void performTSP()
    {
        List<NodeData> cities = new ArrayList<>();
        for (Component cmp: MouseAdapterLabel.nodesPicked)
        {
            int nodeId = Integer.parseInt(cmp.getName());
            cities.add(_algorithm.getGraph().getNode(nodeId));
        }
        cities = _algorithm.tsp(cities);
        canvas.setPath(cities);

        for (int key: shpeNode.keySet())
        {
            shpeNode.get(key).setVisible(false);
            labelNode.get(key).setVisible(false);
        }
        for (NodeData node: cities)
        {
            shpeNode.get(node.getKey()).setVisible(true);
            labelNode.get(node.getKey()).setVisible(true);
        }
        
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

    public GeoLocation getStartPoint()
    {
        return canvas.get_startPoint();
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
    public static int needToPick = 0;
    public static ArrayList<Component> nodesPicked = new ArrayList<>();
    private boolean _isPressed;

    public MouseAdapterLabel(Component cmp, Component second, Canvas canvas)
    {
        _cmp = cmp;
        _scndCmp = second;
        _canvas = canvas;
        _isPressed = false;
    }

    public static void resetAll()
    {
        canMove = false;
        needToPick = 0;
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
            if (nodesPicked.size() == needToPick && !_isPressed)
                resetPicked();

            if (_isPressed)
            {
                _scndCmp.setForeground(Color.BLACK);
                nodesPicked.remove(_scndCmp);
            }
            else
            {
                nodesPicked.add(_scndCmp);
                _scndCmp.setForeground(Color.BLUE);
            }



            _isPressed = !_isPressed;
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
                _canvas.updateLocation(src, _cmp.getLocation());
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

