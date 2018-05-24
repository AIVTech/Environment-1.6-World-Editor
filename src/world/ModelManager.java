package world;

import loaders.OBJLoader;
import loaders.StaticLoader;
import models.Mesh;
import models.MeshTexture;
import models.StaticModel;

public class ModelManager {
	
	public StaticModel villageHousesModel;
	public StaticModel lowPolyTree;
	public StaticModel stall;
	public StaticModel fern;
	public StaticModel pine;
	public StaticModel lamp;
	
	public ModelManager(StaticLoader loader) {
		init(loader);
	}
	
	private void init(StaticLoader loader) {
		stall = loadModel(loader, "stall", "stallTexture.png");
		villageHousesModel = loadModel(loader, "house", "house.png");
		lowPolyTree = loadModel(loader, "lowPolyTree", "lowPolyTree.png");
		fern = loadModel(loader, "fern", "fern.png");
		pine = loadModel(loader, "pine", "pine.png");
		lamp = loadModel(loader, "lamp", "lamp.png");
	}
	
	private StaticModel loadModel(StaticLoader loader, String obj, String tex) {
		Mesh mesh = OBJLoader.loadMeshFromFile(obj, loader);
		MeshTexture texture = loader.loadMeshTexture(tex);
		return new StaticModel(mesh, texture);
	}

}
