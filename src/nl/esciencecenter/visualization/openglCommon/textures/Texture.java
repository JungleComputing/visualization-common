package nl.esciencecenter.visualization.openglCommon.textures;

import javax.media.opengl.GL;

public class Texture {
    protected int glMultiTexUnit;

    public Texture(int gLMultiTexUnit) {
        this.glMultiTexUnit = gLMultiTexUnit;
    }

    public int getGLMultiTexUnit() {
        return glMultiTexUnit;
    }

    public int getMultitexNumber() {
        int result = glMultiTexUnit - GL.GL_TEXTURE0;

        return result;
    }

    public static int toMultitexNumber(int i) {
        int result = i + GL.GL_TEXTURE0;

        return result;
    }
}
