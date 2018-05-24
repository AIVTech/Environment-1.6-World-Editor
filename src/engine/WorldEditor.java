package engine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import camera.EditorCamera;
import display.DisplayManager;
import entities.Entity;
import loaders.StaticLoader;
import models.StaticModel;
import rendering.CoreRenderer;
import utility.MousePicker;
import world.World;

public class WorldEditor {

	private static Thread glThread;

	private static CoreRenderer renderer;
	private static StaticLoader loader;
	private static MousePicker mousePicker;
	private static World world;

	private static StaticModel selectedModel = null;

	private static List<Entity> newEntities = new ArrayList<Entity>();
	private static Entity selectedEntity = null;
	private static List<Entity> selectedEntities = new ArrayList<Entity>();
	private static Vector3f selectedEntityInitialPosition = null;

	// Controlling flags
	private static boolean entityAddingInProgress = false;
	private static boolean mouseClicked = false;

	private static void stopGL() {
		try {
			glThread.join();
		} catch (InterruptedException e) {
			// handle exception
			e.printStackTrace();
		}
	}

	private static void startMain() {
		glThread = new Thread(new Runnable() {
			public void run() {
				new DisplayManager().embedDisplay();
				DisplayManager.createDisplay();
				Keyboard.enableRepeatEvents(true);

				/*****************************************************/
				// ================= PREPARATIONS ====================//

				loader = new StaticLoader();
				renderer = new CoreRenderer();

				world = new World(loader);

				EditorCamera camera = new EditorCamera();

				mousePicker = new MousePicker(camera, CoreRenderer.getProjectionMatrix(), world.terrains.get(0));

				// ***************************************************//

				while (!Display.isCloseRequested()) {

					// Logic
					camera.update();
					mousePicker.update();

					// Rendering
					renderer.prepare();
					renderer.renderScene(world.entities, world.terrains, camera, world.lights);

					// Processing
					ProcessWindowInput();
					updateWorld();

					DisplayManager.updateDisplay();
				}

				// Clean Up
				renderer.cleanUp();
				loader.cleanUp();

				Display.destroy();
				stopGL();
				System.exit(0);
			}
		}, "LWJGL Thread");

		glThread.start();
	}

	public static void main(String[] args) {
		startMain();
	}

	private static void ProcessWindowInput() {
		processPlacingEntity();
		lookForEntity();
	}

	private static void updateWorld() {
		if (DisplayManager.changeModel) {					// Change model
			processModelName(DisplayManager.modelName);
			DisplayManager.changeModel = false;
		}
		if (DisplayManager.deleteSelectedEntity) {			// Delete selected entity
			if (selectedEntity != null) {
				newEntities.remove(selectedEntity);
				world.entities.remove(selectedEntity);
				DisplayManager.deleteSelectedEntity = false;
			}
		}
		if (DisplayManager.enableFreeMove) {
			if (selectedEntity != null) {
				processEntityFreeMove();
			}
		}
	}

	private static void processModelName(String name) {
		if (name == null) {
			return;
		}
		switch (name) {
		case "LowPoly Tree":
			selectedModel = world.models.lowPolyTree;
			break;
		case "Fern":
			selectedModel = world.models.fern;
			break;
		case "Pine":
			selectedModel = world.models.pine;
			break;
		case "Stall":
			selectedModel = world.models.stall;
			break;
		case "Village":
			selectedModel = world.models.villageHousesModel;
			break;

		default:
			selectedModel = null;
			break;
		}
	}

	private static void processPlacingEntity() {
		if (DisplayManager.addNewEntity) {
			if (!entityAddingInProgress) {
				entityAddingInProgress = true;
				if (selectedModel != null) {
					selectedEntity = new Entity(selectedModel,
							new Vector3f(DisplayManager.entityPosX, DisplayManager.entityPosY,
									DisplayManager.entityPosZ),
							DisplayManager.entityRotX, DisplayManager.entityRotY, DisplayManager.entityRotZ,
							DisplayManager.entityScale);
					newEntities.add(selectedEntity); // Log entity
					world.entities.add(selectedEntity); // Add to rendering entities
				}
			} 

			if (selectedEntity != null) {
				Vector3f rayPosition = mousePicker.getCurrentTerrainPoint();
				if (rayPosition != null) {
					selectedEntity.setPosition(rayPosition);
				}
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				if (entityAddingInProgress) {
					newEntities.remove(selectedEntity);
					world.entities.remove(selectedEntity);
					
					DisplayManager.addNewEntity = false;
					selectedEntity = null;
					entityAddingInProgress = false;
				}
			}
			
			if (Mouse.isButtonDown(1) && !mouseClicked && entityAddingInProgress) {
				mouseClicked = true;
				selectedEntity = null;
				entityAddingInProgress = false;
			}
			if (!Mouse.isButtonDown(1)) {
				mouseClicked = false;
			}
		}
	}
	
	private static void lookForEntity() {
		if (DisplayManager.lookingForEntity) {
			float targetRadius = 8;
			Vector3f rayPosition = mousePicker.getCurrentTerrainPoint();
			if (rayPosition != null) {
				for (Entity e : world.entities) {
					if (getDistanceBetweenObjects(rayPosition, e.getPosition()) < targetRadius) {
						e.setHighlight(true);
						selectedEntity = e;
					}
					else {
						e.setHighlight(false);
					}
				}
			}
			if (Mouse.isButtonDown(1)) {
				DisplayManager.lookingForEntity = false;
				for (Entity e : world.entities) {
					e.setHighlight(false);
					if (selectedEntity != null) {
						if (e.getPosition() == selectedEntity.getPosition() && getDistanceBetweenObjects(rayPosition, e.getPosition()) < targetRadius) {
							e.setHighlight(true);
						}
					}
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				DisplayManager.lookingForEntity = false;
				for (Entity e : world.entities) {
					e.setHighlight(false);
				}
				selectedEntity = null;
			}
		}
		if (DisplayManager.addNewEntity) {
			for (Entity e : world.entities) {
				e.setHighlight(false);
			}
		}
	}
	
	private static float getDistanceBetweenObjects(Vector3f first, Vector3f second) {
		float xDiff = (first.x - second.x) * (first.x - second.x);
		float yDiff = (first.y - second.y) * (first.y - second.y);
		float zDiff = (first.z - second.z) * (first.z - second.z);
		
		return (float) Math.sqrt((xDiff + yDiff + zDiff));
	}
	
	private static void processEntityFreeMove() {
		if (selectedEntity == null) {
			return;
		}
		
		if (selectedEntityInitialPosition == null) {
			selectedEntityInitialPosition = selectedEntity.getPosition();
		}
		
		Vector3f rayPosition = mousePicker.getCurrentTerrainPoint();
		if (rayPosition == null) {
			return;
		}
		
		selectedEntity.setPosition(rayPosition);
		if (Mouse.isButtonDown(1)) {
			DisplayManager.enableFreeMove = false;
			selectedEntity.setHighlight(false);
			selectedEntity = null;
			selectedEntityInitialPosition = null;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			selectedEntity.setPosition(selectedEntityInitialPosition);
			
			DisplayManager.enableFreeMove = false;
			selectedEntity.setHighlight(false);
			selectedEntity = null;
			selectedEntityInitialPosition = null;
		}
	}
	
}
