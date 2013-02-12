package nl.esciencecenter.visualization.openglCommon.textures;

public class Perlin3D extends Texture3D {
    public int size;

    public Perlin3D(int gLMultiTexUnit, int width, int height, int depth) {
        super(gLMultiTexUnit);

        this.width = width;
        this.height = height;
        this.depth = depth;

        makePerlin3d(width, height, depth);
    }

    private void makePerlin3d(int width, int height, int depth) {
        System.out.print("Generating noise");
        Noise n = new Noise(4, width, height, depth);
        System.out.println("done");

        pixelBuffer = n.pixelBuffer;
    }
}
