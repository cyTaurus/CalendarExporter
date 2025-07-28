package calendar.swingGUI;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Dimension;
//import java.awt.event.*;
import java.util.Date;
import javax.swing.*;


public class Settings extends JFrame {

    private JTextField textField;
    private JSpinner startSpinner;
    private JSpinner endSpinner;
    private MainWindow parent;

    public Settings(MainWindow parent) {
        super("Settings");
        this.parent = parent;
        this.setTitle("Settings");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(900,500));
        this.setBackground(Color.DARK_GRAY);
        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout());

        //Textfeld zum Eintragen der Kalender-URL
        textField = new JTextField(20); //200 ist VIEL zu lang

        //Datumsauswahl Von
        SpinnerDateModel startModel = new SpinnerDateModel();
        startSpinner = new JSpinner(startModel);
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startSpinner, "yyyy-MM-dd");
        startSpinner.setEditor(startEditor);
        
        //Datumsauswal Bis
        SpinnerDateModel endModel = new SpinnerDateModel();
        endSpinner = new JSpinner(endModel);
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endSpinner, "yyyy-MM-dd");
        endSpinner.setEditor(endEditor);
        
        add(new JLabel("Von"));
        add(startSpinner);
        add(new JLabel("Bis"));
        add(endSpinner);
        add(new JLabel("Kalender-URL"));
        this.add(textField);
        
        //Buttons
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> okClick());
        //cancelButton.addActionListener(e -> CancelClick()); //not yet implemented

        this.add(okButton);

        //Settings ausgeben
        this.pack();
        this.setVisible(true);
    }
    //Hilfsmethoden
    private void okClick() {
        String calendarId = textField.getText();
        Date start = (Date) startSpinner.getValue();
        Date end = (Date) endSpinner.getValue();
        parent.loadAndDisplayData(calendarId, start, end);
        dispose();
    }
}
