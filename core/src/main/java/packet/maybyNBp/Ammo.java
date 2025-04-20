package packet.maybyNBp;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Ammo {
    float vx,vy;
    Vector2 pos;
    Rectangle hitbox;
    public Ammo(float x,float y){
        pos = new Vector2(x,y);
        hitbox = new Rectangle(x,y,16,16);
        vx=0;
        vy=0;
    }
    public void setHitbox(){
        hitbox.x = pos.x;
        hitbox.y = pos.y;
    }
    public void approach(float targetX, float targetY){
        if (targetX+10>pos.x && targetX-10<pos.x) {
            vx = targetX > pos.x ? 0.5f : -0.5f;
        }else vx=0;
        if (targetY+10>pos.y && targetY-10<pos.y) {
            vy = targetY > pos.y ? 0.7f : -0.7f;
        }else vy=0;
        pos.x += vx;
        pos.y += vy;
        hitbox.x = pos.x;
        hitbox.y = pos.y;
    }
}
