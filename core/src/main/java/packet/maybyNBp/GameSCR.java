package packet.maybyNBp;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** First screen of the application. Displayed after the application is created. */
public class GameSCR implements Screen {

    Main main;
    Batch batch;
    Joystick joystick;
    Vector3 touch;
    BitmapFont tittleFont;
    Button gameOverBtn;
    public static TiledMap map;
    Vector3 touch2;
    OrthogonalTiledMapRenderer renderer;
    Hero hero;
    CameraMovement cameraMovement;
    FitViewport viewport;
    List<Enemy> enemies = new ArrayList<Enemy>();
    List<Heart> hearts = new ArrayList<Heart>();
    long timeSinceSpawn, intervalSpawn = 1000;
    long timeSinceHeal, intervalHeal =10500;
    private static final int TILE_SIZE = 16;  // Size of each tile in pixels
    private static final int WIDTH = 200;
    private static final int HEIGHT = 150;
    private TextureRegion grassTexture,grassTexture1, waterTexture, stoneTexture,bushTexture,snowTexture;
    private TiledMap tiledMap;
    private PerlinNoise perlinNoise;
    private BitmapFont uiFont;
    OrthographicCamera uiCamera;
    long finalTime = 0;



    public GameSCR(Main m) {
        main = m;
        hero= new Hero(500,500);
        timeSinceSpawn =TimeUtils.millis();
        this.tittleFont = m.tittleFont;
        this.uiFont = m.uiFont;
        touch2 = new Vector3();
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false,Gdx.graphics.getWidth() ,Gdx.graphics.getHeight() );
        cameraMovement = new CameraMovement(50,50,uiFont);
        gameOverBtn = new Button(cameraMovement.x+100,cameraMovement.y+50,tittleFont,"GAME OVER!");
        map = new TmxMapLoader().load("levels/level.tmx");
        tiledMap = new TiledMap();
        MapLayers layers = tiledMap.getLayers();
        grassTexture = new TextureRegion(new Texture("5b.png"));
        grassTexture1 = new TextureRegion(new Texture("5h.png"));
        waterTexture = new TextureRegion(new Texture("6b.png"));
        stoneTexture = new TextureRegion(new Texture("1c.png"));
        bushTexture = new TextureRegion(new Texture("4e.png"));
        TiledMapTileLayer layer = new TiledMapTileLayer(WIDTH, HEIGHT, TILE_SIZE, TILE_SIZE);
        perlinNoise = new PerlinNoise(new Random().nextInt());
        joystick = new Joystick(batch,5,5,250);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                double noiseValue = perlinNoise.noise(x * 0.1, y * 0.1); // Random value between -1 and 1
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                // Assign tile based on noise value
                if (noiseValue < -0.5) {
                    cell.setTile(new StaticTiledMapTile(stoneTexture));
                } else if (noiseValue < 0.2) {
                    cell.setTile(new StaticTiledMapTile(grassTexture));
                } else if (noiseValue < 0.5) {
                    cell.setTile(new StaticTiledMapTile(grassTexture1));
                } else if (noiseValue < 0.8) {
                    cell.setTile(new StaticTiledMapTile(bushTexture));
                }
                else {
                    cell.setTile(new StaticTiledMapTile(stoneTexture));
                }
                layer.setCell(x, y, cell);
            }
        }
        layers.add(layer);

        batch = m.batch;
        viewport = m.viewport;
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        touch = new Vector3();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new Input());
    }

    @Override
    public void render(float delta) {
        spawnEnemy();

        // Update game world camera
        viewport.getCamera().position.set(cameraMovement.x + 8, cameraMovement.y + 8, 0);
        viewport.getCamera().update();

        if (hero.x - 100 > 0 && hero.x < viewport.getScreenWidth()) {
            cameraMovement.moveX();
            if (!(hero.x > 0 && hero.x < viewport.getScreenWidth() + 100)) {
                hero.stop();
            }
        }
        if (hero.y - 100 > 0 && hero.y < viewport.getScreenHeight()) {
            cameraMovement.moveY();
            if (!(hero.y > 0 && hero.y < viewport.getScreenHeight() + 100)) {
                hero.stop();
            }
        }

        cameraMovement.approach(hero);

        // Clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        ScreenUtils.clear(0, 0.5f, 0.3f, 0);

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer.setView((OrthographicCamera) viewport.getCamera());
        renderer.render();

        // ------------------ WORLD RENDERING ------------------
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        if (cameraMovement.health > 0) {
            hero.move();
            batch.draw(hero.texture, hero.hitBox.x, hero.hitBox.y, 16, 16);
        }
        if(cameraMovement.health < 80 && cameraMovement.health > 0 ){
            spawnHeart();
        for (int h = 0; h < hearts.size(); h++){
            batch.draw(new Texture("heart-Photoroom.png"), hearts.get(h).x,hearts.get(h).y,16,16);
            if (hero.isHit(hearts.get(h).hitbox) && !hearts.get(h).isUsed){
                cameraMovement.health+=5;
                cameraMovement.healthText.text = Integer.toString(cameraMovement.health);
                hearts.remove(h);
            }
        }
            }

        for (Enemy e : enemies) {
            if (cameraMovement.health > 0) {
                if (hero.isHit(e.hitBox)) {
                    e.getDamage(cameraMovement);
                    cameraMovement.healthText.text = Integer.toString(cameraMovement.health);
                }
                e.move(hero);
            } else {
                e.unmove(hero);
            }
            batch.draw(e.texture, e.hitBox.x, e.hitBox.y, 16, 16);
        }

        batch.end(); // End world rendering

        // ---------------- FIX UI SHAKING & MAKE IT BIGGER ----------------
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        if (cameraMovement.health > 0) {
            // Bigger UI elements
            float screenHealthX = 20; // Move slightly right
            float screenHealthY = Gdx.graphics.getHeight() - 40; // Move lower

            // Increase health bar size
            float healthBarHeight = 20;
            float healthBarWidth = cameraMovement.health * 2; // Scale width

            // Increase font size
            cameraMovement.xBtn.font.getData().setScale(2f);
            cameraMovement.healthText.font.getData().setScale(2f);
            cameraMovement.timerBtn.font.getData().setScale(2f);

            // Draw UI elements
            //cameraMovement.xBtn.font.draw(batch, cameraMovement.xBtn.text, Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight()-40);
            batch.draw(new Texture("2b.png"), screenHealthX, screenHealthY-100, healthBarWidth*5, healthBarHeight*5);
            cameraMovement.healthText.font.draw(batch, cameraMovement.healthText.text, screenHealthX + 10, screenHealthY + 15);
            cameraMovement.timerBtn.font.draw(batch, cameraMovement.timer(false), 50, Gdx.graphics.getHeight() - 200);
            batch.draw(joystick.backgroundTexture, joystick.Srcx()-100, joystick.Srcy()-100, joystick.radius, joystick.radius);
            joystick.update(delta);
        } else {
            //GameOver();
        }

        batch.end(); // End UI rendering
    }

    @Override
    public void resize(int width, int height) {
        viewport.setScreenSize(width, height);
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        batch.dispose();
        // Destroy screen's assets here.
    }
    void spawnEnemy(){
        if (TimeUtils.millis() >= timeSinceSpawn + intervalSpawn){
            float ex = hero.x+MathUtils.random(-1000f,1000f);
            float ey = hero.y+MathUtils.random(-1000f,1000f);
            enemies.add(new Enemy(ex,ey));
            timeSinceSpawn = TimeUtils.millis();
        }
    }
    void spawnHeart(){
        if (hearts.size() > 10) hearts.clear();
        if (TimeUtils.millis() >= timeSinceHeal + intervalHeal){
            float ex = hero.x+MathUtils.random(-100f,100f);
            float ey = hero.y+MathUtils.random(-100f,100f);
            hearts.add(new Heart(ex,ey));
            timeSinceHeal = TimeUtils.millis();
        }
    }

    void GameOver(){
        cameraMovement.gameOverBtn.font.draw(batch,cameraMovement.gameOverBtn.text,cameraMovement.x, cameraMovement.y);
        cameraMovement.endTime.font.draw(batch,cameraMovement.timer(true),cameraMovement.x, cameraMovement.y-200);

    }
    class PerlinNoise {
        private int[] permutation;

        public PerlinNoise(int seed) {
            permutation = new int[512];
            Random random = new Random(seed);
            int[] p = new int[256];
            for (int i = 0; i < 256; i++) p[i] = i;
            for (int i = 0; i < 256; i++) {
                int swap = random.nextInt(256);
                int temp = p[i];
                p[i] = p[swap];
                p[swap] = temp;
            }
            for (int i = 0; i < 512; i++) permutation[i] = p[i & 255];
        }

        public double noise(double x, double y) {
            int X = (int) Math.floor(x) & 255;
            int Y = (int) Math.floor(y) & 255;
            x -= Math.floor(x);
            y -= Math.floor(y);
            double u = fade(x);
            double v = fade(y);

            int A = permutation[X] + Y;
            int B = permutation[X + 1] + Y;

            return lerp(v, lerp(u, grad(permutation[A], x, y), grad(permutation[B], x - 1, y)),
                lerp(u, grad(permutation[A + 1], x, y - 1), grad(permutation[B + 1], x - 1, y - 1)));
        }

        private double fade(double t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        private double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        private double grad(int hash, double x, double y) {
            int h = hash & 7;
            double u = h < 4 ? x : y;
            double v = h < 4 ? y : x;
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
    }
    class Input implements InputProcessor{

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                uiCamera.unproject(touch);
                if(cameraMovement.xBtn.hit(touch)){
                    main.setScreen(main.menu);
                }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            touch2.set(screenX, screenY, 0);
            uiCamera.unproject(touch2);
            System.out.println(touch.x);
            System.out.println(touch.y);
            if(touch2.x > cameraMovement.x +1600 && touch2.y > cameraMovement.y+400){
                main.setScreen(main.menu);
            }
            hero.stop();
            return false;
        }

        @Override
        public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            touch.set(screenX, screenY, 0);
            viewport.getCamera().unproject(touch);
            if(touch.x < cameraMovement.x -50 && touch.x > cameraMovement.x - 75) {
                joystick.shiftX = (cameraMovement.x - touch.x);
                hero.vx = -(joystick.shiftX - 63) / 10;
            }
            if(touch.y < cameraMovement.y-10){
                joystick.shiftY = (cameraMovement.y - touch.y);
                hero.vy = -(joystick.shiftY - 25) / 10;
            }


            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            return false;
        }
    }
}
