package packet.maybyNBp;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

public class CameraMovement extends Enemy{
    int health, maxHealth = 100;
    Rectangle healthLine;
    Button gameOverBtn;
    Button healthText;
    Button endTime;
    Button waveBtn;

    Button xBtn;
    int wave;
    Button timerBtn;
    long timeSinceStart, gametime;
    public CameraMovement(float x, float y, BitmapFont font) {
        super(x,y);
        health = maxHealth;
        healthLine = new Rectangle(x,y,15,health);

        gameOverBtn = new Button(x,y,font,"Game Over!");
        waveBtn = new Button(x,y,font,"Wave: 0");
        xBtn = new Button(x,y,font,"X");
        healthText = new Button(x,y,font, Integer.toString(health));
        timerBtn = new Button(x,y,font,""); // temp value
        endTime = new Button(x,y,font,"");  // temp value

        // after all buttons are created, call timer() safely
        String timeStr = timer(false);
        endTime.text = timeStr;
        timerBtn.text = timeStr;

        timeSinceStart = TimeUtils.millis();
    }
    String timer(boolean isStopped){
        if (!isStopped) gametime = TimeUtils.millis() - timeSinceStart;
        int minutes = (int) (gametime / 60000);
        int seconds = (int) ((gametime % 60000) / 1000);
        wave = (minutes*60 + seconds) / 30 + 1;
        waveBtn.text = "Wave: " + wave;
        return String.format("%d:%02d", minutes, seconds);
    }
    public float srcX(){
        return x+8;
    }
    public float srcY(){
        return y+8;
    }

    public void setHealth(int health) {
        if(health + this.health > maxHealth) health = maxHealth;
        else this.health = health;
    }

    @Override
    public void approach(Hero h) {

            super.approach(h);

        healthLine.x = x-90;
        healthLine.y = y+45;
        healthText.x = healthLine.x;
        healthText.y = healthLine.y;
        waveBtn.x = healthLine.x + 100;
        waveBtn.y = healthLine.y;
        timerBtn.x = x-90;
        timerBtn.y = y+45;
        xBtn.x = x+100;
        xBtn.y = y+60;
        gameOverBtn.x = x+250;
        gameOverBtn.y = y+100;
    }
}
