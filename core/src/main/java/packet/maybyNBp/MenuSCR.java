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
    Button plains;
    Button start;
    Button underground;
    Button exit;
    BitmapFont tittleFont;
    String page;
    Preferences preferences = Gdx.app.getPreferences("data");
    String time = "00:00";
    int killedEnemies = 0;

    public MenuSCR(Main main){
        this.main = main;
        this.touch = main.touch;
        this.viewport = main.viewport;
        this.batch = main.batch;
        this.tittleFont = main.tittleFont;
        main.worldset = 0;
        loadData();
        page = "main";
        tittle = new Button(viewport.getWorldWidth()-100, 105,tittleFont,"STRELALKA \nKRYTAYA");
        play = new Button(20,50,tittleFont,"Play");
        exit = new Button(20,25, tittleFont, "Exit");
        plains = new Button(8,50,tittleFont,"Plains");
        underground = new Button(8,25,tittleFont,"Underground(15 to get)");
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
                    if (exit.hit(touch)) {
                        Gdx.app.exit();
                    }
                    break;
                }
                case "worldChose": {
                    if (plains.hit(touch)) {
                        main.worldset = 1;
                        underground.text = "Underground";
                        plains.text = "Plains(selected)";
                    }
                    if (underground.hit(touch) && preferences.getInteger("killed enemies") > 15) {
                        main.worldset = 2;
                        plains.text = "Plains";
                        underground.text = "Underground(selected)";
                    }
                    if (start.hit(touch) && main.worldset != 0) {
                        main.setScreen(main.game);
                        page = "main";
                        main.worldset=0;
                    }
                }
            }
        }
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ScreenUtils.clear(Color.CYAN);
        batch.begin();
        switch (page) {
            case"main": {
                tittle.font.draw(batch, tittle.text, tittle.x, tittle.y);
                play.font.draw(batch, play.text, play.x, play.y);
                exit.font.draw(batch, exit.text, exit.x, exit.y);
                break;
            }
            case"worldChose": {
                plains.font.draw(batch, plains.text, plains.x, plains.y);
                underground.font.draw(batch, underground.text, underground.x, underground.y);
                start.font.draw(batch, start.text, start.x, start.y);
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
            time = preferences.getString("Time");
            killedEnemies = preferences.getInteger("killed enemies");
        } catch (Exception e) {
            time = "00:00";
            killedEnemies = 0;
        }
        }
}
