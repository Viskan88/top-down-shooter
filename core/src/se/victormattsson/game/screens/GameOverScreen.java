package se.victormattsson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import se.victormattsson.game.ShooterGame;

/**
 * Created by Victor Mattsson on 2015-12-08.
 */
public class GameOverScreen implements Screen {

    private ShooterGame game;
    private TextureAtlas buttonAtlas;
    private TextureRegionDrawable drawableIdleBtn, drawablePressedBtn, drawableHoverBtn;
    private final BitmapFont white;
    private Stage stage;
    private Skin skin;
    private Table table;

    public GameOverScreen(ShooterGame game) {

        this.game = game;

        if (this.game.music != null && !game.music.isPlaying()) {
            this.game.music = Gdx.audio.newMusic(Gdx.files.internal("sounds/Jesús Lastra - Hydrosphere.mp3"));
            this.game.music.setLooping(true);
            this.game.music.play();
        }

        buttonAtlas = new TextureAtlas("buttons/buttons.pack");

        TextureRegion idleBtnRegion = new TextureRegion(buttonAtlas.findRegion("buttons_scaled"), 0, 0, 800, 45);
        TextureRegion pressedBtnRegion = new TextureRegion(buttonAtlas.findRegion("buttons_scaled"), 0, 150, 800, 45);
        TextureRegion hoverBtnRegion = new TextureRegion(buttonAtlas.findRegion("buttons_scaled"), 0, 100, 800, 45);

        drawableIdleBtn = new TextureRegionDrawable(idleBtnRegion);
        drawablePressedBtn = new TextureRegionDrawable(pressedBtnRegion);
        drawableHoverBtn = new TextureRegionDrawable(hoverBtnRegion);

        white = new BitmapFont(Gdx.files.internal("fonts/gabriola_white.fnt"), false);

        stage = new Stage();
        table = new Table();
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = white;
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = drawableIdleBtn;
        buttonStyle.down = drawablePressedBtn;
        buttonStyle.over = drawableHoverBtn;
        buttonStyle.font = white;

        Label gameOverLabel = new Label("GAME OVER", labelStyle);
        TextButton playAgainBtn = new TextButton("Play Again", buttonStyle);
        playAgainBtn.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
                dispose();
            }
        });
        TextButton mainMenuBtn = new TextButton("Back To Main Menu", buttonStyle);
        mainMenuBtn.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        });

        table.setFillParent(true);
        table.add(gameOverLabel).padBottom(20f);
        table.row();
        table.add(playAgainBtn).pad(5f);
        table.row();
        table.add(mainMenuBtn).pad(5f);

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
        skin.dispose();
        stage.dispose();
        buttonAtlas.dispose();
    }
}
