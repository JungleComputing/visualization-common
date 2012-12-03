package nl.esciencecenter.visualization.openglCommon.shaders;

import java.io.FileNotFoundException;

import javax.media.opengl.GL3;

import nl.esciencecenter.visualization.openglCommon.exceptions.CompilationFailedException;


public class GeometryShader extends Shader {
    public GeometryShader(String shaderName, String filename) throws FileNotFoundException {
        super(shaderName, filename);
    }

    @Override
    public void init(GL3 gl) throws CompilationFailedException {
        shader = gl.glCreateShader(GL3.GL_GEOMETRY_SHADER);
        // gl.glProgramParameteri(shader, GL3.GL_GEOMETRY_INPUT_TYPE,
        // GL3.GL_TRIANGLES);
        // gl.glProgramParameteri(shader, GL3.GL_GEOMETRY_OUTPUT_TYPE,
        // GL3.GL_TRIANGLE_STRIP);
        //
        // IntBuffer n = IntBuffer.allocate(1);
        // gl.glGetIntegerv(GL3.GL_MAX_GEOMETRY_OUTPUT_VERTICES, n);
        // gl.glProgramParameteri(shader, GL3.GL_GEOMETRY_VERTICES_OUT,
        // n.get(0));

        super.init(gl);
    }
}
