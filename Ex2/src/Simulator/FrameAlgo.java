package Simulator;

import api.DirectedWeightedGraphAlgorithms;
import api.GraphAlgorithms;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static java.awt.Frame.MAXIMIZED_BOTH;

public class FrameAlgo {

    public static int iter = 0;
    private DirectedWeightedGraphAlgorithms _algorithm;
    private GraphZone graphZone;
    private JFrame frame = new JFrame("GraphAlgorithms");
    private JPanel panel;
    private boolean showCenter;

    public FrameAlgo(DirectedWeightedGraphAlgorithms algorithm)
    {
        _algorithm = algorithm;
        showCenter = false;
        frame.setExtendedState(MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        JButton load = new JButton("Set Graph");
        load.setBounds(30, 200, 150, 50);
        panel.add(load);

        JButton shortestPath = new JButton("Shortest Path");
        shortestPath.setBounds(30, 300, 150, 50);
        panel.add(shortestPath);

        JButton isConnected = new JButton("Is Connected");
        isConnected.setBounds(30, 400, 150, 50);
        panel.add(isConnected);

        JButton center = new JButton("Show Center");
        center.setBounds(30, 500, 150, 50);
        panel.add(center);

        JButton button5 = new JButton("TSP");
        button5.setBounds(30, 600, 150, 50);
        panel.add(button5);

        JButton finish = new JButton("Finish Choosing");
        finish.setBounds(30, 700, 150, 50);
        panel.add(finish);
        finish.setVisible(false);

        JLabel label = new JLabel("GraphAlgorithms", SwingConstants.CENTER);
        label.setBounds(10, 10, 100, 100);
        panel.add(label);

        load.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
                {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    if (loadGrpah(path))
                    {
                        showCenter = false;
                        MouseAdapterLabel.resetAll();
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

                JOptionPane.showMessageDialog(frame, "Please Press on the wanted 2 nodes");
                MouseAdapterLabel.needToPick = 2;
                MouseAdapterLabel.isShortest = true;
                finish.setVisible(true);
            }
        });
        finish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (graphZone == null)
                    return;

                if (MouseAdapterLabel.isShortest)
                {
                    if (MouseAdapterLabel.nodesPicked.size() == 2)
                    {
                        int src = Integer.parseInt(MouseAdapterLabel.nodesPicked.get(0).getName());
                        int dest = Integer.parseInt(MouseAdapterLabel.nodesPicked.get(1).getName());
                        MouseAdapterLabel.isShortest = false;
                        finish.setVisible(false);
                        double dist = graphZone.performShortestPath(src, dest);
                        MouseAdapterLabel.resetPicked();
                        MouseAdapterLabel.needToPick = 0;
                        JOptionPane.showMessageDialog(frame, "The shortest path for\t" + src + " --> " + dest + "  =  " + dist);
                    }
                }
            }
        });

        isConnected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graphZone == null)
                    return;

                String con = _algorithm.isConnected() ? "" : "NOT ";
                JOptionPane.showMessageDialog(frame, "The cuurent graph is " + con + "Connected");
            }
        });
        center.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (graphZone == null)
                    return;

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

        graphZone = null;

        //frame.pack();
        frame.repaint();

    }

    public boolean loadGrpah(String file)
    {
        if (_algorithm.load(file))
        {
            if(graphZone != null)
            {
                graphZone.forget();
            }
            graphZone = new GraphZone(_algorithm, 200, frame.getY(), frame.getWidth() - 200, frame.getHeight(), panel);
            System.out.println(_algorithm.center());
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        DirectedWeightedGraphAlgorithms algorithm = new GraphAlgorithms();

        FrameAlgo frameAlgo = new FrameAlgo(algorithm);


    }
}