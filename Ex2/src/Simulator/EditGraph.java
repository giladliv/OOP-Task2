package Simulator;
import api.DirectedWeightedGraphAlgorithms;
import api.Edge;
import api.GraphAlgorithms;
import api.Node;

import javax.swing.*;
import java.awt.*;
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

        JButton deleteNode = new JButton("Delete Node");
        deleteNode.setBounds(button2.getX() + button2.getWidth() + 10, button2.getY(), button2.getWidth(), button2.getHeight());
        panel.add(deleteNode);

        JButton addEdge = new JButton("Add Edge");
        addEdge.setBounds(deleteNode.getX() + deleteNode.getWidth() + 10, deleteNode.getY(), deleteNode.getWidth(), deleteNode.getHeight());
        panel.add(addEdge);

        JButton button5 = new JButton("Delete Edge");
        button5.setBounds(addEdge.getX() + addEdge.getWidth() + 10, addEdge.getY(), addEdge.getWidth(), addEdge.getHeight());
        panel.add(button5);

        JButton button6 = new JButton("Save Graph");
        button6.setBounds(button5.getX() + button5.getWidth() + 10, button5.getY(), button5.getWidth(), button5.getHeight());
        panel.add(button6);

        JButton finish = new JButton("Finish Choosing");
        finish.setBounds(button6.getX() + button6.getWidth() + 10, button6.getY(), button6.getWidth(), button6.getHeight());
        panel.add(finish);
        finish.setVisible(false);
        finish.setBackground(Color.RED);

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
        deleteNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                MouseAdapterLabel.needToPick = 1;
                if (mode == Modes.MOVE_NODE)
                    button1.doClick();

                mode = Modes.DELETE_NODE;
                finish.setVisible(true);
            }
        });

        //add edge
        addEdge.addActionListener(new ActionListener() {
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
                MouseAdapterLabel.needToPick = 2;
                if (mode == Modes.MOVE_NODE)
                    button1.doClick();

                mode = Modes.DELETE_EDGE;
                finish.setVisible(true);
            }
        });
            // save graph
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //GetFileName form = new GetFileName();
                //System.out.println(form.textField1.getText());
            }
        });

        finish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mode == Modes.DELETE_NODE)
                {
                    if (MouseAdapterLabel.nodesPicked.size() == 1)
                    {
                        int key = Integer.parseInt(MouseAdapterLabel.nodesPicked.get(0).getName());
                        mode = Modes.NONE;
                        finish.setVisible(false);
                        if (_algorithm.getGraph().removeNode(key) != null)
                        {
                            graphZone.paintAllNodesEdges();
                            JOptionPane.showMessageDialog(frame, "Node removed successfully");
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(frame, "Failed to removed node");
                        }
                        MouseAdapterLabel.resetPicked();
                        MouseAdapterLabel.needToPick = 0;

                    }
                }
                else if (mode == Modes.DELETE_EDGE)
                {
                    if (MouseAdapterLabel.nodesPicked.size() == 2)
                    {
                        int src = Integer.parseInt(MouseAdapterLabel.nodesPicked.get(0).getName());
                        int dest = Integer.parseInt(MouseAdapterLabel.nodesPicked.get(1).getName());

                        mode = Modes.NONE;
                        finish.setVisible(false);

                        if (_algorithm.getGraph().removeEdge(src, dest) != null)
                        {
                            graphZone.paintAllNodesEdges();
                            String message = "Edge (%d, %d) removed successfully".formatted(src, dest);
                            JOptionPane.showMessageDialog(frame, message);
                        }
                        else
                        {
                            String message = "Failed to remove Edge (%d, %d)".formatted(src, dest);
                            JOptionPane.showMessageDialog(frame, message);
                        }
                        MouseAdapterLabel.resetPicked();
                        MouseAdapterLabel.needToPick = 0;

                    }
                }
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