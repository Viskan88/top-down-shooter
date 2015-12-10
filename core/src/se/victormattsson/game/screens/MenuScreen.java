package se.victormattsson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import se.victormattsson.game.ShooterGame;

/**
 * Created by victormattsson on 2015-12-03.
 */
public class MenuScreen implements Screen {

    private ShooterGame game;
    private TextureRegionDrawable drawableIdleBtn, drawablePressedBtn, drawableHoverBtn;
    private TextureAtlas buttonAtlas;
    private Stage stage;
    private Table table;
    private BitmapFont white, black;
    private Skin skin;

    public MenuScreen(final ShooterGame game) {

        buttonAtlas = new TextureAtlas("buttons/buttons.pack");
        this.game = game;

        if (this.game.music != null && !game.music.isPlaying()) {
            this.game.music = Gdx.audio.newMusic(Gdx.files.internal("sounds/Jesús Lastra - Hydrosphere.mp3"));
            this.game.music.setLooping(true);
            this.game.music.play();
        }

        TextureRegion idleBtnRegion = new TextureRegion(buttonAtlas.findRegion("buttons_scaled"), 0, 0, 800, 45);
        TextureRegion pressedBtnRegion = new TextureRegion(buttonAtlas.findRegion("buttons_scaled"), 0, 150, 800, 45);
        TextureRegion hoverBtnRegion = new TextureRegion(buttonAtlas.findRegion("buttons_scaled"), 0, 100, 800, 45);

        drawableIdleBtn = new TextureRegionDrawable(idleBtnRegion);
        drawablePressedBtn = new TextureRegionDrawable(pressedBtnRegion);
        drawableHoverBtn = new TextureRegionDrawable(hoverBtnRegion);

        white = new BitmapFont(Gdx.files.internal("fonts/gabriola_white.fnt"), false);
        black = new BitmapFont(Gdx.files.internal("fonts/gabriola_black.fnt"), false);

        stage = new Stage();
        skin = new Skin();
        skin.addRegions(buttonAtlas);
        table = new Table();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        table.setFillParent(true);

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = drawableIdleBtn;
        buttonStyle.down = drawablePressedBtn;
        buttonStyle.over = drawableHoverBtn;
        buttonStyle.font = white;

        TextButton playBtn = new TextButton("Play", buttonStyle);
        playBtn.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.music != null) game.music.stop();
                game.setScreen(new PlayScreen(game));
//                game.setScreen(new GameOverScreen(game));
                dispose();
            }
        });
        TextButton settingsBtn = new TextButton("Settings", buttonStyle);
        settingsBtn.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game));
                dispose();
            }
        });
        TextButton exitBtn = new TextButton("Exit", buttonStyle);
        exitBtn.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(playBtn).pad(5f);
        table.row();
        table.add(settingsBtn).pad(5f);
        table.row();
        table.add(exitBtn).pad(5f);
        table.row();
        stage.addActor(table);

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        white.dispose();
        black.dispose();
        skin.dispose();
        stage.dispose();
        buttonAtlas.dispose();
    }
}
