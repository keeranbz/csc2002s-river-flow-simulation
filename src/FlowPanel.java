import java.awt.Graphics;
import javax.swing.JPanel;
import java.util.concurrent.atomic.AtomicBoolean;

public class FlowPanel extends JPanel implements Runnable {
	public Terrain land;
	public Water water;
	public AtomicBoolean play;

	/* Default constructor */
	FlowPanel(Terrain terrain) {
		this.land = terrain;
	}

	/*
	 * Constructor overloading Initalizes play to false, and initializes the terrain
	 * and water
	 * 
	 * @param terrain the land terrain
	 * 
	 * @param water the water running over the terrain
	 */
	FlowPanel(Terrain terrain, Water water) {

		this.land = terrain;
		this.water = water;
		this.play = new AtomicBoolean(false);
		land.genPermute();
	}

	/**
	 * Responsible for painting the terrain and water as images
	 **/
	@Override
	protected void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		super.paintComponent(g);

		// draw the landscape in greyscale as an image
		if (land.getImage() != null) {
			g.drawImage(land.getImage(), 0, 0, null);
			g.drawImage(water.getImage(), 0, 0, null);
		}
	}

	public void drawWater() {
		repaint();
	}

	public void play() {
		this.play.set(true);
	}

	/* Removes the water on the terrain once reset button pressed */
	public void clear() {
		for (int x = 0; x < land.dimx - 1; x++) {
			for (int y = 3; y < land.dimy - 2; y++) {
				this.water.waterValues[x][y].set(0);
				this.water.img.setRGB(x, y, 0);
			}
		}
		// set timer from flow to 0
		// set the flow timer text box to zero
	}

	public void stop() {
		this.play.set(false);
	}

	public void run() {
		boolean run = true;
		int numThreads = 4; // number of threads
		ThreadManager[] threads = new ThreadManager[numThreads];

		while (run) { // while the simulation is running

			if (this.play.get()) {
				for (int i = 0; i < numThreads; i++) {
					int start = (i * (this.land.dimx * this.land.dimy)) / numThreads;
					int stop = ((i + 1) * (this.land.dimx * this.land.dimy)) / numThreads;
					threads[i] = new ThreadManager(start, stop, this.land, this.water);
					threads[i].start();
				}

				for (int t = 0; t < numThreads; t++) {
					try {
						threads[t].join();
						repaint();
						// increment timer

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} else {
				try {
					Thread.sleep(1000);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		// display loop here
		// to do: this should be controlled by the GUI
		// to allow stopping and starting
		repaint();
	}
}