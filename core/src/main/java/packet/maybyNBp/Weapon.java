package packet.maybyNBp;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.collision.Sphere;

public class Weapon extends Item{
    float x,y;
    Rectangle hitbox;
    public Weapon(float x, float y) {
        super(x,y);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle(x-15,y-15,30,30);
    }

}
