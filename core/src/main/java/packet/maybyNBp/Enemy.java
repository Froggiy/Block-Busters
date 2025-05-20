package packet.maybyNBp;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

public class Enemy {
    long timeSinceDamage, intervalDamage = 800;
    long timeSinceShoot, intervalShoot = 1500;
    float x,y,vx,vy, typeSpeed;
    float size;
    int health;
    Texture texture;
    public Rectangle hitBox;
    int type;
    public Enemy (float x,float y){
        this.x = x;
        this.y = y;
        vx =0;
        vy=0;
    }

    public Enemy(float x, float y, int type) {
        this.x = x;
        this.y = y;
        vx =0;
        vy=0;
        this.type = type;
        if(type < 40){
                typeSpeed = 1.5f;
                size = 10;
                health = 1;
                texture = new Texture("8a.png");
            }
        else if(type < 80){
                typeSpeed = 1.1f;
                size = 24;
                health = 2;
                texture = new Texture("8b.png");
            }
        else{
                typeSpeed = 0.8f;
                size = 32;
                health = 5;
                texture = new Texture("8c.png");
            }
        hitBox = new Rectangle(x,y,size,size);
        timeSinceDamage = TimeUtils.millis();
        timeSinceShoot = TimeUtils.millis();
    }
    void getDamage(UI UI){
        if (TimeUtils.millis() >= timeSinceDamage + intervalDamage){
            UI.health-=type/10;
            timeSinceDamage = TimeUtils.millis();
        }
    }
    public boolean doShoot(){
        if(TimeUtils.millis() >= timeSinceShoot +intervalShoot){
           timeSinceShoot = TimeUtils.millis();
           return true;
        }
        return false;
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
        x+=vx+MathUtils.random(-0.4f,0.4f)*typeSpeed;
        y+=vy-MathUtils.random(-0.4f,0.4f)*typeSpeed;
        hitBox.x=x;
        hitBox.y=y;
    }
    public void unmove(Hero h){
        vx = h.x>x?MathUtils.random(0.1f,0.7f):-MathUtils.random(0.1f,0.7f);
        vy = h.y>y?MathUtils.random(0.1f,0.7f):-MathUtils.random(0.1f,0.7f);
        x-=vx+MathUtils.random(-0.4f,0.4f)*typeSpeed;
        y-=vy-MathUtils.random(-0.4f,0.4f)*typeSpeed;
        hitBox.x=x;
        hitBox.y=y;
    }
    public void approach(Hero h){
        vx = (h.x+8)-x;
        vy = (h.y+8)-y;
    }

    public boolean isHit(Rectangle rect){
        if (hitBox.overlaps(rect)) {
            return true;
        }
        return false;
    }
}
