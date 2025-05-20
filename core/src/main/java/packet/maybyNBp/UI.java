package packet.maybyNBp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

public class UI extends Enemy{
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
    public UI(float x, float y, BitmapFont font) {
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

    public void setHealth(int health) {
        if(health + this.health > maxHealth) health = maxHealth;
        else this.health = health;
    }

    @Override
    public void approach(Hero h) {

        super.approach(h);
        healthLine.x = 50;
        healthLine.y = Gdx.graphics.getHeight() - 100;
        healthText.x = healthLine.x;
        healthText.y = healthLine.y+50;
        waveBtn.x = healthLine.x + 500;
        waveBtn.y = healthLine.y + 50;
        timerBtn.x = 50;
        timerBtn.y = Gdx.graphics.getHeight() - 100;
        xBtn.x = Gdx.graphics.getWidth() -100;
        xBtn.y = Gdx.graphics.getHeight() - 50;
        gameOverBtn.x = Gdx.graphics.getWidth()/2;
        gameOverBtn.y = Gdx.graphics.getHeight()/2;
    }
}
