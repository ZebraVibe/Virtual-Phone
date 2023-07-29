package com.ouken.phone.utils.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shaders {
	
	/**fragment parameter*/
	public static final String 
	U_TEXTURE = "u_texture",
	U_MASK_TEXTURE = "u_maskTexture";
	
	
	private static final String DEFAULT_VERTEX_SHADER = 
			"attribute vec4 a_position; \n" + 
            "attribute vec4 a_color; \n" +
            "attribute vec2 a_texCoord0; \n" + 
            "uniform mat4 u_projTrans; \n" + 
            "varying vec4 v_color; \n"+ 
            "varying vec2 v_texCoords; \n" + 
            "void main()                  \n" + 
            "{                            \n" + 
            "   v_color = vec4(1, 1, 1, 1); \n" + 
            "   v_texCoords = a_texCoord0; \n" + 
            "   gl_Position =  u_projTrans * a_position;  \n"      + 
            "}                            \n" ;
	
	private static final String DEFAULT_FRAGMENT_SHADER = 
			  "#ifdef GL_ES\n" +
          "precision mediump float;\n" + 
          "#endif\n" + 
          "varying vec4 v_color;\n" + 
          "varying vec2 v_texCoords;\n" + 
          "uniform sampler2D u_texture;\n" + 
          "void main()                                  \n" + 
          "{                                            \n" + 
          "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
          "}";
	
	
	
	
	
	private static final String ALPHA_MASK_WITH_2_TEXTURES_VERTEX_SHADER = 
			"attribute vec4 a_position; \n" + 
            "attribute vec4 a_color; \n" +
            "attribute vec2 a_texCoord0; \n" + 
//            "attribute vec2 a_texCoord1; \n" + 
            "uniform mat4 u_projTrans; \n" + 
            "varying vec4 v_color; \n"+ 
            "varying vec2 v_texCoords; \n" +
//            "varying vec2 v_maskCoords; \n" + 
            "void main()                  \n" + 
            "{                            \n" + 
            "   v_color = vec4(1, 1, 1, 1); \n" + 
            "   v_texCoords = a_texCoord0; \n" + 
//            "   v_maskCoords = a_texCoord1; \n" + 
            "   gl_Position =  u_projTrans * a_position;  \n"      + 
            "}                            \n" ;
	

	
	/**
	 * if a shader doesnt us a variable it gets deleted & when trying to set uniforms to that variable it would crash
	 * using: ShaderProgram.pedantic = false; ignores an unused variable nd doesnt delete it.
	 * https://stackoverflow.com/questions/26835347/no-uniform-with-name-u-proj-in-shader
	 */
	private static final String ALPHA_MASK_WITH_2_TEXTURES_FRAGMENT_SHADER = 
		  "#ifdef GL_ES\n" +
          "precision mediump float;\n" + 
          "#endif\n" + 
          "varying vec4 v_color;\n" + 
          "varying vec2 v_texCoords;\n" + 
//          "varying vec2 v_maskCoords;\n" + 
          "uniform sampler2D " + U_TEXTURE + ";\n" + 
          "uniform sampler2D " + U_MASK_TEXTURE + ";\n" + 
          "void main()                                  \n" + 
          "{                                            \n" + 
//          "  vec4 texColor = texture2D(" + U_TEXTURE + ", v_texCoords);\n" +
//          "  vec4 maskColor = texture2D( " + U_MASK_TEXTURE + ", v_maskCoords );\n" +
		  "  vec4 texColor = texture2D(" + U_TEXTURE + ", v_texCoords);\n" +
		  "  vec4 maskColor = texture2D( " + U_MASK_TEXTURE + ", v_texCoords );\n" +
          "  gl_FragColor =  v_color * vec4(texColor.rgb, maskColor.a) ;\n" + //  "  gl_FragColor = v_color * vec4(texColor.rgb, texColor.a * maskColor.a);\n" +
          "}";
	
	private static final String DISCARD_TRANS_PXLS_FRAGMENT_SHADER = 
			  "#ifdef GL_ES\n" +
              "precision mediump float;\n" + 
              "#endif\n" + 
              "varying vec4 v_color;\n" + 
              "varying vec2 v_texCoords;\n" + 
              "uniform sampler2D u_texture;\n" + 
              "void main()                                  \n" + 
              "{                                            \n" + 
              "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
              "  if (gl_FragColor.a <= 0.0) discard;\n"+ // discard transparent fragments
              "}";
	
	private static final String WHITE_FRAGMENT_SHADER = 
			  "#ifdef GL_ES\n" +
              "precision mediump float;\n" + 
              "#endif\n" + 
              "varying vec4 v_color;\n" + 
              "varying vec2 v_texCoords;\n" + 
              "uniform sampler2D u_texture;\n" + 
              "void main()                                  \n" + 
              "{                                            \n" + 
              "  gl_FragColor = vec4(1,1,1,1);\n" + // or do "= v_color" since above v_color = vec4(1,1,1,1) which is white 
              "}";
             
	private static final String WHITE_BUT_BLACK_FRAGMENT_SHADER = 
			  "#ifdef GL_ES\n" +
              "precision mediump float;\n" + 
              "#endif\n" + 
              "varying vec4 v_color;\n" + 
              "varying vec2 v_texCoords;\n" + 
              "uniform sampler2D u_texture;\n" + 
              "void main()                                  \n" + 
              "{                                            \n" + 
              "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
              "  if (gl_FragColor.r != 0 && gl_FragColor.g != 0 && gl_FragColor.b != 0) gl_FragColor = vec4(1,1,1,1);\n"+ 
              "}";


	public static ShaderProgram createDiscardTranparentPixelsShader () {
		ShaderProgram shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, DISCARD_TRANS_PXLS_FRAGMENT_SHADER);
		if (!shader.isCompiled()) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
		return shader;
	}
	
	/*  
	 *  https://stackoverflow.com/questions/25788952/opengl-alpha-mask
	 *  
	 *  https://stackoverflow.com/questions/22447270/passing-several-textures-to-shader-in-libgdx
	 *  
	 *  https://stackoverflow.com/questions/9046228/binding-2-textures-only-see-1
	 * [texture units (scroll down)]
	 *  https://learnopengl.com/Getting-started/Textures
	 *  
	 *  [using 2 textureregions as textures in shader, requires 2nd texCoord]
	 *  https://stackoverflow.com/questions/34758493/libgdx-how-to-combine-2-textures-in-single-location-for-multitexturing-using-sp
	 *  
	 *  https://stackoverflow.com/questions/32222957/libgdx-custom-shader-per-vertex-attribute
	 * */
	public static ShaderProgram createAlphaMaskWithSecondTextureShader () {
		ShaderProgram shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, ALPHA_MASK_WITH_2_TEXTURES_FRAGMENT_SHADER);
		if (!shader.isCompiled()) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
		return shader;
	}
	
	private Shaders() {}

}
