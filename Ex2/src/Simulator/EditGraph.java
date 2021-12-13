package Simulator;
import api.*;

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
    private DirectedWeightedGraph _currGraph;
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
        _currGraph = _algorithm.copy();
        frame = new JFrame("EditGraph");

        frame.setExtendedState(MAXIMIZED_BOTH);
        //frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        JButton moveNode = new JButton("Move Node");
        moveNode.setBounds(10, 10, 130, 50);
        panel.add(moveNode);

        JButton addNode = new JButton("Add Node");
        addNode.setBounds(moveNode.getX() + moveNode.getWidth() + 10, moveNode.getY(), moveNode.getWidth(), moveNode.getHeight());
        panel.add(addNode);

        JButton deleteNode = new JButton("Delete Node");
        deleteNode.setBounds(addNode.getX() + addNode.getWidth() + 10, addNode.getY(), addNode.getWidth(), addNode.getHeight());
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

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (_currGraph.getMC() != _algorithm.getGraph().getMC())
                {
                    int answer = JOptionPane.showConfirmDialog(frame,
                            "Some files have changes and not been saved, do you want to save them?",
                            "save changes",
                            JOptionPane.YES_NO_OPTION);
                    if (answer == 0)
                    {
                        button6.doClick();
                    }
                    else
                    {
                        _algorithm.init(_currGraph);
                        oldFame.frame.setVisible(true);
                    }
                }
                MouseAdapterLabel.canMove = false;
                oldFame.graphZone.paintAllNodesEdges();

            }
        });

        moveNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mode == Modes.NONE)
                {
                    MouseAdapterLabel.canMove = true;
                    mode = Modes.MOVE_NODE;
                    moveNode.setText("Stop Move");
                }
                else if (mode == Modes.MOVE_NODE)
                {
                    MouseAdapterLabel.canMove = false;
                    mode = Modes.NONE;
                    moveNode.setText("Move Node");
                }
            }
        });

        // add node
        addNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddNodeFrame nodeFrame = new AddNodeFrame(frame, _algorithm, graphZone);
                frame.setVisible(false);
            }
        });

        // delete node
        deleteNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                MouseAdapterLabel.needToPick = 1;
                if (mode == Modes.MOVE_NODE)
                    moveNode.doClick();

                mode = Modes.DELETE_NODE;
                finish.setVisible(true);
            }
        });

        //add edge
        addEdge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddEdgeFrame addEdgeFrame = new AddEdgeFrame(frame, _algorithm, graphZone);
            }
        });

        //delete edge
        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MouseAdapterLabel.needToPick = 2;
                if (mode == Modes.MOVE_NODE)
                    moveNode.doClick();

                mode = Modes.DELETE_EDGE;
                finish.setVisible(true);
            }
        });

        // save graph
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (_currGraph.getMC() == _algorithm.getGraph().getMC())
                    return;

                SaveFileForm saveFileForm = new SaveFileForm(EditGraph.this, frame, _algorithm);
                frame.setVisible(false);

                //saveFileForm
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

    public void setGraphCurr()
    {
        _currGraph = _algorithm.copy();
    }
}