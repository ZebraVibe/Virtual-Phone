package com.ouken.phone.desktop;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.PixelFormat;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.badlogic.gdx.graphics.Color;
import com.ouken.phone.Phone;
import com.ouken.phone.config.Config;

public class PhoneJFrame extends JFrame{
	
	
	public PhoneJFrame() throws HeadlessException{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int) Config.WIDTH;
		config.height = (int) Config.HEIGHT;
		config.resizable = false;
		config.stencil = 8;
//		config.initialBackgroundColor = new Color(0,0,0,0);
//		config.undecorated = true;
		setLayout(new BorderLayout());
		
		// changes to the right size but doesnt draw the second canvas
		getContentPane().add(createLwjglCanvas(config).getCanvas(), BorderLayout.WEST);
//		getContentPane().add(createLwjglAWTCanvas(config).getCanvas(), BorderLayout.EAST);
		
		
		
		//works
//		getContentPane().add(createLwjglAWTCanvas(config).getCanvas(), BorderLayout.WEST);
//		getContentPane().add(createLwjglAWTCanvas(config).getCanvas(), BorderLayout.EAST);
		

		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private LwjglCanvas createLwjglCanvas(LwjglApplicationConfiguration config) {
		LwjglCanvas lwjglCanvas = new LwjglCanvas(Phone.INSTANCE, config); // <- for ONE canvas to set Display.setParent(canvas)
		lwjglCanvas.getCanvas().setSize(config.width, config.height);
//		getContentPane().add(lwjglCanvas.getCanvas(), BorderLayout.CENTER);
//		pack();
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(lwjglCanvas != null) {
					ApplicationListener app = lwjglCanvas.getApplicationListener();
					app.pause();
					app.dispose();
					lwjglCanvas.stop(); // (never executed !!) disposing application
				}
				e.getWindow().dispose();
			}
		});
		
//		addWindowListener(new WindowAdapter() {
//			
//			@Override
//			public void windowClosing(WindowEvent e) {
//				if(lwjglCanvas != null)lwjglCanvas.stop();
//				Runtime.getRuntime().halt(0);
//			}
//			
//		});
		
//		 // Will finish up application properly on close
//        addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosed(WindowEvent e) {
//                System.exit(0);
//            }
//        });
//        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		return lwjglCanvas;
		
	}
	
	
	private LwjglAWTCanvas createLwjglAWTCanvas(LwjglApplicationConfiguration config) {
		LwjglAWTCanvas lwjglAWTCanvas = new LwjglAWTCanvas(Phone.INSTANCE, config); //<- for multiple awt canvases
		Canvas canvas = lwjglAWTCanvas.getCanvas();
		canvas.setSize(config.width, config.height);
		
		
//		AWTGLCanvas awtglCanvas = (AWTGLCanvas)canvas;
//		
//		try {
//			awtglCanvas.setPixelFormat(new PixelFormat(config.r + config.g + config.b, config.a, config.depth, config.stencil, config.samples));
//			
//		} catch (LWJGLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
//		getContentPane().add(lwjglAWTCanvas.getCanvas(), BorderLayout.EAST);
//		setSize(config.width, config.height); // else decoration included in size
		
		//when exiting we want to dispose stuff
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (lwjglAWTCanvas != null) {
					lwjglAWTCanvas.stop();// calls our dispose of libgdx application and stops it
				}
			}
		});
		return lwjglAWTCanvas;
	}
	

	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				JFrame frame = new PhoneJFrame();
				frame.setAlwaysOnTop(true);
//				frame.setUndecorated(true); // only for this jframe - read setDefaultLookAndFeelDecoration
//				frame.setBackground(new java.awt.Color(0,0,0,0));

//		      frame.getRootPane().putClientProperty("apple.awt.draggableWindowBackground", false);
//				frame.getRootPane().putClientProperty("RootPane.draggableWindowBackground", false);
				
				
//				frame.getContentPane().setLayout(new java.awt.BorderLayout());
//				frame.getContentPane().add(new JTextField("text field north"), java.awt.BorderLayout.NORTH);
//				frame.getContentPane().add(new JTextField("text field south"), java.awt.BorderLayout.SOUTH);
				
//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				frame.setVisible(true);
//				frame.setFocusableWindowState(false); // cant type no more in textfield
			}
		});


	}
	
}
