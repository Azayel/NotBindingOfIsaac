package game.objects;

import game.engine.objects.AbstractGameObject;
import game.engine.objects.GameObjectList;
import game.map.Isaac_World;
import game.utils.Const;

import java.awt.Color;
import java.awt.image.BufferedImage;


public class Isaac_ZombieAI extends AbstractGameObject
{
    private static final int HUNTING  = 1;
    private static final int STUCK    = 2;
    private static final int CLEARING = 3;

    private int    state;
    private double alfaClear;
    private double secondsClear;

    // life of a zombie
    private double life = 1.0;

    public Isaac_ZombieAI(double x, double y,int radius,int speed, BufferedImage image)
    {
        super(x,y,0,speed,radius, image);
        this.isMoving = false;

        state = HUNTING;

        // turn left or right to clear
        alfaClear = Math.PI;
        if(Math.random()<0.5) alfaClear = -alfaClear;

    }

    public Isaac_ZombieAI(double x, double y)
    {
        super(x,y,0,60,15,new Color(160,80,40));
        this.isMoving = false;

        state = HUNTING;

        // turn left or right to clear
        alfaClear = Math.PI;
        if(Math.random()<0.5) alfaClear = -alfaClear;

    }


    public void process(double diffSeconds)
    {
        // if avatar is too far away: stop
        double dist = world.getPhysicsSystem()
                .distance(x,y,world.avatar.x,world.avatar.y);

        if(dist > 800)
        { this.isMoving=false;
            return;
        }
        else
        { this.isMoving = true;
        }


        // state HUNTING
        //

        if(state==HUNTING)
        {
            this.setDestination(world.avatar);

            this.processMovement(diffSeconds);

            // handle collisions of the zombie
            GameObjectList collisions = world.getPhysicsSystem().getCollisions(this);
            for(int i=0; i<collisions.size(); i++)
            {
                AbstractGameObject obj = collisions.get(i);

                int type = obj.type();

                // if object is avatar, game over
                //if(type== Const.TYPE_AVATAR)
                //{ this.moveBack();
                //    world.gameOver=true;
                //}

                // if object is zombie, step back
                if(type==Const.TYPE_ZOMBIE)
                {
                    moveBack();
                    state = STUCK;
                    return;
                }

                // if Object is a tree, move back one step
                if(obj.type()==Const.TYPE_TREE)
                {
                    moveBack();
                    state = STUCK;
                    return;
                }
            }
        }

        // state STUCK
        //

        else if(state==STUCK)
        {
            // seconds left for clearing
            secondsClear = 1.0+Math.random()*0.5;
            // turn and hope to get clear
            alfa += alfaClear*diffSeconds;

            // try to clear
            state = CLEARING;
        }


        // state CLEARING
        //
        else if(state==CLEARING)
        {
            // check, if the clearing time has ended
            secondsClear -= diffSeconds;
            if(secondsClear<0)
            {
                state = HUNTING;
                return;
            }


            // try step in this direction
            this.processMovement(diffSeconds);

            // check if path was unblocked
            GameObjectList collisions = world.getPhysicsSystem().getCollisions(this);
            if(collisions.size()>0)
            {
                moveBack();

                // stuck again
                this.state=STUCK;
                return;
            }

        }

    }

    public void processMovement(double diffSeconds) {
        if(!isMoving) return;

        // move if object has a destination
        if(hasDestination)
        {
            // stop if destination is reached
            double diffX = Math.abs(x-destX);
            double diffY = Math.abs(y-destY);
            if(diffX<3 && diffY<3)
            { isMoving = false;
                return;
            }
        }

        // remember old position
        xOld=x; yOld=y;

        // move one step
        double step_x = Math.cos(alfa)*speed*diffSeconds;
        double step_y = Math.sin(alfa)*speed*diffSeconds;
        x += step_x;
        y += step_y;
        this.boundingBox.move(step_x, step_y);
    }

    // inform zombie it is hit
    public void hasBeenShot()
    {
        // every shot decreases life
        life -= 0.21;

        // if Zombie is dead (haha), delete it
        if(life<=0)
        {
            ((Isaac_World)world).addScore(10);
            this.isLiving=false;
            return;
        }
    }


    public int type() { return Const.TYPE_ZOMBIE; }
}
