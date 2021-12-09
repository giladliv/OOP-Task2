package api;

import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DFS
{
    private DirectedWeightedGraph _g;
    private HashMap<Integer, Integer> _parent;
    private HashMap<Integer, Integer> _f;
    private HashMap<Integer, Integer> _d;
    private int _time;
    private HashMap<Integer, HashMap<Integer, List<NodeData>>> _bestRoutes;
    private HashMap<Integer, HashMap<Integer, Double>> _bestWeigths;


    public DFS(DirectedWeightedGraph g)
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


    public void mainfunc()
    {
        Iterator<NodeData> it = _g.nodeIter();
        while (it.hasNext())
        {
            NodeData node = it.next();
            node.setTag(Node.WHITE);
        }
        _time = 0;

        it = _g.nodeIter();
        while (it.hasNext())
        {
            NodeData node = it.next();
            if (node.getTag() == Node.WHITE)
            {
                DFSvisit(node);
            }
        }
    }

    public void DFSvisit(NodeData node)
    {
        node.setTag(Node.GRAY);
        _time = _time +1;
        _d.put(node.getKey(), _time);
        Iterator<EdgeData> it = _g.edgeIter(node.getKey());
        while (it.hasNext())
        {
            EdgeData edge = it.next();
            int dest = edge.getDest();
            NodeData dstNode = _g.getNode(dest);
            if (dstNode.getTag()== Node.WHITE){
                _parent.put(dest, node.getKey());
                DFSvisit(dstNode);
            }
        }
        node.setTag(Node.BLACK);
        _time += 1;
        _f.put(node.getKey(), _time);
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
//                    System.out.println(x + " --> " + y
//                            + ":\t" + Arrays.toString(dfsAlg.getShortestPathNodes(x, y).toArray()));
                }
                //System.out.println();
                this.removeAllMaxDouble();
            }
        }
    }

    private HashMap<Integer, List<NodeData>> allVersions = new HashMap<>();
    private HashMap<Integer, HashMap<List<NodeData>, Double>> allCircles = new HashMap<>();

    public List<NodeData> setCircles(int x, int y)
    {
        List<NodeData> nodes = new ArrayList<>();
        if (_bestWeigths.containsKey(x) && _bestWeigths.get(x).containsKey(y) && _bestWeigths.get(x).get(y) != Double.MAX_VALUE)
        {
            if (_bestWeigths.containsKey(y) && _bestWeigths.get(y).containsKey(x) && _bestWeigths.get(y).get(x) != Double.MAX_VALUE)
            {
                List<NodeData> nodeDataList = _bestRoutes.get(x).get(y);
                nodes.addAll(nodeDataList);
                nodes.addAll(_bestRoutes.get(y).get(x));
                nodes.remove(nodeDataList.size());
            }
        }
        return nodes;
    }

    public void setAllCircles(List<NodeData> nodes)
    {
        for (int i = 0; i < nodes.size(); i++)
        {
            for (int j = 0; j < nodes.size(); j++)
            {
                int x = nodes.get(i).getKey();
                int y = nodes.get(j).getKey();

                if (x == y)
                    continue;
                if (x == 3)
                {
                    int c = 0;
                }
                List<NodeData> listNodes = setCircles(x, y);

                if (!listNodes.isEmpty())
                {
                    if (!allCircles.containsKey(x))
                    {
                        allCircles.put(x, new HashMap<>());
                    }

                    allCircles.get(x).put(listNodes, getWeight(listNodes));
                }

                if (!listNodes.isEmpty())
                    System.out.println(x + " --> " + y + " --> " + x + ":\t" + Arrays.toString(listNodes.toArray()));
            }
        }
    }

    private double getWeight(List<NodeData> nodes)
    {
        double w = 0.0;
        for (int i = 0; i < nodes.size() - 1; i++)
        {
            w += getShortestPathWeight(nodes.get(i).getKey(), nodes.get(i + 1).getKey());
        }
        return w;
    }




}
