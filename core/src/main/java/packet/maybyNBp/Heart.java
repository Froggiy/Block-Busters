package packet.maybyNBp;


import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class Heart extends Item{
    Rectangle hitbox;
    boolean isGolden;
    float x,y;
    public Heart(float x, float y){
        super(x,y);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle((int)x,(int)y,16,16);
        isGolden = new Random().nextBoolean();
    }
}
