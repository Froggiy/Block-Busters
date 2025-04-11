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
        this.x = x;
        this.y = y;
        this.font = font;
        this.text = text;
        // Estimate width and height based on font
        GlyphLayout layout = new GlyphLayout(font, text);
        this.width = layout.width;
        this.height = layout.height;
    }

    public boolean hit(Vector3 t) {
        return t.x >= x && t.x <= x + width &&
            t.y >= y - height && t.y <= y;
    }

}
