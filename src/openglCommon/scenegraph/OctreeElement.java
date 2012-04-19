package openglCommon.scenegraph;

import openglCommon.math.VecF3;

public class OctreeElement {
    private final VecF3 center;
    
    public OctreeElement(VecF3 center) {
        this.center = center;
    }
    
    public VecF3 getCenter() {
        return this.center;
    }
}
