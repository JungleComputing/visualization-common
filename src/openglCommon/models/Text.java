package openglCommon.models;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL3;

import openglCommon.datastructures.FBO;
import openglCommon.datastructures.GLSLAttrib;
import openglCommon.datastructures.Material;
import openglCommon.datastructures.VBO;
import openglCommon.exceptions.UninitializedException;
import openglCommon.math.MatF4;
import openglCommon.math.MatrixFMath;
import openglCommon.math.VecF2;
import openglCommon.math.VecF3;
import openglCommon.math.VecF4;
import openglCommon.math.VectorFMath;
import openglCommon.models.base.RBOQuad;
import openglCommon.shaders.Program;
import openglCommon.text.GlyphShape;
import openglCommon.text.OutlineShape;
import openglCommon.text.TypecastFont;

import com.jogamp.graph.geom.Triangle;
import com.jogamp.graph.geom.Vertex;
import com.jogamp.graph.geom.opengl.SVertex;

public class Text extends Model {
    private boolean           initialized  = false;

    private String            cachedString = "";

    private final BoundingBox bbox;
    private FloatBuffer       tCoords;

    private FBO               fbo;

    private RBOQuad           quad;

    public Text(Material material) {
        super(material, vertex_format.TRIANGLES);

        int numVertices = 0;

        VecF4[] points = new VecF4[numVertices];
        VecF2[] texCoords = new VecF2[numVertices];

        this.numVertices = numVertices;

        this.vertices = VectorFMath.toBuffer(points);

        this.bbox = new BoundingBox();
        this.tCoords = VectorFMath.toBuffer(texCoords);
    }

    public void setString(GL3 gl, Program program, TypecastFont font, String str, boolean[] mask, int fontSize) {
        if (str.compareTo(cachedString) != 0) {
            // Get the outline shapes for the current string in this font
            ArrayList<OutlineShape> shapes = font.getOutlineShapes(str, fontSize, SVertex.factory());

            // Make a set of glyph shapes from the outlines
            int numGlyps = shapes.size();
            ArrayList<GlyphShape> glyphs = new ArrayList<GlyphShape>();

            for (int index = 0; index < numGlyps; index++) {
                if (mask[index]) {
                    if (shapes.get(index) == null) {
                        continue;
                    }
                    GlyphShape glyphShape = new GlyphShape(SVertex.factory(), shapes.get(index));

                    if (glyphShape.getNumVertices() < 3) {
                        continue;
                    }
                    glyphs.add(glyphShape);
                }
            }

            // Create list of vertices based on the glyph shapes
            ArrayList<Vertex> vertices = new ArrayList<Vertex>();
            for (int i = 0; i < glyphs.size(); i++) {
                GlyphShape glyph = glyphs.get(i);

                ArrayList<Triangle> gtris = glyph.triangulate();
                for (Triangle t : gtris) {
                    vertices.add(t.getVertices()[0]);
                    vertices.add(t.getVertices()[1]);
                    vertices.add(t.getVertices()[2]);
                }
            }

            // Transform the vertices from Vertex objects to Vec4 objects and
            // update BoundingBox.
            VecF4[] myVertices = new VecF4[vertices.size()];
            VecF2[] myTexCoords = new VecF2[vertices.size()];
            int i = 0;
            for (Vertex v : vertices) {
                VecF3 vec = new VecF3(v.getX(), v.getY(), v.getZ());
                bbox.resize(vec);

                myVertices[i] = new VecF4(vec, 1f);
                myTexCoords[i] = new VecF2(v.getTexCoord()[0], v.getTexCoord()[1]);

                i++;
            }

            vbo.delete(gl);
            this.vertices = VectorFMath.toBuffer(myVertices);
            this.tCoords = VectorFMath.toBuffer(myTexCoords);
            GLSLAttrib vAttrib = new GLSLAttrib(this.vertices, "MCvertex", GLSLAttrib.SIZE_FLOAT, 4);
            GLSLAttrib tAttrib = new GLSLAttrib(tCoords, "MCtexCoord", GLSLAttrib.SIZE_FLOAT, 2);
            vbo = new VBO(gl, vAttrib, tAttrib);

            program.setUniformVector("ColorStatic", material.getColor());
            program.setUniform("Alpha", 1f);

            this.numVertices = vertices.size();
            this.cachedString = str;

            // Prepare the FBO for 2 pass rendering
            int textureWidth = 1024;
            int textureHeight = (int) (((textureWidth * bbox.getHeight()) / bbox.getWidth()) + 0.5f);
            fbo = new FBO(textureWidth, textureHeight, GL3.GL_TEXTURE6);
            fbo.init(gl);

            // Prepare the quad, to be rendered with texture in case of 2 pass
            // rendering
            if (quad != null) {
                quad.delete(gl);
            }

            quad = new RBOQuad(material, bbox.getWidth(), bbox.getHeight(), bbox.getCenter());
            quad.init(gl);

            initialized = true;
        }
    }

    private void render2FBO(GL3 gl, Program program) throws UninitializedException {
        MatF4 PMVMatrix = new MatF4();

        fbo.bind(gl);
        gl.glClear(GL3.GL_DEPTH_BUFFER_BIT | GL3.GL_COLOR_BUFFER_BIT);
        try {
            int minX = (int) Math.floor(bbox.getMin().get(0));
            int minY = (int) Math.floor(bbox.getMin().get(1));
            int maxX = (int) Math.ceil(bbox.getMax().get(0));
            int maxY = (int) Math.ceil(bbox.getMax().get(1));

            PMVMatrix = MatrixFMath.ortho(minX, maxX, minY, maxY, -1f, 1f);
        } catch (UninitializedException e1) {
            e1.printStackTrace();
        }

        program.setUniformMatrix("PMVMatrix", PMVMatrix);

        try {
            program.use(gl);
        } catch (UninitializedException e) {
            e.printStackTrace();
        }

        vbo.bind(gl);

        program.linkAttribs(gl, vbo.getAttribs());

        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVertices);

        fbo.unBind(gl);
    }

    private void renderFBO(GL3 gl, Program program, MatF4 PMVMatrix) {
        try {
            fbo.getTexture().use(gl);
        } catch (UninitializedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        program.setUniform("RBOTexture", fbo.getTexture().getGLMultiTexUnit());

        quad.draw(gl, program, PMVMatrix);
    }

    @Override
    public void draw(GL3 gl, Program program, MatF4 PMVMatrix) {
        if (initialized) {
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

    public void draw2pass(GL3 gl, Program program, MatF4 PMVMatrix) {
        if (initialized) {
            try {
                render2FBO(gl, program);
                renderFBO(gl, program, PMVMatrix);
            } catch (UninitializedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init(GL3 gl) {
        if (!initialized) {
            GLSLAttrib vAttrib = new GLSLAttrib(vertices, "MCvertex", GLSLAttrib.SIZE_FLOAT, 4);
            GLSLAttrib tAttrib = new GLSLAttrib(tCoords, "MCtexCoord", GLSLAttrib.SIZE_FLOAT, 2);
            vbo = new VBO(gl, vAttrib, tAttrib);
        }
        initialized = true;
    }

    public static MatF4 getPMVForHUD(float canvasWidth, float canvasHeight, float RasterPosX, float RasterPosY) {

        MatF4 mv = new MatF4();
        mv = mv.mul(MatrixFMath.translate((RasterPosX / canvasWidth), (RasterPosY / canvasHeight), 0f));

        MatF4 PMatrix = MatrixFMath.ortho(0f, canvasWidth, 0f, canvasHeight, -1f, 1f);
        mv = mv.mul(PMatrix);

        return mv;
    }

}
