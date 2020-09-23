import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents water on the terrain. Water depth at each terrain grid position is
 * encoded as an integer.
 **/
public class Water {

    public static final float depth = 0.01f; // water unit depth (0.01m)
    public static float addedWater = 0.00f;
    public static float waterDrain = 0.00f;

    public AtomicInteger[][] waterValues; // have a grid;
    public float[][] surfaceLayer;

    public int dimX; // dimensions of the data
    public int dimY; // dimensions of the data
    public static BufferedImage img; // to display the terrain top-down. greyscale;

    /**
     * Water Constructor.
     * 
     * @param dimX     x-dimensions of the water
     * @param dimY     y-dimensions of the water
     * @param landdata terrain grid.
     */
    public Water(int dimX, int dimY, Terrain landdata) {
        this.dimX = dimX;
        this.dimY = dimY;
        createImage();
        createSurfaceLayer(landdata);
    }

    /**
     * @Returns the data dimensions of the water
     **/
    public int dim() {
        return dimX * dimY;
    }

    /**
     * @Returns the x-dimensions of the water
     **/
    public int getDimX() {
        return dimX;
    }

    /**
     * @Returns the y-dimensions of the water
     **/
    public int getDimY() {
        return dimY;
    }

    /**
     * @Returns blue water image
     **/
    public BufferedImage getImage() {
        return img;
    }

    /**
     * Returns the total water level in meters.
     * 
     * @return water level in meters.
     */
    public float getWaterVolume() {
        float waterVolume = 0.00f;
        for (int x = 0; x < dimX - 1; x++) {
            for (int y = 0; y < dimY - 1; y++) {
                waterVolume += waterValues[x][y].get() * depth;
            }
        }
        return waterVolume;

    }

    /**
     * Creates the image used to represent water particles.
     */
    public void createImage() {
        img = new BufferedImage(dimY, dimX, BufferedImage.TYPE_INT_ARGB);
        this.waterValues = new AtomicInteger[dimX][dimY];
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                AtomicInteger s = new AtomicInteger();
                this.waterValues[x][y] = s;
            }
        }
    }

    /**
     * Creates the overlay of the terrain.
     * 
     * @param terrain Terrain object representing the grid.
     */
    public void createSurfaceLayer(Terrain terrain) {
        this.surfaceLayer = new float[dimX][dimY];
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                this.surfaceLayer[x][y] = terrain.height[x][y] + this.waterValues[x][y].get() * depth;
            }
        }
    }

    /**
     * Checks if a cell is a basin. i.e Surrounding cells have a high surface level.
     * 
     * @param r Row of the selected cell.
     * @param c Column of the selected cell.
     * @return True if cell is a basin. False is not basin.
     */
    public boolean isBasin(int r, int c) {
        float[] peerLayers = { surfaceLayer[r - 1][c - 1], surfaceLayer[r - 1][c], surfaceLayer[r - 1][c + 1],
                surfaceLayer[r][c - 1], surfaceLayer[r][c + 1], surfaceLayer[r + 1][c - 1], surfaceLayer[r + 1][c],
                surfaceLayer[r + 1][c + 1] };

        for (int i = 0; i < 8; i++) {
            if (peerLayers[i] < surfaceLayer[r][c]) {
                return false;
            }
        }
        return true;
    }

    /**
     * convert linear position into 2D location in grid
     * 
     * @param pos
     * @param ind
     */
    public void locate(int pos, int[] ind) {
        ind[0] = (int) pos / dimY; // x
        ind[1] = pos % dimX; // y
    }

    /**
     * Checks if cell is on the boundary.
     * 
     * @param x x-position
     * @param y y-position
     * @return True if cell is on the boundary. False if not on boundary.
     */
    public boolean isBoundary(int x, int y) {
        if (x == 0 || x == dimX - 1 || y == 0 || y == dimY - 1) {
            return true;
        }

        return false;
    }

    /**
     * Runs checks before moving water to next cell.
     * 
     * @param x x-position
     * @param y y-position
     */
    public void riverFlowPrep(int x, int y) {
        if (isBoundary(x, y)) {
            Water.waterDrain += this.waterValues[x][y].get() * depth;
            this.waterValues[x][y].set(0);
        }

        if (!isBoundary(x, y) && !isBasin(x, y) && this.waterValues[x][y].get() != 0) {
            int r = x;
            int c = y;
            int min = 0;

            float[] peerLayers = { this.surfaceLayer[r - 1][c - 1], this.surfaceLayer[r - 1][c],
                    this.surfaceLayer[r - 1][c + 1], this.surfaceLayer[r][c + 1], this.surfaceLayer[r][c - 1],
                    this.surfaceLayer[r + 1][c + 1], this.surfaceLayer[r + 1][c], this.surfaceLayer[r + 1][c - 1] };

            for (int i = 1; i < 8; i++) {
                if (peerLayers[i] - surfaceLayer[x][y] + depth < peerLayers[min] - surfaceLayer[x][y]) {
                    min = i;
                }

            }

            switch (min) {
                case 0:
                    riverFlow(x, y, r - 1, c - 1);
                case 1:
                    riverFlow(x, y, r - 1, c);
                case 2:
                    riverFlow(x, y, r - 1, c + 1);
                case 3:
                    riverFlow(x, y, r, c + 1);
                case 4:
                    riverFlow(x, y, r, c - 1);
                case 5:
                    riverFlow(x, y, r + 1, c + 1);
                case 6:
                    riverFlow(x, y, r + 1, c);
                case 7:
                    riverFlow(x, y, r + 1, c - 1);
                default:
                    riverFlow(x, y, r, c);
            }
        }
    }

    /**
     * Synchronized method that transfers water to next cell.
     * 
     * @param x1 x-position of current cell.
     * @param y1 y-position of current cell.
     * @param x2 x-position of target cell.
     * @param y2 y-position of target cell.
     */
    private synchronized void riverFlow(int x1, int y1, int x2, int y2) {
        if (this.waterValues[x1][y1].get() > 0) {
            this.waterValues[x1][y1].set(this.waterValues[x1][y1].get() - 1);
            this.waterValues[x2][y2].set(this.waterValues[x2][y2].get() + 1);
            this.surfaceLayer[x1][y1] = this.surfaceLayer[x1][y1] - depth;
            this.surfaceLayer[x2][y2] = this.surfaceLayer[x2][y2] + depth;

            Color colour = new Color(0, 0, 153);
            this.img.setRGB(x2, y2, colour.getRGB());
            if (this.waterValues[x1][y1].get() == 0) {
                this.img.setRGB(x1, y1, 0);
            }
        }
    }

    /**
     * Draws a block of water on specified cell.
     * 
     * @param x x-position of cell.
     * @param y y-position of cell.
     */
    public void plotWater(int x, int y) {
        int size = 4;
        for (int i = (x - size); i < (x + size); i++) {
            for (int k = (y - size); k < (y + size); k++) {
                Color colour = new Color(0, 0, 153);
                this.img.setRGB(i, k, colour.getRGB());
                this.waterValues[i][k].set(this.waterValues[i][k].get() + size);
                Water.addedWater += size * depth;
            }
        }
    }
}