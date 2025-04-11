package packet.maybyNBp;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Objects;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    Batch batch;
    FitViewport viewport;
    Vector3 touch;
    GameSCR game;
    MenuSCR menu;
    BitmapFont tittleFont;
    BitmapFont uiFont;
    int worldset;
    @Override
    public void create() {

        batch = new SpriteBatch();
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(213, 108, camera);
        tittleFont = new BitmapFont(Gdx.files.internal("fonts/20font.fnt"));
        uiFont = new BitmapFont(Gdx.files.internal("fonts/16font.fnt"));
        uiFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        touch = new Vector3();
        game = new GameSCR(this);
        menu = new MenuSCR(this);
        setScreen(menu);
    }
}
