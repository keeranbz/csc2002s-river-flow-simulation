import javax.swing.JLabel;
import java.util.concurrent.atomic.*;

/**
 * Thread Updates the timer display for each timestep on the GUI
 **/
public class TimerDisplay implements Runnable {
    private FlowPanel fp;
    JLabel timer;
    public static volatile boolean finished = false;

    /*
     * Constructor Method Initializes timer display label
     * 
     * @param timeLabel display label for the timesteps on GUI
     */
    public TimerDisplay(JLabel timeLabel, FlowPanel fp) {
        this.timer = timeLabel;
        this.fp = fp;
        this.finished = false;
    }

    /* Thread updates the display of the timer */
    public void run() {
        while (!finished) {
            timer.setText("Timestep: " + fp.getTimeStep());
        }
    }
}