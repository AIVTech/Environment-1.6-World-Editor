package display;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {

	private static int DISPLAY_WIDTH = 1440;
	private static int DISPLAY_HEIGHT = 970;
	private static int preferedFPS_CAP = 500;

	private static long lastFrameTime;
	private static float delta;

	private static Canvas canvas;

	private int frameWidth = 1920;
	private int frameHeight = 1080;
	
	// World Editor Variables
	public static boolean addNewEntity = false;
	
	public static boolean changeModel = true;			// model should be assigned at startup
	public static String modelName = "LowPoly Tree";	// default model
	
	public static boolean lookingForEntity = false;
	public static boolean deleteSelectedEntity = false;
	public static boolean enableFreeMove = false;
	
	public static float entityPosX = 0;
	public static float entityPosY = -100;
	public static float entityPosZ = 0;
	
	public static float entityRotX = 0;
	public static float entityRotY = 0;
	public static float entityRotZ = 0;
	
	public static float entityScale = 1;
	
	private static List<String> modelNames = new ArrayList<String>();
	
	private JFrame frame;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void embedDisplay() {
		frame = new JFrame("Envrironment 1.6 World Editor");
		frame.setSize(frameWidth, frameHeight);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				int result = JOptionPane.showConfirmDialog(frame, "Do you want to quit the Application?", null,
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
				if (result == JOptionPane.OK_OPTION) {
					frame.setVisible(false);
					frame.dispose(); // canvas's removeNotify() will be called
					System.exit(0);
				}
			}
		});
		
		modelNames.add("LowPoly Tree");
		modelNames.add("Pine");
		modelNames.add("Village");
		modelNames.add("Stall");
		modelNames.add("Fern");

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(null);
		frame.setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		mainPanel.setBackground(Color.LIGHT_GRAY);
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(null);
		controlPanel.setSize(new Dimension(1440, 150));
		controlPanel.setLocation(0, 0);
		controlPanel.setBackground(Color.LIGHT_GRAY);
		mainPanel.add(controlPanel);
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(null);
		optionsPanel.setSize(new Dimension(480, 150));
		optionsPanel.setLocation(1440, 0);
		optionsPanel.setBackground(Color.LIGHT_GRAY);
		mainPanel.add(optionsPanel);

		/************** ALL THE GUI STUFF HERE **********************/
		
		/////////*************************** CONTROL PANEL ***************************/////////
		
		JButton addEntityBtn = new JButton("Add Entity");
		addEntityBtn.setBounds(10, 10, 200, 60);
		addEntityBtn.setBackground(new Color(144, 238, 144));
		addEntityBtn.setVisible(true);
		controlPanel.add(addEntityBtn);
		addEntityBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNewEntity = true;
				enableFreeMove = false;
				lookingForEntity = false;
				canvas.requestFocus();
			}
		});
		
		JButton selectEntityBtn = new JButton("Select Entity");
		selectEntityBtn.setBounds(10, 80, 200, 60);
		selectEntityBtn.setVisible(true);
		controlPanel.add(selectEntityBtn);
		selectEntityBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lookingForEntity = true;
				enableFreeMove = false;
				canvas.requestFocus();
			}
		});
		
		JButton addLightBtn = new JButton("Add Light");
		addLightBtn.setBounds(220, 10, 200, 60);
		addLightBtn.setBackground(new Color(144, 238, 144));
		addLightBtn.setVisible(true);
		controlPanel.add(addLightBtn);
		addLightBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		JButton selectLightBtn = new JButton("Select Light");
		selectLightBtn.setBounds(220, 80, 200, 60);
		selectLightBtn.setVisible(true);
		controlPanel.add(selectLightBtn);
		selectLightBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		JComboBox modelsCombobox = new JComboBox(new DefaultComboBoxModel(modelNames.toArray()));
		modelsCombobox.setBounds(680, 10, 200, 30);
		modelsCombobox.setVisible(true);
		controlPanel.add(modelsCombobox);
		modelsCombobox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modelName = modelsCombobox.getSelectedItem().toString();
				changeModel = true;
				canvas.requestFocus();
			}
		});
		
		
		/////////*************************** OPTIONS PANEL ***************************/////////
		
		JButton editPositionBtn = new JButton("Edit Position");
		editPositionBtn.setBounds(40, 10, 200, 40);
		editPositionBtn.setVisible(true);
		optionsPanel.add(editPositionBtn);
		editPositionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		JButton editRotationBtn = new JButton("Edit Rotation");
		editRotationBtn.setBounds(250, 10, 200, 40);
		editRotationBtn.setVisible(true);
		optionsPanel.add(editRotationBtn);
		editRotationBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		JButton deleteObjectBtn = new JButton("Delete Object");
		deleteObjectBtn.setBounds(40, 60, 200, 40);
		deleteObjectBtn.setBackground(Color.PINK);
		deleteObjectBtn.setVisible(true);
		optionsPanel.add(deleteObjectBtn);
		deleteObjectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSelectedEntity = true;
				canvas.requestFocus();
			}
		});
		
		JButton objFreeMoveBtn = new JButton("Free Move");
		objFreeMoveBtn.setBounds(250, 60, 200, 40);
		objFreeMoveBtn.setBackground(Color.CYAN);
		objFreeMoveBtn.setVisible(true);
		optionsPanel.add(objFreeMoveBtn);
		objFreeMoveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enableFreeMove = true;
				deleteSelectedEntity = false;
				lookingForEntity = false;
				canvas.requestFocus();
			}
		});
		
		/******** ###################################### *************///

		canvas = new Canvas() {
			private static final long serialVersionUID = 1L;
		};
		canvas.setPreferredSize(new Dimension(DISPLAY_WIDTH, DISPLAY_HEIGHT));
		canvas.setSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
		canvas.setIgnoreRepaint(true);

		try {
			Display.setParent(canvas);
		} catch (LWJGLException e) {
			// handle exception
			e.printStackTrace();
		}
		JPanel canvasPanel = new JPanel();
		canvasPanel.setBounds(0, 150, DISPLAY_WIDTH, DISPLAY_HEIGHT);
		canvasPanel.add(canvas);
		mainPanel.add(canvasPanel);

		frame.getContentPane().add(mainPanel);
		frame.setResizable(false);

		// frame.pack();
		frame.setVisible(true);
	}

	public static void createDisplay() {

		ContextAttribs attribs = new ContextAttribs(4, 4).withForwardCompatible(true).withProfileCore(true);

		try {
			Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
			Display.create(new PixelFormat().withSamples(8).withDepthBits(24), attribs); // setting the format
			Display.setLocation(0, 150);
			Display.setTitle("Environment 1.6 World Editor"); // setting the title
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		// telling OpenGL where to setup the display
		GL11.glViewport(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
		lastFrameTime = getCurrentTime();
	}

	public static void updateDisplay() {
		Display.sync(preferedFPS_CAP); // lets it sync with a constant fps to avoid lag
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f; // this will give us the time it took the frame to render in
															// seconds.
		lastFrameTime = currentFrameTime;
	}

	public static float getFrameTimeSeconds() {
		return delta;
	}

	public static void closeDisplay() {
		Display.destroy();
		// any additional closing statements

	}

	private static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

}
