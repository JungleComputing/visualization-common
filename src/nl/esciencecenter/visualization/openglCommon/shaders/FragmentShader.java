package nl.esciencecenter.visualization.openglCommon.shaders;

import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.GL3;

import nl.esciencecenter.visualization.openglCommon.exceptions.CompilationFailedException;

/**
 * Fragment shader object representation.
 * These shaders represent the final stage of the rendering process, where every
 * proto-pixel is given a color.
 * 
 * @author Maarten van Meersbergen <m.van.meersbergen@esciencecenter.nl>
 */
public class FragmentShader extends Shader {

    /**
     * Constructor for Fragment shader.
     * 
     * @param shaderName
     *            The library-internal name of this shader.
     * @param file
     *            The file containing the shader source code.
     * @throws FileNotFoundException
     *             If the file was absent.
     */
    public FragmentShader(String shaderName, File file) throws FileNotFoundException {
        super(shaderName, file);
    }

    /**
     * Constructor for Fragment shader.
     * 
     * @param shaderName
     *            The library-internal name of this shader.
     * @param shaderCode
     *            The string containing the code for this shader.
     * @throws FileNotFoundException
     *             If the file was absent.
     */
    public FragmentShader(String shaderName, String shaderCode) throws FileNotFoundException {
        super(shaderName, shaderCode);
    }

    @Override
    public void init(GL3 gl) throws CompilationFailedException {
        shaderPointer = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
        super.init(gl);
    }
}
