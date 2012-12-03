package nl.esciencecenter.visualization.openglCommon;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

public class NewtWindow implements GLEventListener {
    final GLWindow glWindow;

    public static void main(String[] arguments) {
        new NewtWindow(0, 800, 600);
    }

    public NewtWindow(int screenIdx, int width, int height) {
        Display dpy = NewtFactory.createDisplay(null);
        Screen screen = NewtFactory.createScreen(dpy, screenIdx);

        GLCapabilities glCapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2ES2));
        glCapabilities.setBackgroundOpaque(true);

        // Anti-Aliasing
        glCapabilities.setSampleBuffers(true);
        glCapabilities.setAlphaBits(4);
        glCapabilities.setNumSamples(4);

        glWindow = GLWindow.create(screen, glCapabilities);

        glWindow.setTitle("NEWT Test");

        glWindow.setSize(width, height);
        glWindow.setPosition(0, 0);

        glWindow.setUndecorated(false);
        glWindow.setAlwaysOnTop(false);
        glWindow.setFullscreen(true);
        glWindow.setPointerVisible(true);
        glWindow.confinePointer(false);

        Animator animator = new Animator(glWindow);

        glWindow.addWindowListener(new WindowAdapter() {
            public void windowDestroyed(WindowEvent arg0) {
                super.windowDestroyed(arg0);
                System.exit(0);
            }
        });

        animator.start();

        glWindow.setVisible(true);
    }

    public GLWindow getGLWindow() {
        return glWindow;
    }

    @Override
    public void display(GLAutoDrawable arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose(GLAutoDrawable arg0) {
    }

    @Override
    public void init(GLAutoDrawable arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
    }
}
