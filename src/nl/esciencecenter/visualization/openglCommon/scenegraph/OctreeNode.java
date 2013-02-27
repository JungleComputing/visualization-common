package nl.esciencecenter.visualization.openglCommon.scenegraph;

import java.util.ArrayList;

import javax.media.opengl.GL3;

import nl.esciencecenter.visualization.openglCommon.exceptions.UninitializedException;
import nl.esciencecenter.visualization.openglCommon.input.InputHandler;
import nl.esciencecenter.visualization.openglCommon.math.MatF4;
import nl.esciencecenter.visualization.openglCommon.math.MatrixFMath;
import nl.esciencecenter.visualization.openglCommon.math.VecF3;
import nl.esciencecenter.visualization.openglCommon.math.VecF4;
import nl.esciencecenter.visualization.openglCommon.models.Model;
import nl.esciencecenter.visualization.openglCommon.shaders.ShaderProgram;
import nl.esciencecenter.visualization.openglCommon.util.Settings;

public class OctreeNode {
    protected final int                      maxElements;
    protected final ArrayList<OctreeElement> elements;
    protected final VecF3                    center;
    protected final float                    cubeSize;
    protected final int                      depth;
    protected final Model                    model;
    protected final MatF4                    TMatrix;
    protected final float                    scale;

    protected OctreeNode                     ppp, ppn, pnp, pnn, npp, npn, nnp,
            nnn;
    protected int                            childCounter;
    protected boolean                        subdivided  = false;
    protected boolean                        initialized = false;
    protected boolean                        drawable    = false;
    protected VecF4                          color;
    protected int                            subdivision;

    public OctreeNode(Model baseModel, int maxElements, int depth,
            int subdivision, VecF3 corner, float halfSize) {
        this.model = baseModel;
        this.maxElements = maxElements;
        this.depth = depth;
        this.subdivision = subdivision;
        this.center = corner.add(new VecF3(halfSize, halfSize, halfSize));
        this.cubeSize = halfSize;
        this.TMatrix = MatrixFMath.translate(center);
        this.scale = halfSize * 3f;
        this.elements = new ArrayList<OctreeElement>();
        this.childCounter = 0;
    }

    public OctreeNode(OctreeNode other) {
        this.maxElements = other.maxElements;
        this.elements = other.elements;
        this.center = other.center;
        this.cubeSize = other.cubeSize;
        this.depth = other.depth;
        this.model = other.model;
        this.TMatrix = other.TMatrix;
        this.scale = other.scale;

        this.ppp = other.ppp;
        this.ppn = other.ppn;
        this.pnp = other.pnp;
        this.pnn = other.pnn;
        this.npp = other.npp;
        this.npn = other.npn;
        this.nnp = other.nnp;
        this.nnn = other.nnn;
        this.childCounter = other.childCounter;
        this.initialized = other.initialized;
        this.subdivided = other.subdivided;
        this.drawable = other.drawable;
        this.color = other.color;
        this.subdivision = other.subdivision;
    }

    public void init(GL3 gl) {
        if (!initialized) {
            model.init(gl);

            if (subdivided) {
                ppp.init(gl);
                ppn.init(gl);
                pnp.init(gl);
                pnn.init(gl);
                npp.init(gl);
                npn.init(gl);
                nnp.init(gl);
                nnn.init(gl);
            }
        }

        initialized = true;
    }

    public void delete(GL3 gl) {
        if (initialized) {
            model.delete(gl);

            if (subdivided) {
                ppp.delete(gl);
                ppn.delete(gl);
                pnp.delete(gl);
                pnn.delete(gl);
                npp.delete(gl);
                npn.delete(gl);
                nnp.delete(gl);
                nnn.delete(gl);
            }
        }
    }

    protected void subDiv() {
        float size = cubeSize / 2f;
        ppp = new OctreeNode(model, maxElements, depth + 1, subdivision,
                center.add(new VecF3(0f, 0f, 0f)), size);
        ppn = new OctreeNode(model, maxElements, depth + 1, subdivision,
                center.add(new VecF3(0f, 0f, -cubeSize)), size);
        pnp = new OctreeNode(model, maxElements, depth + 1, subdivision,
                center.add(new VecF3(0f, -cubeSize, 0f)), size);
        pnn = new OctreeNode(model, maxElements, depth + 1, subdivision,
                center.add(new VecF3(0f, -cubeSize, -cubeSize)), size);
        npp = new OctreeNode(model, maxElements, depth + 1, subdivision,
                center.add(new VecF3(-cubeSize, 0f, 0f)), size);
        npn = new OctreeNode(model, maxElements, depth + 1, subdivision,
                center.add(new VecF3(-cubeSize, 0f, -cubeSize)), size);
        nnp = new OctreeNode(model, maxElements, depth + 1, subdivision,
                center.add(new VecF3(-cubeSize, -cubeSize, 0f)), size);
        nnn = new OctreeNode(model, maxElements, depth + 1, subdivision,
                center.add(new VecF3(-cubeSize, -cubeSize, -cubeSize)), size);

        for (OctreeElement element : elements) {
            addElementSubdivided(element);
        }

        elements.clear();

        subdivided = true;
    }

    public void addElement(OctreeElement element) {
        VecF3 location = element.getCenter();
        if ((location.get(0) > center.get(0) - cubeSize)
                && (location.get(1) > center.get(1) - cubeSize)
                && (location.get(2) > center.get(2) - cubeSize)
                && (location.get(0) < center.get(0) + cubeSize)
                && (location.get(1) < center.get(1) + cubeSize)
                && (location.get(2) < center.get(2) + cubeSize)) {
            if (childCounter > maxElements && !subdivided) {
                if (depth < Settings.getInstance().getMaxOctreeDepth()) {
                    subDiv();
                } else {
                    System.out.println("Max division!");
                }
            }
            if (subdivided) {
                addElementSubdivided(element);
            } else {
                elements.add(element);
            }
            childCounter++;
        }
    }

    public void finalizeAdding() {
        elements.clear();

        if (subdivided) {
            ppp.finalizeAdding();
            ppn.finalizeAdding();
            pnp.finalizeAdding();
            pnn.finalizeAdding();
            npp.finalizeAdding();
            npn.finalizeAdding();
            nnp.finalizeAdding();
            nnn.finalizeAdding();
        } else {
            drawable = true;
        }
    }

    public void addElementSubdivided(OctreeElement element) {
        VecF3 location = element.getCenter();
        if (location.get(0) < center.get(0)) {
            if (location.get(1) < center.get(1)) {
                if (location.get(2) < center.get(2)) {
                    nnn.addElement(element);
                } else {
                    nnp.addElement(element);
                }
            } else {
                if (location.get(2) < center.get(2)) {
                    npn.addElement(element);
                } else {
                    npp.addElement(element);
                }
            }
        } else {
            if (location.get(1) < center.get(1)) {
                if (location.get(2) < center.get(2)) {
                    pnn.addElement(element);
                } else {
                    pnp.addElement(element);
                }
            } else {
                if (location.get(2) < center.get(2)) {
                    ppn.addElement(element);
                } else {
                    ppp.addElement(element);
                }
            }
        }
    }

    public void draw(GL3 gl, ShaderProgram program, MatF4 MVMatrix)
            throws UninitializedException {
        if (initialized) {
            if (subdivided) {
                draw_sorted(gl, program, MVMatrix);
            } else {
                if (drawable) {
                    MatF4 newM = MVMatrix.mul(TMatrix);
                    program.setUniformMatrix("MVMatrix", newM);
                    program.setUniformMatrix("SMatrix",
                            MatrixFMath.scale(scale));
                    program.setUniformVector("Color", color);

                    program.use(gl);

                    model.draw(gl, program);
                }
            }
        } else {
            throw new UninitializedException();
        }
    }

    protected void draw_sorted(GL3 gl, ShaderProgram program, MatF4 MVMatrix) {
        InputHandler inputHandler = InputHandler.getInstance();

        try {
            if (inputHandler.getCurrentViewOctant() == InputHandler.octants.NNN) {
                ppp.draw(gl, program, MVMatrix);

                npp.draw(gl, program, MVMatrix);
                pnp.draw(gl, program, MVMatrix);
                ppn.draw(gl, program, MVMatrix);

                nnp.draw(gl, program, MVMatrix);
                pnn.draw(gl, program, MVMatrix);
                npn.draw(gl, program, MVMatrix);

                nnn.draw(gl, program, MVMatrix);
            } else if (inputHandler.getCurrentViewOctant() == InputHandler.octants.NNP) {
                ppn.draw(gl, program, MVMatrix);

                npn.draw(gl, program, MVMatrix);
                pnn.draw(gl, program, MVMatrix);
                ppp.draw(gl, program, MVMatrix);

                nnn.draw(gl, program, MVMatrix);
                pnp.draw(gl, program, MVMatrix);
                npp.draw(gl, program, MVMatrix);

                nnp.draw(gl, program, MVMatrix);
            } else if (inputHandler.getCurrentViewOctant() == InputHandler.octants.NPN) {
                pnp.draw(gl, program, MVMatrix);

                nnp.draw(gl, program, MVMatrix);
                ppp.draw(gl, program, MVMatrix);
                pnn.draw(gl, program, MVMatrix);

                npp.draw(gl, program, MVMatrix);
                ppn.draw(gl, program, MVMatrix);
                nnn.draw(gl, program, MVMatrix);

                npn.draw(gl, program, MVMatrix);
            } else if (inputHandler.getCurrentViewOctant() == InputHandler.octants.NPP) {
                pnn.draw(gl, program, MVMatrix);

                nnn.draw(gl, program, MVMatrix);
                ppn.draw(gl, program, MVMatrix);
                pnp.draw(gl, program, MVMatrix);

                npn.draw(gl, program, MVMatrix);
                ppp.draw(gl, program, MVMatrix);
                nnp.draw(gl, program, MVMatrix);

                npp.draw(gl, program, MVMatrix);
            } else if (inputHandler.getCurrentViewOctant() == InputHandler.octants.PNN) {
                npp.draw(gl, program, MVMatrix);

                ppp.draw(gl, program, MVMatrix);
                nnp.draw(gl, program, MVMatrix);
                npn.draw(gl, program, MVMatrix);

                pnp.draw(gl, program, MVMatrix);
                nnn.draw(gl, program, MVMatrix);
                ppn.draw(gl, program, MVMatrix);

                pnn.draw(gl, program, MVMatrix);
            } else if (inputHandler.getCurrentViewOctant() == InputHandler.octants.PNP) {
                npn.draw(gl, program, MVMatrix);

                ppn.draw(gl, program, MVMatrix);
                nnn.draw(gl, program, MVMatrix);
                npp.draw(gl, program, MVMatrix);

                pnn.draw(gl, program, MVMatrix);
                nnp.draw(gl, program, MVMatrix);
                ppp.draw(gl, program, MVMatrix);

                pnp.draw(gl, program, MVMatrix);
            } else if (inputHandler.getCurrentViewOctant() == InputHandler.octants.PPN) {
                nnp.draw(gl, program, MVMatrix);

                pnp.draw(gl, program, MVMatrix);
                npp.draw(gl, program, MVMatrix);
                nnn.draw(gl, program, MVMatrix);

                ppp.draw(gl, program, MVMatrix);
                npn.draw(gl, program, MVMatrix);
                pnn.draw(gl, program, MVMatrix);

                ppn.draw(gl, program, MVMatrix);
            } else if (inputHandler.getCurrentViewOctant() == InputHandler.octants.PPP) {
                nnn.draw(gl, program, MVMatrix);

                pnn.draw(gl, program, MVMatrix);
                npn.draw(gl, program, MVMatrix);
                nnp.draw(gl, program, MVMatrix);

                ppn.draw(gl, program, MVMatrix);
                npp.draw(gl, program, MVMatrix);
                pnp.draw(gl, program, MVMatrix);

                ppp.draw(gl, program, MVMatrix);
            }
        } catch (UninitializedException e) {
            e.printStackTrace();
        }
    }
}
