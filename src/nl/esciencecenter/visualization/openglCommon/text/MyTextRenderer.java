package nl.esciencecenter.visualization.openglCommon.text;

/**
 * Copyright 2010 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import com.jogamp.opengl.util.glsl.ShaderState;

public class MyTextRenderer extends TextRenderer {
    public MyTextRenderer(RenderState rs, int type) {
        super(rs, type);
    }

    @Override
    protected boolean initShaderProgram(GL2ES2 gl) {
        final ShaderState st = rs.getShaderState();

        ShaderCode rsVp = ShaderCode.create(gl, GL2ES2.GL_VERTEX_SHADER, 1,
                MyTextRenderer.class, "shader", "shader/bin",
                "curverenderer01-gl2");
        ShaderCode rsFp = ShaderCode.create(gl, GL2ES2.GL_FRAGMENT_SHADER, 1,
                MyTextRenderer.class, "shader", "shader/bin",
                "curverenderer01b-gl2");

        ShaderProgram sp = new ShaderProgram();
        sp.add(rsVp);
        sp.add(rsFp);

        sp.init(gl);
        st.attachShaderProgram(gl, sp);
        st.bindAttribLocation(gl, AttributeNames.VERTEX_ATTR_IDX,
                AttributeNames.VERTEX_ATTR_NAME);
        st.bindAttribLocation(gl, AttributeNames.TEXCOORD_ATTR_IDX,
                AttributeNames.TEXCOORD_ATTR_NAME);

        if (!sp.link(gl, System.err)) {
            throw new GLException("MyTextRenderer: Couldn't link program: "
                    + sp);
        }
        st.useProgram(gl, true);

        return true;
    }

    @Override
    protected void destroyImpl(GL2ES2 gl) {
        super.destroyImpl(gl);
    }

    @Override
    public void drawString3D(GL2ES2 gl, Font font, String str,
            float[] position, int fontSize, int texSize) {
        if (!isInitialized()) {
            throw new GLException("TextRendererImpl01: not initialized!");
        }
        GlyphString glyphString = getCachedGlyphString(font, str, fontSize);
        if (null == glyphString) {
            glyphString = GlyphString.createString(null, rs.getVertexFactory(),
                    font, fontSize, str);
            glyphString.createRegion(gl, renderModes);
            addCachedGlyphString(gl, font, str, fontSize, glyphString);
        }

        glyphString.renderString3D(gl, rs, vp_width, vp_height, texSize);
    }

    protected void drawImpl(GL2ES2 gl, Region region, float[] position,
            int texSize) {
        ((GLRegion) region).draw(gl, rs, vp_width, vp_height, texSize);
    }

}