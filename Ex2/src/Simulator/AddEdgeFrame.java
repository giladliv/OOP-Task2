package Simulator;
import api.DirectedWeightedGraphAlgorithms;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class AddEdgeFrame
{
        public int src = 0;
        public int dest = 0;
        public double weight = 0;
        private JFrame frame;
        private JPanel panel;

        public AddEdgeFrame(JFrame old, DirectedWeightedGraphAlgorithms algorithm, GraphZone graphZone)
        {
                frame= new JFrame("Add Edge Frame");
                JTextField t1, t2, t3;

                frame.setSize(500,400);

                panel = new JPanel();
                panel.setLayout(null);

                JLabel label1 = new JLabel("Enter src:", SwingConstants.CENTER);
                label1.setSize(100, 50);
                label1.setLocation(32, 20);
                panel.add(label1);
                t1 =new JTextField("");
                t1.setBounds(30,60, 100,30);
                panel.add(t1);

                JLabel label2 = new JLabel("Enter dest:", SwingConstants.CENTER);
                label2.setSize(100, 50);
                label2.setLocation(202, 20);
                panel.add(label2);
                t2 =new JTextField("");
                t2.setBounds(200,60, 100,30);
                panel.add(t2);

                JLabel label3 = new JLabel("Enter Weight:", SwingConstants.CENTER);
                label3.setSize(100, 50);
                label3.setLocation(372, 20);
                panel.add(label3);
                t3 =new JTextField("");
                t3.setBounds(370,60, 100,30);
                frame.setVisible(true);
                panel.add(t3);


                JButton AddEdgeButton = new JButton("Add Edge");
                AddEdgeButton.setSize(130, 50);
                AddEdgeButton.setLocation((frame.getWidth() - AddEdgeButton.getWidth())/2, 150);
                panel.add(AddEdgeButton);

                frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                                old.setVisible(true);
                                super.windowClosing(e);
                                MouseAdapterLabel.nodesPicked.clear();

                        }
                });
                AddEdgeButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                try
                                {
                                        if (t1.getText().equals(""))
                                        {
                                                throw new Exception();
                                        }
                                        src = Integer.parseInt(t1.getText());
                                        if (src < 0)
                                        {
                                                throw new Exception();
                                        }

                                        if (t2.getText().equals(""))
                                        {
                                                throw new Exception();
                                        }
                                        dest = Integer.parseInt(t2.getText());
                                        if (dest < 0)
                                        {
                                                throw new Exception();
                                        }

                                        if (t3.getText().equals(""))
                                        {
                                                throw new Exception();
                                        }
                                        weight = Double.parseDouble(t3.getText());
                                        if (weight< 0)
                                        {
                                                throw new Exception();
                                        }

                                        if (t1.getText() == t2.getText())
                                        {
                                                throw new Exception();
                                        }

                                        if (old != null && graphZone != null && algorithm != null)
                                        {
                                                old.setVisible(true);
                                                algorithm.getGraph().connect(src, dest, weight);
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
                frame.add(panel);
                frame.setVisible(true);

        }


}



