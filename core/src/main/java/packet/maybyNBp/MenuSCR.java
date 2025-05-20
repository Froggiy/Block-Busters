package packet.maybyNBp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuSCR implements Screen {

    Main main;
    Vector3 touch;
    Viewport viewport;
    Batch batch;
    Button tittle;
    Button play;
    Button statistics;
    Button maxWaves;
    Button killedEnemiesBtn;
    Button BestTime;
    Button plains;
    Button start;
    Button underground;
    Button exit;
    BitmapFont tittleFont;
    String page;
    Preferences preferences = Gdx.app.getPreferences("data");
    String time = "00:00";
    int killedEnemies = 0;
    int wave = 0;
    BitmapFont lockedFont;
    BitmapFont unchosenFont;
    BitmapFont descriptionFont;

    public MenuSCR(Main main){
        this.main = main;
        this.touch = main.touch;
        this.viewport = main.viewport;
        this.batch = main.batch;
        this.tittleFont = main.tittleFont;
        this.lockedFont = main.lockedFont;
        this.unchosenFont = main.unchosenFont;
        this.descriptionFont = main.descriptionFont;
        main.worldset = 0;
        loadData();
        page = "main";
        tittle = new Button(viewport.getWorldWidth()-100, 105,tittleFont,"Block \nBusters");
        play = new Button(10,75,tittleFont,"Play");
        statistics = new Button(10,50,tittleFont,"Statistics");
        maxWaves = new Button(10,75, tittleFont, "Max waves: " + wave);
        killedEnemiesBtn = new Button(10,50, tittleFont, "Killed enemies: " + killedEnemies);
        BestTime = new Button(10,25, tittleFont, "Best time: " + time);
        exit = new Button(10,25, tittleFont, "Exit");
        plains = new Button(8,50,tittleFont,"Plains");
        underground = new Button(8,25,tittleFont,"Underground");
        start = new Button(viewport.getWorldWidth()-65, viewport.getWorldHeight()-10, tittleFont,"Start");
    }

    @Override
    public void show() {
        loadData();
        plains.text = "Plains";
        underground.text = "Underground";

    }

    @Override
    public void render(float delta) {
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        if(Gdx.input.justTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.getCamera().unproject(touch);
            System.out.println(touch.x + " " + touch.y);
            switch (page) {
                case "main": {
                    if (play.hit(touch)) {
                        page = "worldChose";
                    }
                    if(statistics.hit(touch)){
                        page = "statistics";
                    }
                    if (exit.hit(touch)) {
                        Gdx.app.exit();
                    }
                    break;
                }
                case "worldChose": {
                    if (plains.hit(touch)) {
                        main.worldset = 1;
                        start.text = "Start";
                        start.x = viewport.getWorldWidth()-65;
                    }
                    if (underground.hit(touch) ) {
                        if (wave>=10) main.worldset = 2;
                        else {
                            start.font = descriptionFont;
                            start.text = "Survive 10 waves \n to unlock";
                            start.x = viewport.getWorldHeight()-20;
                        }
                    }
                    if (start.hit(touch) && main.worldset != 0) {
                        main.setScreen(main.game);
                        page = "main";
                        main.worldset=0;

                    }
                    break;
                }
                case "statistics": {
                    if (Gdx.input.isTouched()) page = "main";
                    break;
                }
            }
        }
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ScreenUtils.clear(Color.CYAN);
        switch (main.worldset){
            case 0: {
                plains.font = unchosenFont;
                underground.font = wave>=10?unchosenFont: lockedFont;
                break;
            }
            case 1: {
                plains.font = tittleFont;
                underground.font = wave>=10?unchosenFont: lockedFont;
                break;
            }
            case 2: {
                plains.font = unchosenFont;
                underground.font = tittleFont;
                break;
            }
        }
        viewport.getCamera().update();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        viewport.getCamera().update();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        switch (page) {
            case"main": {
                tittle.font.draw(batch, tittle.text, tittle.x, tittle.y);
                play.font.draw(batch, play.text, play.x, play.y);
                statistics.font.draw(batch,statistics.text, statistics.x, statistics.y);
                exit.font.draw(batch, exit.text, exit.x, exit.y);
                break;
            }
            case"worldChose": {
                plains.font.draw(batch, plains.text, plains.x, plains.y);
                underground.font.draw(batch, underground.text, underground.x, underground.y);
                start.font.draw(batch, start.text, start.x, start.y);
                break;
            }
            case "statistics":{
                maxWaves.font.draw(batch, maxWaves.text, maxWaves.x, maxWaves.y);
                killedEnemiesBtn.font.draw(batch, killedEnemiesBtn.text, killedEnemiesBtn.x, killedEnemiesBtn.y);
                BestTime.font.draw(batch, BestTime.text, BestTime.x, BestTime.y);
                break;
            }
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.apply();    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public void loadData(){
        try {
            time = preferences.getString("Best time");
            killedEnemies = preferences.getInteger("killed enemies");
            wave = preferences.getInteger("Max waves");
        } catch (Exception e) {
            time = "00:00";
            killedEnemies = 0;
            wave = 0;
        }
        }
}
