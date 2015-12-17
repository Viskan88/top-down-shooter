package se.victormattsson.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Game;

import se.victormattsson.game.screens.MenuScreen;
import se.victormattsson.game.util.AudioManager;

public class ShooterGame extends Game{

    public static final int V_WIDTH = 1600;
    public static final int V_HEIGHT = 1024;
    public static final float PPM = 50;
    public SpriteBatch batch;
    public static int currentLvl;
    public final static int LEVEL_NR = 2;

    public static final short WALL_BIT = 1;
    public static final short CRATE_BIT = 2;
    public static final short PLAYER_PROJECTILE_BIT = 4;
    public static final short PLAYER_BIT = 8;
    public static final short CURSOR_BIT = 16;
    public static final short FIRST_AID_BIT = 32;
    public static final short AMMO_BIT = 64;
    public static final short ENEMY_BIT = 128;
    public static final short ENEMY_PROJECTILE_BIT = 256;

    @Override
	public void create () {
        currentLvl = 1;
        AudioManager.music = Gdx.audio.newMusic(Gdx.files.internal("sounds/Jesús Lastra - Hydrosphere.mp3"));
        AudioManager.setVolume(0.6f);
        batch = new SpriteBatch();
		setScreen(new MenuScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
        batch.dispose();
        AudioManager.dispose();
	}

	@Override
	public void render () {
		super.render();
	}

}
