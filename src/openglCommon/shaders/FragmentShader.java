package openglCommon.shaders;

import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.GL3;

import openglCommon.exceptions.CompilationFailedException;

public class FragmentShader extends Shader {
    public FragmentShader(File file) throws FileNotFoundException {
        super(file);
    }

    public FragmentShader(String shaderCode) throws FileNotFoundException {
        super(shaderCode);
    }

    @Override
    public void init(GL3 gl) throws CompilationFailedException {
        shader = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
        super.init(gl);
    }
}
