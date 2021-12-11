package Simulator;
import api.DirectedWeightedGraphAlgorithms;
import api.Edge;
import api.GraphAlgorithms;
import api.Node;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.awt.Frame.MAXIMIZED_BOTH;


public class EditGraph
{
    public static int iter = 0;
    private DirectedWeightedGraphAlgorithms _algorithm;
    private JFrame frame;
    private JPanel panel;
    private GraphZone graphZone;
    private FrameAlgo oldFame;
    private static Modes mode = Modes.NONE;
    private static enum Modes
    {
        NONE,
        MOVE_NODE,
        ADD_NODE,
        DELETE_NODE,
        ADD_EDGE,
        DELETE_EDGE,
        SAVE
    }

    public EditGraph(FrameAlgo old, DirectedWeightedGraphAlgorithms algorithm)
    {
        oldFame = old;
        _algorithm = algorithm;
        frame = new JFrame("EditGraph");

        frame.setExtendedState(MAXIMIZED_BOTH);
        //frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        JButton button1 = new JButton("Move Node");
        button1.setBounds(10, 10, 130, 50);
        panel.add(button1);

        JButton button2 = new JButton("Add Node");
        button2.setBounds(button1.getX() + button1.getWidth() + 10, button1.getY(), button1.getWidth(), button1.getHeight());
        panel.add(button2);

        JButton button3 = new JButton("Delete Node");
        button3.setBounds(button2.getX() + button2.getWidth() + 10, button2.getY(), button2.getWidth(), button2.getHeight());
        panel.add(button3);

        JButton button4 = new JButton("Add Edge");
        button4.setBounds(button3.getX() + button3.getWidth() + 10, button3.getY(), button3.getWidth(), button3.getHeight());
        panel.add(button4);

        JButton button5 = new JButton("Delete Edge");
        button5.setBounds(button4.getX() + button4.getWidth() + 10, button4.getY(), button4.getWidth(), button4.getHeight());
        panel.add(button5);

        JButton button6 = new JButton("Save Graph");
        button6.setBounds(button5.getX() + button5.getWidth() + 10, button5.getY(), button5.getWidth(), button5.getHeight());
        panel.add(button6);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mode == Modes.NONE)
                {
                    MouseAdapterLabel.canMove = true;
                    mode = Modes.MOVE_NODE;
                    button1.setText("Stop Move");
                }
                else if (mode == Modes.MOVE_NODE)
                {
                    MouseAdapterLabel.canMove = false;
                    mode = Modes.NONE;
                    button1.setText("Move Node");
                }
            }

        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                oldFame.frame.setVisible(true);
                oldFame.graphZone.paintAllNodesEdges();
                super.windowClosing(e);
            }
        });

        // add node
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int key = 0;
                Node myNode = new Node(_algorithm.getGraph().nodeSize());
                myNode.setLocation(graphZone.getStartPoint());
                _algorithm.getGraph().addNode(myNode);
                graphZone.paintAllNodesEdges();
                //graphZone.paintAllNodesEdges();
            }
        });

        // delete node
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                int key=0;
                _algorithm.getGraph().removeNode(key);
                graphZone.paintAllNodesEdges();
            }
        });

        //add edge
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int src = 0;
                int dest = 1;
                double w = 0;
                _algorithm.getGraph().connect(src, dest, w);
                graphZone.paintAllNodesEdges();
            }
        });

        //delete edge
        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int src=0;
                int dest=1;
                _algorithm.getGraph().removeEdge(src, dest);
                graphZone.paintAllNodesEdges();
            }
        });
            // save graph
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GetFileName form = new GetFileName();
                System.out.println(form.textField1.getText());
            }
        });

        graphZone = new GraphZone(_algorithm, 0, 60, frame.getWidth(), frame.getHeight() - 60, panel);
        frame.repaint();
    }

    public static void main(String[] args)
    {
        DirectedWeightedGraphAlgorithms algo = new GraphAlgorithms();
        algo.load("data/G1.json");
        EditGraph graph = new EditGraph(null, algo);
    }
}