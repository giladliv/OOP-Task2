package Simulator;

import api.DirectedWeightedGraphAlgorithms;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SaveFileForm
{
    private JFrame frame;
    private JPanel panel;
    public SaveFileForm(EditGraph editor, JFrame old, DirectedWeightedGraphAlgorithms algorithm)
    {
        frame = new JFrame("Save Changes");
        frame.setSize(400,400);

        panel = new JPanel();
        panel.setLayout(null);

        JLabel label = new JLabel("Please enter here a file name (only name)", SwingConstants.CENTER);
        label.setSize(400, 20);
        label.setLocation((frame.getWidth() - label.getWidth())/2, 70);
        panel.add(label);

        JTextField t1 = new JTextField();
        t1.setSize(200,30);
        t1.setLocation((frame.getWidth() - t1.getWidth())/2, 100);

        panel.add(t1);

        JButton saveButton = new JButton("Save Me");
        saveButton.setSize(130, 50);
        saveButton.setLocation((frame.getWidth() - saveButton.getWidth())/2, 150);
        panel.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isGoodName(t1.getText()))
                {
                    if (algorithm.save(t1.getText() + ".json"))
                    {
                        editor.setGraphCurr();
                        JOptionPane.showMessageDialog(frame, "saved");
                        frame.dispose();
                        old.setVisible(true);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(frame, "Failed to save, Please try again");
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(frame, "please enter letters and numbers only");
                }
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    public static boolean isGoodName(String str)
    {
        String curr = str.replaceAll("[A-Za-z0-9]+", "");
        return (curr.equals("") && !str.equals(""));
    }
}
