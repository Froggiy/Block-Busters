package packet.maybyNBp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Joystick {

    Texture backgroundTexture;
    private Texture knobTexture;
    private Vector2 backgroundPosition;
    private Vector2 knobPosition;
    private Vector2 centerPosition;
    float radius;
    private boolean touched;
    Batch batch;
    private int pointer; // For multitouch support
    private Vector2 output; // Normalized output vector
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

    public void setShiftX(float tx){
        if (54 < tx && tx > 80 ) shiftX= tx;
    }

    public void setShiftY(float ty){
        if (7 < ty && ty > 27 ) shiftX= ty;
    }
    public void update(float delta) {
        if (Gdx.input.isTouched()) {
            for (int i = 0; i < 20; i++){//Gdx.input.getMaxPointers();
                if(Gdx.input.isTouched(i)){
                    Vector2 touchPos = new Vector2(Gdx.input.getX(i), Gdx.graphics.getHeight() - Gdx.input.getY(i)); // Invert Y for LibGDX's coordinate system

                    if (!touched && touchPos.dst(centerPosition) <= radius) {
                        touched = true;
                        pointer = i;
                    }

                    if (touched && pointer == i) {
                        knobPosition.set(touchPos);
                        if (knobPosition.dst(centerPosition) > radius) {
                            knobPosition.sub(centerPosition).nor().scl(radius).add(centerPosition);
                        }
                        output.set(knobPosition).sub(centerPosition).nor();
                    }
                }
            }
        } else {
            touched = false;
            pointer = -1;
            knobPosition.set(centerPosition);
            output.set(0, 0);
        }
    }

    public void draw(Batch batch,float x,float y) {
        batch.draw(backgroundTexture, x-80, y-40,radius,radius);
    }
    public float Srcx(){
        return centerPosition.x + radius/2;
    }
    public float Srcy(){
        return centerPosition.y + radius/2;
    }
    public Vector2 getOutput() {
        return output;
    }
    public boolean hit(Vector3 t){
        return backgroundPosition.x<t.x && t.x<backgroundPosition.x+backgroundTexture.getWidth() && backgroundPosition.y>t.y && t.y>backgroundPosition.y-backgroundTexture.getHeight();
    }
}
