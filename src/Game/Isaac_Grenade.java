package Game;

import Game.Const;
import Game.GameObject;

import java.awt.Color;

class Isaac_Grenade extends GameObject
{
    double life = Const.LIFE_GRENADE;

    public Isaac_Grenade(double x, double y)
    {
        super(x,y,0,0,15,Color.ORANGE);
    }

    public void move(double diffSeconds)
    {
        life -= diffSeconds;
        if(life<0)
        { this.isLiving=false;
            return;
        }

    }

    public int type() { return Const.TYPE_GRENADE; }
}