package calendar.swingGUI;

import java.util.Date;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.*;

import calendar.swingGUI.GUIutils.*;


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
        this.setPreferredSize(new Dimension(300,200));
        this.setBackground(Color.DARK_GRAY);
        this.setLocationRelativeTo(null);
        this.setResizable(false);


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

    //-------------------------------------------//
    //               LAYOUT                      //
    //-------------------------------------------//

        this.setLayout(new BorderLayout());
        
        //GUI-Elemente

        //Textfeld zum Eintragen der Kalender-URL
        textField = new JTextField(20);
        
        JLabel vonLabel = new JLabel("Von");
        JLabel bisLabel = new JLabel("Bis");
        JLabel idLabel = new JLabel("Kalender-ID");
        idLabel.setToolTipText("If you don't know your calendar-ID, and you only have one Google Calendar, it's probably 'primary'");

        //Buttons
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> okClick());
        cancelButton.addActionListener(e -> dispose());

        //Panels
        JPanel datePanel = new JPanel();
        datePanel.add(vonLabel);
        datePanel.add(startSpinner);
        datePanel.add(bisLabel);
        datePanel.add(endSpinner);
        add(datePanel, BorderLayout.NORTH);

        JPanel textPanel = new JPanel();
        textPanel.add(idLabel);
        textPanel.add(textField);
        add(textPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

       
        
        

      


        //Settings ausgeben
        this.pack();
        this.setVisible(true);
    }

    //-------------------------------------------//
    //               METHODEN                    //
    //-------------------------------------------//

    private void okClick() {
        String calendarId = textField.getText();

        //Inputvalidierung: Ist die Kalender-ID ausgefüllt?
         if (calendarId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter valid calendar-ID!");
            return;
        }

        Date start = (Date) startSpinner.getValue();
        Date end = (Date) endSpinner.getValue();

        //Inputvalidierung: liegt Start zeitlich vor Ende?
         if (!start.before(end)) {
            JOptionPane.showMessageDialog(this, "End date cannot be before start date!");
            return;
        }

        TableUtils.fetchData(parent,calendarId, start, end);
        dispose();
    }
}
