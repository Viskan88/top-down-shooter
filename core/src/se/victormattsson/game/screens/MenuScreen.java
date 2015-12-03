package se.victormattsson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import se.victormattsson.game.ShooterGame;

/**
 * Created by victormattsson on 2015-12-03.
 */
public class MenuScreen implements Screen {

    private ShooterGame game;

    private TextureRegion idleBtn, pressedBtn, hoverBtn;
    private TextureRegionDrawable drawableIdleBtn, drawablePressedBtn, drawableHoverBtn;
    private TextureAtlas atlas;

    private Stage stage;
    private Label header;
    private Table table;
    private TextButton buttonPlay, buttonExit, buttonOptions;
    private BitmapFont white, black;
    private Skin skin;



    public MenuScreen(ShooterGame game) {

        atlas = new TextureAtlas("buttons/buttons.pack");
        this.game = game;

        idleBtn = new TextureRegion(atlas.findRegion("buttons_scaled"), 0, 0, 800, 45);
        pressedBtn = new TextureRegion(atlas.findRegion("buttons_scaled"), 0, 150, 800, 45);
        hoverBtn = new TextureRegion(atlas.findRegion("buttons_scaled"), 0, 100, 800, 45);

        drawableIdleBtn = new TextureRegionDrawable(idleBtn);
        drawablePressedBtn = new TextureRegionDrawable(pressedBtn);
        drawableHoverBtn = new TextureRegionDrawable(hoverBtn);

        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas("buttons/buttons.pack");
        skin = new Skin(atlas);

        table = new Table();
       // table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table.setFillParent(true);

        white = new BitmapFont(Gdx.files.internal("fonts/gabriola_white.fnt"), false);
        black = new BitmapFont(Gdx.files.internal("fonts/gabriola_black.fnt"), false);

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = drawableIdleBtn;
        buttonStyle.down = drawablePressedBtn;
        buttonStyle.over = drawableHoverBtn;
        buttonStyle.font = white;

        buttonPlay = new TextButton("Play", buttonStyle);
        buttonOptions = new TextButton("Options", buttonStyle);
        buttonExit = new TextButton("Exit", buttonStyle);

        table.add(buttonPlay);
        table.row();
        table.add(buttonOptions);
        table.row();
        table.add(buttonExit);
        table.row();
        stage.addActor(table);

    }

    @Override
    public void show() {


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
        atlas.dispose();
    }
}
