package Simulator;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static java.awt.Frame.MAXIMIZED_BOTH;

public class FrameAlgo {

    public static int iter = 0;
    private DirectedWeightedGraphAlgorithms _algorithm;
    public GraphZone graphZone;
    public JFrame frame = new JFrame("GraphAlgorithms");
    private JPanel panel;
    private boolean showCenter;
    private int mode;
    public static final int NONE = 0;
    public static final int SHORTEST_PATH = 1;
    public static final int TSP = 2;


    public FrameAlgo(DirectedWeightedGraphAlgorithms algorithm)
    {
        mode = NONE;
        _algorithm = algorithm;
        showCenter = false;
        frame.setExtendedState(MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        // create buttons for us to use

        JButton load = new JButton("Set Graph");
        load.setBounds(30, 100, 150, 50);
        panel.add(load);

        JButton shortestPath = new JButton("Shortest Path");
        shortestPath.setBounds(30, 200, 150, 50);
        panel.add(shortestPath);

        JButton isConnected = new JButton("Is Connected");
        isConnected.setBounds(30, 300, 150, 50);
        panel.add(isConnected);

        JButton center = new JButton("Show Center");
        center.setBounds(30, 400, 150, 50);
        panel.add(center);

        JButton tsp = new JButton("TSP");
        tsp.setBounds(30, 500, 150, 50);
        panel.add(tsp);

        JButton finish = new JButton("Finish Choosing");
        finish.setBounds(30, 600, 150, 50);
        panel.add(finish);
        finish.setVisible(false);
        finish.setBackground(Color.RED);

        JButton editGraph = new JButton("Edit Graph");
        editGraph.setBounds(30, 700, 150, 50);
        editGraph.setVisible(loadGrpah());
        panel.add(editGraph);

        JLabel label = new JLabel("GraphAlgorithms", SwingConstants.CENTER);
        label.setBounds(10, 10, 100, 100);
        panel.add(label);


        // when load has been pressed
        load.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //gets from user the directory
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

                // if the user agreed
                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
                {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    if (loadGrpah(path)) // load and init all the sroundings
                    {
                        showCenter = false;
                        MouseAdapterLabel.resetAll();
                        editGraph.setVisible(true);
                    }
                }

            }
        });

        shortestPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (graphZone == null)
                    return;

                graphZone.paintAllNodesEdges(); // repaint to start
                JOptionPane.showMessageDialog(frame, "Please Press on the wanted 2 nodes,\n if you wish to cancel one of the - just click on it");
                MouseAdapterLabel.needToPick = 2;   // can only select 2 points
                mode = SHORTEST_PATH;               // the mode now is the shortest path
                finish.setVisible(true);            // need to finish
            }
        });
        finish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (graphZone == null)
                    return;

                if (mode == SHORTEST_PATH) // if now shrtest path then select only two points
                {
                    if (MouseAdapterLabel.nodesPicked.size() == 2)
                    {
                        int src = Integer.parseInt(MouseAdapterLabel.nodesPicked.get(0).getName()); // get the first
                        int dest = Integer.parseInt(MouseAdapterLabel.nodesPicked.get(1).getName());// get the dest
                        mode = NONE;
                        finish.setVisible(false);
                        double dist = graphZone.performShortestPath(src, dest);
                        MouseAdapterLabel.resetPicked();
                        MouseAdapterLabel.needToPick = 0;
                        JOptionPane.showMessageDialog(frame, "The shortest path for  " + src + " --> " + dest + "  =  " + dist);
                    }
                    else
                    {
                        finish.setVisible(false);
                        MouseAdapterLabel.resetPicked();
                    }
                }
                else if(mode == TSP)
                {
                    if (MouseAdapterLabel.nodesPicked.size() > 0)
                    {
                        mode = NONE;
                        finish.setVisible(false);
                        graphZone.performTSP();
                        MouseAdapterLabel.resetPicked();
                        MouseAdapterLabel.needToPick = 0;
                    }
                    else
                    {
                        finish.setVisible(false);
                        MouseAdapterLabel.resetPicked();
                    }
                }
            }
        });

        isConnected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graphZone == null)
                    return;

                graphZone.paintAllNodesEdges(); // reset the graph
                String con = _algorithm.isConnected() ? "" : "NOT ";
                JOptionPane.showMessageDialog(frame, "The cuurent graph is " + con + "Connected"); // show if connected or not
            }
        });

        center.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (graphZone == null)
                    return;

                graphZone.paintAllNodesEdges();

                if (!showCenter)
                {
                    graphZone.perfornCenter();
                    showCenter = true;
                    center.setText("Disable Center");
                }
                else
                {
                    graphZone.disableCenter();
                    center.setText("Show Center");
                    showCenter = false;
                }

            }
        });
        tsp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graphZone == null)
                    return;

                graphZone.paintAllNodesEdges();
                JOptionPane.showMessageDialog(frame,"Please Press on the wanted nodes\nAfter That please press on finished");
                MouseAdapterLabel.needToPick = _algorithm.getGraph().nodeSize();
                mode = TSP;
                finish.setVisible(true);
            }
        });

        editGraph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                EditGraph editWin = new EditGraph(FrameAlgo.this, _algorithm);
            }
        });

        //frame.pack();
        frame.repaint();

    }

    /**
     * load graph at initialization
     * @return
     */
    public boolean loadGrpah()
    {
        if(graphZone != null)   // if already exists, repaint
        {
            graphZone.paintAllNodesEdges();
            return true;
        }
        if (_algorithm == null || _algorithm.getGraph() == null) // if canot be used then set to null
        {
            graphZone = null;
            return false;
        }
        //set the graph zone with known values
        graphZone = new GraphZone(_algorithm, 200, frame.getY(), frame.getWidth() - 200, frame.getHeight(), panel);
        return true;
    }

    /**
     * load graph form file
     * @param file
     * @return
     */
    public boolean loadGrpah(String file)
    {
        if (_algorithm.load(file)) // if can be loaded
        {
            if(graphZone != null) // if already has been created just update
            {
                graphZone.paintAllNodesEdges();
                return true;
            }
            //esle create it
            graphZone = new GraphZone(_algorithm, 200, frame.getY(), frame.getWidth() - 200, frame.getHeight(), panel);
            return true;
        }
        return false;
    }

}