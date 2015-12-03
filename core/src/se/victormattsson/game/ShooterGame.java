package se.victormattsson.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Game;

import se.victormattsson.game.screens.MenuScreen;
import se.victormattsson.game.screens.PlayScreen;

public class ShooterGame extends Game{

    public static final int V_WIDTH = 1200;
    public static final int V_HEIGHT = 800;
    public SpriteBatch batch;

    @Override
	public void create () {
        batch = new SpriteBatch();
		setScreen(new MenuScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
        batch.dispose();
	}

	@Override
	public void render () {
		super.render();
	}

}
