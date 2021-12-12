package Simulator;

import api.*;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Line2D;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

public class Canvas extends JComponent
{
    private Line2D line = new Line2D.Double();
    private Polygon tri = new Polygon();
    private HashMap<Integer, JLabel> nodes;
    private HashMap<Integer, HashMap<Integer, Double>> edges;
    private static int LEN = GraphZone.LEN;
    private DirectedWeightedGraphAlgorithms _algorithm;
    private GeoLocation _ratioAxis;
    private GeoLocation _startPoint;
    private static final double arwHead = 6.0;
    private static final double radiusCut = Math.sqrt(Math.pow(LEN/2.0, 2) - Math.pow(arwHead, 2));
    private HashSet<String> specialEdges;


    public Canvas(int x, int y, int w, int h, DirectedWeightedGraphAlgorithms algorithm)
    {
        setBounds(x + LEN / 2, y + LEN / 2, w - LEN, h - LEN);
        System.out.println();
        nodes = new HashMap<>();
        edges = new HashMap<>();
        _algorithm = algorithm;
        setRatioPoins();
        specialEdges = new HashSet<>();
    }

    public void setPath(List<NodeData> nodes)
    {
        specialEdges.clear();
        for (int i = 0; i < nodes.size() - 1; i++)
        {
            specialEdges.add(nodes.get(i) + "," + nodes.get(i + 1));
        }
        repaint();
        //specialEdges.clear();
    }

    public void removePath()
    {
        specialEdges.clear();
    }

    public GeoLocation get_startPoint()
    {
        return new GeoLocationImp(_startPoint);
    }

    private GeoLocation[] getRangeNodes()
    {
        double[][] range = new double[][] {{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE},
                {Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE}};

        Iterator<NodeData> itNode = _algorithm.getGraph().nodeIter();
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
        double dx = (minMaxPoints[1].x() - minMaxPoints[0].x()) / (getWidth() - 2*LEN);
        double dy = (minMaxPoints[1].y() - minMaxPoints[0].y()) / (getHeight() - 2 *LEN);
        _ratioAxis = new GeoLocationImp(dx, dy, 0);
        _startPoint = minMaxPoints[0];
    }

    public Point getPointFromGeo(GeoLocation location)
    {
        double x = location.x(), y = location.y();
        Point p = new Point((int)((x - _startPoint.x()) / _ratioAxis.x()), (int)((y-_startPoint.y()) / _ratioAxis.y()));
        p.setLocation(p.x, getHeight() - p.y - 2*LEN);
        return p;
    }
    public GeoLocation getGeoFromPoint(Point p)
    {
        double x = p.x * _ratioAxis.x() + _startPoint.x();
        double y = (getHeight() - p.y - 2*LEN) * _ratioAxis.y() + _startPoint.y();

        return new GeoLocationImp(x, y, 0);
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

    public void setArrow(JLabel node1, JLabel node2, double w)
    {
        try
        {
            int src = Integer.parseInt(node1.getName());
            int dest = Integer.parseInt(node2.getName());
            nodes.put(src, node1);
            nodes.put(dest, node2);
            if (!edges.containsKey(src))
            {
                edges.put(src, new HashMap<>());
            }
            edges.get(src).put(dest, w);
            //repaint();
            //printArrow(src, dest);
        }
        catch (Exception ex)
        {

        }

    }

    public Point getNearRadPoint(Point point, double beta, double dist)
    {
        Point p = new Point(point);
        p.translate((int)(Math.cos(beta)*dist), (int)(Math.sin(beta)*dist));
        return p;
    }

    public void printDoubeEdge(int src, int dest, Graphics2D canvas)
    {
        JLabel node1 = nodes.get(src);
        JLabel node2 = nodes.get(dest);
        Point center1 = node1.getLocation();
        int r = LEN / 2;
        center1.translate(r, r);
        Point center2 = node2.getLocation();
        center2.translate(r, r);

        Point original1 = new Point(center1);
        Point original2 = new Point(center2);

        double angle = Math.atan2(center2.y - center1.y, center2.x - center1.x);
        double beta = angle + Math.toRadians(90);
        center1.translate((int)(Math.cos(beta)*arwHead), (int)(Math.sin(beta)*arwHead));
        center2.translate((int)(Math.cos(beta)*arwHead), (int)(Math.sin(beta)*arwHead));
        canvas.setColor(specialEdges.contains(src + "," + dest) ? Color.BLUE : Color.BLACK);
        if (specialEdges.isEmpty() || (!specialEdges.isEmpty() && canvas.getColor() == Color.BLUE))
        {
            setPartsOfArrow(canvas, radiusCut, center1, center2, edges.get(src).get(dest));
            drawString(canvas, center1, center2, false, edges.get(src).get(dest));
        }


        beta = angle - Math.toRadians(90);
        center1 = new Point(original1);
        center2 = new Point(original2);
        center1.translate((int)(Math.cos(beta)*arwHead), (int)(Math.sin(beta)*arwHead));
        center2.translate((int)(Math.cos(beta)*arwHead), (int)(Math.sin(beta)*arwHead));
        canvas.setColor(specialEdges.contains(dest + "," + src) ? Color.BLUE : Color.BLACK);

        if (specialEdges.isEmpty() || (!specialEdges.isEmpty() && canvas.getColor() == Color.BLUE))
        {
            setPartsOfArrow(canvas, radiusCut, center2, center1, edges.get(dest).get(src));
            drawString(canvas, center1, center2, true, edges.get(dest).get(src));
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

        canvas.setColor(specialEdges.contains(src + "," + dest) ? Color.BLUE : Color.BLACK);
        if (specialEdges.isEmpty() || (!specialEdges.isEmpty() && canvas.getColor() == Color.BLUE))
        {
            setPartsOfArrow(canvas, r, center1, center2, edges.get(src).get(dest));
            drawString(canvas, center1, center2, true, edges.get(src).get(dest));
        }

    }

    public void drawString(Graphics2D canvas, Point center1, Point center2, boolean isUp, double w)
    {
        String num = (float) Math.round(w * 100) / 100 + "";
        AttributedString attr = new AttributedString(num);
        attr.addAttribute(TextAttribute.BACKGROUND, Color.white);
        attr.addAttribute(TextAttribute.SIZE, 8);
        //canvas.setFont(new Font("TimesRoman", Font.BOLD, 5));
        double alpha = Math.atan2(center2.y - center1.y, center2.x - center1.x);
        alpha += (isUp ? Math.toDegrees(90) : Math.toDegrees(-90));
        Point p1 = getNearRadPoint(center1, alpha, arwHead);
        Point p2 = getNearRadPoint(center2, alpha, arwHead);
        canvas.drawString(attr.getIterator() , (p1.x + p2.x - num.length() * 4)/2, (p1.y + p2.y)/2);
    }

    public void setPartsOfArrow(Graphics2D canvas, double r, Point center1, Point center2, double w)
    {
        double dist = center1.distance(center2);
        double angle = Math.atan2(center2.y - center1.y, center2.x - center1.x);

        Point p4 = new Point(center1);
        p4.translate((int)((dist-r)*Math.cos(angle)), (int)((dist-r)*Math.sin(angle)));

        dist = center1.distance(p4) - 10;
        double deg = Math.abs(Math.toDegrees(Math.asin(arwHead/dist)));
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

    }



    public boolean updateLocation(int nodeId, Point p)
    {
        try
        {
            _algorithm.getGraph().getNode(nodeId).setLocation(getGeoFromPoint(p));
        }
        catch (Exception ex)
        {
            return false;
        }
        return true;
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        // a 2D graph
        Graphics2D canvas = (Graphics2D) g;
        canvas.setColor(Color.BLACK);
        canvas.setStroke(new BasicStroke(2));
        //set the border
        HashSet<String> edgesStr = new HashSet<>();
        for (int src: edges.keySet())
        {
            for (int dest: edges.get(src).keySet())
            {
                String currStr = src + "," + dest;
                if (edges.containsKey(dest) && edges.get(dest).containsKey(src))
                {
                    // complete double check
                    if (!edgesStr.contains(currStr))
                    {
                        printDoubeEdge(src, dest, canvas);
                        edgesStr.add(dest + "," + src);
                    }
                }
                else
                {
                    printArrow(src, dest, canvas);
                }

                edgesStr.add(currStr);

            }
        }
        removePath();

    }
}
