package calendar.swingGUI;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Dimension;

import javax.swing.*;



//-------------------------------------------//
//               ABOUTFENSTER                //
//-------------------------------------------//

public class About extends JFrame{
      public About() {
        this.setTitle("About");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(300,200));
        this.setBackground(Color.DARK_GRAY);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
     
        //Der Text wird aus einer Methode geholt und der Variable aboutText zugewiesen, um Inline HTML zu vermeiden
        String aboutText = getAboutText();

        //eigentlicher Text
        JLabel label = new JLabel(aboutText, SwingConstants.CENTER);
        
        
        //GUI-Elemente
        this.setLayout(new FlowLayout());
        add(label);
        

        //About ausgeben
        this.pack();
        this.setVisible(true);
    }

  //-------------------------------------------//
  //               METHODEN                    //
  //-------------------------------------------//

    private String getAboutText() {
        return "<html><center>" + "Entwickler: S.J.<br>" + "Projektarbeit 'Programmieren in Java'<br>" + "DHBW AI2" +"</center></html>"; 
    }
}
