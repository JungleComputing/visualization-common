package nl.esciencecenter.visualization.openglCommon;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import nl.esciencecenter.visualization.openglCommon.input.InputHandler;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

/**
 * @author maarten
 *         Common (extendible) class for an OpenGL window.
 * 
 */
public abstract class CommonNewtWindow {
    static int screenIdx = 0;

    /**
     * 
     * @param forceGL3
     * @param inputHandler
     * @param glEventListener
     * @param width
     * @param height
     * @param windowTitle
     */
    public CommonNewtWindow(boolean forceGL3, InputHandler inputHandler,
            GLEventListener glEventListener, int width, int height,
            String windowTitle) {
        final GLProfile glp;
        if (forceGL3) {
            glp = GLProfile.get(GLProfile.GL3);
        } else {
            glp = GLProfile.get(GLProfile.GL2ES2);
        }

        // Set up the GL context
        final GLCapabilities caps = new GLCapabilities(glp);
        caps.setBackgroundOpaque(true);
        caps.setHardwareAccelerated(true);
        caps.setDoubleBuffered(true);

        // Add Anti-Aliasing
        caps.setSampleBuffers(true);
        caps.setAlphaBits(4);
        caps.setNumSamples(4);

        // Create the Newt Window
        Display dpy = NewtFactory.createDisplay(null);
        Screen screen = NewtFactory.createScreen(dpy, screenIdx);
        final GLWindow glWindow = GLWindow.create(screen, caps);

        glWindow.setTitle(windowTitle);

        // Add listeners
        glWindow.addMouseListener(inputHandler);
        glWindow.addKeyListener(inputHandler);
        glWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(WindowEvent arg0) {
                System.exit(0);
            }

            @Override
            public void windowDestroyed(WindowEvent arg0) {
                System.exit(0);
            }
        });
        glWindow.addGLEventListener(glEventListener);

        // Create the Animator
        final Animator animator = new Animator();
        animator.add(glWindow);
        animator.start();

        glWindow.setSize(width, height);

        glWindow.setVisible(true);
    }
}
