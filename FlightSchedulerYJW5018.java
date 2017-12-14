/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flightscheduleryjw5018;


import flightscheduleryjw5018.gui.FlightSchedulerFrame;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author Yingjie(Chelsea) Wang
 */
public class FlightSchedulerYJW5018{

    
    public FlightSchedulerYJW5018(){
    }
    
    public static void main(String[] args) {
       
        //Create and set up the window.
        FlightSchedulerFrame frame = new FlightSchedulerFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
