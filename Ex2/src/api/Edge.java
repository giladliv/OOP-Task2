package api;

import java.util.Comparator;

public class Edge implements EdgeData, Comparable<Edge>
{
    private int src;
    private int dest;
    private double weight;
    private int tag =0;

    public Edge(int src, int dest, double weight)
    {
    this.src = src;
    this.dest = dest;
    this.weight = weight;
    }

    @Override
    public int getSrc()
    {
        return this.src;
    }

    @Override
    public int getDest()
    {
        return this.dest;
    }

    @Override
    public double getWeight()
    {
        return this.weight;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void setInfo(String s) {

    }

    @Override
    public int getTag() {
        return 0;
    }

    @Override
    public void setTag(int t)
    {
    this.tag = t;
    }

    @Override
    public int compareTo(Edge o)
    {
        if (this.src != o.src)
            return Integer.compare(this.src, o.src);
        return Integer.compare(this.dest, o.dest);
    }

    @Override
    public String toString() {
        return src + " --> " + dest + " : " + weight;
    }
}
