package packet.maybyNBp;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
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
    UI UI;
    FitViewport viewport;
    List<Enemy> enemies = new ArrayList<>();
    List<Ammo> enemyShoots = new ArrayList<>();
    List<Heart> hearts = new ArrayList<>();
    List<Weapon> weapons = new ArrayList<>();
    List<Ammo> ammo = new ArrayList<>();
    long timeSinceSpawn, intervalSpawn = 1000;
    long timeSinceHeal, intervalHeal = 10500;
    long timeSinceShoot, intervalShoot = 3000;
    long timeSinceTake, intervalTake = 300;
    long timeSinceWeapon, intervalWeapon = 30500;
    private static final int TILE_SIZE = 16;  // Size of each tile in pixels
    private static final int WIDTH = 200;
    private static final int HEIGHT = 150;
    private TextureRegion grassTexture1, grassTexture2, waterTexture, stoneTexture, bushTexture;
    private TextureRegion lavaTexture, redstoneTexture1, redstoneTexture2, sandTexture;

    private TiledMap tiledMap;
    private PerlinNoise perlinNoise;
    private BitmapFont uiFont;
    OrthographicCamera uiCamera;
    Texture heartTexture;
    Texture goldenHeartTexture;
    Music music;
    int worldset;
    int killedEnemies;
    Enemy closestEnemy;

    public GameSCR(Main m) {
        main = m;
        this.tittleFont = m.tittleFont;
        this.uiFont = m.uiFont;
        touch2 = new Vector3();
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        UI = new UI(50, 50, uiFont);
        gameOverBtn = new Button(UI.x + 100, UI.y + 50, tittleFont, "GAME OVER!");
        map = new TmxMapLoader().load("levels/level.tmx");
        tiledMap = new TiledMap();
        heartTexture = new Texture("heart.png");
        goldenHeartTexture = new Texture("goldheart.png");
        grassTexture1 = new TextureRegion(new Texture("5b.png"));
        grassTexture2 = new TextureRegion(new Texture("5h.png"));
        waterTexture = new TextureRegion(new Texture("6b.png"));
        stoneTexture = new TextureRegion(new Texture("1c.png"));
        bushTexture = new TextureRegion(new Texture("4e.png"));
        lavaTexture = new TextureRegion(new Texture("2d.png"));
        sandTexture = new TextureRegion(new Texture("3h.png"));
        redstoneTexture1 = new TextureRegion(new Texture("3b.png"));
        redstoneTexture2 = new TextureRegion(new Texture("3g.png"));
        bushTexture = new TextureRegion(new Texture("4e.png"));
        joystick = new Joystick(batch, 5, 5, 250);

        batch = m.batch;
        viewport = m.viewport;
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        touch = new Vector3();
        int killedEnemies;
        closestEnemy = new Enemy(0, 0,1);

    }

    @Override
    public void show() {
        gameStart();
        Gdx.input.setInputProcessor(new Input());
    }

    public void gameStart() {
        hero = new Hero(500,500);
        UI.x = hero.x;
        UI.y = hero.y;
        timeSinceSpawn = TimeUtils.millis();
        timeSinceTake = TimeUtils.millis();
        timeSinceWeapon = TimeUtils.millis();
        UI.timeSinceStart = TimeUtils.millis();
        UI.maxHealth = 100;
        UI.health = UI.maxHealth;
        UI.healthText.text = Integer.toString(UI.health);
        worldset = main.worldset;
        hero.weapon = "no";
        killedEnemies = 0;
        MapLayers layers = tiledMap.getLayers();
        TiledMapTileLayer layer = new TiledMapTileLayer(WIDTH, HEIGHT, TILE_SIZE, TILE_SIZE);
        perlinNoise = new PerlinNoise(new Random().nextInt());
        music = Gdx.audio.newMusic(worldset==1?Gdx.files.internal("music/luliby.mp3"):Gdx.files.internal("music/foreshadow.wav"));
        music.setLooping(true);
        music.play();
        enemies.clear();
        hearts.clear();
        weapons.clear();
        ammo.clear();
            for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                double noiseValue = perlinNoise.noise(x * 0.1, y * 0.1);
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

                StaticTiledMapTile stone = new StaticTiledMapTile(stoneTexture);
                stone.getProperties().put("solid", true);
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
                            cell.setTile(stone);
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
        viewport.getCamera().position.set(UI.x + 8, UI.y + 8, 0);
        viewport.getCamera().update();

        if (hero.x > 0) hero.moveX();
        else hero.x = 1;
        if (hero.x < viewport.getScreenWidth()+105) hero.moveX();
        else hero.x = viewport.getScreenWidth()+104;
        if (hero.y > 0) hero.moveY();
        else hero.y = 1;
        if(hero.y < viewport.getScreenHeight()+50)hero.moveX();
        else hero.y = viewport.getScreenHeight()+49;

            if (hero.x - 90 > 0 && hero.x < viewport.getScreenWidth()) {UI.moveX();}
            if (hero.y - 35 > 0 && hero.y < viewport.getScreenHeight()) {UI.moveY();}

        UI.approach(hero);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        switch (worldset) {
            case 1:
                ScreenUtils.clear(0, 0.5f, 0.3f, 0);
                break;
            case 2:
                ScreenUtils.clear(0.5f, 0.3f, 0, 0);
                break;
        }

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer.setView((OrthographicCamera) viewport.getCamera());
        renderer.render();

        // ------------------ WORLD RENDERING ------------------
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();


        if (UI.health > 0) {
            batch.draw(hero.texture, hero.hitBox.x, hero.hitBox.y, 16, 16);
            if(hero.weaponUses>=0) {
                if (Objects.equals(hero.weapon, "sword")) {
                    batch.draw(hero.weaponTexture, hero.x + 10, hero.y + 10, 16, 16);
                }
                if (hero.weapon.equals("staff")) {
                    batch.draw(hero.staffTexture, hero.x + 10, hero.y + 10, 16, 16);
                }
            } else hero.weapon = "no";
        }
        if (UI.health < UI.maxHealth - 10 && UI.health > 0)
            spawnHeart();
        for (int h = 0; h < hearts.size(); h++) {
            if (viewport.getCamera().frustum.boundsInFrustum(hearts.get(h).x, hearts.get(h).y, 0, 8, 8, 0)) {
                batch.draw(hearts.get(h).isGolden? goldenHeartTexture :heartTexture, hearts.get(h).x, hearts.get(h).y, 16, 16);
                if (TimeUtils.millis() >= timeSinceTake + intervalTake && hero.isHit(hearts.get(h).hitbox)) {
                    if(hearts.get(h).isGolden) {
                        UI.maxHealth +=5;
                        UI.health +=2;
                    }
                    else UI.health += 5;
                    UI.healthText.text = Integer.toString(UI.health);
                    hearts.remove(h);
                    timeSinceTake = TimeUtils.millis();
                }
            }


        }


        for (int e = 0; e < enemies.size(); e++) {
            if (Math.abs((enemies.get(e).x + enemies.get(e).y) - (hero.x + hero.y)) <= Math.abs((closestEnemy.x + closestEnemy.y) - (hero.x + hero.y))) {
                closestEnemy = enemies.get(e);
            }

            if (UI.health > 0) {
                if (viewport.getCamera().frustum.boundsInFrustum(enemies.get(e).x + enemies.get(e).size/2, enemies.get(e).y + enemies.get(e).size/2, 0, enemies.get(e).size/2, enemies.get(e).size/2, 0)) {
                    batch.draw(enemies.get(e).texture, enemies.get(e).hitBox.x, enemies.get(e).hitBox.y, enemies.get(e).size, enemies.get(e).size);
                    if(enemies.get(e).size == 32 && enemies.get(e).doShoot()) enemyShoots.add(new Ammo(enemies.get(e).x,enemies.get(e).y));
                }
                if (hero.isInRange(enemies.get(e).hitBox) && hero.weapon.equals("staff")) {
                    spawnAmmo();
                }
                if (hero.isHit(enemies.get(e).hitBox)) {
                    switch(hero.weapon){
                        case "sword":
                                enemies.get(e).health-=2;
                                hero.weaponUses--;
                                break;
                        case "no":
                            enemies.get(e).getDamage(UI);
                            UI.healthText.text = Integer.toString(UI.health);
                            break;
                    }
                    if(enemies.get(e).health<=0){
                        enemies.remove(e);
                        killedEnemies++;
                        break;
                    }
                }
               //for (int s = 0; s < enemyShoots.size(); s++){
               //    enemyShoots.get(s).pos.lerp(new Vector2(hero.x,hero.y),0.0005f);
               //    if (viewport.getCamera().frustum.boundsInFrustum(enemyShoots.get(s).pos.x, enemyShoots.get(e).pos.y, 0, 8, 8, 0)) {
               //        batch.draw(sandTexture, enemyShoots.get(s).pos.x, enemyShoots.get(e).pos.y, 16, 16);
               //        if (hero.isHit(enemyShoots.get(s).hitbox)) {
               //            UI.health--;
               //            enemyShoots.remove(s);
               //            break;
               //        }
               //    }
               //}
                enemies.get(e).move(hero);
            } else {
                enemies.get(e).unmove(hero);
            }
            if (hero.weaponUses > 0) {
                for (int a = 0; a < ammo.size(); a++) {
                    ammo.get(a).pos.lerp(new Vector2(closestEnemy.x, closestEnemy.y), 0.002f);
                    if (viewport.getCamera().frustum.boundsInFrustum(ammo.get(a).pos.x, ammo.get(a).pos.y, 0, 8, 8, 0)) {
                        batch.draw(hero.ammoTexture, ammo.get(a).pos.x, ammo.get(a).pos.y, 16, 16);
                    }
                    if (hero.isInRange(enemies.get(e).hitBox)) {
                        ammo.get(a).setHitbox();
                    }
                    if (enemies.get(e).isHit(ammo.get(a).hitbox)) {
                        enemies.get(e).health-=3;
                        hero.weaponUses--;
                        ammo.remove(a);
                        if(enemies.get(e).health<=0){
                            enemies.remove(e);
                            killedEnemies++;
                        }
                        break;
                    }

                }
            }
            for (int w = 0; w < weapons.size(); w++) {
                if (viewport.getCamera().frustum.boundsInFrustum(weapons.get(w).x, weapons.get(w).y, 0, 8, 8, 0)) {
                    batch.draw(Objects.equals(weapons.get(w).type, "sword") ? hero.weaponTexture : hero.staffTexture, weapons.get(w).x, weapons.get(w).y, 16, 16);
                    if (hero.isHit(weapons.get(w).hitbox) && !hero.weapon.equals(weapons.get(w).type)) {
                        hero.weapon = weapons.get(w).type;
                        hero.weaponUses += weapons.get(w).uses;
                        weapons.remove(w);
                        break;
                    }
                }
            }
        }

        batch.end();
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        if (UI.health > 0) {

            float screenHealthX = 20; // Move slightly right
            float screenHealthY = Gdx.graphics.getHeight() - 40; // Move lower

            float healthBarHeight = 20;
            float healthBarWidth = UI.health * 2; // Scale width

            UI.xBtn.font.getData().setScale(2f);
            UI.healthText.font.getData().setScale(2f);
            UI.timerBtn.font.getData().setScale(2f);

            //cameraMovement.xBtn.font.draw(batch, cameraMovement.xBtn.text, Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight()-40);
            batch.draw(new Texture("2b.png"), screenHealthX, screenHealthY - 100, healthBarWidth * 5, healthBarHeight * 5);
            UI.healthText.font.draw(batch, UI.healthText.text + "/" + UI.maxHealth, screenHealthX + 10, screenHealthY + 15);
            UI.waveBtn.font.draw(batch, UI.waveBtn.text,screenHealthX+1150,screenHealthY+25);
            UI.timerBtn.font.draw(batch, UI.timer(false), 50, Gdx.graphics.getHeight() - 200);
            batch.draw(joystick.backgroundTexture, joystick.Srcx() - 100, joystick.Srcy() - 100, joystick.radius, joystick.radius);
            joystick.update(delta);
        } else {

            GameOver();
        }

        batch.end(); // End UI rendering
        closestEnemy = new Enemy(0, 0);
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

    void spawnEnemy() {
            int type = MathUtils.random(1, 100);
            if (TimeUtils.millis() >= timeSinceSpawn + intervalSpawn && type < UI.wave*300) {
                float ex = hero.x + MathUtils.random(-1000f, 1000f);
                float ey = hero.y + MathUtils.random(-1000f, 1000f);
                enemies.add(new Enemy(ex, ey,type));
                UI.wave -= type;
                timeSinceSpawn = TimeUtils.millis();
            }
    }

    void spawnHeart() {
        if (hearts.size() > 10) hearts.clear();
        if (TimeUtils.millis() >= timeSinceHeal + intervalHeal) {
            float hx = hero.x + MathUtils.random(-50f, 50f);
            float hy = hero.y + MathUtils.random(-50f, 50f);
            hearts.add(new Heart(hx, hy));
            timeSinceHeal = TimeUtils.millis();
        }
    }

    private void spawnWeapon() {
        if (enemies.size() > 5 && weapons.size() < 10) {
            if (TimeUtils.millis() >= timeSinceWeapon + intervalWeapon) {
                float wx = hero.x + MathUtils.random(-50f, 50f);
                float wy = hero.y + MathUtils.random(-50f, 50f);
                weapons.add(new Weapon(wx, wy));
                timeSinceWeapon = TimeUtils.millis();
            }
        }
    }

    private void spawnAmmo() {
        if (TimeUtils.millis() >= timeSinceShoot + intervalShoot && hero.weapon.equals("staff")) {
            float sx = hero.x + MathUtils.random(-20f, 20f);
            float sy = hero.y + MathUtils.random(-20f, 20f);
            ammo.add(new Ammo(sx, sy));
            hero.weaponUses--;
            timeSinceShoot = TimeUtils.millis();
        }
    }






    void GameOver(){
        saveData();
        UI.gameOverBtn.font.draw(batch, UI.gameOverBtn.text, UI.x, UI.y);
        UI.endTime.font.draw(batch, UI.timer(true), UI.x, UI.y-200);
        music.stop();
        ammo.clear();
        hearts.clear();
        weapons.clear();
    }
    void saveData(){
        Preferences preferences = Gdx.app.getPreferences("data");
        try {
            if(UI.wave > preferences.getInteger("Max waves")){
                preferences.putInteger("Max waves", UI.wave);
            }
             preferences.putInteger("killed enemies", preferences.getInteger("killed enemies")+killedEnemies);

            if(UI.timer(true).hashCode()>preferences.getString("Best time").hashCode()){
                preferences.putString("Best time", UI.timer(true));
            }
        }catch (Exception e){
            preferences.putInteger("waves", UI.wave);
            preferences.putInteger("killed enemies", killedEnemies);
            preferences.putString("Best time", UI.timer(true));
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
                if (UI.health <=0){
                    main.setScreen(main.menu);

                }
            if(touch2.x > UI.x +1600 && touch2.y > UI.y+400){
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
            if((touch.x < UI.x -50 && touch.x > UI.x - 75)) {
                    joystick.shiftX = (UI.x - touch.x);
                    hero.vx = -(joystick.shiftX - 63) / 10;
                }
            if(touch.y < UI.y-10){
                    joystick.shiftY = (UI.y - touch.y);
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
