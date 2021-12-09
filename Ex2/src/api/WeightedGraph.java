package api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class WeightedGraph implements DirectedWeightedGraph
{
    private HashMap<Integer, NodeData> _nodeHash = new HashMap<>();
    private HashMap<Integer,HashMap<Integer,EdgeData>> _edgeHash = new HashMap<>();
    private HashSet<EdgeData> _edgeSet = new HashSet<>();
    private final HashMap<Integer, EdgeData> emptyEdgeMap = new HashMap<>();
    private int _changes;


    public WeightedGraph()
    {
        this._nodeHash = new HashMap<>();
        this._edgeHash = new HashMap<>();
        this._edgeSet = new HashSet<>();
        _changes = 0;

    }

    public WeightedGraph(DirectedWeightedGraph other)
    {
        this();
        Iterator<NodeData> itNode = other.nodeIter();
        while (itNode.hasNext())
        {
            NodeData node = itNode.next();
            this.addNode(new Node(node));
        }

        Iterator<EdgeData> itEdge = other.edgeIter();
        while (itEdge.hasNext())
        {
            EdgeData edge = itEdge.next();
            this.connect(edge.getSrc(), edge.getDest(), edge.getWeight());
        }
    }

    @Override
    public NodeData getNode(int key)
    {
        return _nodeHash.get(key);
    }

    @Override
    public EdgeData getEdge(int src, int dest)
    {
        return _edgeHash.get(src).get(dest);
    }

    @Override
    public void addNode(NodeData n)
    {
        _nodeHash.put(n.getKey(), n);
        _changes++;
    }

    public void addNode(int n)
    {
        _nodeHash.put(n, new Node(n));
        _changes++;
    }

    @Override
    public void connect(int src, int dest, double w)
    {
        EdgeData edge = new Edge(src, dest, w);
        if(!_edgeHash.containsKey(src))
        {
            _edgeHash.put(src, new HashMap<>());
        }
        _edgeHash.get(src).put(dest, edge);
        _edgeSet.add(edge);
        _changes++;
    }

    @Override
    public Iterator<NodeData> nodeIter()
    {
        return _nodeHash.values().iterator();
    }

    @Override
    public Iterator<EdgeData> edgeIter() {
        return _edgeSet.iterator();
    }

    @Override
    public Iterator<EdgeData> edgeIter(int node_id)
    {
        if (_edgeHash.containsKey(node_id))
        {
            return _edgeHash.get(node_id).values().iterator();
        }

        return (emptyEdgeMap.values().iterator());
    }

    @Override
    public NodeData removeNode(int key)
    {
        if (!_nodeHash.containsKey(key))
            return null;

        _edgeHash.remove(key);
        for (int node : _nodeHash.keySet())
        {
            removeEdge(node, key);
        }
        _changes++;
        return _nodeHash.remove(key);
    }

    @Override
    public EdgeData removeEdge(int src, int dest)
    {
        if (!(_edgeHash.containsKey(src)) || !(_edgeHash.get(src).containsKey(dest)))
            return null;

        EdgeData edge = _edgeHash.get(src).remove(dest);

        if (_edgeHash.get(src).isEmpty())
        {
            _edgeHash.remove(src);
        }
        _edgeSet.remove(edge);
        _changes++;
        return edge;
    }

    @Override
    public int nodeSize()
    {
        return _nodeHash.size();
    }

    @Override
    public int edgeSize()
    {
        return _edgeSet.size();
    }

    @Override
    public int getMC()
    {
        return _changes;
    }
}
