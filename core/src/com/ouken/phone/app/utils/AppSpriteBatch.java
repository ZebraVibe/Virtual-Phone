package com.ouken.phone.app.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;

public class AppSpriteBatch extends SpriteBatch {
	
	private static final Logger log = new Logger(AppSpriteBatch.class.getName(), Logger.DEBUG);
	
	// -- constructor --
	public AppSpriteBatch() { super(); }
	
	
	// -- public methods --
	
	/**Does nothing to be able to pass apps the same batch. <br>
	 * Use {@link AppSpriteBatch#dispoeBatch()} instead*/
	@Override
	public void dispose() { 
		// do nothing when called by an app
		log.debug("dispose() : ...Nothing happened!");
	}
	
	/**To be used to dispose the batch instead of dipose()*/
	public void dispoeBatch() {
		super.dispose();
		log.debug("disposBatch() : Batch disposed!");
	}
	
}
