import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Represents water on the terrain. 
 * Water depth at each terrain grid position is encoded as an integer. 
 **/
public class Water {

	public static final float depth = 0.01f;                //water unit depth (0.01m)
    public static float addedWater =  0.00f;
    public static float waterDrain =  0.00f;
    
    public AtomicInteger [][] waterValues;                               //have a grid;
    public float [][] surfaceLayer;
    
    public int dimX;                                               //dimensions of the data
    public int dimY;                                               //dimensions of the data
    public static BufferedImage img;                               //to display the terrain top-down. greyscale;


    public Water (int dimX, int dimY, Terrain landdata) {
        this.dimX = dimX;
        this.dimY = dimY;
        createImage();
        createSurfaceLayer(landdata);
    }

    /**
     *   @Returns the data dimensions of the water
     **/
    public int dim(){
        return dimX*dimY;
    }

    /**
     *   @Returns the x-dimensions of the water
     **/
    public int getDimX(){
        return dimX;
    }

    /**
     *   @Returns the y-dimensions of the water
     **/
    public int getDimY(){
        return dimY;
    }

    /**
     *   @Returns blue water image
     **/
    public BufferedImage getImage() {
        return img;
    }

    public float getWaterVolume(){
        float waterVolume = 0.00f;
        for(int x=0; x < dimX-1 ; x++){
            for(int y=0; y < dimY-1; y++){
                waterVolume += waterValues[x][y].get() * depth;
            }
        }
        return waterVolume;

    }

    public void createImage(){
        img = new BufferedImage(dimY, dimX, BufferedImage.TYPE_INT_ARGB);
        this.waterValues = new AtomicInteger[dimX][dimY];
        for (int x=0; x < dimX ; x++){
            for(int y=0; y < dimY; y++){
                AtomicInteger s = new AtomicInteger();
                this.waterValues[x][y] = s;
            }
        }
    }

    public void createSurfaceLayer(Terrain terrain){
        this.surfaceLayer = new float[dimX][dimY];
        for (int x=0; x < dimX; x++){
            for(int y=0; y < dimY; y++){
                this.surfaceLayer[x][y] = terrain.height[x][y] + this.waterValues[x][y].get()*depth;
            }
        }
    }

    
    public boolean isBasin(int r, int c){
        float [] peerLayers = 
        {
            surfaceLayer[r-1][c-1], 
            surfaceLayer[r-1][c], 
            surfaceLayer[r-1][c+1],
            surfaceLayer[r][c-1],
            surfaceLayer[r][c+1],
            surfaceLayer[r+1][c-1], 
            surfaceLayer[r+1][c],
            surfaceLayer[r+1][c+1]
        };

        for (int i=0; i < 8 ; i++){
            if (peerLayers[i] < surfaceLayer[r][c]){
                return false;
            }          
        }
        return true;
    }

    // convert linear position into 2D location in grid
	public void locate(int pos, int [] ind)
	{
		ind[0] = (int) pos / dimY;       // x
		ind[1] = pos % dimX;             // y	
    }
    
    public boolean isBoundary (int x, int y) {
        if(x==0 || x==dimX-1 || y==0 || y==dimY-1){
            return true;
        }

        return false;
    }

    public void waterFlowPrep(int x, int y){
        if(isBoundary(x, y)){
            Water.waterDrain  += this.waterValues[x][y].get() * depth;
            this.waterValues[x][y].set(0);
        }

        
        if (!isBoundary(x,y) && !isBasin(x,y) && this.waterValues[x][y].get() != 0){
            int r = x;
            int c = y;
            int min =0;


            float [] peerLayers = 
            {
                this.surfaceLayer[r-1][c-1], 
                this.surfaceLayer[r-1][c], 
                this.surfaceLayer[r-1][c+1],
                this.surfaceLayer[r][c+1],
                this.surfaceLayer[r][c-1],
                this.surfaceLayer[r+1][c+1],
                this.surfaceLayer[r+1][c], 
                this.surfaceLayer[r+1][c-1]
            };

            for (int i = 1; i <8; i++){
                if (peerLayers[i] - surfaceLayer[x][y] + depth < peerLayers[min] - surfaceLayer[x][y]){
                    min = i;
                }

            }

            switch (min) {
                case 0: waterFlow(x, y, r-1, c-1);
                case 1: waterFlow(x, y, r-1, c);
                case 2: waterFlow(x, y, r-1, c+1);
                case 3: waterFlow(x, y, r, c+1);
                case 4: waterFlow(x, y, r, c-1);
                case 5: waterFlow(x, y, r+1, c+1);
                case 6: waterFlow(x, y, r+1, c);
                case 7: waterFlow(x, y, r+1, c-1);
                default: waterFlow(x, y, r, c);
            }
        }
    }

    private synchronized void waterFlow(int x1,int y1,int x2, int y2){
        if(this.waterValues[x1][y1].get() > 0){
            this.waterValues[x1][y1].set(this.waterValues[x1][y1].get() - 1);
            this.waterValues[x2][y2].set(this.waterValues[x2][y2].get() + 1);
            this.surfaceLayer[x1][y1] = this.surfaceLayer[x1][y1] - depth;            
            this.surfaceLayer[x2][y2] = this.surfaceLayer[x2][y2] + depth;

            Color colour = new Color(0, 0, 153);
            this.img.setRGB(x2, y2, colour.getRGB());
            if(this.waterValues[x1][y1].get() == 0){
                this.img.setRGB(x1,y1,0);
            }
        }
    }

    public void plotWater(int x, int y){
        int size = 4;
        for (int i = (x-size); i < (x+size); i++){
            for(int k = (y-size); k < (y+size); k++){
                Color colour = new Color(0, 0, 153);
                this.img.setRGB(i, k, colour.getRGB());
                this.waterValues[i][k].set(this.waterValues[i][k].get()+size);
                Water.addedWater += size * depth;
            }
        }
    }
}