package imps;

import api.DirectedWeightedGraph;
import api.EdgeData;
import api.NodeData;

import java.util.*;


////////////////////////////
/*

 */
////////////////////////////

public class AlgoHelper
{
    private DirectedWeightedGraph _g;
    private HashMap<Integer, Integer> _parent;
    private HashMap<Integer, Integer> _f;
    private HashMap<Integer, Integer> _d;
    private int _time;
    private HashMap<Integer, HashMap<Integer, List<NodeData>>> _bestRoutes;
    private HashMap<Integer, HashMap<Integer, Double>> _bestWeigths;

    public AlgoHelper(DirectedWeightedGraph g)
    {
        _g = g;
        _time = 0;
        _parent = new HashMap<>();
        _f = new HashMap<>();
        _d = new HashMap<>();

        Iterator<NodeData> it = _g.nodeIter();
        while (it.hasNext())
        {
            NodeData node = it.next();
            node.setTag(Node.WHITE);
        }
        _bestRoutes = new HashMap<>();
        _bestWeigths = new HashMap<>();
    }

    public void reset()
    {
        _f.clear();
        _d.clear();
        _parent.clear();
        _time = 0;
        _bestRoutes.clear();
        _bestWeigths.clear();
    }

    private void setRoutePair(NodeData node, int lastNode)
    {
        if (!_bestRoutes.containsKey(node.getKey()))
        {
            _bestRoutes.put(node.getKey(), new HashMap<>());
        }
        if (!_bestRoutes.get(node.getKey()).containsKey(lastNode))
        {
            _bestRoutes.get(node.getKey()).put(lastNode, new ArrayList<>());
            _bestRoutes.get(node.getKey()).get(lastNode).add(node);
        }
    }

    private void setWeightPair(NodeData node, int lastNode, double weight)
    {
        if (!_bestWeigths.containsKey(node.getKey()))
        {
            _bestWeigths.put(node.getKey(), new HashMap<>());
        }
        _bestWeigths.get(node.getKey()).put(lastNode, weight);
    }

    public void removeAllMaxDouble()
    {
        ArrayList<int[]> arrSrcDest = new ArrayList<>();
        for (int src: _bestWeigths.keySet())
        {
            for (int dest: _bestWeigths.get(src).keySet())
            {
                if (_bestWeigths.get(src).get(dest) == Double.MAX_VALUE)
                {
                    arrSrcDest.add(new int[] {src, dest});
                }
            }

        }

        for (int[] arr: arrSrcDest)
        {
            int src = arr[0];
            int dest = arr[1];
            _bestWeigths.get(src).remove(dest);
            _bestRoutes.get(src).remove(dest);
            if ( _bestWeigths.get(src).isEmpty())
                _bestWeigths.remove(src);
            if ( _bestRoutes.get(src).isEmpty())
                _bestRoutes.remove(src);
        }

    }

    public double DFSvisit(int nodeId, int lastNode)
    {
        NodeData node = _g.getNode(nodeId);
        Iterator<EdgeData> it = _g.edgeIter(node.getKey());
        double weight = Double.MAX_VALUE;

        if (_bestWeigths.containsKey(nodeId) && _bestWeigths.get(nodeId).containsKey(lastNode))
            return _bestWeigths.get(nodeId).get(lastNode);

        setRoutePair(node, lastNode);
        if(node.getKey() == lastNode)
        {
            setWeightPair(node, lastNode, 0.0);
            return 0.0;
        }

        node.setTag(Node.GRAY);
        List<NodeData> currBest = new ArrayList<>();
        while (it.hasNext())
        {
            EdgeData edge = it.next();
            int dest = edge.getDest();

            NodeData dstNode = _g.getNode(dest);

            if (dstNode.getTag() == Node.WHITE)
            {
                double retDfs = DFSvisit(dest, lastNode);

                if (retDfs != Double.MAX_VALUE)
                {
                    weight = Math.min(weight, retDfs + edge.getWeight());
                }

                if(weight == retDfs + edge.getWeight())
                {
                    currBest = getShortestPathNodes(dest, lastNode);
                }
            }
        }
        if (weight == Double.MAX_VALUE)
        {
            if (_bestRoutes.containsKey(nodeId))
                _bestRoutes.get(nodeId).remove(lastNode);
        }
        else
        {
            _bestRoutes.get(node.getKey()).get(lastNode).addAll(currBest);
        }

        node.setTag(Node.WHITE);
        setWeightPair(node, lastNode, weight);
        return weight;
    }

    public List<NodeData> getShortestPathNodes(int src, int dest)
    {
        if (!(_bestRoutes.containsKey(src)) || !(_bestRoutes.get(src).containsKey(dest)))
        {
            DFSvisit(src, dest);
        }

        if (!(_bestRoutes.containsKey(src)) || !(_bestRoutes.get(src).containsKey(dest)))
        {
            return new ArrayList<>();
        }

        return _bestRoutes.get(src).get(dest);
    }

    public double getShortestPathWeight(int src, int dest)
    {
        if (!(_bestWeigths.containsKey(src)) || !(_bestWeigths.get(src).containsKey(dest)))
            return DFSvisit(src, dest);

        return _bestWeigths.get(src).get(dest);
    }

    public boolean isExistPath(int src, int dst)
    {
        try
        {
            double w = _bestWeigths.get(src).get(dst);
            return (w != Double.MAX_VALUE);
        }
        catch(Exception ex)
        {
            return false;
        }

    }

    public void setAllShortPath(List<NodeData> nodes)
    {
        for (int times = 0; times < 2; times++)
        {
            for (int i = 0; i < nodes.size(); i++)
            {
                for (int j = 0; j < nodes.size(); j++)
                {
                    int x = nodes.get(j).getKey();
                    int y = nodes.get(i).getKey();
                    this.DFSvisit(x, y);
                }
                this.removeAllMaxDouble();
            }
        }
    }

    public HashMap<Integer, HashMap<List<NodeData>, Double>> pathBest = new HashMap<>();

    public void setPathsList(List<NodeData> nodes, HashSet<Integer> nodesRemain, double w)
    {
        if (nodesRemain.isEmpty())
        {
            NodeData first = nodes.get(0);
            if (!pathBest.containsKey(first.getKey()))
            {
                pathBest.put(first.getKey(), new HashMap<>());
            }
            pathBest.get(first.getKey()).put(nodes, w);
        }

        NodeData last = nodes.get(nodes.size() - 1);
        for (int node: _bestRoutes.get(last.getKey()).keySet())
        {
            if (node != last.getKey() && nodesRemain.contains(node))
            {
                List<NodeData> bestP = new ArrayList<>(_bestRoutes.get(last.getKey()).get(node));
                bestP.remove(0);
                List<NodeData> tempList = new ArrayList<>(nodes);
                HashSet<Integer> tempHash = new HashSet<>(nodesRemain);
                tempList.addAll(bestP);
                for (NodeData curr: bestP)
                {
                    tempHash.remove(curr.getKey());
                }
                setPathsList(tempList, tempHash, w + _bestWeigths.get(last.getKey()).get(node));
            }
        }

    }

}
