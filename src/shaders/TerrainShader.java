package shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import camera.EditorCamera;
import camera.FpsCamera;
import light.Light;
import utility.Maths;

public class TerrainShader extends ShaderBase {

	private static final String VERTEX_FILE = "src/shaders/terrainVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/terrainFragmentShader.txt";

	private final int MAX_LIGHTS = 10;

	private int transformationMatrixLocation;
	private int projectionMatrixLocation;
	private int viewMatrixLocation;
	private int lightPositionLocation[];
	private int lightColorLocation[];
	private int lightAttenuationLocation[];

	private int backgroundTextureLocation;
	private int rTextureLocation;
	private int gTextureLocation;
	private int bTextureLocation;
	private int blendMapLocation;

	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		transformationMatrixLocation = super.getUniformLocation("transformationMatrix");
		projectionMatrixLocation = super.getUniformLocation("projectionMatrix");
		viewMatrixLocation = super.getUniformLocation("viewMatrix");
		backgroundTextureLocation = super.getUniformLocation("backgroundTexture");
		rTextureLocation = super.getUniformLocation("rTexture");
		gTextureLocation = super.getUniformLocation("gTexture");
		bTextureLocation = super.getUniformLocation("bTexture");
		blendMapLocation = super.getUniformLocation("blendMap");
		
		lightPositionLocation = new int[MAX_LIGHTS];
		lightColorLocation = new int[MAX_LIGHTS];
		lightAttenuationLocation = new int[MAX_LIGHTS];
		for (int i = 0; i < MAX_LIGHTS; i++) {
			lightPositionLocation[i] = super.getUniformLocation("lightPosition[" + i + "]");
			lightColorLocation[i] = super.getUniformLocation("lightColor[" + i + "]");
			lightAttenuationLocation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "vertexPosition");
		super.bindAttribute(1, "textureCoordinate");
		super.bindAttribute(2, "normalVector");
	}

	public void loadLights(List<Light> lights) {
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if (i < lights.size()) {
				super.loadVector(lightPositionLocation[i], lights.get(i).getPosition());
				super.loadVector(lightColorLocation[i], lights.get(i).getColor());
				super.loadVector(lightAttenuationLocation[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector(lightPositionLocation[i], new Vector3f(0, 0, 0));
				super.loadVector(lightColorLocation[i], new Vector3f(0, 0, 0));
				super.loadVector(lightAttenuationLocation[i], new Vector3f(1, 0, 0));
			}
		}
	}

	public void connectTextureUnits() {
		super.loadInt(backgroundTextureLocation, 0);
		super.loadInt(rTextureLocation, 1);
		super.loadInt(gTextureLocation, 2);
		super.loadInt(bTextureLocation, 3);
		super.loadInt(blendMapLocation, 4);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix4(transformationMatrixLocation, matrix);
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix4(projectionMatrixLocation, matrix);
	}

	public void loadViewMatrix(FpsCamera camera) {
		Matrix4f matrix = Maths.createViewMatrix(camera);
		super.loadMatrix4(viewMatrixLocation, matrix);
	}
	
	public void loadViewMatrix(EditorCamera camera) {
		Matrix4f matrix = Maths.createViewMatrix(camera);
		super.loadMatrix4(viewMatrixLocation, matrix);
	}

}
