package calendar.swingGUI;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Dimension;
//import java.awt.event.*;

import javax.swing.*;


public class Settings extends JFrame {

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
        JTextField textField = new JTextField(20); //200 ist VIEL zu lang

        //Datumsauswahl Von
        SpinnerDateModel startModel = new SpinnerDateModel();
        JSpinner startSpinner = new JSpinner(startModel);
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startSpinner, "yyyy-MM-dd");
        startSpinner.setEditor(startEditor);
        
        //Datumsauswal Bis
        SpinnerDateModel endModel = new SpinnerDateModel();
        JSpinner endSpinner = new JSpinner(endModel);
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endSpinner, "yyyy-MM-dd");
        endSpinner.setEditor(endEditor);
        
        add(new JLabel("Von"));
        add(startSpinner);
        add(new JLabel("Bis"));
        add(endSpinner);

        this.add(textField);
        
        

        //Settings ausgeben
        this.pack();
        this.setVisible(true);
    }
}
