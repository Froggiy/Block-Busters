package packet.maybyNBp;

import com.badlogic.gdx.graphics.Texture;

public class Tile {
    float x,y;
    float size;
    Texture texture;

    public Tile(float x, float y, Texture texture) {
        this.x = x;
        this.y = y;
        this.size = 64;
        this.texture = texture;
    }
    public float xSrc(){
        return x + size/2;
    }
    public float ySrc(){
        return y + size/2;
    }
}
