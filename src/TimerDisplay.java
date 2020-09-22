import javax.swing.JLabel;
import java.util.concurrent.atomic.*;


/**
 * Update the timer display
 * Controls display of each timestep
 **/
public class TimerDisplay  implements Runnable {
   JLabel timer;
	public static volatile boolean stop = false;
	
   /* Constructor Method
    * Initializes timer
    */
	public TimerDisplay(JLabel t) {
        this.timer = t;
        System.out.println("Good job");

    }
	
   /* Thread updates the display of the timer */
	public void run() { 
        while (!stop) {
        	   timer.setText("Timer: " + Flow.getTime()); 
        }
    }
}