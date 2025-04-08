package packet.maybyNBp;


import com.badlogic.gdx.math.Rectangle;

public class Heart extends Item{
    Rectangle hitbox;
    Boolean isUsed = false;
    float x,y;
    public Heart(float x, float y){
        this.x = x;
        this.y = y;
        hitbox = new Rectangle((int)x,(int)y,16,16);
    }
}
