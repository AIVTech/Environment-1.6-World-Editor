package world;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import light.Light;
import loaders.StaticLoader;
import terrain.Terrain;

public class World {
	
	public ModelManager models;
	
	public List<Entity> entities = new ArrayList<Entity>();
	public List<Terrain> terrains = new ArrayList<Terrain>();
	public List<Light> lights = new ArrayList<Light>();
	
	public World(StaticLoader loader) {
		models = new ModelManager(loader);
		init(loader);
	}
	
	private void init(StaticLoader loader) {
		loadTerrains(loader);
		loadEntities();
		loadLights();
	}
	
	private void loadTerrains(StaticLoader loader) {
		Terrain terrain = new Terrain(0, -1, loader, "Assets/Terrains/forestTown.ter");
		terrains.add(terrain);
	}
	
	private void loadEntities() {
		Entity house = new Entity(models.villageHousesModel, new Vector3f(400, terrains.get(0).getHeightOfTerrain(400, -758), -758), 0, 180, 0, 2.8f);
		entities.add(house);
		
		entities.add(new Entity(models.lamp, new Vector3f(400, terrains.get(0).getHeightOfTerrain(400, -788), -788), 0, 0, 0, 0.8f));
		entities.add(new Entity(models.lamp, new Vector3f(400, terrains.get(0).getHeightOfTerrain(400, -848), -848), 0, 0, 0, 0.8f));
		entities.add(new Entity(models.lamp, new Vector3f(400, terrains.get(0).getHeightOfTerrain(400, -898), -898), 0, 0, 0, 0.8f));
		entities.add(new Entity(models.lamp, new Vector3f(400, terrains.get(0).getHeightOfTerrain(400, -948), -948), 0, 0, 0, 0.8f));
	}
	
	private void loadLights() {
		Light sun = new Light(new Vector3f(600, 400, -600), new Vector3f(0.8f, 0.8f, 0.8f));
		Light light2 = new Light(new Vector3f(400, terrains.get(0).getHeightOfTerrain(400, -788)+12.6f, -788), new Vector3f(0, 1, 0), new Vector3f(0.001f, 0.001f, 0.002f));
		Light light3 = new Light(new Vector3f(400, terrains.get(0).getHeightOfTerrain(400, -848)+13.0f, -848), new Vector3f(0, 0, 1), new Vector3f(0.001f, 0.001f, 0.002f));
		Light light4 = new Light(new Vector3f(400, terrains.get(0).getHeightOfTerrain(400, -898)+12.6f, -898), new Vector3f(1, 0, 0), new Vector3f(0.001f, 0.001f, 0.002f));
		Light light5 = new Light(new Vector3f(400, terrains.get(0).getHeightOfTerrain(400, -948)+12.6f, -948), new Vector3f(1, 1, 0), new Vector3f(0.001f, 0.001f, 0.002f));
		
		lights.add(sun);
		lights.add(light2);
		lights.add(light3);
		lights.add(light4);
		lights.add(light5);
	}
}
