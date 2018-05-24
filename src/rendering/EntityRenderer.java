package rendering;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.Mesh;
import models.StaticModel;
import shaders.StaticEntityShader;
import utility.Maths;

public class EntityRenderer {
	
	private StaticEntityShader shader;
	
	public EntityRenderer(StaticEntityShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Map<StaticModel, List<Entity>> entities) {
        for (StaticModel model : entities.keySet()) {
            prepareModel(model);
            List<Entity> batch = entities.get(model);
            for (Entity entity : batch) {
                prepareInstance(entity);
                if (entity.isHighlighted()) {
                	shader.loadHighlightedState(true);
                }
                else {
                	shader.loadHighlightedState(false);
                }
                
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getMesh().getVertexCount(),
                        GL11.GL_UNSIGNED_INT, 0);
            }
            unbindModel();
        }
    }
  
    private void prepareModel(StaticModel model) {
        Mesh mesh = model.getMesh();
        GL30.glBindVertexArray(mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
    }
  
    private void unbindModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }
  
    private void prepareInstance(Entity entity) {
    	Vector3f position = entity.getPosition();
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(position,
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }

}
