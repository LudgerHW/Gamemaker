package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
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
		
		//**TERRAIN TEXTURE STUFF******
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		
		//*****************************
		
		TexturedModel tree = new TexturedModel(OBJLoader.loadObjModel("tree", loader), new ModelTexture(loader.loadTexture("tree")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), 
				new ModelTexture(loader.loadTexture("grassTexture")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernTextureAtlas);
		fern.getTexture().setHasTransparency(true);

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		//Terrain terrain2 = new Terrain(-1 , -1, loader, texturePack, blendMap, "heightmap");
		
		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for(int i = 0; i < 400; i++){
			if(i % 20 == 0){
				float x = random.nextFloat() * 800 - 400;
				float z = random.nextFloat() * -600;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1));				
			}
			if(i % 5 == 0){
				float x = random.nextFloat() * 800 - 400;
				float z = random.nextFloat() * -600;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(tree, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 3));
				
				x = random.nextFloat() * -600;
				z = random.nextFloat() * 800 - 400;
				y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(grass, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1));
			}
		}
		
		Light light = new Light(new Vector3f(20000, 40000, 20000), new Vector3f(1, 1, 1));
		
		
		MasterRenderer renderer = new MasterRenderer();
		
		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("playerTexture")));
		stanfordBunny.getTexture().setReflectivity(0.1f);
		stanfordBunny.getTexture().setShineDamper(0.2f);
		
		Player player = new Player(stanfordBunny, new Vector3f(-10, 0, -100), 0, 0, 0, 0.5f);
		Camera camera = new Camera(player);
		
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);
		GuiTexture gui2 = new GuiTexture(loader.loadTexture("thinmatrix"), new Vector2f(0.3f, 0.58f), new Vector2f(0.4f, 0.4f));
		guis.add(gui2);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		while(!Display.isCloseRequested()){
			//game logic
			camera.move();
			player.move(terrain);
			
			renderer.processTerrain(terrain);
			//renderer.processTerrain(terrain2);
			for(Entity entity:entities){
				renderer.processEntity(entity);				
			}
			renderer.processEntity(player);
			renderer.render(light,camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
			
		}
		
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		
	}

}
