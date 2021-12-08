package Simulator;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.*;

public class Canvas extends JComponent
{
    private Line2D line = new Line2D.Double();
    private Polygon tri = new Polygon();
    private HashMap<Integer, JLabel> nodes;
    private HashMap<Integer, HashSet<Integer>> edges;
    private int LEN = ShowSimulator.LEN;


    public Canvas(JFrame frame)
    {
        setBounds(frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight());
        System.out.println();
        nodes = new HashMap<>();
        edges = new HashMap<>();
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
