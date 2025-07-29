package calendar.swingGUI;

import java.awt.Color;
import java.awt.Dimension;
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
        this.setPreferredSize(new Dimension(300,200));
        this.setBackground(Color.DARK_GRAY);
        this.setLocationRelativeTo(null);
        

        //Textfeld zum Eintragen der Kalender-URL
        textField = new JTextField(20); 

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
        okButton.addActionListener(e -> okClick());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        this.add(okButton);
        this.add(cancelButton);


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
        
        parent.fetchData(calendarId, start, end);
        dispose();
    }
}
