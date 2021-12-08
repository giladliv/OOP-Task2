package Simulator;

import api.DirectedWeightedGraph;
import api.GeoLocation;
import api.GeoLocationImp;
import api.NodeData;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.*;

public class Canvas extends JComponent
{
    private Line2D line = new Line2D.Double();
    private Polygon tri = new Polygon();
    private HashMap<Integer, JLabel> nodes;
    private HashMap<Integer, HashSet<Integer>> edges;
    private int LEN = ShowSimulator.LEN;
    private DirectedWeightedGraph _graph;
    private GeoLocation _ratioAxis;
    private GeoLocation _startPoint;


    public Canvas(JFrame frame, DirectedWeightedGraph graph)
    {
        setBounds(frame.getX() + LEN / 2, frame.getY() + LEN / 2, frame.getWidth() - 2*LEN, frame.getHeight() - 2*LEN);
        System.out.println();
        nodes = new HashMap<>();
        edges = new HashMap<>();
        _graph = graph;
        setRatioPoins();
    }

    private GeoLocation[] getRangeNodes()
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

        return new GeoLocation[]{new GeoLocationImp(range[0][0], range[0][1], range[0][2]),
                                    new GeoLocationImp(range[1][0], range[1][1], range[1][2])};
    }

    public void setRatioPoins()
    {
        GeoLocation[] minMaxPoints = getRangeNodes();
        double dx = (minMaxPoints[1].x() - minMaxPoints[0].x()) / getWidth();
        double dy = (minMaxPoints[1].y() - minMaxPoints[0].y()) / getHeight();
        _ratioAxis = new GeoLocationImp(dx, dy, 0);
        _startPoint = minMaxPoints[0];
    }



    public Point getPointFromGeo(GeoLocation location)
    {
        double x = location.x(), y = location.y();
        Point p = new Point((int)((x - _startPoint.x()) / _ratioAxis.x()), (int)((y-_startPoint.y()) / _ratioAxis.y()));
        p.setLocation(p.x - LEN / 2, getHeight() - p.y - LEN/2);
        return p;
    }

    private Line2D.Double getLine(int x1, int y1, int x2, int y2)
    {
        return new Line2D.Double(x1, y1, x2, y2); //line to paint from point to another point
    }

    public void setLine(Point p1, Point p2)
    {
        line = new Line2D.Double(p1, p2);
    }

    public void setLine(Line2D line)
    {
        this.line = line;
        //repaint();
    }

    public void setTri(Point p1, Point p2, Point p3)
    {
        tri = new Polygon();
        tri.addPoint(p1.x, p1.y);
        tri.addPoint(p2.x, p2.y);
        tri.addPoint(p3.x, p3.y);
    }

    public void setArrow(JLabel node1, JLabel node2)
    {
        try
        {
            int src = Integer.parseInt(node1.getName());
            int dest = Integer.parseInt(node2.getName());
            nodes.put(src, node1);
            nodes.put(dest, node2);
            if (!edges.containsKey(src))
            {
                edges.put(src, new HashSet<>());
            }
            edges.get(src).add(dest);
            repaint();
            //printArrow(src, dest);
        }
        catch (Exception ex)
        {

        }

    }

    public void printArrow(int src, int dest, Graphics2D canvas)
    {
        JLabel node1 = nodes.get(src);
        JLabel node2 = nodes.get(dest);
        Point center1 = node1.getLocation();
        int r = LEN / 2;
        center1.translate(r, r);
        Point center2 = node2.getLocation();
        center2.translate(r, r);

        double dist = center1.distance(center2);
        double angle = Math.atan2(center2.y - center1.y, center2.x - center1.x);


        Point p4 = new Point(center1);
        p4.translate((int)((dist-r)*Math.cos(angle)), (int)((dist-r)*Math.sin(angle)));

        dist = center1.distance(p4) - 10;
        double deg = Math.abs(Math.toDegrees(Math.asin(6.0/dist)));
        double alpha = Math.toDegrees(Math.atan2(p4.y - center1.y, p4.x - center1.x));
        double beta = Math.toRadians(alpha + deg);
        Point pTri1 = new Point(center1);
        pTri1.translate((int)(Math.cos(beta)*dist), (int)(Math.sin(beta)*dist));
        beta = Math.toRadians(alpha - deg);
        Point pTri2 = new Point(center1);
        pTri2.translate((int)(Math.cos(beta)*dist), (int)(Math.sin(beta)*dist));

        setLine(center1, center2);
        setTri(p4, pTri1, pTri2);
        canvas.draw(line);
        canvas.fill(tri);

//        Line2D line = new Line2D.Double(0, 0, 25, 25);
//        can.setLine(line);
//        can.repaint();
//        line.setLine(100, 200, 500, 600);
//        can.repaint();

        //panel.add(shape);
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        // a 2D graph
        Graphics2D canvas = (Graphics2D) g;
        canvas.setColor(Color.BLACK);

        //set the border
        for (int src: edges.keySet())
        {
            for (int dest: edges.get(src))
            {
                printArrow(src, dest, canvas);
            }
        }

    }
}
