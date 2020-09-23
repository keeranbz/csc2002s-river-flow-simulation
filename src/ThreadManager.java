/**
 * Class containing processes that will make use of multithreading.
 */
public class ThreadManager extends java.lang.Thread {
    public Water water;
    public Terrain terrain;
    public int a;
    public int b;

    /**
     * Constructor.
     * 
     * @param a       Lower bound of permuted array.
     * @param b       Upper bound of permuted array.
     * @param terrain Terrain grid.
     * @param water   Water object.
     */
    public ThreadManager(int a, int b, Terrain terrain, Water water) {
        this.a = a;
        this.b = b;
        this.terrain = terrain;
        this.water = water;
    }

    /**
     * This code is run when the multithreading has started.
     */
    public void run() {
        for (int i = a; i < b; i++) {
            int[] positions = new int[2];
            terrain.getPermute(terrain.permute.get(i), positions); // positions[] is populated
            this.water.riverFlowPrep(positions[0], positions[1]);
        }
    }
}