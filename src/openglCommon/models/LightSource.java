package openglCommon.models;

import openglCommon.math.Color4;
import openglCommon.math.VecF3;
import openglCommon.shaders.Program;

public class LightSource {
    private Color4 color;
    private VecF3 position;

    public LightSource(Color4 color, VecF3 position) {
        this.color = color;
        this.position = position;
    }

    public void use(Program p) {
        p.setUniformVector("LightColor", color);
        p.setUniformVector("LightPos", position);
    }
}
