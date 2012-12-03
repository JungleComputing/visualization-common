package nl.esciencecenter.visualization.openglCommon;

import javax.media.opengl.DefaultGLCapabilitiesChooser;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.GLProfile;

import nl.esciencecenter.visualization.openglCommon.util.Settings;


public class GLOffscreenContext {
    private GLContext offScreenContext;

    public GLOffscreenContext(GLProfile glp) {
        GLDrawableFactory factory = GLDrawableFactory.getFactory(glp);

        // Make the offscreen context for screenshotting
        if (factory.canCreateGLPbuffer(factory.getDefaultDevice())) {
            GLCapabilities offScreenCapabilities = new GLCapabilities(glp);

            offScreenCapabilities.setHardwareAccelerated(true);
            offScreenCapabilities.setDoubleBuffered(false);

            // Anti-Aliasing
            offScreenCapabilities.setSampleBuffers(true);
            offScreenCapabilities.setNumSamples(4);
            offScreenCapabilities.setAlphaBits(4);

            GLPbuffer pbuffer = factory.createGLPbuffer(factory.getDefaultDevice(), offScreenCapabilities,
                    new DefaultGLCapabilitiesChooser(), Settings.getInstance().getScreenshotScreenWidth(), Settings
                            .getInstance().getScreenshotScreenHeight(), null);

            offScreenContext = pbuffer.createContext(null);
            offScreenContext.setSynchronized(true);

            if (offScreenContext == null) {
                System.err.println("PBuffer failed.");
            }
        } else {
            System.err.println("No offscreen rendering support.");
        }
    }

    public GLContext getContext() {
        return offScreenContext;
    }
}
