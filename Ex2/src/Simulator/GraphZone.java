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
    //sets the circle shape
    private final ImageIcon circle = new ImageIcon(this.getClass().getResource("/Simulator/circleS.png"));
    private HashMap<Integer, JLabel> shpeNode;      // images of circles
    private HashMap<Integer, JLabel> labelNode;     // text of nodes
    private Canvas canvas;
    private DirectedWeightedGraphAlgorithms _algorithm;
    private DirectedWeightedGraph _graph;
    private int _x, _y, _w, _h;


    public GraphZone(DirectedWeightedGraphAlgorithms algorithm, int x, int y, int w, int h, JPanel panel)
    {
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

        paintAllNodesEdges();       // reset all the arrows and nodes
        canvas.setVisible(true);
        canvas.repaint();
        MouseAdapterLabel.canMove = false;  // the nodes cannot be moved at first
    }

    public void paintAllNodesEdges()
    {
        this.shpeNode.clear();
        this.labelNode.clear();
        forget();               // forget the old canvas
        canvas = new Canvas(_x, _y, _w, _h, _algorithm);    // make new one
        panel.add(canvas);

        Iterator<NodeData> itNode = _algorithm.getGraph().nodeIter();
        while (itNode.hasNext())
        {
            makeVer(itNode.next(), LEN);        // show all the nodes in the canvas
        }
        Iterator<EdgeData> itEdge = _algorithm.getGraph().edgeIter();

        while (itEdge.hasNext())
        {
            EdgeData edge = itEdge.next();
            // set each edge as arrow
            canvas.setArrow(shpeNode.get(edge.getSrc()), shpeNode.get(edge.getDest()), edge.getWeight());
        }
        // paint the settings
        canvas.repaint();
    }

    /**
     * by given 2 nodes id, (src,dest) we want to show the shortest path between those two
     * @param src
     * @param dest
     * @return
     */
    public double performShortestPath(int src, int dest)
    {
        List<NodeData> nodes = _algorithm.shortestPath(src, dest); // get the list of nodes that sets the shortest path
        canvas.setPath(nodes);

        for (int key: shpeNode.keySet()) // make all unvisible
        {
            shpeNode.get(key).setVisible(false);
            labelNode.get(key).setVisible(false);
        }
        for (NodeData node: nodes)  // show only noed that in the path
        {
            shpeNode.get(node.getKey()).setVisible(true);
            labelNode.get(node.getKey()).setVisible(true);
        }
        return _algorithm.shortestPathDist(src, dest);  // return the weight of the shortest path
    }

    /**
     * by giving list of noded present the shortest path that reaches all the nodes inside
     */
    public void performTSP()
    {
        List<NodeData> cities = new ArrayList<>();
        for (Component cmp: MouseAdapterLabel.nodesPicked)      // set all the picked nodes to a list
        {
            int nodeId = Integer.parseInt(cmp.getName());
            cities.add(_algorithm.getGraph().getNode(nodeId));
        }
        cities = _algorithm.tsp(cities);    // get the path by the tsp algo
        canvas.setPath(cities);

        for (int key: shpeNode.keySet()) // make all unvisible
        {
            shpeNode.get(key).setVisible(false);
            labelNode.get(key).setVisible(false);
        }
        for (NodeData node: cities)  // show only noed that in the path
        {
            shpeNode.get(node.getKey()).setVisible(true);
            labelNode.get(node.getKey()).setVisible(true);
        }
        
    }

    /**
     * make the center more visible, by coloring its border
     */
    public void perfornCenter()
    {
        NodeData node = _algorithm.center();
        JLabel circleShape = shpeNode.get(node.getKey());
        JLabel circleLabel = labelNode.get(node.getKey());
        circleShape.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
        circleLabel.setForeground(Color.BLUE);

    }

    /**
     * remove the marking from the center
     */
    public void disableCenter()
    {
        NodeData node = _algorithm.center();
        JLabel circleShape = shpeNode.get(node.getKey());
        JLabel circleLabel = labelNode.get(node.getKey());
        circleShape.setBorder(BorderFactory.createEmptyBorder());
        circleLabel.setForeground(Color.BLACK);

    }

    /**
     * drop the canvas
     */
    public void forget()
    {
        panel.remove(canvas);
    }

    /**
     * create a visible node with text for ant new node
     * @param node
     * @param len width/height of the shape
     */
    public void makeVer(NodeData node, int len)
    {
        Point p = canvas.getPointFromGeo(node.getLocation());
        makeVer(node.getKey(), p.x, p.y, len);
    }

    /**
     * create a visible node with text for ant new node
     * @param num node id
     * @param x location x
     * @param y location y
     * @param len width of shape
     */
    public void makeVer(int num, int x, int y, int len)
    {
        String numStr = "" + num;
        // resize the circle
        ImageIcon partOf = new ImageIcon(circle.getImage().getScaledInstance(LEN, LEN, Image.SCALE_SMOOTH));

        //create the circle
        JLabel shape = new JLabel(partOf);
        shape.setBounds(x, y, len, len);

        //create the text of the node (node id)
        JLabel lbOnShape = new JLabel(numStr, SwingConstants.CENTER);
        lbOnShape.setBounds(x, y, len, len);

        // set the mouse actions for each node - works like button
        MouseAdapterLabel mouseAdapterLabel = new MouseAdapterLabel(shape, lbOnShape, canvas);
        shape.addMouseListener(mouseAdapterLabel);
        shape.addMouseMotionListener(mouseAdapterLabel);

        // more easy to handle with names
        shape.setName(numStr);
        lbOnShape.setName(numStr);

        //set to the DS and to the visual component
        this.shpeNode.put(num, shape);
        this.labelNode.put(num, lbOnShape);
        canvas.add(lbOnShape);
        canvas.add(shape);
    }

    /**
     * gets the minimal point in axis
     * @return
     */
    public GeoLocation getStartPoint()
    {
        return canvas.get_startPoint();
    }


}


/**
 * a class that reacts to mouse operations
 */
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

    /**
     * c'tor
     * @param cmp the shape component
     * @param second the label (text) component
     * @param canvas
     */
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

    /**
     * reset all the nodes that clicked
     */
    public static void resetPicked()
    {
        for (int i = 0; i < nodesPicked.size(); i++) {
            nodesPicked.get(i).setForeground(Color.BLACK);
        }
        nodesPicked.clear();
    }

    /**
     * when mouse pressed sets to picked list or make the mode
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e)
    {
        if (needToPick > 0) // if List selected nodes is not empty
        {
            if (nodesPicked.size() == needToPick && !_isPressed)    // if over the limit num and picked
                resetPicked();  // reset all

            if (_isPressed)
            {
                _scndCmp.setForeground(Color.BLACK);    // set the text to be black again if pressed already
                nodesPicked.remove(_scndCmp);
            }
            else
            {
                nodesPicked.add(_scndCmp);
                _scndCmp.setForeground(Color.BLUE);    // set the color of the text to be blue if yet to be picked
            }

            _isPressed = !_isPressed;
        }
        if (!canMove)   // if can be moved then set to be moved
        {
            inDrag = false;
            return;
        }
        Point p = MouseInfo.getPointerInfo().getLocation(); // set the start point
        startX = p.x - _cmp.getLocation().x;
        startY = p.y - _cmp.getLocation().y;
        inDrag = true;
    }

    /**
     * when mouse is no longen pressed
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        inDrag = false;
        if (canMove)
        {
            try
            {
                int src = Integer.parseInt(_cmp.getName());
                _canvas.updateLocation(src, _cmp.getLocation());  // after releasing save the curr location
            }
            catch (Exception ex)
            {

            }
        }
    }


    /**
     * if the mouse has been draged then update locaiton
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (!canMove)
        {
            inDrag = false;
            return;
        }

        Point p = MouseInfo.getPointerInfo().getLocation(); // sets the new location
        p.translate(-startX, -startY);
        if (inDrag)
        {
            _cmp.setLocation(p);
            _scndCmp.setLocation(p);
            _canvas.repaint();
        }
    }
}

