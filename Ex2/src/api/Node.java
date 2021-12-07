package api;

public class Node implements NodeData
{
    private int key;
    private GeoLocation location;
    private int tag;
    public static final int WHITE = 0;
    public static final int GRAY = 1;
    public static final int BLACK = 2;

    public Node(int key,double x, double y, double z)
    {
       this.key=key;
       this.location= new GeoLocationImp( x,y,z );
       tag = WHITE;
    }

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

    public Node(NodeData other)
    {
        this.key = other.getKey();
        this.location = new GeoLocationImp(other.getLocation());
        this.tag = other.getTag();
    }

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
        return 0;
    }

    @Override
    public void setWeight(double w)
    {

    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void setInfo(String s) {

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
