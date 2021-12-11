package Simulator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GetFileName {
    public JTextField textField1;
    private JPanel panel1;
    private JButton enterAFileNameButton;

    private void createUIComponents() {
        // TODO: place custom component creation code here
        enterAFileNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}
