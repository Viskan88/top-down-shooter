package se.victormattsson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import se.victormattsson.game.ShooterGame;

/**
 * Created by victormattsson on 2015-12-04.
 */
public class SettingsScreen implements Screen {

    private final ShooterGame game;
    private TextureRegionDrawable drawableIdleBtn, drawablePressedBtn, drawableHoverBtn;
    private Stage stage;
    private Slider slider;
    private CheckBox soundCheck;
    private Label volumeLabelPercent;
    private TextureAtlas buttonAtlas;
    private Skin skin;
    private Table table;
    private BitmapFont white, black, kefa;

    public SettingsScreen(ShooterGame game) {
        this.game = game;
        stage = new Stage();

        buttonAtlas = new TextureAtlas("buttons/buttons.pack");
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        soundCheck = new CheckBox("On", skin);
        soundCheck.setChecked(true);
        TextureRegion idleBtnRegion = new TextureRegion(buttonAtlas.findRegion("buttons_scaled"), 0, 0, 800, 45);
        TextureRegion pressedBtnRegion = new TextureRegion(buttonAtlas.findRegion("buttons_scaled"), 0, 150, 800, 45);
        TextureRegion hoverBtnRegion = new TextureRegion(buttonAtlas.findRegion("buttons_scaled"), 0, 100, 800, 45);

        slider = new Slider(0f, 100f, 1, false, skin);
        slider.setValue((int)(game.music.getVolume() * 100));

        drawableIdleBtn = new TextureRegionDrawable(idleBtnRegion);
        drawablePressedBtn = new TextureRegionDrawable(pressedBtnRegion);
        drawableHoverBtn = new TextureRegionDrawable(hoverBtnRegion);

        white = new BitmapFont(Gdx.files.internal("fonts/gabriola_white.fnt"), false);
        black = new BitmapFont(Gdx.files.internal("fonts/gabriola_black.fnt"), false);
        kefa = new BitmapFont(Gdx.files.internal("fonts/kefa.fnt"), false);

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);

    }

    @Override
    public void show() {

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = white;

        Label volumeLbl = new Label("Volume", labelStyle);
        Label soundLbl = new Label("Sound", labelStyle);
        volumeLabelPercent = new Label((int)(game.music.getVolume() * 100) + "%", skin);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = slider.getValue() / 100;
                game.music.setVolume(volume);
                volumeLabelPercent.setText((int)slider.getValue() + "%");
            }
        });

        soundCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!soundCheck.isChecked()) {
                    game.music.stop();
                    game.music = null;
                    soundCheck.setText("Off");
                }else {
                    game.music = Gdx.audio.newMusic(Gdx.files.internal("sounds/Jesús Lastra - Hydrosphere.mp3"));
                    game.music.play();
                    soundCheck.setText("On");
                }
            }
        });

        table.add(volumeLbl).right().padRight(10);
        table.add(slider).left().padLeft(10);
        table.add(volumeLabelPercent);
        table.row();

        table.add(soundLbl).right().padRight(10);
        table.add(soundCheck).left().padLeft(10);
        table.row();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = drawableIdleBtn;
        buttonStyle.down = drawablePressedBtn;
        buttonStyle.over = drawableHoverBtn;
        buttonStyle.font = white;

        TextButton backBtn = new TextButton("Back", buttonStyle);
        backBtn.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        });

        table.add(backBtn).colspan(3).pad(10f);
//        table.debug();
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
