import javax.swing.*;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.*;

/**
 * View Main display window that shows the landscape as a greyscale image, with
 * black representing the lowest elevation and white the highest. Overlaid on
 * the terrain is an image representing the locations of water in blue.
 **/
public class Flow {
	static float startTime = 0;
	static int frameX;
	static int frameY;
	static FlowPanel fp;
	static TimerDisplay timer; // threaded display of counters

	/**
	 * Starts timer
	 */
	private static void tick() {
		startTime = System.currentTimeMillis();
	}

	/**
	 * stop timer, return time elapsed in seconds
	 */
	private static float tock() {
		return (System.currentTimeMillis() - startTime) / 1000.0f;
	}

	/**
	 * returns time elapsed
	 */
	public static float getTime() {
		return tock();
	}

	/**
	 * Gui Setup
	 * 
	 * @param frameX   size on x-axis
	 * @param frameY   size on y-axis
	 * @param landdata Terrain object.
	 * @param water    Water object.
	 */
	public static void setupGUI(int frameX, int frameY, Terrain landdata, Water water) {

		Dimension fsize = new Dimension(800, 800);
		JFrame frame = new JFrame("Waterflow");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JPanel g = new JPanel();
		g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS));

		fp = new FlowPanel(landdata, water);
		fp.setPreferredSize(new Dimension(frameX, frameY));
		g.add(fp);

		// MouseListener
		fp.addMouseListener(new customizedMouseListener(fp));

		/*
		 * A counter that displays the number of timesteps since the start of the
		 * simulation
		 */
		JPanel t = new JPanel();
		t.setLayout(new BoxLayout(t, BoxLayout.LINE_AXIS));
		t.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JLabel timerDisplay = new JLabel("Timer: " + startTime);
		timer = new TimerDisplay(timerDisplay);

		// Button Pane
		JPanel b = new JPanel();
		b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));
		b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		/*
		 * Reset button that zeroes both the water depth across the entire landscape and
		 * the timestep count.
		 */
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fp.stop();
				fp.clear();
				fp.repaint();
				// add timer set it to zero
				startTime = 0;
				// set text to zero
				timerDisplay.setText("Timer: " + startTime);
			}
		});

		/* Pause button that temporarily stops the simulation. */
		JButton pauseButton = new JButton("Pause");
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop = true; // stop counter thread
				// startTime = tock(); // stop timer
				fp.stop(); // stops simulation threads
			}
		});

		/*
		 * Play button that starts the simulation and allows it to continue running if
		 * previously paused.
		 */
		JButton playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				fp.play(); // runs simulation threads
			}
		});

		/* End button that closes the window and exits the program */
		JButton endButton = new JButton("End");
		endButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop = true; // stop counter thread
				fp.stop(); // stops simulation threads
				frame.dispose();
				System.exit(0);
			}
		});

		t.add(timerDisplay);

		b.add(Box.createHorizontalGlue());
		b.add(resetButton);
		b.add(Box.createHorizontalGlue());
		b.add(pauseButton);
		b.add(Box.createHorizontalGlue());
		b.add(playButton);
		b.add(Box.createHorizontalGlue());
		b.add(endButton);
		b.add(Box.createHorizontalGlue());

		g.add(t);
		g.add(b);

		frame.setSize(frameX, frameY + 80); // a little extra space at the bottom for buttons
		frame.setLocationRelativeTo(null); // center window on screen
		frame.add(g); // add contents to window
		frame.setContentPane(g);
		frame.setVisible(true);
		Thread fpt = new Thread(fp);
		fpt.start();
	}

	/**
	 * Mouse listener waiting for click.
	 */
	static class customizedMouseListener implements MouseListener {
		public FlowPanel flowp;

		customizedMouseListener(FlowPanel flowp) {
			this.flowp = flowp;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			flowp.water.plotWater(e.getX(), e.getY());
			this.flowp.drawWater();
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	/**
	 * Main method. Controls program loop.
	 * 
	 * @param args filepath to input data.
	 */
	public static void main(String[] args) {
		Terrain landdata = new Terrain();

		// check that number of command line arguments is correct

		if (args.length != 1) {
			System.out.println(
					"Incorrect number of command line arguments. Should have form: java -jar flow.java intputfilename");
			System.exit(0);
		}

		// landscape information from file supplied as argument

		landdata.readData(args[0]);

		Water water = new Water(landdata.getDimX(), landdata.getDimY(), landdata);

		frameX = landdata.getDimX();
		frameY = landdata.getDimY();
		SwingUtilities.invokeLater(() -> setupGUI(frameX, frameY, landdata, water));
	}
}
