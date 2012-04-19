package openglCommon.util;

import javax.media.opengl.GLProfile;

public class GLProfileSelector {
    public static final void printAvailable() {
        System.out.println(GLProfile.glAvailabilityToString());
    }
}
