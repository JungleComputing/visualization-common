package nl.esciencecenter.visualization.openglCommon.textures;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL3;

import nl.esciencecenter.visualization.openglCommon.exceptions.UninitializedException;

import com.jogamp.common.nio.Buffers;

public class Texture3D extends Texture {
    protected IntBuffer  pointer;
    protected ByteBuffer pixelBuffer;

    protected int        width, height, depth;

    protected boolean    initialized = false;

    public Texture3D(int glMultiTexUnit) {
        super(glMultiTexUnit);
        pointer = null;
    }

    public void init(GL3 gl) {
        if (!initialized) {
            if (pixelBuffer == null || width == 0 || height == 0 || depth == 0) {
                System.err
                        .println("Add a pixelbuffer and w/h/d first, by using a custom constructor. The Texture3D constructor is only meant to be extended.");
            }

            gl.glActiveTexture(glMultiTexUnit);
            gl.glEnable(GL3.GL_TEXTURE_3D);

            // Create new texture pointer and bind it so we can manipulate it.

            pointer = Buffers.newDirectIntBuffer(1);
            gl.glGenTextures(1, pointer);

            gl.glBindTexture(GL3.GL_TEXTURE_3D, pointer.get(0));

            // Wrap.
            gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_WRAP_S,
                    GL3.GL_MIRRORED_REPEAT);
            gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_WRAP_T,
                    GL3.GL_MIRRORED_REPEAT);
            gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_WRAP_R,
                    GL3.GL_MIRRORED_REPEAT);
            gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_MIN_FILTER,
                    GL3.GL_LINEAR);
            gl.glTexParameteri(GL3.GL_TEXTURE_3D, GL3.GL_TEXTURE_MAG_FILTER,
                    GL3.GL_LINEAR);

            gl.glPixelStorei(GL3.GL_UNPACK_ALIGNMENT, 1);

            gl.glTexImage3D(GL3.GL_TEXTURE_3D, 0, // Mipmap level.
                    GL3.GL_RGBA, // GL.GL_RGBA, // Internal Texel Format,
                    width, height, depth, 0, // Border
                    GL3.GL_RGBA, // External format from image,
                    GL3.GL_BYTE, pixelBuffer // Imagedata as ByteBuffer
            );

            initialized = true;
        }
    }

    public ByteBuffer getPixelBuffer() {
        return pixelBuffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public void delete(GL3 gl) {
        gl.glDeleteTextures(1, pointer);
    }

    public void use(GL3 gl) throws UninitializedException {
        if (!initialized) {
            init(gl);
        }

        gl.glActiveTexture(glMultiTexUnit);
        gl.glEnable(GL3.GL_TEXTURE_3D);
        gl.glBindTexture(GL3.GL_TEXTURE_3D, getPointer());
    }

    public int getPointer() throws UninitializedException {
        if (pointer == null)
            throw new UninitializedException();
        return pointer.get(0);
    }
}
