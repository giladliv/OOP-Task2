package Simulator;
import api.*;
import imps.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;

public class AddNodeFrame
{
        public int key = 0;
        private JFrame frame;
        private JPanel panel;

        public AddNodeFrame(JFrame old, DirectedWeightedGraphAlgorithms algorithm, GraphZone graphZone)
        {
                frame = new JFrame("Add Node Frame");
                JTextField t1;

                frame.setSize(400,400);


                panel = new JPanel();
                panel.setLayout(null);

                JLabel label = new JLabel("Enter a Key:", SwingConstants.CENTER);
                label.setSize(100, 20);
                label.setLocation((frame.getWidth() - label.getWidth())/2, 70);
                panel.add(label);

                t1 = new JTextField();
                t1.setSize(200,30);
                t1.setLocation((frame.getWidth() - t1.getWidth())/2, 100);

                panel.add(t1);

                JButton AddNodeButton = new JButton("Add Node");
                AddNodeButton.setSize(130, 50);
                AddNodeButton.setLocation((frame.getWidth() - AddNodeButton.getWidth())/2, 150);
                panel.add(AddNodeButton);

                AddNodeButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                try
                                {
                                        if (t1.getText().equals(""))
                                        {
                                                throw new Exception();
                                        }
                                        key = Integer.parseInt(t1.getText());
                                        if (key < 0)
                                        {
                                                throw new Exception();
                                        }

                                        if (old != null && graphZone != null && algorithm != null)
                                        {
                                                old.setVisible(true);
                                                NodeData myNode = new Node(key);
                                                myNode.setLocation(graphZone.getStartPoint());
                                                algorithm.getGraph().addNode(myNode);
                                                graphZone.paintAllNodesEdges();
                                        }

                                        frame.dispose();
                                }
                                catch (Exception ex)
                                {
                                        JOptionPane.showMessageDialog(frame, "Please enter a Non-Negative Integer");
                                }
                        }

                });

                frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                                old.setVisible(true);
                                super.windowClosing(e);
                                MouseAdapterLabel.nodesPicked.clear();

                        }
                });
                frame.add(panel);
                frame.setVisible(true);
        }
}


