package openglCommon.models;

import java.nio.FloatBuffer;

import javax.media.opengl.GL3;

import openglCommon.datastructures.GLSLAttrib;
import openglCommon.datastructures.Material;
import openglCommon.datastructures.VBO;
import openglCommon.exceptions.UninitializedException;
import openglCommon.math.MatF4;
import openglCommon.math.MatrixFMath;
import openglCommon.shaders.Program;

public class Model {
    public static enum vertex_format {
        TRIANGLES, POINTS, LINES
    };

    protected vertex_format format;
    protected FloatBuffer   vertices, normals, texCoords;
    protected VBO           vbo;
    protected int           numVertices;

    protected Material      material;
    protected float         scale;

    private boolean         initialized = false;

    public Model(Material material, vertex_format format) {
        vertices = null;
        normals = null;
        texCoords = null;
        numVertices = 0;

        this.material = material;
        this.format = format;
        this.scale = 1f;
    }

    public void init(GL3 gl) {
        if (!initialized) {
            GLSLAttrib vAttrib = new GLSLAttrib(vertices, "MCvertex", GLSLAttrib.SIZE_FLOAT, 4);
            GLSLAttrib nAttrib = new GLSLAttrib(normals, "MCnormal", GLSLAttrib.SIZE_FLOAT, 3);
            GLSLAttrib tAttrib = new GLSLAttrib(texCoords, "MCtexCoord", GLSLAttrib.SIZE_FLOAT, 3);

            vbo = new VBO(gl, vAttrib, nAttrib, tAttrib);
        }
        initialized = true;
    }

    public void delete(GL3 gl) {
        vertices = null;
        normals = null;
        texCoords = null;

        if (initialized) {
            vbo.delete(gl);
        }
    }

    public void setScale(float newScale) {
        this.scale = newScale;
    }

    public VBO getVBO() {
        return vbo;
    }

    public int getNumVertices() {
        return numVertices;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material newMaterial) {
        material = newMaterial;
    }

    public void draw(GL3 gl, Program program, MatF4 MVMatrix) {
        program.setUniformVector("DiffuseMaterial", material.diffuse);
        program.setUniformVector("AmbientMaterial", material.ambient);
        program.setUniformVector("SpecularMaterial", material.specular);

        program.setUniformMatrix("MVMatrix", MVMatrix.mul(MatrixFMath.scale(scale)));

        try {
            program.use(gl);
        } catch (UninitializedException e) {
            e.printStackTrace();
        }

        vbo.bind(gl);

        program.linkAttribs(gl, vbo.getAttribs());

        if (format == vertex_format.TRIANGLES) {
            gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVertices);
        } else if (format == vertex_format.POINTS) {
            gl.glDrawArrays(GL3.GL_POINTS, 0, numVertices);
        } else if (format == vertex_format.LINES) {
            gl.glDrawArrays(GL3.GL_LINES, 0, numVertices);
        }
    }
}
