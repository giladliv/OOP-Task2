package api;
import com.google.gson.Gson;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class GraphAlgorithms implements DirectedWeightedGraphAlgorithms
{
    private DirectedWeightedGraph _g;

    @Override
    public void init(DirectedWeightedGraph g)
    {
       _g = g;
    }

    @Override
    public DirectedWeightedGraph getGraph()
    {
        return _g;
    }

    @Override
    public DirectedWeightedGraph copy()
    {
        if (_g == null)
            return null;
        return new WeightedGraph(_g);
    }

    @Override
    public boolean isConnected()
    {
        DirectedWeightedGraph tempGraph = new WeightedGraph(_g);
        Iterator<EdgeData> itEdge = _g.edgeIter();
        while (itEdge.hasNext())
        {
            EdgeData edge = itEdge.next();
            tempGraph.connect(edge.getDest(), edge.getSrc(), edge.getWeight());
        }

        Iterator<NodeData> itNode = tempGraph.nodeIter();
        if (!itNode.hasNext())
        {
            return false;
        }

        //BFS algo
        Queue<NodeData> nodesQ = new LinkedList<>();
        nodesQ.add(itNode.next());
        nodesQ.peek().setTag(Node.GRAY);
        while (!nodesQ.isEmpty())
        {
            NodeData node = nodesQ.remove();
            itEdge = tempGraph.edgeIter(node.getKey());
            while (itEdge.hasNext())
            {
                NodeData dstNode = tempGraph.getNode(itEdge.next().getDest());
                if (dstNode.getTag() == Node.WHITE)
                {
                    dstNode.setTag(Node.GRAY);
                    nodesQ.add(dstNode);
                }
            }
            node.setTag(Node.BLACK);
        }

        itNode = tempGraph.nodeIter();
        while (itNode.hasNext())
        {
            if (itNode.next().getTag() == Node.WHITE)
                return false;
        }

        return true;
    }

    @Override
    public double shortestPathDist(int src, int dest)
    {
        DFS dfsAlgo = new DFS(_g);
        return dfsAlgo.getShortestPathWeight(src, dest);
    }

    @Override
    public List<NodeData> shortestPath(int src, int dest)
    {
        DFS dfsAlgo = new DFS(_g);
        return dfsAlgo.getShortestPathNodes(src, dest);
    }

    @Override
    public NodeData center()
    {
        DFS graphAlgo = new DFS(_g);

        List<NodeData> nodes = new ArrayList<>();
        Iterator<NodeData> itNode = _g.nodeIter();
        while (itNode.hasNext())
        {
            nodes.add(itNode.next());
        }

        graphAlgo.setAllShortPath(nodes);

        HashMap<Integer, Integer> allBestRoutes = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++)
        {
            double w = -1;
            int currBestNode = 0;
            int src = nodes.get(i).getKey();

            for (int j = 0; j < nodes.size(); j++)
            {
                int dest = nodes.get(j).getKey();

                if (src == dest)
                    continue;

                if (graphAlgo.isExistPath(src, dest))
                {
                    if (graphAlgo.getShortestPathWeight(src, dest) > w)
                    {
                        w = graphAlgo.getShortestPathWeight(src, dest);
                        currBestNode = dest;
                    }
                }
            }

            if (currBestNode > 0)
            {
                allBestRoutes.put(src, currBestNode);
                System.out.println("center: " + src + " --> " + w);
            }
        }

        int[] bestEdge = new int[] {-1, -1};
        double w = Double.MAX_VALUE;
        for (int src: allBestRoutes.keySet())
        {
            int dest = allBestRoutes.get(src);
            if (graphAlgo.getShortestPathWeight(src, dest) < w)
            {
                w = graphAlgo.getShortestPathWeight(src, dest);
                bestEdge[0] = src;
                bestEdge[1] = dest;
            }
        }

        return _g.getNode(bestEdge[0]);
    }

    @Override
    public List<NodeData> tsp(List<NodeData> cities)
    {
        DirectedWeightedGraph graph = new WeightedGraph(_g);
        DFS dfsAlg = new DFS(graph);
        List<NodeData> currNodes = new ArrayList<>();
        double currW = Double.MAX_VALUE;
        dfsAlg.setAllShortPath(cities);

        for (int i = 0; i < cities.size(); i++)
        {
            for (int j = 0; j < cities.size(); j++)
            {
                int x = cities.get(j).getKey();
                int y = cities.get(i).getKey();
                dfsAlg.DFSvisit(x, y);
                List<NodeData> listNodes = dfsAlg.getShortestPathNodes(x, y);
                if (!listNodes.isEmpty())
                    System.out.println(x + " --> " + y + ":\t" + Arrays.toString(listNodes.toArray()));
            }
        }

        System.out.println();
        dfsAlg.setAllCircles(cities);

//        for (int i = 0; i < cities.size(); i++)
//        {
//            for (int j = 0; j < cities.size(); j++)
//            {
//                if (dfsAlg.getShortestPathNodes(i, j).size() == cities.size())
//                {
//                    if (currW < dfsAlg.getShortestPathWeight(i, j))
//                    {
//                        currW = dfsAlg.getShortestPathWeight(i, j);
//                        currNodes = dfsAlg.getShortestPathNodes(i, j);
//                    }
//                }
//            }
//        }

        return currNodes;
    }

    @Override
    public boolean save(String file)
    {
        Gson gson = new Gson();
        try
        {
            ArrayList<Map<String, Object>> nodes = new ArrayList<>();
            Iterator<NodeData> itNodes = _g.nodeIter();
            while (itNodes.hasNext())
            {
                NodeData node = itNodes.next();
                Map<String, Object> mapNode = new HashMap<>();
                mapNode.put("id", node.getKey());
                mapNode.put("pos", node.getLocation().toString());
                nodes.add(mapNode);
            }

            ArrayList<Map<String, Object>> edges = new ArrayList<>();
            Iterator<EdgeData> itEdges = _g.edgeIter();
            while (itEdges.hasNext())
            {
                EdgeData edge = itEdges.next();
                Map<String, Object> mapEdge = new HashMap<>();
                mapEdge.put("src", edge.getSrc());
                mapEdge.put("w", edge.getWeight());
                mapEdge.put("dest", edge.getDest());
                edges.add(mapEdge);
            }

            Map<String, Object> contentToJson = new HashMap<>();
            contentToJson.put("Nodes", nodes);
            contentToJson.put("Edges", edges);
            String jsonData = gson.toJson(contentToJson);

            Files.writeString(Paths.get(file), jsonData);

        }
        catch (Exception ex)
        {
            System.err.println(ex.toString());
            return false;
        }
        return true;
    }

    @Override
    public boolean load(String file)
    {
        _g = new WeightedGraph();
        Gson gson = new Gson();
        try
        {
            String strJson = Files.readString(Paths.get(file));
            Map<String, Object> jsonMap = gson.fromJson(strJson, Map.class);
            if (jsonMap.containsKey("Nodes"))
            {
                ArrayList<Map<String,Object>> arrNodes = (ArrayList)jsonMap.get("Nodes");
                for (Map<String, Object> nodeMap: arrNodes)
                {
                    double id = (double)nodeMap.get("id");
                    _g.addNode(new Node((int)id, (String)nodeMap.get("pos")));
                }
            }
            if (jsonMap.containsKey("Edges"))
            {
                ArrayList<Map<String,Object>> arrEdges = (ArrayList)jsonMap.get("Edges");
                for (Map<String, Object> edgeMap: arrEdges)
                {
                    double src = (double)edgeMap.get("src");
                    double dest = (double)edgeMap.get("dest");
                    double w = (double)edgeMap.get("w");
                    _g.connect((int)src, (int)dest, w);
                }
            }
        }
        catch (Exception ex)
        {
            System.err.println(ex.toString());
            return false;
        }
        return true;
    }

    public static void main(String[] args)
    {
        WeightedGraph graph = new WeightedGraph();
        for (int i = 1; i <= 7; i++)
        {
            graph.addNode(i);
        }
        graph.connect(1,2,1.0);
        graph.connect(2,3,2.0);
        graph.connect(2,5,3.0);
        graph.connect(3,4,1.0);
        graph.connect(4,3,2.0);
        graph.connect(3,5,3.0);
        graph.connect(6,5,4.0);
        graph.connect(6,1,8.0);
        graph.connect(3,7,0.1);
        graph.connect(7,5,0.1);
        graph.connect(1,6,10.0);

        DirectedWeightedGraphAlgorithms algo = new GraphAlgorithms();
        algo.init(graph);
        DFS dfs = new DFS(graph);
        double low = dfs.DFSvisit(1, 5);
        System.out.println(low);
        System.out.println("the center is " + algo.center());
        ////used to be at the dfs:
        //low = dfs..get(1).get(5);
        //System.out.println(low);
        //System.out.println();
        //System.out.println(Arrays.toString(dfs._bestRoutes.get(1).get(5).toArray()));

        System.out.println(algo.isConnected());  // should print "true"
        graph.addNode(8);
        graph.addNode(9);
        graph.addNode(10);
        graph.connect(8,9,10.0);
        graph.connect(9,10,10.0);
        System.out.println(algo.isConnected());  // should print "false"
        graph.removeEdge(8,9);
        graph.removeEdge(9,10);
        graph.connect(3, 9, 2);
        graph.connect(9, 3, 1);
        //graph.connect(4, 9, 0.5);

        //System.out.println(graph.getNode(12) == null);
        //System.out.println(graph.edgeIter(12).hasNext());
        //graph.removeNode(1);
//        Iterator<NodeData> nodeIter = graph.nodeIter();
//        while (nodeIter.hasNext())
//        {
//            graph.removeNode(nodeIter.next().getKey());
//        }
        List<NodeData> cities = new ArrayList<>();
        cities.add(graph.getNode(2));
        cities.add(graph.getNode(5));
        cities.add(graph.getNode(4));
        cities.add(graph.getNode(3));
        cities.add(graph.getNode(9));

        cities = algo.tsp(cities);
        for (int i = 0; i < cities.size(); i++)
        {
            System.out.print(cities.get(i) + " ");
        }
        System.out.println();

        HashSet<List<NodeData>> hashSet = new HashSet<>();
        List<NodeData> temp = new ArrayList<>();
        temp.add(new Node(2));
        hashSet.add(temp);
        temp = new ArrayList<>();
        temp.add(new Node(1));
        hashSet.add(temp);
        temp = new ArrayList<>();
        temp.add(new Node(2));
        temp.add(new Node(1));
        hashSet.add(temp);
        temp = new ArrayList<>();
        temp.add(new Node(1));
        hashSet.add(temp);
        System.out.println(hashSet.contains(temp));

        DFS tempDFS = new DFS(graph);
        algo.save("out/Try.json");

        //GeoLocationImp geo = new GeoLocationImp("15,t,8");
        //System.out.println("x: " + geo.x() + "\ty: " + geo.y() + "\tz: " + geo.z());

        graph = new WeightedGraph();
        String file =  "data/G1.json";
        Gson gson = new Gson();
        try
        {
            String strJson = Files.readString(Paths.get(file));
            Map<String, Object> jsonMap = gson.fromJson(strJson, Map.class);
            if (jsonMap.containsKey("Nodes"))
            {
                ArrayList<Map<String,Object>> arrNodes = (ArrayList)jsonMap.get("Nodes");
                for (Map<String, Object> nodeMap: arrNodes)
                {
                    double id = (double)nodeMap.get("id");
                    graph.addNode(new Node((int)id, (String)nodeMap.get("pos")));
                }
            }
            if (jsonMap.containsKey("Edges"))
            {
                ArrayList<Map<String,Object>> arrEdges = (ArrayList)jsonMap.get("Edges");
                for (Map<String, Object> edgeMap: arrEdges)
                {
                    double src = (double)edgeMap.get("src");
                    double dest = (double)edgeMap.get("dest");
                    double w = (double)edgeMap.get("w");
                    graph.connect((int)src, (int)dest, w);
                }
            }

            jsonMap.toString();
        }
        catch (Exception ex)
        {
            System.err.println("Oops i did it again...");
            System.err.println(ex.toString());
        }
    }
}
