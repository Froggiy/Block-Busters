package packet.maybyNBp;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    Batch batch;
    FitViewport viewport;
    Vector3 touch;
    GameSCR game;
    MenuSCR menu;
    BitmapFont tittleFont;
    BitmapFont uiFont;
    BitmapFont lockedFont;
    BitmapFont unchosenFont;
    BitmapFont descriptionFont;
    int worldset;
    @Override
    public void create() {

        batch = new SpriteBatch();
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(213, 108, camera);
        tittleFont = new BitmapFont(Gdx.files.internal("fonts/20font.fnt"));
        uiFont = new BitmapFont(Gdx.files.internal("fonts/16font.fnt"));
        lockedFont = new BitmapFont(Gdx.files.internal("fonts/locked.fnt"));
        unchosenFont = new BitmapFont(Gdx.files.internal("fonts/unchosen.fnt"));
        descriptionFont = new BitmapFont(Gdx.files.internal("fonts/description.fnt"));
        touch = new Vector3();
        game = new GameSCR(this);
        menu = new MenuSCR(this);
        setScreen(menu);
    }
}
