package com.ouken.phone.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.Phone;

public class GdxUtils {
	
	/**glViewport has no effect on clearing, but scissors do*/
	public static void clearScreen(Color color) {
		clearScreen(color.r, color.g, color.b, color.a);
	}
	
	/**glViewport has no effect on clearing, but scissors do*/
	public static void clearScreen(float r, float g, float b, float a) {
		Gdx.gl.glColorMask(true, true, true, true); // allows to manipulate all of rgba 
		Gdx.gl.glClearColor(r, g, b, a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	public static void clearScreenStencil() {
		Gdx.gl.glStencilMask(0xFF); // allows to change buffer values
		// Gdx.gl.glClearStencil(0); // already set by default: the vlaue to clear the buffer with
		Gdx.gl.glClear(GL20.GL_STENCIL_BUFFER_BIT);
		Gdx.gl.glStencilMask(0x00);
	}

	private GdxUtils() {}
	
	
}
