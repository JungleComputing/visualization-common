package nl.esciencecenter.visualization.openglCommon;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;

import nl.esciencecenter.visualization.openglCommon.input.InputHandler;
import nl.esciencecenter.visualization.openglCommon.math.MatF4;
import nl.esciencecenter.visualization.openglCommon.math.MatrixFMath;
import nl.esciencecenter.visualization.openglCommon.math.Point4;
import nl.esciencecenter.visualization.openglCommon.math.VecF3;
import nl.esciencecenter.visualization.openglCommon.math.VecF4;
import nl.esciencecenter.visualization.openglCommon.shaders.ProgramLoader;
import nl.esciencecenter.visualization.openglCommon.text.FontFactory;
import nl.esciencecenter.visualization.openglCommon.text.TypecastFont;

/**
 * Common (extendible) class for OpenGL event listeners, providing
 * several helper methods.
 * 
 * @author Maarten van Meersbergen <m.van.meersbergen@esciencecenter.nl>
 * 
 */
public abstract class CommonGLEventListener implements GLEventListener {
    /** General variables needed for lookAt method */
    protected final float         radius  = 1.0f;
    protected final float         ftheta  = 0.0f;
    protected final float         phi     = 0.0f;

    /** General variables needed for a default perspective */
    protected final float         fovy    = 45.0f;
    protected final float         zNear   = 0.1f;
    protected final float         zFar    = 3000.0f;

    /**
     * A default implementation of the ProgramLoader, needed for programmable
     * shader functionality
     */
    protected final ProgramLoader loader;

    /**
     * Aspect ratio variable, normally set by the reshape function
     */
    protected float               aspect;

    protected int                 fontSet = FontFactory.UBUNTU;
    protected TypecastFont        font;

    protected float               inputRotationX, inputRotationY;
    protected float               inputViewDistance;
    protected InputHandler        inputHandler;

    /**
     * Creates a new GLEventListener
     */
    public CommonGLEventListener() {
        this.loader = new ProgramLoader();
        this.font = (TypecastFont) FontFactory.get(fontSet).getDefault();
    }

    /**
     * Creates a new GLEventListener with a predefined inputHandler
     * 
     * @param inputHandler
     *            A predefined Input event Handler (example available in
     *            nl.esciencecenter.visualization.openglCommon.input)
     */
    public CommonGLEventListener(InputHandler inputHandler) {
        this.loader = new ProgramLoader();
        this.font = (TypecastFont) FontFactory.get(fontSet).getDefault();
        this.inputHandler = inputHandler;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        drawable.getContext().makeCurrent();

        // First, init the 'normal' context
        GL3 gl = drawable.getGL().getGL3();

        // Anti-Aliasing
        gl.glEnable(GL3.GL_LINE_SMOOTH);
        gl.glHint(GL3.GL_LINE_SMOOTH_HINT, GL3.GL_NICEST);
        gl.glEnable(GL3.GL_POLYGON_SMOOTH);
        gl.glHint(GL3.GL_POLYGON_SMOOTH_HINT, GL3.GL_NICEST);

        // Depth testing
        gl.glEnable(GL3.GL_DEPTH_TEST);
        gl.glDepthFunc(GL3.GL_LEQUAL);
        gl.glClearDepth(1.0f);

        // Culling
        gl.glEnable(GL3.GL_CULL_FACE);
        gl.glCullFace(GL3.GL_BACK);

        // Enable Blending (needed for both Transparency and
        // Anti-Aliasing
        gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL3.GL_BLEND);

        // Enable Vertical Sync
        gl.setSwapInterval(1);

        // Set black background
        gl.glClearColor(0f, 0f, 0f, 0f);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        try {
            final int status = drawable.getContext().makeCurrent();
            if ((status != GLContext.CONTEXT_CURRENT)
                    && (status != GLContext.CONTEXT_CURRENT_NEW)) {
                System.err.println("Error swapping context to onscreen.");
            }
        } catch (final GLException e) {
            System.err.println("Exception while swapping context to onscreen.");
            e.printStackTrace();
        }

        final GL3 gl = drawable.getContext().getGL().getGL3();

        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * A helper function that generates a ModelView Matrix based on either the
     * current
     * inputRotationX, inputRotationY and inputViewDistance, or, if an Input
     * event Handler was defined by the constructor, on the viewDist and
     * rotation defined therein.
     * Also uses the predifined radius, ftheta and phi global variables.
     * 
     * @return
     *         A new Modelview Matrix that defines a rotated and translated view
     *         at coordinates (0,0,0).
     */
    public MatF4 lookAt() {
        Point4 eye = new Point4(
                (float) (radius * Math.sin(ftheta) * Math.cos(phi)),
                (float) (radius * Math.sin(ftheta) * Math.sin(phi)),
                (float) (radius * Math.cos(ftheta)), 1.0f);
        Point4 at = new Point4(0.0f, 0.0f, 0.0f, 1.0f);
        VecF4 up = new VecF4(0.0f, 1.0f, 0.0f, 0.0f);

        MatF4 mv = MatrixFMath.lookAt(eye, at, up);

        if (inputHandler != null) {
            mv = mv.mul(MatrixFMath.translate(new VecF3(0f, 0f,
                    inputViewDistance)));
            mv = mv.mul(MatrixFMath.rotationX(inputRotationX));
            mv = mv.mul(MatrixFMath.rotationY(inputRotationY));
        } else {
            mv = mv.mul(MatrixFMath.translate(new VecF3(0f, 0f, inputHandler
                    .getViewDist())));
            mv = mv.mul(MatrixFMath
                    .rotationX(inputHandler.getRotation().get(0)));
            mv = mv.mul(MatrixFMath
                    .rotationY(inputHandler.getRotation().get(1)));
        }

        return mv;
    }

    /**
     * A helper function that generates a Perspective Matrix.
     * Uses the fovy, aspect, zNear and zFar global variables.
     * 
     * @return
     *         A new Perspective Matrix that defines a common perspective
     *         frustum.
     */
    public MatF4 perspective() {
        return MatrixFMath.perspective(fovy, aspect, zNear, zFar);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        try {
            final int status = drawable.getContext().makeCurrent();
            if ((status != GLContext.CONTEXT_CURRENT)
                    && (status != GLContext.CONTEXT_CURRENT_NEW)) {
                System.err.println("Error swapping context to onscreen.");
            }
        } catch (final GLException e) {
            System.err.println("Exception while swapping context to onscreen.");
            e.printStackTrace();
        }

        final GL3 gl = drawable.getContext().getGL().getGL3();

        int width = drawable.getWidth();
        int height = drawable.getHeight();
        aspect = (float) width / (float) height;

        gl.glViewport(0, 0, width, height);
        gl.glViewport(0, 0, w, h);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        loader.cleanup(gl);
    }

    /**
     * @return the inputRotationX
     */
    public float getInputRotationX() {
        return inputRotationX;
    }

    /**
     * @param inputRotationX
     *            the inputRotationX to set
     */
    public void setInputRotationX(float inputRotationX) {
        this.inputRotationX = inputRotationX;
    }

    /**
     * @return the inputRotationY
     */
    public float getInputRotationY() {
        return inputRotationY;
    }

    /**
     * @param inputRotationY
     *            the inputRotationY to set
     */
    public void setInputRotationY(float inputRotationY) {
        this.inputRotationY = inputRotationY;
    }

    /**
     * @return the inputViewDistance
     */
    public float getInputViewDistance() {
        return inputViewDistance;
    }

    /**
     * @param inputViewDistance
     *            the inputViewDistance to set
     */
    public void setInputViewDistance(float inputViewDistance) {
        this.inputViewDistance = inputViewDistance;
    }
}
