package openglCommon.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import openglCommon.math.VecF3;

public class InputHandler implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
    public static enum octants {
        PPP, PPN, PNP, PNN, NPP, NPN, NNP, NNN
    }

    private VecF3   rotation;
    private float   viewDist            = -150f;

    private float   rotationXorigin     = 0;
    private float   rotationX;

    private float   rotationYorigin     = 0;
    private float   rotationY;

    private float   dragLeftXorigin;
    private float   dragLeftYorigin;

    private octants current_view_octant = octants.PPP;

    private static class SingletonHolder {
        public static final InputHandler instance = new InputHandler();
    }

    public static InputHandler getInstance() {
        return SingletonHolder.instance;
    }

    private InputHandler() {
        rotation = new VecF3();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        // Empty - unneeded
    }

    public void mouseExited(MouseEvent e) {
        // Empty - unneeded
    }

    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            dragLeftXorigin = e.getPoint().x;
            dragLeftYorigin = e.getPoint().y;
        }
    }

    public void mouseReleased(MouseEvent e) {
        rotationXorigin = rotationX;
        rotationYorigin = rotationY;
    }

    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            // x/y reversed because of axis orientation. (up/down => x axis
            // rotation in OpenGL)
            if (e.isShiftDown()) {
                rotationX = ((e.getPoint().x - dragLeftXorigin) / 10f + rotationXorigin) % 360;
                rotationY = ((e.getPoint().y - dragLeftYorigin) / 10f + rotationYorigin) % 360;
            } else {
                rotationX = ((e.getPoint().x - dragLeftXorigin) + rotationXorigin) % 360;
                rotationY = ((e.getPoint().y - dragLeftYorigin) + rotationYorigin) % 360;
            }
            // Make sure the numbers are always positive (so we can determine
            // the octant we're in more easily)
            if (rotationX < 0)
                rotationX = 360f + rotationX % 360;
            if (rotationY < 0)
                rotationY = 360f + rotationY % 360;

            rotation.set(0, rotationY);
            rotation.set(1, rotationX);
            rotation.set(2, 0f); // We never rotate around the Z axis.
            setCurrentOctant(rotation);
        }
    }

    public void mouseMoved(MouseEvent e) {
        // Empty - unneeded
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        float newViewDist = this.viewDist;

        if (e.isShiftDown()) {
            newViewDist -= e.getWheelRotation() * 2;
        } else {
            newViewDist -= e.getWheelRotation() * 10;
        }
        viewDist = newViewDist;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    private void setCurrentOctant(VecF3 rotation) {
        float x = rotation.get(0);
        int qx = (int) Math.floor(x / 90f);
        float y = rotation.get(1);
        int qy = (int) Math.floor(y / 90f);

        if (qx == 0 && qy == 0) {
            current_view_octant = octants.NPP;
        } else if (qx == 0 && qy == 1) {
            current_view_octant = octants.NPN;
        } else if (qx == 0 && qy == 2) {
            current_view_octant = octants.PPN;
        } else if (qx == 0 && qy == 3) {
            current_view_octant = octants.PPP;

        } else if (qx == 1 && qy == 0) {
            current_view_octant = octants.PPN;
        } else if (qx == 1 && qy == 1) {
            current_view_octant = octants.PPP;
        } else if (qx == 1 && qy == 2) {
            current_view_octant = octants.NPP;
        } else if (qx == 1 && qy == 3) {
            current_view_octant = octants.NPN;

        } else if (qx == 2 && qy == 0) {
            current_view_octant = octants.PNN;
        } else if (qx == 2 && qy == 1) {
            current_view_octant = octants.PNP;
        } else if (qx == 2 && qy == 2) {
            current_view_octant = octants.NNP;
        } else if (qx == 2 && qy == 3) {
            current_view_octant = octants.NNN;

        } else if (qx == 3 && qy == 0) {
            current_view_octant = octants.NNP;
        } else if (qx == 3 && qy == 1) {
            current_view_octant = octants.NNN;
        } else if (qx == 3 && qy == 2) {
            current_view_octant = octants.PNN;
        } else if (qx == 3 && qy == 3) {
            current_view_octant = octants.PNP;
        }
    }

    public octants getCurrentOctant() {
        return current_view_octant;
    }

    public VecF3 getRotation() {
        return rotation;
    }

    public void setRotation(VecF3 rotation) {
        this.rotation = rotation;
    }

    public float getViewDist() {
        return viewDist;
    }

    public void setViewDist(float dist) {
        this.viewDist = dist;
    }
}
