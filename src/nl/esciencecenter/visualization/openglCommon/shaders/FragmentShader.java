package nl.esciencecenter.visualization.openglCommon.shaders;

import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.GL3;

import nl.esciencecenter.visualization.openglCommon.exceptions.CompilationFailedException;


public class FragmentShader extends Shader {
    public FragmentShader(String shaderName, File file) throws FileNotFoundException {
        super(shaderName, file);
    }

    public FragmentShader(String shaderName, String shaderCode) throws FileNotFoundException {
        super(shaderName, shaderCode);
    }

    @Override
    public void init(GL3 gl) throws CompilationFailedException {
        shader = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
        super.init(gl);
    }
}
