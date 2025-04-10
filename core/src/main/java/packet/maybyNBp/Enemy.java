package packet.maybyNBp;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

public class Enemy implements Pool.Poolable {
    long timeSinceDamage, intervalDamage = 800;
    float x,y,vx,vy;

    Texture texture;
    public Rectangle hitBox;

    public Enemy(float x, float y) {
        this.x = x;
        this.y = y;
        vx =0;
        vy=0;
        hitBox = new Rectangle(x,y,16,16);
        texture = new Texture("4b.png");
        timeSinceDamage = TimeUtils.millis();
    }
    void getDamage(CameraMovement cameraMovement){
        if (TimeUtils.millis() >= timeSinceDamage + intervalDamage){
            cameraMovement.health-=3;
            timeSinceDamage = TimeUtils.millis();
        }
    }

    public void moveX(){
        x+=vx;
    }
    public void moveY(){

        y+=vy;
    }
    public void move(Hero h){
        vx = h.x>x?MathUtils.random(0.1f,0.7f):-MathUtils.random(0.1f,0.7f);
        vy = h.y>y?MathUtils.random(0.1f,0.7f):-MathUtils.random(0.1f,0.7f);
        x+=vx+MathUtils.random(-0.4f,0.4f);
        y+=vy-MathUtils.random(-0.4f,0.4f);
        hitBox.x=x;
        hitBox.y=y;
    }
    public void unmove(Hero h){
        vx = h.x>x?MathUtils.random(0.1f,0.7f):-MathUtils.random(0.1f,0.7f);
        vy = h.y>y?MathUtils.random(0.1f,0.7f):-MathUtils.random(0.1f,0.7f);
        x-=vx+MathUtils.random(-0.4f,0.4f);
        y-=vy-MathUtils.random(-0.4f,0.4f);
        hitBox.x=x;
        hitBox.y=y;
    }
    public void approach(Hero h){
        vx = h.x-x;
        vy = h.y-y;
    }

    @Override
    public void reset() {

    }

    public void init(float x, float y) {
        this.x = x;
        this.y = y;
        hitBox.set(x, y, 16, 16);
    }
}
