package imps;

import api.*;

public class Node implements NodeData
{
    private int key;
    private GeoLocation location;
    private int tag;
    double _w;
    String _info;
    public static final int WHITE = 0;      // color white for tagging
    public static final int GRAY = 1;       // color Gray for tagging
    public static final int BLACK = 2;      // color Black for tagging

    /**
     * c'tor by key of node and position
     * @param key
     * @param x
     * @param y
     * @param z
     */
    public Node(int key, double x, double y, double z)
    {
       this.key=key;
       this.location = new GeoLocationImp( x,y,z );
       tag = WHITE;
    }

    /**
     * c'tor by position in string and key
     * @param key
     * @param pos
     */
    public Node(int key, String pos)
    {
        this.key=key;
        this.location= new GeoLocationImp(pos);
        tag = WHITE;
    }

    public Node(int key)        // C'tor overloading
    {
        this(key, 0, 0, 0);
    }

    /**
     * copy c'tor
     * @param other
     */
    public Node(NodeData other)
    {
        this.key = other.getKey();
        this.location = new GeoLocationImp(other.getLocation());
        this.tag = other.getTag();
    }

    // Getters + Setters

    @Override
    public int getKey()
    {
        return this.key;
    }

    @Override
    public GeoLocation getLocation()
    {
        return location;
    }

    @Override
    public void setLocation(GeoLocation p)
    {
        location = p;
    }

    @Override
    public double getWeight()
    {
        return _w;
    }

    @Override
    public void setWeight(double w)
    {
        _w = w;
    }

    @Override
    public String getInfo() {
        return _info;
    }

    @Override
    public void setInfo(String s)
    {
        _info = s;
    }

    @Override
    public int getTag()
    {
        return tag;
    }

    @Override
    public void setTag(int t)
    {
        this.tag = t;
    }

    @Override
    public String toString()
    {
        return "" + key;
    }
}
