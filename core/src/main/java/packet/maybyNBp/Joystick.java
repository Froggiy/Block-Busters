package packet.maybyNBp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Joystick {

    Texture backgroundTexture;
    private Texture knobTexture;
    private Vector2 backgroundPosition;
    private Vector2 knobPosition;
    private Vector2 centerPosition;
    float radius;
    private boolean touched;
    Batch batch;
    private int pointer;
    private Vector2 output;
    float shiftX,shiftY;

    public Joystick(Batch batch,float x, float y, float radius) {
        this.backgroundTexture = new Texture("bigjoystickxcf.png");
        this.knobTexture = new Texture("smalljoystick.png");
        this.backgroundPosition = new Vector2(x, y);
        this.knobPosition = new Vector2(x, y);
        this.centerPosition = new Vector2(x + backgroundTexture.getWidth() / 2f, y + backgroundTexture.getHeight() / 2f);
        this.radius = radius;
        this.batch = batch;
        this.touched = false;
        this.pointer = -1;
        this.output = new Vector2();
        shiftX = 0;
        shiftY = 0;
    }
}
