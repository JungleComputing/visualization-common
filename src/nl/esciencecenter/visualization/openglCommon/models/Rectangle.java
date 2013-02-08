package nl.esciencecenter.visualization.openglCommon.models;

import nl.esciencecenter.visualization.openglCommon.datastructures.Material;
import nl.esciencecenter.visualization.openglCommon.math.Point4;
import nl.esciencecenter.visualization.openglCommon.math.VecF3;
import nl.esciencecenter.visualization.openglCommon.math.VectorFMath;

public class Rectangle extends Model {
    public Rectangle(Material material, float height, float width, float depth, VecF3 center, boolean bottom) {
        super(material, vertex_format.TRIANGLES);
        Point4[] vertices = makeVertices(height, width, depth, center);

        int numVertices;
        if (bottom) {
            numVertices = 36;
        } else {
            numVertices = 30;
        }

        Point4[] points = new Point4[numVertices];
        VecF3[] normals = new VecF3[numVertices];
        VecF3[] tCoords = new VecF3[numVertices];

        int arrayindex = 0;
        for (int i = arrayindex; i < arrayindex + 6; i++) {
            normals[i] = new VecF3(0, 0, -1);
        }
        arrayindex = newQuad(points, arrayindex, vertices, tCoords, 1, 0, 3, 2); // FRONT

        for (int i = arrayindex; i < arrayindex + 6; i++) {
            normals[i] = new VecF3(1, 0, 0);
        }
        arrayindex = newQuad(points, arrayindex, vertices, tCoords, 2, 3, 7, 6); // RIGHT

        if (bottom) {
            for (int i = arrayindex; i < arrayindex + 6; i++) {
                normals[i] = new VecF3(0, -1, 0);
            }
            arrayindex = newQuad(points, arrayindex, vertices, tCoords, 3, 0, 4, 7); // BOTTOM
        }

        for (int i = arrayindex; i < arrayindex + 6; i++) {
            normals[i] = new VecF3(0, 1, 0);
        }
        arrayindex = newQuad(points, arrayindex, vertices, tCoords, 6, 5, 1, 2); // TOP

        for (int i = arrayindex; i < arrayindex + 6; i++) {
            normals[i] = new VecF3(0, 0, 1);
        }
        arrayindex = newQuad(points, arrayindex, vertices, tCoords, 4, 5, 6, 7); // BACK

        for (int i = arrayindex; i < arrayindex + 6; i++) {
            normals[i] = new VecF3(-1, 0, 0);
        }
        arrayindex = newQuad(points, arrayindex, vertices, tCoords, 5, 4, 0, 1); // LEFT

        this.numVertices = numVertices;
        this.vertices = VectorFMath.toBuffer(points);
        this.normals = VectorFMath.toBuffer(normals);
        this.texCoords = VectorFMath.toBuffer(tCoords);
    }

    private Point4[] makeVertices(float height, float width, float depth, VecF3 center) {
        float x = center.get(0);
        float y = center.get(1);
        float z = center.get(2);

        float xpos = x + width / 2f;
        float xneg = x - width / 2f;
        float ypos = y + height / 2f;
        float yneg = y - height / 2f;
        float zpos = z + depth / 2f;
        float zneg = z - depth / 2f;

        Point4[] result = new Point4[] { new Point4(xneg, yneg, zpos, 1.0f), new Point4(xneg, ypos, zpos, 1.0f),
                new Point4(xpos, ypos, zpos, 1.0f), new Point4(xpos, yneg, zpos, 1.0f),
                new Point4(xneg, yneg, zneg, 1.0f), new Point4(xneg, ypos, zneg, 1.0f),
                new Point4(xpos, ypos, zneg, 1.0f), new Point4(xpos, yneg, zneg, 1.0f) };

        return result;
    }

    private int newQuad(Point4[] points, int arrayindex, Point4[] source, VecF3[] tCoords, int a, int b, int c, int d) {
        points[arrayindex] = source[a];
        tCoords[arrayindex] = new VecF3(source[a]);
        arrayindex++;
        points[arrayindex] = source[b];
        tCoords[arrayindex] = new VecF3(source[b]);
        arrayindex++;
        points[arrayindex] = source[c];
        tCoords[arrayindex] = new VecF3(source[c]);
        arrayindex++;
        points[arrayindex] = source[a];
        tCoords[arrayindex] = new VecF3(source[a]);
        arrayindex++;
        points[arrayindex] = source[c];
        tCoords[arrayindex] = new VecF3(source[c]);
        arrayindex++;
        points[arrayindex] = source[d];
        tCoords[arrayindex] = new VecF3(source[d]);
        arrayindex++;

        return arrayindex;
    }
}
