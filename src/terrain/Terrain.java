package terrain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import loaders.StaticLoader;
import models.Mesh;
import utility.Maths;

public class Terrain {

	private final float SIZE = 4096;
	private int VERTEX_COUNT = 1024;
	private float[][] heights;

	private float x, z;
	private Mesh mesh;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	
	public List<TerrainPoint> vertices = new ArrayList<TerrainPoint>();

	public Terrain(int gridX, int gridZ, StaticLoader loader) {
		this.texturePack = new TerrainTexturePack(new TerrainTexture(0), new TerrainTexture(0), new TerrainTexture(0), new TerrainTexture(0));
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.mesh = generateTerrain(loader);
	}
	
	public Terrain(int gridX, int gridZ, StaticLoader loader, String filepath) {
		this.texturePack = new TerrainTexturePack(new TerrainTexture(0), new TerrainTexture(0), new TerrainTexture(0), new TerrainTexture(0));
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		loadFromFile(filepath, loader);
	}

	private Mesh generateTerrain(StaticLoader loader) {
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer * 3 + 1] = 0;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadRawMesh(vertices, textureCoords, normals, indices);
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}
	
	public TerrainTexture getBlendMap() {
		return blendMap;
	}
	
	private Mesh loadPreBuiltTerrain(List<Float> vertexFloats, List<Float> normalArray, int vertexCount,
			StaticLoader loader) {
		vertices.clear();
		this.VERTEX_COUNT = vertexCount;
		int count = VERTEX_COUNT * VERTEX_COUNT;
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		float[] vertexArray = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				float pointHeight = vertexFloats.get(vertexPointer * 3 + 1);
				TerrainPoint vertex = new TerrainPoint(vertexFloats.get(vertexPointer * 3), vertexFloats.get(vertexPointer * 3 + 2));
				heights[j][i] = pointHeight;
				vertex.setHeight(pointHeight);
				vertices.add(vertex);
				normals[vertexPointer * 3] = normalArray.get(vertexPointer * 3);
				normals[vertexPointer * 3 + 1] = normalArray.get(vertexPointer * 3 + 1);
				normals[vertexPointer * 3 + 2] = normalArray.get(vertexPointer * 3 + 2);
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}

		vertexArray = getVerticesFromList(vertices);

		return loader.loadRawMesh(vertexArray, textureCoords, normals, indices);
	}
	
	private float[] getVerticesFromList(List<TerrainPoint> vertices) {
		float[] vertexArray = new float[vertices.size() * 3];
		int vertexPointer = 0;
		for (TerrainPoint vertex : vertices) {
			vertexArray[vertexPointer * 3] = vertex.x;
			vertexArray[vertexPointer * 3 + 1] = vertex.getHeight();
			vertexArray[vertexPointer * 3 + 2] = vertex.z;
			vertexPointer++;
		}
		return vertexArray;
	}
	
	public void loadFromFile(String filepath, StaticLoader loader) {
		FileReader isr = null;
		File terFile = new File(filepath);

		try {
			isr = new FileReader(terFile);
		} catch (FileNotFoundException e) {
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(isr);
		} catch(Exception e) {
			System.err.println("Could not load terrain:  " + filepath);
		}
		String line;

		try {
			String vertexCountLine = reader.readLine();
			String[] parsedLine = vertexCountLine.split("\\s+");
			int vertex_count = Integer.valueOf(parsedLine[1]);

			List<Float> vertexFloats = new ArrayList<Float>();
			List<Float> normals = new ArrayList<Float>();
			while (true) {
				line = reader.readLine();
				try {
					if (line.startsWith("-blend_map ")) {
						String[] currentLine = line.split("\\s+");
						String filename = currentLine[1];
						loadBlendMap(filename, loader);
					}
					if (line.startsWith("-background_texture ")) {
						String[] currentLine = line.split("\\s+");
						String filename = currentLine[1];
						loadBackgroundTexture(filename, loader);
					}
					if (line.startsWith("-r_texture ")) {
						String[] currentLine = line.split("\\s+");
						String filename = currentLine[1];
						loadRTexture(filename, loader);
					}
					if (line.startsWith("-g_texture ")) {
						String[] currentLine = line.split("\\s+");
						String filename = currentLine[1];
						loadGTexture(filename, loader);
					}
					if (line.startsWith("-b_texture ")) {
						String[] currentLine = line.split("\\s+");
						String filename = currentLine[1];
						loadBTexture(filename, loader);
					}
					
					if (line.startsWith("-point ")) {
						String[] currentLine = line.split("\\s+");
						float xPos = Float.valueOf(currentLine[1]);
						float height = Float.valueOf(currentLine[2]);
						float zPos = Float.valueOf(currentLine[3]);
						vertexFloats.add(xPos);
						vertexFloats.add(height);
						vertexFloats.add(zPos);
						normals.add(0f);
						normals.add(1f);
						normals.add(0f);
					}
					if (line.startsWith("-end")) {
						break;
					}
				} catch (NullPointerException ne) {
					return;
				}
			}

			this.mesh = loadPreBuiltTerrain(vertexFloats, normals, vertex_count, loader);
		} catch (IOException e) {
			return;
		}
	}
	
	public float getHeightOfTerrain(float worldX, float worldZ) {
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		float gridSquareSize = SIZE / ((float) heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

		if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
			return 0;
		}

		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float answer;
        
        if (xCoord <= (1-zCoord)) {
            answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ], 0), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
           
        return answer;
	}
	
	private void loadBlendMap(String blendMapFilePath, StaticLoader loader) {
		this.blendMap = loader.loadTerrainTexture(blendMapFilePath);
	}
	
	private void loadRTexture(String filepath, StaticLoader loader) {
		this.texturePack.setrTexture(loader.loadTerrainTexture(filepath));
	}
	
	private void loadGTexture(String filepath, StaticLoader loader) {
		this.texturePack.setgTexture(loader.loadTerrainTexture(filepath));
	}
	
	private void loadBTexture(String filepath, StaticLoader loader) {
		this.texturePack.setbTexture(loader.loadTerrainTexture(filepath));
	}
	
	private void loadBackgroundTexture(String filepath, StaticLoader loader) {
		this.texturePack.setBackgroundTexture(loader.loadTerrainTexture(filepath));
	}
}
