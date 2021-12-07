package Simulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BatelSimu
{
    public static int iter = 0;
    public BatelSimu()
    {
        JFrame frame = new JFrame("Hello World");
        frame.setSize(900, 800);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JButton button1 = new JButton("ClickMe");
        button1.setBounds(200, 200, 100, 50);
        panel.add(button1);

        JLabel label = new JLabel("Hello", SwingConstants.CENTER);
        label.setBounds(10, 10, 100, 100);
        panel.add(label);

        button1.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println(e.toString());
            }
        });


        frame.add(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args)
    {
        BatelSimu batelSimu = new BatelSimu();
    }

}
