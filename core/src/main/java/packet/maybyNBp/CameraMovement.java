package packet.maybyNBp;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

public class CameraMovement extends Enemy{
    int health = 100;
    Rectangle healthLine;
    Button gameOverBtn;
    Button healthText;
    Button endTime;

    Button xBtn;
    Button timerBtn;
    long timeSinceStart, gametime;
    public CameraMovement(float x, float y, BitmapFont font) {
        super(x, y);
        healthLine = new Rectangle(x,y,15,health);
        gameOverBtn = new Button(x,y,font,"Game Over!");
        endTime = new Button(x,y,font,timer(false));
        xBtn = new Button(x,y,font,"X");
        healthText = new Button(x,y,font, Integer.toString(health));
        timerBtn = new Button(x,y,font,timer(true));
        timeSinceStart = TimeUtils.millis();
    }
    String timer(boolean isStopped){
        if (!isStopped) gametime = TimeUtils.millis() - timeSinceStart;
        int minutes = (int) (gametime / 60000);
        int seconds = (int) ((gametime % 60000) / 1000);
        return Integer.toString(minutes) + ":" + Integer.toString(seconds);
    }
    public float srcX(){
        return x+6;
    }
    public float srcY(){
        return y+6;
    }

    @Override
    public void approach(Hero h) {

            super.approach(h);

        healthLine.x = x-90;
        healthLine.y = y+45;
        healthText.x = healthLine.x;
        healthText.y = healthLine.y;
        timerBtn.x = x-90;
        timerBtn.y = y+45;
        xBtn.x = x+100;
        xBtn.y = y+60;
        gameOverBtn.x = x+250;
        gameOverBtn.y = y+100;
    }
}
