package game.objects.player;

import game.engine.objects.AbstractGameObject;
import game.engine.objects.AbstractTextObject;
import game.engine.objects.GameObjectList;
import game.objects.enemy.IEnemy;
import game.utils.Const;
import game.utils.Isaac_TextureAvatar;

import java.awt.*;

public class Isaac_Avatar extends AbstractGameObject {

    int health = Const.INITIAL_HEALTH;
    double invincibletime=0;
    private double inertX = 0.0, inertY = 0.0;
    private double slipperiness = 1.6;  // shows how slippery GameObject would move, should be value between ]1.0. 2.0[

    public Isaac_Avatar(double x, double y)
    {
        super(x,y,0,200,Isaac_TextureAvatar.avatarDefault);
        this.isMoving = false;

        var liveDisplay = new AbstractTextObject(50, 50, Color.GRAY) {
            @Override
            public String toString() {
                return "Health: " + health;
            }
        };
        world.textObjects.add(liveDisplay);
    }

    void debugPrintLive(){
        System.out.println("Current lives: " + health);
    }

    public void addHealth(int health){
        this.health += health;
    }

    public void tick(double diffSeconds)
    {


        if(invincibletime > 0)
            invincibletime -= diffSeconds;
        else if(invincibletime < 0)
            invincibletime = 0;

        // calculate all collisions with other Objects
        GameObjectList collisions = world.getPhysicsSystem().getCollisions(this);
        boolean inWorld = false;
        for(int i=0; i<collisions.size(); i++)
        {
            AbstractGameObject obj = collisions.get(i);
            // collode with zombies
            if(obj instanceof IEnemy && invincibletime == 0)
            {
                health-=10;
                invincibletime=Const.INVINCIBILITY_AFTER_HIT;
                debugPrintLive();
                if(health <= 0){
                    world.gameOver=true;
                }

            }
        }

        // move Avatar one step forward
        this.processMovement(diffSeconds);
    }

    public void move(double dx, double dy) {
        double maxInert = 2.0;  // determines the maximal speed GameObject can achieve
        if(Math.abs(inertX) < maxInert) {
            inertX += dx;
        }

        if(Math.abs(inertY) < maxInert) {
            inertY += dy;
        }
    }

    public void processMovement(double diffSeconds)
    {
        double step_x = speed*diffSeconds*inertX;

        double step_y = speed*diffSeconds*inertY;

        inertX /= slipperiness;
        inertY /= slipperiness;

        System.out.println(x);

        if(x + step_x + 45 < Const.WORLD_WIDTH && x + step_x - 45 > 0)
            x += step_x;
        if(y + step_y + 45 < Const.WORLD_HEIGHT && y + step_y - 45 > 0) // calculate world borders
            y += step_y;

        this.boundingBox.setPosition(x, y);
    }
}
