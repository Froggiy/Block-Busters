package packet.maybyNBp;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Button {
    float x,y;
    float width,height;
    Rectangle rectangle;
    BitmapFont font;
    String text;

    public Button(float x, float y, BitmapFont font, String text) {
        super();
        this.x = x;
        this.y = y;
        this.font = font;
        this.text = text;
        GlyphLayout glyphLayout = new GlyphLayout(font,text);
        width = glyphLayout.width;
        height = glyphLayout.height;
        rectangle = new Rectangle(x,y,width,height);
    }
    public boolean hit(Vector3 t){
        return x<t.x && t.x<x+width && y>t.y && t.y>y-height;
    }
}
