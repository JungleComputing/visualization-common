package nl.esciencecenter.visualization.openglCommon.input;

import nl.esciencecenter.visualization.openglCommon.math.VecF3;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

/**
 * @author Maarten van Meersbergen <m.van.meersbergen@esciencecenter.nl>
 *         A singleton pattern generic Input event Handler for use in OpenGL
 *         applications.
 *         Currently handles only basic mouse events (left-click-drag,
 *         scrollwheel).
 * 
 */
public class InputHandler implements MouseListener,
        KeyListener {

    /**
     * Octants are used to define a direction from which the viewer is looking
     * at the scene, these are useful in Octrees.
     */
    public static enum octants {
        PPP, PPN, PNP, PNN, NPP, NPN, NNP, NNN
    }

    protected float rotationXorigin     = 0;
    protected float rotationX;

    protected float rotationYorigin     = 0;
    protected float rotationY;

    protected float dragLeftXorigin;
    protected float dragLeftYorigin;

    public VecF3    rotation;
    public float    viewDist            = -150f;
    private octants current_view_octant = octants.PPP;

    private static class SingletonHolder {
        public static final InputHandler instance = new InputHandler();
    }

    /**
     * The only access point for this singleton class.
     * 
     * @return
     *         The only instance of this class allowed at one time.
     */
    public static InputHandler getInstance() {
        return SingletonHolder.instance;
    }

    protected InputHandler() {
        rotation = new VecF3();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Empty - unneeded
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Empty - unneeded
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isButtonDown(MouseEvent.BUTTON1)) {
            dragLeftXorigin = e.getX();
            dragLeftYorigin = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        rotationXorigin = rotationX;
        rotationYorigin = rotationY;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.isButtonDown(MouseEvent.BUTTON1)) {
            // x/y reversed because of axis orientation. (up/down => x axis
            // rotation in OpenGL)
            if (e.isShiftDown()) {
                rotationX = ((e.getX() - dragLeftXorigin) / 10f + rotationXorigin) % 360;
                rotationY = ((e.getY() - dragLeftYorigin) / 10f + rotationYorigin) % 360;
            } else {
                rotationX = ((e.getX() - dragLeftXorigin) + rotationXorigin) % 360;
                rotationY = ((e.getY() - dragLeftYorigin) + rotationYorigin) % 360;
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

    @Override
    public void mouseMoved(MouseEvent e) {
        // Empty - unneeded
    }

    @Override
    public void mouseWheelMoved(MouseEvent e) {
        float newViewDist = this.viewDist;

        if (e.isShiftDown()) {
            newViewDist -= e.getWheelRotation() * 2;
        } else {
            newViewDist -= e.getWheelRotation() * 10;
        }
        viewDist = newViewDist;
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

    @Override
    public void keyPressed(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * 
     * @return the rotation
     */
    public VecF3 getRotation() {
        return rotation;
    }

    /**
     * @param rotation
     *            the rotation to set
     */
    public void setRotation(VecF3 rotation) {
        this.rotation = rotation;
    }

    /**
     * @return the viewDist
     */
    public float getViewDist() {
        return viewDist;
    }

    /**
     * @param viewDist
     *            the viewDist to set
     */
    public void setViewDist(float viewDist) {
        this.viewDist = viewDist;
    }

    /**
     * @return the current_view_octant
     */
    public octants getCurrent_view_octant() {
        return current_view_octant;
    }
}
