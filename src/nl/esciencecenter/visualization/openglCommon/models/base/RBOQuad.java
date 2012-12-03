package nl.esciencecenter.visualization.openglCommon.models.base;

import javax.media.opengl.GL3;

import nl.esciencecenter.visualization.openglCommon.datastructures.Material;
import nl.esciencecenter.visualization.openglCommon.exceptions.UninitializedException;
import nl.esciencecenter.visualization.openglCommon.math.MatF4;
import nl.esciencecenter.visualization.openglCommon.math.VecF2;
import nl.esciencecenter.visualization.openglCommon.math.VecF3;
import nl.esciencecenter.visualization.openglCommon.math.VecF4;
import nl.esciencecenter.visualization.openglCommon.math.VectorFMath;
import nl.esciencecenter.visualization.openglCommon.models.Model;
import nl.esciencecenter.visualization.openglCommon.shaders.Program;


public class RBOQuad extends Model {
    float width;
    float height;

    public RBOQuad(Material material, float width, float height, VecF3 center) {
        super(material, vertex_format.TRIANGLES);

        makeQuad(material, width, height, center);
    }

    private void makeQuad(Material material, float width, float height, VecF3 center) {
        this.width = width;
        this.height = height;

        int numVertices = 6;

        // VERTICES
        float dx = 0.5f * width;
        float dy = 0.5f * height;

        float lX = center.get(0) - dx;
        float hX = center.get(0) + dx;
        float lY = center.get(1) - dy;
        float hY = center.get(1) + dy;
        float Z = center.get(2);

        // System.out.println("BBox");
        // System.out.println(new VecF3(lX, lY, Z));
        // System.out.println(new VecF3(hX, lY, Z));
        // System.out.println(new VecF3(hX, hY, Z));
        // System.out.println(new VecF3(lX, lY, Z));
        // System.out.println(new VecF3(hX, hY, Z));
        // System.out.println(new VecF3(lX, hY, Z));

        VecF4[] verticesArray = new VecF4[numVertices];
        verticesArray[0] = new VecF4(lX, lY, Z, 1f);
        verticesArray[1] = new VecF4(hX, lY, Z, 1f);
        verticesArray[2] = new VecF4(hX, hY, Z, 1f);
        verticesArray[3] = new VecF4(lX, lY, Z, 1f);
        verticesArray[4] = new VecF4(hX, hY, Z, 1f);
        verticesArray[5] = new VecF4(lX, hY, Z, 1f);

        // NORMALS
        VecF3[] normalsArray = new VecF3[numVertices];
        for (int i = 0; i < 6; i++) {
            normalsArray[i] = new VecF3(0, 0, -1);
        }

        // TEXTURE COORDINATES
        VecF2[] texCoordsArray = new VecF2[numVertices];
        texCoordsArray[0] = new VecF2(5f, 5f);
        texCoordsArray[1] = new VecF2(6f, 5f);
        texCoordsArray[2] = new VecF2(6f, 6f);
        texCoordsArray[3] = new VecF2(5f, 5f);
        texCoordsArray[4] = new VecF2(6f, 6f);
        texCoordsArray[5] = new VecF2(5f, 6f);

        this.numVertices = numVertices;
        this.vertices = VectorFMath.toBuffer(verticesArray);
        this.normals = VectorFMath.toBuffer(normalsArray);
        this.texCoords = VectorFMath.toBuffer(texCoordsArray);
    }

    @Override
    public void draw(GL3 gl, Program program, MatF4 PMVMatrix) {
        program.setUniformMatrix("PMVMatrix", PMVMatrix);

        try {
            program.use(gl);
        } catch (UninitializedException e) {
            e.printStackTrace();
        }

        vbo.bind(gl);

        program.linkAttribs(gl, vbo.getAttribs());

        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVertices);
    }
}
