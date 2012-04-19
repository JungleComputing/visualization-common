package openglCommon.shaders;

import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.GL3;

import openglCommon.exceptions.CompilationFailedException;

public class VertexShader extends Shader {
    public VertexShader(File file) throws FileNotFoundException {
        super(file);
    }

    public VertexShader(String shaderCode) throws FileNotFoundException {
        super(shaderCode);
    }

    public void init(GL3 gl) throws CompilationFailedException {
        shader = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
        super.init(gl);
    }
}
