package com.mygdx.game.desktop;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import GameObjects.PlantsvsZombies;
public class DesktopLauncher 
{
	public static void main(String[] arg)
        {
	    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.width=1254;
            config.height=756;
	    new LwjglApplication(new PlantsvsZombies(), config);
	}
}
