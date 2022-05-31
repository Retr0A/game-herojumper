package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();

		Loader loader = new Loader();
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrain/grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrain/sand"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrain/flowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrain/stones"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,
				rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("terrain/blendMap"));
		
		RawModel treeModel = OBJLoader.loadObjModel("treeModel", loader);
		RawModel grassModel = OBJLoader.loadObjModel("grassModel", loader);
		RawModel playerModel = OBJLoader.loadObjModel("cylinder", loader);
		
		TexturedModel treeTexturedModel = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("treeTexture")));
		TexturedModel grassTexturedModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel playerTexturedModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("playerTexture")));
		
		List<Entity> entities = new ArrayList<Entity>();
		
		Player player = new Player(playerTexturedModel, new Vector3f(100, 0, -50), 0, 0, 0, 1);
		entities.add(player);
		
		Terrain terrain1 = new Terrain(-0.5f, -1, loader, texturePack, blendMap, "heightMap");
		
		Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1,1,1));
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer();
		
		Random random = new Random();
		for (int i = 0; i < 200; i++) {
			float spawnX = random.nextFloat()*-800 - -400;
			float spawnZ = random.nextFloat() * -600;
			float spawnY = terrain1.getHeightOfTerrain(spawnX, spawnZ);
			entities.add(new Entity(treeTexturedModel, new Vector3f(spawnX, spawnY, spawnZ),0,0,0,10));
		}
		for (int i = 0; i < 200; i++) {
			float spawnX = random.nextFloat()*-800 - -400;
			float spawnZ = random.nextFloat() * -600;
			float spawnY = terrain1.getHeightOfTerrain(spawnX, spawnZ);
			entities.add(new Entity(grassTexturedModel, new Vector3f(spawnX, spawnY, spawnZ),0,0,0,1));
		}
		
		while (!Display.isCloseRequested()) {
			camera.move();
			player.move(terrain1);
			
			renderer.processTerrain(terrain1);
			for (Entity entity : entities) {
				renderer.processEntity(entity);
			}
			renderer.render(light, camera);
			
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();

		DisplayManager.closeDisplay();
	}

}
