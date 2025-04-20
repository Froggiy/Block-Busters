package packet.maybyNBp;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.collision.Sphere;

import java.util.Random;

public class Weapon extends Item{
    float x,y;
    Rectangle hitbox;
    String type;
    Texture texture;
    int uses;
    public Weapon(float x, float y) {
        super(x,y);
        this.x = x;
        this.y = y;
        switch (MathUtils.random(1,2)){
            case (1):{
                type = "sword";
                texture = new Texture("sword.png");
                uses = 4;
                break;
            }
            case (2):{
                type = "staff";
                texture = new Texture("8b.png");
                uses = 15;
            }
        }
        hitbox = new Rectangle(x,y,16,16);
    }

}
