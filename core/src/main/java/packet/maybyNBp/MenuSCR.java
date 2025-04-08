package packet.maybyNBp;

import com.badlogic.gdx.Gdx;
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
    Button exit;
    BitmapFont tittleFont;

    public MenuSCR(Main main){
        this.main = main;
        this.touch = main.touch;
        this.viewport = main.viewport;
        this.batch = main.batch;
        this.tittleFont = main.tittleFont;
        tittle = new Button(110,105,tittleFont,"STRELALKA \nKRYTAYA");
        play = new Button(20,50,tittleFont,"Play");
        exit = new Button(20,25, tittleFont, "Exit");
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.getCamera().unproject(touch);
            if(play.hit(touch)){
                main.setScreen(main.game);
            }
            if(exit.hit(touch)){
                Gdx.app.exit();
            }
        }
        batch.setProjectionMatrix(viewport.getCamera().combined);
        viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),true);
        ScreenUtils.clear(Color.CYAN);
        batch.begin();
        tittle.font.draw(batch,tittle.text,tittle.x, tittle.y);
        play.font.draw(batch,play.text,play.x, play.y);
        exit.font.draw(batch,exit.text,exit.x, exit.y);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.setScreenSize(width, height);
    }

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

    }
}
