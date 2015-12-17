package se.victormattsson.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.sprites.player.Player;
import se.victormattsson.game.util.Box2DWorldCreator;

/**
 * Created by Victor Mattsson on 2015-12-11.
 */
public class HUD implements Disposable {

    public Stage stage;
    private Viewport viewport;
    private Table statTable;
    private Table objTable;
    private Label clipLbl, totalAmmoLbl, healthPointsLbl, objLbl, enemiesLbl;
    private Skin skin;
    private int enemiesKilled;
    private int totalEnemies;
    private boolean objectiveDone;

    public HUD(SpriteBatch batch){

        totalEnemies = Box2DWorldCreator.getEnemies().size;
        enemiesKilled = 0;

        viewport = new FitViewport(ShooterGame.V_WIDTH, ShooterGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, batch);
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));

        objTable = new Table();
        objTable.right();
        objTable.setFillParent(true);

        statTable = new Table();
        statTable.bottom();
        statTable.setFillParent(true);

        objLbl = new Label("Objective", skin);
        objLbl.setFontScale(2);
        enemiesLbl = new Label("Eliminate enemies: " + enemiesKilled + "/" + totalEnemies, skin);
        enemiesLbl.setFontScale(1.5f);
        clipLbl = new Label("Ammo: " + String.valueOf(Player.clipAmmo), skin);
        clipLbl.setFontScale(2);
        totalAmmoLbl = new Label(String.valueOf(Player.totalAmmunition), skin);
        healthPointsLbl = new Label("HP: " + String.valueOf(Player.healthPoints + " %"), skin);
        healthPointsLbl.setFontScale(2);

        objTable.add(objLbl).pad(10);
        objTable.row();
        objTable.add(enemiesLbl).pad(10);

        statTable.add(clipLbl).expandX().right().padRight(5);
        statTable.add(totalAmmoLbl).left().padLeft(5);
        statTable.add(healthPointsLbl).expandX().left().padLeft(10);

//        objTable.debug();
//        statTable.debug();

        stage.addActor(objTable);
        stage.addActor(statTable);
    }

    public void update() {

        clipLbl.setText("Ammo: " + String.valueOf(Player.clipAmmo));
        totalAmmoLbl.setText(String.valueOf(Player.totalAmmunition));
        healthPointsLbl.setText("HP: " + String.valueOf(Player.healthPoints) + " %");
        enemiesLbl.setText("Eliminate enemies: " + enemiesKilled + "/" + totalEnemies);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void enemyKilled(){
        enemiesKilled++;
    }

    public boolean isObjectiveDone(){
        return totalEnemies == enemiesKilled;
    }

}
