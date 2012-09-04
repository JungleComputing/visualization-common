package openglCommon.models;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.opengl.GL3;

import openglCommon.datastructures.GLSLAttrib;
import openglCommon.datastructures.Material;
import openglCommon.datastructures.VBO;
import openglCommon.exceptions.UninitializedException;
import openglCommon.math.Color4;
import openglCommon.math.MatF4;
import openglCommon.math.MatrixFMath;
import openglCommon.math.VecF2;
import openglCommon.math.VecF3;
import openglCommon.math.VecF4;
import openglCommon.math.VectorFMath;
import openglCommon.shaders.Program;
import openglCommon.text.GlyphShape;
import openglCommon.text.OutlineShape;
import openglCommon.text.TypecastFont;

import com.jogamp.graph.geom.Triangle;
import com.jogamp.graph.geom.Vertex;
import com.jogamp.graph.geom.opengl.SVertex;

public class MultiColorText extends Model {
    private boolean                            initialized = false;

    private final HashMap<Integer, GlyphShape> glyphs;
    private final HashMap<Integer, VecF4>      colors;

    FloatBuffer                                vertexColors;

    private final BoundingBox                  bbox;
    private FloatBuffer                        tCoords;

    // private HDRFBO fbo;
    // private RBOQuad quad;
    private String                             cachedString;
    private final TypecastFont                 font;
    private final int                          fontSize;

    public MultiColorText(Material material, TypecastFont font, int fontSize) {
        super(material, vertex_format.TRIANGLES);

        this.font = font;
        this.fontSize = fontSize;

        cachedString = "";

        this.bbox = new BoundingBox();
        colors = new HashMap<Integer, VecF4>();
        glyphs = new HashMap<Integer, GlyphShape>();

        numVertices = 0;

        VecF4[] points = new VecF4[numVertices];
        VecF2[] texCoords = new VecF2[numVertices];

        this.vertices = VectorFMath.toBuffer(points);
        this.vertexColors = VectorFMath.toBuffer(points);
        this.texCoords = VectorFMath.toBuffer(texCoords);
    }

    @Override
    public void init(GL3 gl) {
        if (!initialized) {
            GLSLAttrib vAttrib = new GLSLAttrib(vertices, "MCvertex",
                    GLSLAttrib.SIZE_FLOAT, 4);
            GLSLAttrib cAttrib = new GLSLAttrib(vertexColors, "MCvertexColors",
                    GLSLAttrib.SIZE_FLOAT, 4);
            GLSLAttrib tAttrib = new GLSLAttrib(texCoords, "MCtexCoord",
                    GLSLAttrib.SIZE_FLOAT, 2);

            vbo = new VBO(gl, vAttrib, cAttrib, tAttrib);
        }
        initialized = true;
    }

    public void setString(GL3 gl, String str, Color4 basicColor) {
        if (cachedString.compareTo(str) != 0) {
            colors.clear();
            glyphs.clear();

            if (str.compareTo(cachedString) != 0) {
                // Get the outline shapes for the current string in this font
                ArrayList<OutlineShape> shapes = font.getOutlineShapes(str,
                        fontSize, SVertex.factory());

                // Make a set of glyph shapes from the outlines
                int numGlyps = shapes.size();

                for (int index = 0; index < numGlyps; index++) {
                    if (shapes.get(index) == null) {
                        colors.put(index, null);
                        glyphs.put(index, null);
                        continue;
                    }
                    GlyphShape glyphShape = new GlyphShape(SVertex.factory(),
                            shapes.get(index));

                    if (glyphShape.getNumVertices() < 3) {
                        colors.put(index, null);
                        glyphs.put(index, null);
                        continue;
                    }
                    colors.put(index, basicColor);
                    glyphs.put(index, glyphShape);
                }

                makeVBO(gl);
                this.cachedString = str;
            }
        }
    }

    private void makeVBO(GL3 gl) {
        // Create list of vertices based on the glyph shapes
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        ArrayList<VecF4> vertexColors = new ArrayList<VecF4>();
        for (int i = 0; i < glyphs.size(); i++) {
            if (glyphs.get(i) != null) {
                GlyphShape glyph = glyphs.get(i);
                VecF4 glypColor = colors.get(i);

                ArrayList<Triangle> gtris = glyph.triangulate();
                for (Triangle t : gtris) {
                    vertices.add(t.getVertices()[0]);
                    vertices.add(t.getVertices()[1]);
                    vertices.add(t.getVertices()[2]);

                    vertexColors.add(glypColor);
                    vertexColors.add(glypColor);
                    vertexColors.add(glypColor);
                }
            }
        }

        // Transform the vertices from Vertex objects to Vec4 objects
        // and
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

        if (vbo != null) {
            vbo.delete(gl);
        }
        this.vertices = VectorFMath.toBuffer(myVertices);
        this.vertexColors = VectorFMath.vec4ListToBuffer(vertexColors);
        this.tCoords = VectorFMath.toBuffer(myTexCoords);
        GLSLAttrib vAttrib = new GLSLAttrib(this.vertices, "MCvertex",
                GLSLAttrib.SIZE_FLOAT, 4);
        GLSLAttrib cAttrib = new GLSLAttrib(this.vertexColors, "MCvertexColor",
                GLSLAttrib.SIZE_FLOAT, 4);
        GLSLAttrib tAttrib = new GLSLAttrib(this.tCoords, "MCtexCoord",
                GLSLAttrib.SIZE_FLOAT, 2);
        vbo = new VBO(gl, vAttrib, cAttrib, tAttrib);

        this.numVertices = vertices.size();

        // Prepare the FBO for 2 pass rendering
        // int textureWidth = 1024;
        // int textureHeight = (int) (((textureWidth * bbox.getHeight()) / bbox
        // .getWidth()) + 0.5f);
        //
        // if (fbo != null) {
        // fbo.delete(gl);
        // }
        // fbo = new HDRFBO(textureWidth, textureHeight, GL3.GL_TEXTURE0);
        // fbo.init(gl);

        // Prepare the quad, to be rendered with texture in case of 2
        // pass
        // rendering
        // if (quad != null) {
        // quad.delete(gl);
        // }
        // quad = new RBOQuad(material, bbox.getWidth(), bbox.getHeight(),
        // bbox.getCenter());
        // quad.init(gl);

        initialized = true;
    }

    public void setSubstringColors(GL3 gl, HashMap<String, Color4> map) {
        for (Map.Entry<String, Color4> entry : map.entrySet()) {
            setSubstringColorWordBounded(gl, entry.getKey(), entry.getValue());
        }
    }

    public void setSubstringColorWordBounded(GL3 gl, String subString,
            Color4 newColor) {
        if (cachedString.contains(subString) && subString.compareTo("") != 0) {
            Pattern p = Pattern.compile("\\b" + subString + "\\b");
            Matcher m = p.matcher(cachedString);

            int startIndex = 0;
            while (m.find(startIndex)) {
                startIndex = m.start();
                for (int i = 0; i < subString.length(); i++) {
                    colors.put(startIndex + i, newColor);
                }
                startIndex++; // read past to avoid never-ending loop
            }
        }
    }

    public void setSubstringColor(GL3 gl, String subString, Color4 newColor) {
        if (cachedString.contains(subString) && subString.compareTo("") != 0) {
            int startIndex = cachedString.indexOf(subString);
            while (startIndex > -1) {
                for (int i = 0; i < subString.length(); i++) {
                    colors.put(startIndex + i, newColor);
                }
                startIndex = cachedString.indexOf(subString, startIndex + 1);
            }
        }
    }

    public void setSubstringAtIndexColor(GL3 gl, int startIndex,
            String subString, Color4 newColor) {
        if (cachedString.contains(subString) && subString.compareTo("") != 0) {
            for (int i = 0; i < subString.length(); i++) {
                colors.put(startIndex + i, newColor);
            }
        }
    }

    public void finalizeColorScheme(GL3 gl) {
        makeVBO(gl);
    }

    // private void render2FBO(GL3 gl, Program program)
    // throws UninitializedException {
    // MatF4 PMVMatrix = new MatF4();
    //
    // fbo.bind(gl);
    // gl.glClear(GL3.GL_DEPTH_BUFFER_BIT | GL3.GL_COLOR_BUFFER_BIT);
    // try {
    // int minX = (int) Math.floor(bbox.getMin().get(0));
    // int minY = (int) Math.floor(bbox.getMin().get(1));
    // int maxX = (int) Math.ceil(bbox.getMax().get(0));
    // int maxY = (int) Math.ceil(bbox.getMax().get(1));
    //
    // PMVMatrix = MatrixFMath.ortho(minX, maxX, minY, maxY, -1f, 1f);
    // } catch (UninitializedException e1) {
    // e1.printStackTrace();
    // }
    //
    // program.setUniformMatrix("PMVMatrix", PMVMatrix);
    //
    // try {
    // program.use(gl);
    // } catch (UninitializedException e) {
    // e.printStackTrace();
    // }
    //
    // vbo.bind(gl);
    //
    // program.linkAttribs(gl, vbo.getAttribs());
    //
    // gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVertices);
    //
    // fbo.unBind(gl);
    // }
    //
    // private void renderFBO(GL3 gl, Program program, MatF4 PMVMatrix) {
    // try {
    // fbo.getTexture().use(gl);
    // } catch (UninitializedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // program.setUniform("RBOTexture", fbo.getTexture().getGLMultiTexUnit());
    //
    // quad.draw(gl, program, PMVMatrix);
    // }

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

    // public void draw2pass(GL3 gl, Program program, MatF4 PMVMatrix) {
    // if (initialized) {
    // try {
    // render2FBO(gl, program);
    // renderFBO(gl, program, PMVMatrix);
    // } catch (UninitializedException e) {
    // e.printStackTrace();
    // }
    // }
    // }

    @Override
    public String toString() {
        return cachedString;
    }

    public static MatF4 getPMVForHUD(float canvasWidth, float canvasHeight,
            float RasterPosX, float RasterPosY) {

        MatF4 mv = new MatF4();
        mv = mv.mul(MatrixFMath.translate((RasterPosX / canvasWidth),
                (RasterPosY / canvasHeight), 0f));

        MatF4 PMatrix = MatrixFMath.ortho(0f, canvasWidth, 0f, canvasHeight,
                -1f, 1f);
        mv = mv.mul(PMatrix);

        return mv;
    }

}
