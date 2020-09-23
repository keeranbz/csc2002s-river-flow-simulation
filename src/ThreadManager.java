import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class containing processes that will make use of multithreading.
 */
public class ThreadManager extends java.lang.Thread {
    public Water water;
    public Terrain terrain;
    public int start;
    public int stop;

    /**
     * Constructor.
     * 
     * @param start   Lower bound of permuted array.
     * @param stop    Upper bound of permuted array.
     * @param terrain Terrain grid.
     * @param water   Water object.
     */
    public ThreadManager(int start, int stop, Terrain terrain, Water water) {
        this.start = start;
        this.stop = stop;
        this.terrain = terrain;
        this.water = water;
    }

    /**
     * This code is run when the multithreading has started.
     */
    public void run() {
        for (int i = start; i < stop; i++) {
            int[] positions = new int[2];
            terrain.getPermute(terrain.permute.get(i), positions); // positions[] is populated
            this.water.riverFlowPrep(positions[0], positions[1]);
        }
    }
}