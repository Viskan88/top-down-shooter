package se.victormattsson.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Game;

import se.victormattsson.game.screens.MenuScreen;
import se.victormattsson.game.screens.PlayScreen;

public class ShooterGame extends Game{

    public static final int V_WIDTH = 1200;
    public static final int V_HEIGHT = 800;
    public static final float PPM = 50;
    public SpriteBatch batch;
    public Music music;

    public static final short WALL_BIT = 1;
    public static final short CRATE_BIT = 2;
    public static final short PROJECTILE_BIT = 4;
    public static final short PLAYER_BIT = 8;
    public static final short CURSOR_BIT = 16;
    public static final short FIRST_AID_BIT = 32;

    @Override
	public void create () {

        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/Jesús Lastra - Hydrosphere.mp3"));
        music.setVolume(0.7f);
        music.play();
        batch = new SpriteBatch();
		setScreen(new MenuScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
        batch.dispose();
        if (music != null)
            music.dispose();
	}

	@Override
	public void render () {
		super.render();
	}

}
