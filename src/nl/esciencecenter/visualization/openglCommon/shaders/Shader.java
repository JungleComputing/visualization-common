package nl.esciencecenter.visualization.openglCommon.shaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Scanner;

import javax.media.opengl.GL3;

import nl.esciencecenter.visualization.openglCommon.exceptions.CompilationFailedException;
import nl.esciencecenter.visualization.openglCommon.exceptions.UninitializedException;


import com.jogamp.common.nio.Buffers;

public abstract class Shader {
    String   shaderName;
    String   filename;

    String[] source;
    @SuppressWarnings("rawtypes")
    HashMap<String, Class> ins, outs, uniforms;

    int                    shader = -1;        // This value is set by
                                                // creating
                                                // either a
                                                // vertex or
                                                // fragment shader.

    @SuppressWarnings("rawtypes")
    public Shader(String shaderName, File file) throws FileNotFoundException {
        this.shaderName = shaderName;
        this.filename = file.getName();

        // Read file
        StringBuffer buf = new StringBuffer();
        Scanner scan;
        scan = new Scanner(file);

        ins = new HashMap<String, Class>();
        outs = new HashMap<String, Class>();
        uniforms = new HashMap<String, Class>();

        // System.out.println(file.getName());
        while (scan.hasNext()) {
            String line = scan.nextLine();
            buf.append(line + "\n");

            parseInputs(line);
        }
        // System.out.println();

        source = new String[] { buf.toString() };
    }

    @SuppressWarnings("rawtypes")
    public Shader(String shaderName, String shaderCode) {
        this.shaderName = shaderName;
        this.filename = "";

        StringBuffer buf = new StringBuffer();
        Scanner scan = new Scanner(shaderCode);

        ins = new HashMap<String, Class>();
        outs = new HashMap<String, Class>();
        uniforms = new HashMap<String, Class>();

        // System.out.println("code source");
        while (scan.hasNext()) {
            String line = scan.nextLine();
            buf.append(line + "\n");

            parseInputs(line);
        }
        // System.out.println();

        source = new String[] { buf.toString() };
    }

    private void parseInputs(String line) {
        String[] trimmedLine = line.trim().split(";");
        String[] words = trimmedLine[0].split("[\\s,;]+");
        if (words[0].compareTo("in") == 0) {
            // String type = words[1];
            for (int i = 2; i < words.length; i++) {
                // System.out.println("in : " + type + " : " + words[i]);

                @SuppressWarnings("rawtypes")
                Class clazz = null;
                if (words[1].compareTo("vec2") == 0) {
                    clazz = FloatBuffer.class;
                } else if (words[1].compareTo("vec3") == 0) {
                    clazz = FloatBuffer.class;
                } else if (words[1].compareTo("vec4") == 0) {
                    clazz = FloatBuffer.class;
                } else if (words[1].compareTo("float") == 0) {
                    clazz = Float.class;
                } else if (words[1].compareTo("sampler2D") == 0) {
                    clazz = Integer.class;
                } else if (words[1].compareTo("sampler3D") == 0) {
                    clazz = Integer.class;
                } else if (words[1].compareTo("int") == 0) {
                    clazz = Integer.class;
                } else if (words[1].compareTo("mat3") == 0) {
                    clazz = FloatBuffer.class;
                } else if (words[1].compareTo("mat4") == 0) {
                    clazz = FloatBuffer.class;
                }
                ins.put(words[i], clazz);
            }
        } else if (words[0].compareTo("out") == 0) {
            // String type = words[1];
            for (int i = 2; i < words.length; i++) {
                // System.out.println("in : " + type + " : " + words[i]);

                @SuppressWarnings("rawtypes")
                Class clazz = null;
                if (words[1].compareTo("vec2") == 0) {
                    clazz = FloatBuffer.class;
                } else if (words[1].compareTo("vec3") == 0) {
                    clazz = FloatBuffer.class;
                } else if (words[1].compareTo("vec4") == 0) {
                    clazz = FloatBuffer.class;
                } else if (words[1].compareTo("float") == 0) {
                    clazz = Float.class;
                } else if (words[1].compareTo("int") == 0) {
                    clazz = Integer.class;
                } else if (words[1].compareTo("sampler2D") == 0) {
                    clazz = Integer.class;
                } else if (words[1].compareTo("sampler3D") == 0) {
                    clazz = Integer.class;
                } else if (words[1].compareTo("mat3") == 0) {
                    clazz = FloatBuffer.class;
                } else if (words[1].compareTo("mat4") == 0) {
                    clazz = FloatBuffer.class;
                }
                outs.put(words[i], clazz);
            }
        } else if (words[0].compareTo("uniform") == 0) {
            // String type = words[1];
            for (int i = 2; i < words.length; i++) {
                // System.out.println("uniform : " + type + " : " + words[i]);

                @SuppressWarnings("rawtypes")
                Class clazz = null;
                if (words[1].compareTo("vec2") == 0) {
                    clazz = FloatBuffer.class;
                } else if (words[1].compareTo("vec3") == 0) {
                    clazz = FloatBuffer.class;
                } else if (words[1].compareTo("vec4") == 0) {
                    clazz = FloatBuffer.class;
                } else if (words[1].compareTo("float") == 0) {
                    clazz = Float.class;
                } else if (words[1].compareTo("int") == 0) {
                    clazz = Integer.class;
                } else if (words[1].compareTo("sampler2D") == 0) {
                    clazz = Integer.class;
                } else if (words[1].compareTo("sampler3D") == 0) {
                    clazz = Integer.class;
                } else if (words[1].compareTo("mat3") == 0) {
                    clazz = FloatBuffer.class;
                } else if (words[1].compareTo("mat4") == 0) {
                    clazz = FloatBuffer.class;
                }
                uniforms.put(words[i], clazz);
            }
        }
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

    @SuppressWarnings("rawtypes")
    public HashMap<String, Class> getIns() {
        return ins;
    }

    @SuppressWarnings("rawtypes")
    public HashMap<String, Class> getOuts() {
        return outs;
    }

    @SuppressWarnings("rawtypes")
    public HashMap<String, Class> getUniforms() {
        return uniforms;
    }

    public String getName() {
        return shaderName;
    }
}
