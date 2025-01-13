package game.map;


import game.engine.input.UserInput;
import game.engine.objects.AbstractGameObject;
import game.engine.objects.AbstractTextObject;
import game.engine.objects.GameObjectList;
import game.objects.enemy.IEnemy;
import game.engine.world.AbstractWorld;
import game.level.Isaac_Level;
import game.objects.RoomBackgroundGameObject;
import game.engine.sound.SoundEngine;
import game.objects.*;
import game.objects.enemy.Isaac_SpiderAI;
import game.objects.items.Heart;
import game.objects.player.Isaac_Avatar;
import game.utils.Const;
import game.utils.Isaac_TextureSpells;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Isaac_World extends AbstractWorld
{
    private double timePassed = 0;
    private double timeSinceLastShot = 0;

    // for grenades
    private int grenades = 500;
    private Isaac_CounterGrenades counterG;
    private Isaac_Counter counterZ;
    private Isaac_HelpText helpText;
    private double spawnGrenade = 0;

    private double lifeHelpText = 10.0;

    public Isaac_Level level;
    public int score = 0;

    public Isaac_Room startingRoom;
    public List<Isaac_Room> rooms = new ArrayList<Isaac_Room>();
    public Isaac_Room currentRoom;

    public boolean LoadNewRoom=false;

    public void init()
    {
        level = new Isaac_Level(this);
        Isaac_Level.instance.CreateLevel();
        CreateRoom(Isaac_Level.instance.getCurrentRoom());
        //CreateRoom(new Isaac_Room(RoomTexture.mapDefault,1920,1080,Sounds.MainMusic,Isacc_RoomType.NORMAL) );
        var liveDisplay = new AbstractTextObject(50, 80, Color.GRAY) {
            @Override
            public String toString() {
                return "Score: " + score;
            }
        };
        this.textObjects.add(liveDisplay);
        var levelDisplay = new AbstractTextObject(50, 110, Color.GRAY) {
            @Override
            public String toString() {
                return "Level: " + level.getLevel();
            }
        };
        this.textObjects.add(levelDisplay);
    }


    @Override
    public void tick(double timediff) {
        var currentEnemys = 0;
        for(AbstractGameObject obj : gameObjects){
            if(obj instanceof IEnemy){
                currentEnemys++;
            }
        }
        //System.out.println("Enemys: "+currentEnemys);
        if(currentEnemys<=0){
            if (!Isaac_Level.instance.getCurrentRoom().isCleared()) {
                System.out.println("Changed to Clear: " + currentEnemys);
                Isaac_Level.instance.getCurrentRoom().setCleared();
            }
        }
        else {
            Isaac_Level.instance.getCurrentRoom().setCleared(false);
        }

        if(LoadNewRoom){
            if(currentEnemys==0)
                Isaac_Level.instance.LoadRoom();
            LoadNewRoom=false;
        }

    }

    //Create a Room
    public void CreateRoom(Isaac_Room room){
        //Clear gameObjects
        gameObjects.clear();

        //Set Background Image
        background = new RoomBackgroundGameObject(room.backgroundRoomImage);
        gameObjects.add(background);
        //CreateDoors
        room.CreateDoors();
        gameObjects.addAll(room.teleporterList);
        //Spawn Items
        gameObjects.addAll(room.itemList);
        //Set Avatar only if it never existed before
        if(avatar==null)
            avatar = new Isaac_Avatar(0,0);
        avatar.setX(room.playerStartX);
        avatar.setY(room.playerStartY);
        gameObjects.add(avatar);
        //add all Enemys
        gameObjects.addAll(room.gameObjectsEnemyList);
        //add all Enviorment stuff


        //Sound System
        SoundEngine.instance.playMusic(room.backgroundMusic,true);
    }


    public void processUserInput(UserInput userInput, double diffSeconds)
    {
        // distinguish if Avatar shall move or shoots
        int button = userInput.mouseButton;

        //
        // Mouse events
        //
        if(userInput.isMouseEvent)
        {
            // move
            /*
            if(button==1)
            { avatar.setDestination(userInput.mousePressedX+worldPartX,
                    userInput.mousePressedY+worldPartY);
            }

             */
        }

        //
        // Mouse still pressed?
        //

        if(userInput.isMousePressed && button==3)
        {
            // only 1 shot every ... seconds:
            timeSinceLastShot += diffSeconds;
            if(timeSinceLastShot > 0.2)
            {
                timeSinceLastShot = 0;

                Isaac_Shot shot = new Isaac_Shot(
                        avatar.x,avatar.y,userInput.mouseMovedX+worldPartX,userInput.mouseMovedY+worldPartY,750, Isaac_TextureSpells.waterSpell, 5);
                this.gameObjects.add(shot);
            }
        }

        //
        // Keyboard events
        //
//	if(userInput.isKeyEvent)
//	{
//	  if(userInput.keyPressed==' ')
//	  { throwGrenade(userInput.mouseMovedX+worldPartX,userInput.mouseMovedY+worldPartY);
//	  }
//	  else if(userInput.keyPressed==(char)27)
//	  { System.exit(0);
//	  }
//	}
        if(userInput.keys.isIn('w')) {
//            double borderLocation = avatar.y - 45;
//            if(borderLocation > 0)
            ((Isaac_Avatar)avatar).move(0.0, -0.8);
        }
        if(userInput.keys.isIn('s')) {
//            double borderLocation = avatar.y + 45;
//            if(borderLocation < Const.WORLD_HEIGHT)
            ((Isaac_Avatar)avatar).move(0.0, 0.8);
        }
        if(userInput.keys.isIn('a')) {
//            double borderLocation = avatar.x - 45;
//            if(borderLocation > 0)
            ((Isaac_Avatar)avatar).move(-0.8, 0.0);

        }
        if(userInput.keys.isIn('d')) {
//            double borderLocation = avatar.x + 45;
//            if(borderLocation < Const.WORLD_WIDTH)
            ((Isaac_Avatar)avatar).move(0.8, 0.0);
        }

    }

    public void addScore(int addedScore){
        score+=addedScore;
    }




    public void createNewObjects(double diffSeconds)
    {
        createZombie(diffSeconds);
        createGrenade(diffSeconds);

        // delete HelpText after ... seconds
        if(helpText!=null)
        {
            lifeHelpText -= diffSeconds;
            if(lifeHelpText < 0)
            {
                textObjects.remove(helpText);
                helpText = null;
            }
        }
    }


    private void createGrenade(double diffSeconds)
    {
        final double INTERVAL = Const.SPAWN_GRENADE;

        spawnGrenade += diffSeconds;
        if(spawnGrenade>INTERVAL)
        {
            spawnGrenade -= INTERVAL;

            // create new Grenade
            double x = worldPartX+Math.random()*Const.WORLDPART_WIDTH;
            double y = worldPartY+Math.random()*Const.WORLDPART_HEIGHT;

            // if too close to Avatar, cancel
            double dx = x-avatar.x;
            double dy = y-avatar.y;
            if(dx*dx+dy*dy < 200*200)
            { spawnGrenade = INTERVAL;
                return;
            }


            // if collisions occur, cancel
            Isaac_Grenade grenade = new Isaac_Grenade(x,y);
            GameObjectList list = getPhysicsSystem().getCollisions(grenade);
            if(list.size()!=0)
            { spawnGrenade = INTERVAL;
                return;
            }

            // else add zombie to world
            this.gameObjects.add(grenade);
            counterG.setNumber(grenades);
        }

    }



    private void createZombie(double diffSeconds)
    {
        final double INTERVAL = Const.SPAWN_INTERVAL;

        timePassed += diffSeconds;
        if(timePassed>INTERVAL)
        {
            timePassed -= INTERVAL;

            // create new Zombie; preference to current screen
            double x,y;
            if(Math.random() < 0.7)
            { x = Math.random()*Const.WORLD_WIDTH;
                y = Math.random()*Const.WORLD_HEIGHT;
            }
            else
            { x = worldPartX+Math.random()*Const.WORLDPART_WIDTH;
                y = worldPartY+Math.random()*Const.WORLDPART_HEIGHT;
            }


            // if too close to Avatar, cancel
            var dx = x-avatar.x;
            var dy = y-avatar.y;
            if(dx*dx+dy*dy < 400*400)
            { timePassed = INTERVAL;
                return;
            }

            // if collisions occur, cancel
            var zombie = new Isaac_SpiderAI(x,y);
            GameObjectList list = getPhysicsSystem().getCollisions(zombie);
            if(list.size()!=0)
            { timePassed = INTERVAL;
                return;
            }

            // else add zombie to world
            this.gameObjects.add(zombie);
            zombie.setDestination(avatar);
            Isaac_Counter counter = (Isaac_Counter)textObjects.get(0);
            counter.increment();
        }

    }


    public void addGrenade()
    {
        if(grenades<999) { grenades++; }
        counterG.setNumber(grenades);
    }

    //ToDo Old stuff
    /*
    protected void init()
    {
        // add the Avatar
        avatar = new Isaac_Avatar(2500,2000);
        gameObjects.add(avatar);

        // set WorldPart position
        worldPartX = 1500;
        worldPartY = 1500;

        // add a little forrest

        for(int x=0; x<5000; x+=1000)
        {

            for(int y=0; y<4000; y+=800)
            {
                gameObjects.add(new Isaac_Tree(x+300,y+200,80));
                gameObjects.add(new Isaac_Tree(x+600,y+370,50));
                gameObjects.add(new Isaac_Tree(x+200,y+600,50));
                gameObjects.add(new Isaac_Tree(x+500,y+800,40));
                gameObjects.add(new Isaac_Tree(x+900,y+500,100));
                gameObjects.add(new Isaac_Tree(x+760,y+160,40));
            }
        }



        // add one zombie
        gameObjects.add(new Isaac_ZombieAI(100,100));


        counterZ = new Isaac_Counter(20,40);
        counterG = new Isaac_CounterGrenades(770,40);
        helpText = new Isaac_HelpText(100,400);

        counterG.setNumber(grenades);
        textObjects.add(counterZ);
        textObjects.add(counterG);
        textObjects.add(helpText);
    }
    */


}
