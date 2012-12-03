package nl.esciencecenter.visualization.openglCommon;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;

import nl.esciencecenter.visualization.openglCommon.datastructures.Picture;
import nl.esciencecenter.visualization.openglCommon.math.MatF3;
import nl.esciencecenter.visualization.openglCommon.math.MatF4;
import nl.esciencecenter.visualization.openglCommon.math.MatrixFMath;
import nl.esciencecenter.visualization.openglCommon.math.Point4;
import nl.esciencecenter.visualization.openglCommon.math.VecF3;
import nl.esciencecenter.visualization.openglCommon.math.VecF4;
import nl.esciencecenter.visualization.openglCommon.shaders.ProgramLoader;
import nl.esciencecenter.visualization.openglCommon.text.FontFactory;
import nl.esciencecenter.visualization.openglCommon.text.TypecastFont;
import nl.esciencecenter.visualization.openglCommon.util.InputHandler;
import nl.esciencecenter.visualization.openglCommon.util.Settings;


public abstract class CommonWindow implements GLEventListener {
    protected final Settings      settings = Settings.getInstance();

    protected final float         radius   = 1.0f;
    protected final float         ftheta   = 0.0f;
    protected final float         phi      = 0.0f;

    protected final float         fovy     = 45.0f;
    protected final float         zNear    = 0.1f;
    protected final float         zFar     = 3000.0f;

    protected final ProgramLoader loader;
    protected GLContext           offScreenContext;
    protected final InputHandler  inputHandler;

    protected int                 fontSet  = FontFactory.UBUNTU;
    protected TypecastFont        font;

    protected boolean             post_process;

    protected int                 canvasWidth, canvasHeight;

    public CommonWindow(InputHandler inputHandler, boolean post_process) {
        this.loader = new ProgramLoader();
        this.inputHandler = inputHandler;
        this.post_process = post_process;
        this.font = (TypecastFont) FontFactory.get(fontSet).getDefault();
    }

    public void setOffScreenContext(GLContext offScreenContext) {
        this.offScreenContext = offScreenContext;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        drawable.getContext().makeCurrent();

        canvasWidth = drawable.getWidth();
        canvasHeight = drawable.getHeight();

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

        // Now, init the offscreen context as well
        if (offScreenContext != null) {
            try {
                int status = offScreenContext.makeCurrent();
                if (status != GLContext.CONTEXT_CURRENT && status != GLContext.CONTEXT_CURRENT_NEW) {
                    System.err.println("Error swapping context to offscreen.");
                }
            } catch (GLException e) {
                System.err.println("Exception while swapping context to offscreen.");
                e.printStackTrace();
            }

            gl = offScreenContext.getGL().getGL3();

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

            try {
                offScreenContext.release();
            } catch (GLException e) {
                System.err.println("Exception while releasing offscreen context.");
                e.printStackTrace();
            }
        }
    }

    public void display(GLAutoDrawable drawable) {
        drawable.getContext().makeCurrent();
        displayContext(drawable.getGL().getGL3());
        drawable.getContext().release();
    }

    protected void displayContext(GL3 gl) {
        int width = GLContext.getCurrent().getGLDrawable().getWidth();
        int height = GLContext.getCurrent().getGLDrawable().getHeight();
        float aspect = (float) width / (float) height;

        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        Point4 eye = new Point4((float) (radius * Math.sin(ftheta) * Math.cos(phi)),
                (float) (radius * Math.sin(ftheta) * Math.sin(phi)), (float) (radius * Math.cos(ftheta)), 1.0f);
        Point4 at = new Point4(0.0f, 0.0f, 0.0f, 1.0f);
        VecF4 up = new VecF4(0.0f, 1.0f, 0.0f, 0.0f);

        MatF4 mv = MatrixFMath.lookAt(eye, at, up);
        mv = mv.mul(MatrixFMath.translate(new VecF3(0f, 0f, inputHandler.getViewDist())));
        mv = mv.mul(MatrixFMath.rotationX(inputHandler.getRotation().get(0)));
        mv = mv.mul(MatrixFMath.rotationX(inputHandler.getRotation().get(1)));

        MatF3 n = new MatF3();
        MatF4 p = MatrixFMath.perspective(fovy, aspect, zNear, zFar);

        // Vertex shader variables
        loader.setUniformMatrix("NormalMatrix", n);
        loader.setUniformMatrix("PMatrix", p);
        loader.setUniformMatrix("SMatrix", MatrixFMath.scale(1));

        renderScene(gl, mv);

        if (post_process) {
            renderTexturesToScreen(gl, width, height);
        }
    }

    public abstract void renderScene(GL3 gl, MatF4 mv);

    public abstract void renderTexturesToScreen(GL3 gl, int width, int height);

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(0, 0, w, h);

        canvasWidth = w;
        canvasHeight = h;
    }

    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        loader.cleanup(gl);
    }

    public void setPostprocess(boolean newSetting) {
        post_process = newSetting;
    }

    public boolean isPostprocessing() {
        return post_process;
    }

    public synchronized void makeSnapshot(String fileName) {
        if (offScreenContext != null) {
            try {
                int status = offScreenContext.makeCurrent();
                if (status != GLContext.CONTEXT_CURRENT && status != GLContext.CONTEXT_CURRENT_NEW) {
                    System.err.println("Error swapping context to offscreen.");
                }
            } catch (GLException e) {
                System.err.println("Exception while swapping context to offscreen.");
                e.printStackTrace();
            }

            int width = offScreenContext.getGLDrawable().getWidth();
            int height = offScreenContext.getGLDrawable().getHeight();

            GL3 gl = offScreenContext.getGL().getGL3();
            gl.glViewport(0, 0, width, height);

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

            gl.glClearColor(0f, 0f, 0f, 0f);

            // Display context
            displayContext(gl);

            Picture p = new Picture(width, height);

            gl.glFinish();

            p.copyFrameBufferToFile(settings.getScreenshotPath(), fileName);

            try {
                offScreenContext.release();
            } catch (GLException e) {
                e.printStackTrace();
            }
        }
    }
}
