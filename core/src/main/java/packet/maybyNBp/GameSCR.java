package packet.maybyNBp;


import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private Pool<Enemy> enemyPool = new Pool<Enemy>() {
        @Override
        protected Enemy newObject() {
            float ex = hero.x+MathUtils.random(-1000f,1000f);
            float ey = hero.y+MathUtils.random(-1000f,1000f);
            return new Enemy(ex,ey);
        }
    };;
    List<Heart> hearts = new ArrayList<Heart>();
   List<Weapon> weapons = new ArrayList<Weapon>();
    long timeSinceSpawn, intervalSpawn = 1000;
    long timeSinceHeal, intervalHeal =10500;
    long timeSinceTake, intervalTake =300;
    long timeSinceWeapon, intervalWeapon =5500;
    private static final int TILE_SIZE = 16;  // Size of each tile in pixels
    private static final int WIDTH = 200;
    private static final int HEIGHT = 150;
    private TextureRegion grassTexture1,grassTexture2, waterTexture, stoneTexture,bushTexture;
    private TextureRegion lavaTexture,redstoneTexture1, redstoneTexture2, sandTexture;

    private TiledMap tiledMap;
    private PerlinNoise perlinNoise;
    private BitmapFont uiFont;
    OrthographicCamera uiCamera;
    Texture heartTexture;
    Texture enemyTexture;
    Music music;
    int worldset;
    int killedEnemies;

    public GameSCR(Main m) {
        main = m;
        this.tittleFont = m.tittleFont;
        this.uiFont = m.uiFont;
        touch2 = new Vector3();
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false,Gdx.graphics.getWidth() ,Gdx.graphics.getHeight() );
        cameraMovement = new CameraMovement(50,50,uiFont);
        gameOverBtn = new Button(cameraMovement.x+100,cameraMovement.y+50,tittleFont,"GAME OVER!");
        map = new TmxMapLoader().load("levels/level.tmx");
        tiledMap = new TiledMap();
        heartTexture = new Texture("heart-Photoroom.png");
        grassTexture1 = new TextureRegion(new Texture("5b.png"));
        grassTexture2 = new TextureRegion(new Texture("5h.png"));
        waterTexture = new TextureRegion(new Texture("6b.png"));
        stoneTexture = new TextureRegion(new Texture("1c.png"));
        bushTexture = new TextureRegion(new Texture("4e.png"));
        lavaTexture = new TextureRegion(new Texture("2f.png"));
        lavaTexture = new TextureRegion(new Texture("2d.png"));
        sandTexture = new TextureRegion(new Texture("3h.png"));
        redstoneTexture1 = new TextureRegion(new Texture("3b.png"));
        redstoneTexture2 = new TextureRegion(new Texture("3g.png"));
        bushTexture = new TextureRegion(new Texture("4e.png"));
        joystick = new Joystick(batch,5,5,250);
        music = Gdx.audio.newMusic(Gdx.files.internal("music/foreshadow.wav"));
        music.setLooping(true);
        batch = m.batch;
        viewport = m.viewport;
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        touch = new Vector3();
        int killedEnemies;
    }

    @Override
    public void show() {
        gameStart();
        Gdx.input.setInputProcessor(new Input());
    }
    public void gameStart(){
        hero= new Hero(500,500);
        timeSinceSpawn =TimeUtils.millis();
        timeSinceTake = TimeUtils.millis();
        timeSinceWeapon = TimeUtils.millis();
        cameraMovement.timeSinceStart =TimeUtils.millis();
        cameraMovement.health = cameraMovement.maxHealth;
        music.play();
        enemyTexture = worldset == 1? new Texture("4b.png"):new Texture("3d.png");
        worldset = main.worldset;
        hero.weapon = "no";
        killedEnemies = 0;
        MapLayers layers = tiledMap.getLayers();
        TiledMapTileLayer layer = new TiledMapTileLayer(WIDTH, HEIGHT, TILE_SIZE, TILE_SIZE);
        perlinNoise = new PerlinNoise(new Random().nextInt());
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                double noiseValue = perlinNoise.noise(x * 0.1, y * 0.1);
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                // Assign tile based on noise value
                switch (worldset) {
                    case 1: {
                        if (noiseValue < -0.5) {
                            cell.setTile(new StaticTiledMapTile(stoneTexture));
                        } else if (noiseValue < 0.2) {
                            cell.setTile(new StaticTiledMapTile(grassTexture1));
                        } else if (noiseValue < 0.5) {
                            cell.setTile(new StaticTiledMapTile(grassTexture2));
                        } else if (noiseValue < 0.8) {
                            cell.setTile(new StaticTiledMapTile(bushTexture));
                        } else {
                            cell.setTile(new StaticTiledMapTile(stoneTexture));
                        }
                        break;
                    }
                    case 2: {
                        if (noiseValue < -0.5) {
                            cell.setTile(new StaticTiledMapTile(redstoneTexture1));
                        } else if (noiseValue < 0.2) {
                            cell.setTile(new StaticTiledMapTile(redstoneTexture2));
                        } else if (noiseValue < 0.5) {
                            cell.setTile(new StaticTiledMapTile(lavaTexture));
                        } else {
                            cell.setTile(new StaticTiledMapTile(sandTexture));
                        }
                        break;
                    }
                }
                layer.setCell(x, y, cell);
            }
        }
        layers.add(layer);

    }

    @Override
    public void render(float delta) {
        spawnEnemy();
        spawnWeapon();
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        switch (worldset) {
            case 1:  ScreenUtils.clear(0, 0.5f, 0.3f, 0); break;
            case 2:  ScreenUtils.clear(0.5f, 0.3f, 0, 0); break;
        }

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer.setView((OrthographicCamera) viewport.getCamera());
        renderer.render();

        // ------------------ WORLD RENDERING ------------------
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();


        if (cameraMovement.health > 0) {
            hero.move();
            batch.draw(hero.texture, hero.hitBox.x, hero.hitBox.y, 16, 16);
            if (Objects.equals(hero.weapon, "weapon")){
                batch.draw(hero.weaponTexture,hero.x+10,hero.y+10,16,16);
            }
        }
        if(cameraMovement.health < cameraMovement.maxHealth-10 && cameraMovement.health > 0 )
            spawnHeart();
        for (int h = 0; h < hearts.size(); h++){
                if (viewport.getCamera().frustum.boundsInFrustum(hearts.get(h).x,hearts.get(h).y,0,8,8,0)) {
                    batch.draw(heartTexture, hearts.get(h).x, hearts.get(h).y, 16, 16);
                    if (TimeUtils.millis() >= timeSinceTake + intervalTake && hero.isHit(hearts.get(h).hitbox))
                    {
                        cameraMovement.health += 5;
                        cameraMovement.healthText.text = Integer.toString(cameraMovement.health);
                        hearts.remove(h);
                        timeSinceTake = TimeUtils.millis();
                    }
                }


            }



                for (int e = 0; e < enemies.size(); e++) {
                    if (cameraMovement.health > 0) {
                        if( viewport.getCamera().frustum.boundsInFrustum(enemies.get(e).x+4, enemies.get(e).y+4, 0, 8, 8, 0)) {
                            batch.draw(enemyTexture, enemies.get(e).hitBox.x, enemies.get(e).hitBox.y, 16, 16);
                        }
                        if (hero.isHit(enemies.get(e).hitBox)) {
                            if(hero.weapon.equals("no")) {
                                enemies.get(e).getDamage(cameraMovement);
                                cameraMovement.healthText.text = Integer.toString(cameraMovement.health);
                            } else {
                                enemies.remove(e);
                                hero.weapon = "no";
                                killedEnemies++;
                                break;
                            }
                        }

                        enemies.get(e).move(hero);
                    } else {
                        enemies.get(e).unmove(hero);
                    }
                        for (int w = 0; w < weapons.size(); w++) {
                       if (viewport.getCamera().frustum.boundsInFrustum(weapons.get(w).x, weapons.get(w).y, 0,8,8,0)) {
                            batch.draw(hero.weaponTexture,weapons.get(w).x,weapons.get(w).y,16,16);
                            if (hero.isHit(weapons.get(w).hitbox) && !hero.weapon.equals("weapon")) {
                                weapons.remove(w);
                                hero.weapon = "weapon";
                            }
//                                    if (weapons.get(w).isHitEnemies(enemies.get(e)) && viewport.getCamera().frustum.boundsInFrustum(enemies.get(e).x, enemies.get(e).y, 0, 8, 8, 0)) {
//                                        enemies.remove(e);
//                                    }

                               }
                            }
                }

        batch.end();
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        if (cameraMovement.health > 0) {

            float screenHealthX = 20; // Move slightly right
            float screenHealthY = Gdx.graphics.getHeight() - 40; // Move lower

            float healthBarHeight = 20;
            float healthBarWidth = cameraMovement.health * 2; // Scale width

            cameraMovement.xBtn.font.getData().setScale(2f);
            cameraMovement.healthText.font.getData().setScale(2f);
            cameraMovement.timerBtn.font.getData().setScale(2f);

            //cameraMovement.xBtn.font.draw(batch, cameraMovement.xBtn.text, Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight()-40);
            batch.draw(new Texture("2b.png"), screenHealthX, screenHealthY-100, healthBarWidth*5, healthBarHeight*5);
            cameraMovement.healthText.font.draw(batch,cameraMovement.healthText.text+"/"+cameraMovement.maxHealth, screenHealthX + 10, screenHealthY + 15);
            cameraMovement.timerBtn.font.draw(batch, cameraMovement.timer(false), 50, Gdx.graphics.getHeight() - 200);
            batch.draw(joystick.backgroundTexture, joystick.Srcx()-100, joystick.Srcy()-100, joystick.radius, joystick.radius);
            joystick.update(delta);
        } else {

            GameOver();
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
        grassTexture1.getTexture().dispose();
        grassTexture2.getTexture().dispose();
        bushTexture.getTexture().dispose();
        waterTexture.getTexture().dispose();
        stoneTexture.getTexture().dispose();
        // Destroy screen's assets here.
    }
    void spawnEnemy(){
        if (enemies.size() < 30){
        if (TimeUtils.millis() >= timeSinceSpawn + intervalSpawn){
            float ex = hero.x+MathUtils.random(-1000f,1000f);
            float ey = hero.y+MathUtils.random(-1000f,1000f);
            Enemy enemy = enemyPool.obtain();
            enemy.init(ex, ey);  // Set position and update hitbox
            enemies.add(enemy);
            timeSinceSpawn = TimeUtils.millis();
        }
        }
    }
    void spawnHeart(){
        if (hearts.size() > 10) hearts.clear();
        if (TimeUtils.millis() >= timeSinceHeal + intervalHeal){
            float hx = hero.x+MathUtils.random(-50f,50f);
            float hy = hero.y+MathUtils.random(-50f,50f);
            hearts.add(new Heart(hx,hy));
            timeSinceHeal = TimeUtils.millis();
        }
    }
    private void spawnWeapon() {
        if (enemies.size() > 5 && weapons.size() < 10){
            if (TimeUtils.millis() >= timeSinceWeapon + intervalWeapon) {
                float wx = hero.x + MathUtils.random(-50f, 50f);
                float wy = hero.y + MathUtils.random(-50f, 50f);
                weapons.add(new Weapon(wx, wy));
                timeSinceWeapon = TimeUtils.millis();
            }
        }
    }


    void GameOver(){
        saveData();
        cameraMovement.gameOverBtn.font.draw(batch,cameraMovement.gameOverBtn.text,cameraMovement.x, cameraMovement.y);
        cameraMovement.endTime.font.draw(batch,cameraMovement.timer(true),cameraMovement.x, cameraMovement.y-200);
        music.stop();

    }
    void saveData(){
        Preferences preferences = Gdx.app.getPreferences("data");
        try {
            if(killedEnemies > preferences.getInteger("killed enemies")){
             preferences.putInteger("killed enemies", killedEnemies);
            }
            if(cameraMovement.timer(true).equals(preferences.getString("Time"))){
                preferences.putString("Time", cameraMovement.timer(true));
            }
        }catch (Exception e){
            preferences.putInteger("killed enemies", killedEnemies);
            preferences.putString("Time", cameraMovement.timer(true));
        }
        preferences.flush();
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
                touch2.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                uiCamera.unproject(touch);
                if (cameraMovement.health <=0){
                    main.setScreen(main.menu);

                }
            if(touch2.x > cameraMovement.x +1600 && touch2.y > cameraMovement.y+400){
                main.setScreen(main.menu);
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
