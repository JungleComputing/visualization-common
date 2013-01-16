package nl.esciencecenter.visualization.openglCommon;

import java.awt.BorderLayout;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import nl.esciencecenter.visualization.openglCommon.util.GLProfileSelector;
import nl.esciencecenter.visualization.openglCommon.util.InputHandler;

import com.jogamp.opengl.util.FPSAnimator;

public abstract class CommonPanel extends JPanel {
    protected static final long serialVersionUID = 4754345291079348455L;

    protected GLCanvas          glCanvas;
    protected GLContext         offScreenContext;

    public CommonPanel(CommonWindow glWindow, InputHandler inputHandler) {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        setLayout(new BorderLayout(0, 0));

        GLProfileSelector.printAvailable();
        GLProfile glp = GLProfile.get(GLProfile.GL4);

        // Standard GL3 capabilities
        GLCapabilities glCapabilities = new GLCapabilities(glp);

        glCapabilities.setHardwareAccelerated(true);
        glCapabilities.setDoubleBuffered(true);

        // Anti-Aliasing
        glCapabilities.setSampleBuffers(true);
        glCapabilities.setAlphaBits(4);
        glCapabilities.setNumSamples(4);

        // Create the canvas
        offScreenContext = new GLOffscreenContext(glp).getContext();
        glWindow.setOffScreenContext(offScreenContext);
        glCanvas = new GLCanvas(glCapabilities, offScreenContext);

        // Add Mouse event listener
        glCanvas.addMouseListener(inputHandler);
        glCanvas.addMouseMotionListener(inputHandler);
        glCanvas.addMouseWheelListener(inputHandler);

        // Add key event listener
        glCanvas.addKeyListener(inputHandler);

        // Make the GLEventListener
        glCanvas.addGLEventListener(glWindow);

        // Set up animator
        final FPSAnimator animator = new FPSAnimator(glCanvas, 60);
        animator.start();

        add(glCanvas, BorderLayout.CENTER);

        setVisible(true);
        glCanvas.setFocusable(true);
        glCanvas.requestFocusInWindow();
    }
}
