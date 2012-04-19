package openglCommon.shaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.IntBuffer;
import java.util.Scanner;

import javax.media.opengl.GL3;

import openglCommon.exceptions.CompilationFailedException;
import openglCommon.exceptions.UninitializedException;

import com.jogamp.common.nio.Buffers;

public abstract class Shader {
    String   filename;

    String[] source;
    int      shader = -1; // This value is set by creating either a vertex or
                          // fragment shader.

    public Shader(File file) throws FileNotFoundException {
        this.filename = file.getName();

        // Read file
        StringBuffer buf = new StringBuffer();
        Scanner scan;
        scan = new Scanner(file);

        while (scan.hasNext()) {
            buf.append(scan.nextLine());
            buf.append("\n");
        }

        source = new String[] { buf.toString() };
    }

    public Shader(String shaderCode) {
        this.filename = "";

        StringBuffer buf = new StringBuffer();
        Scanner scan = new Scanner(shaderCode);

        while (scan.hasNext()) {
            buf.append(scan.nextLine());
            buf.append("\n");
        }

        source = new String[] { buf.toString() };
    }

    public void init(GL3 gl) throws CompilationFailedException {
        gl.glShaderSource(shader, 1, source, (int[]) null, 0);
        gl.glCompileShader(shader);

        IntBuffer buf = Buffers.newDirectIntBuffer(1);
        gl.glGetShaderiv(shader, GL3.GL_COMPILE_STATUS, buf);
        int status = buf.get(0);
        if (status == GL3.GL_FALSE) {
            gl.glGetShaderiv(shader, GL3.GL_INFO_LOG_LENGTH, buf);
            int logLength = buf.get(0);
            byte[] reason = new byte[logLength];

            // if (logLength != 0) {
            gl.glGetShaderInfoLog(shader, logLength, null, 0, reason, 0);
            throw new CompilationFailedException("Compilation of " + filename + " failed, " + new String(reason));
            // } else {
            // throw new CompilationFailedException("Compilation of " + filename
            // + " failed, No reason could be extracted.");
            // }
        }
    }

    public int getShader() throws UninitializedException {
        if (shader == -1)
            throw new UninitializedException();
        return shader;
    }
}
