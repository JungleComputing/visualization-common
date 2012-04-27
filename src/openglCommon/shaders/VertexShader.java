package openglCommon.shaders;

import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.GL3;

import openglCommon.exceptions.CompilationFailedException;

public class VertexShader extends Shader {
    public VertexShader(String shaderName, File file) throws FileNotFoundException {
        super(shaderName, file);
    }

    public VertexShader(String shaderName, String shaderCode) throws FileNotFoundException {
        super(shaderName, shaderCode);
    }

    public void init(GL3 gl) throws CompilationFailedException {
        shader = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
        super.init(gl);
    }
}
