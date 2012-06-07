package openglCommon.models;

import java.util.ArrayList;
import java.util.List;

import openglCommon.datastructures.Material;
import openglCommon.math.VecF3;
import openglCommon.math.VecF4;
import openglCommon.math.VectorFMath;
import openglCommon.util.Settings;

public class GeoSphere extends Model {
    Settings settings = Settings.getInstance();

    private boolean texCoordsIn3D = false;

    public GeoSphere(Material material, int latribs, int longribs, float radius, boolean texCoordsIn3D) {
        super(material, vertex_format.TRIANGLES);
        this.texCoordsIn3D = texCoordsIn3D;

        List<VecF4> points4List = new ArrayList<VecF4>();
        List<VecF3> normals3List = new ArrayList<VecF3>();
        List<VecF3> tCoords3List = new ArrayList<VecF3>();

        for (int i = 0; i < 20; i++) {
            makeVertices(points4List, normals3List, tCoords3List, latribs, longribs, radius);
        }

        numVertices = points4List.size();

        vertices = VectorFMath.vec4ListToBuffer(points4List);
        normals = VectorFMath.vec3ListToBuffer(normals3List);
        texCoords = VectorFMath.vec3ListToBuffer(tCoords3List);
    }

    private void makeVertices(List<VecF4> pointsList, List<VecF3> normalsList, List<VecF3> tCoords3List, int latribs,
            int lonribs, float radius) {

        float lonAngle = (float) ((2 * Math.PI) / lonribs);
        float latAngle = (float) ((Math.PI) / latribs);
        for (int lon = 0; lon < lonribs; lon++) {
            float startLonAngle = lonAngle * lon;
            float stopLonAngle = lonAngle * (lon + 1);

            for (int lat = 0; lat < latribs; lat++) {
                float flon = lon;
                float flat = lat;
                float flonribs = lonribs;
                float flatribs = latribs;

                float startLatAngle = latAngle * lat;
                float stopLatAngle = latAngle * (lat + 1);

                float x00 = (float) (Math.sin(startLatAngle) * Math.cos(startLonAngle));
                float x10 = (float) (Math.sin(stopLatAngle) * Math.cos(startLonAngle));
                float x01 = (float) (Math.sin(startLatAngle) * Math.cos(stopLonAngle));
                float x11 = (float) (Math.sin(stopLatAngle) * Math.cos(stopLonAngle));

                float y00 = (float) (Math.cos(startLatAngle));
                float y10 = (float) (Math.cos(stopLatAngle));
                float y01 = (float) (Math.cos(startLatAngle));
                float y11 = (float) (Math.cos(stopLatAngle));

                float z00 = (float) (Math.sin(startLatAngle) * Math.sin(startLonAngle));
                float z10 = (float) (Math.sin(stopLatAngle) * Math.sin(startLonAngle));
                float z01 = (float) (Math.sin(startLatAngle) * Math.sin(stopLonAngle));
                float z11 = (float) (Math.sin(stopLatAngle) * Math.sin(stopLonAngle));

                pointsList.add((new VecF4(x00, y00, z00, 1)).mul(radius));
                pointsList.add((new VecF4(x01, y01, z01, 1)).mul(radius));
                pointsList.add((new VecF4(x11, y11, z11, 1)).mul(radius));

                normalsList.add(VectorFMath.normalize(new VecF3(x00, y00, z00)));
                normalsList.add(VectorFMath.normalize(new VecF3(x01, y01, z01)));
                normalsList.add(VectorFMath.normalize(new VecF3(x11, y11, z11)));

                if (texCoordsIn3D) {
                    tCoords3List.add(VectorFMath.normalize(new VecF3((x00 * radius / 2 * radius),
                            (y00 * radius / 2 * radius), (z00 * radius / 2 * radius))));
                    tCoords3List.add(VectorFMath.normalize(new VecF3((x01 * radius / 2 * radius),
                            (y01 * radius / 2 * radius), (z01 * radius / 2 * radius))));
                    tCoords3List.add(VectorFMath.normalize(new VecF3((x11 * radius / 2 * radius),
                            (y11 * radius / 2 * radius), (z11 * radius / 2 * radius))));
                } else {
                    tCoords3List.add(new VecF3(1 - (flon / flonribs), 1 - (flat / flatribs), 0));
                    tCoords3List.add(new VecF3(1 - ((flon + 1) / flonribs), 1 - (flat / flatribs), 0));
                    tCoords3List.add(new VecF3(1 - ((flon + 1) / flonribs), 1 - ((flat + 1) / flatribs), 0));
                }

                pointsList.add((new VecF4(x00, y00, z00, 1)).mul(radius));
                pointsList.add((new VecF4(x11, y11, z11, 1)).mul(radius));
                pointsList.add((new VecF4(x10, y10, z10, 1)).mul(radius));

                normalsList.add(VectorFMath.normalize(new VecF3(x00, y00, z00)));
                normalsList.add(VectorFMath.normalize(new VecF3(x11, y11, z11)));
                normalsList.add(VectorFMath.normalize(new VecF3(x10, y10, z10)));

                if (texCoordsIn3D) {
                    tCoords3List.add(VectorFMath.normalize(new VecF3((x00 * radius / 2 * radius),
                            (y00 * radius / 2 * radius), (z00 * radius / 2 * radius))));
                    tCoords3List.add(VectorFMath.normalize(new VecF3((x11 * radius / 2 * radius),
                            (y11 * radius / 2 * radius), (z11 * radius / 2 * radius))));
                    tCoords3List.add(VectorFMath.normalize(new VecF3((x10 * radius / 2 * radius),
                            (y10 * radius / 2 * radius), (z10 * radius / 2 * radius))));
                } else {
                    tCoords3List.add(new VecF3(1 - (flon / flonribs), 1 - ((flat / flatribs)), 0));
                    tCoords3List.add(new VecF3(1 - ((flon + 1) / flonribs), 1 - ((flat + 1) / flatribs), 0));
                    tCoords3List.add(new VecF3(1 - (flon / flonribs), 1 - ((flat + 1) / flatribs), 0));
                }
            }
        }
    }
}
