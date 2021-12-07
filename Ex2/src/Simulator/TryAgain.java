package Simulator;

import javax.swing.*;

public class TryAgain extends JFrame {
    private JButton button1;
    private JPanel panel1;

    public TryAgain()
    {
        button1.setBounds(10, 20, 100,50);
    }

    public static void main(String[] args)
    {
        TryAgain dialog = new TryAgain();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
