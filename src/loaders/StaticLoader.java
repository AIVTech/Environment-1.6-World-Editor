package loaders;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import core.VAO;
import models.Mesh;
import models.MeshTexture;
import terrain.TerrainTexture;

public class StaticLoader {

	private List<VAO> vaos = new ArrayList<VAO>();
	private List<Integer> textures = new ArrayList<Integer>();

	private static final String texturesFolder = "Assets/Textures/";

	public Mesh loadRawMesh(float[] vertices, float[] uvs, float[] normals, int indices[]) {
		VAO vao = new VAO();
		vaos.add(vao);
		vao.bind();
		vao.bindIndices(indices);
		vao.addAttributef(0, 3, vertices);
		vao.addAttributef(1, 2, uvs);
		vao.addAttributef(2, 3, normals);
		vao.unbind();
		return new Mesh(vao.getID(), indices.length);
	}

	public MeshTexture loadMeshTexture(String filename) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream(texturesFolder + filename));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0f);

			// Anisotropic Filtering
			if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				float amount = Math.min(4.0f,
						GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
						amount);
			}

		} catch (Exception e) {
			System.out.println("Tried to load texture " + filename + ", didn't work");
			return new MeshTexture(0);
		}
		textures.add(texture.getTextureID());
		return new MeshTexture(texture.getTextureID());
	}

	public TerrainTexture loadTerrainTexture(String filename) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream(texturesFolder + filename));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0f);

			// Anisotropic Filtering
			if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				float amount = Math.min(4.0f,
						GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
						amount);
			}
		} catch (Exception e) {
			System.out.println("Tried to load texture " + filename + ", didn't work");
			return new TerrainTexture(0);
		}
		textures.add(texture.getTextureID());
		return new TerrainTexture(texture.getTextureID());
	}

	public void cleanUp() {
		// Delete VAOs
		for (VAO vao : vaos) {
			vao.delete();
		}

		// Delete textures
		for (int texture : textures) {
			GL11.glDeleteTextures(texture);
		}
	}

}
