import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadFlow extends java.lang.Thread {
    public Water water;
    public Terrain land;
    public int start;
    public int stop;


    public ThreadFlow(int start, int stop, Terrain land, Water water) {
        this.start = start;
        this.stop = stop;
        this.land = land;
        this.water = water;
    }

    public void run() {
        for(int i = start; i < stop ; i++){
            int[] locations = new int[2];
            land.getPermute(land.permute.get(i), locations);
            this.water.waterFlowPrep(locations[0],locations[1]);
        }
    }
}