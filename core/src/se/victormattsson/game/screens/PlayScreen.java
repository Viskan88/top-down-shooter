package se.victormattsson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Iterator;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.scenes.HUD;
import se.victormattsson.game.sprites.enemies.Enemy;
import se.victormattsson.game.sprites.items.Ammunition;
import se.victormattsson.game.sprites.items.Crate;
import se.victormattsson.game.sprites.items.FirstAid;
import se.victormattsson.game.sprites.player.Player;
import se.victormattsson.game.util.AudioManager;
import se.victormattsson.game.util.Box2DWorldCreator;
import se.victormattsson.game.util.WorldContactListener;

/**
 * Created by victormattsson on 2015-12-02.
 */
public class PlayScreen implements Screen {

    //Reference to our game
    private ShooterGame game;
    private HUD hud;
    private Player player;
    private HashMap<String, TextureAtlas> atlases = new HashMap<String, TextureAtlas>();
    //Camera and viewport
    private OrthographicCamera camera;
    private Viewport viewport;
    //Box2d variables
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Box2DWorldCreator worldCreator;
    //Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public PlayScreen(ShooterGame game) {

        Gdx.input.setCursorCatched(true);

        atlases.put("player_move", new TextureAtlas("player/shooter_handgun.pack"));
        atlases.put("player_idle", new TextureAtlas("player/handgun_idle.pack"));
        atlases.put("player_shoot", new TextureAtlas("player/handgun_shoot.pack"));
        atlases.put("player_reload", new TextureAtlas("player/handgun_reload.pack"));
        atlases.put("enemy_idle", new TextureAtlas("player/handgun_idle_enemy.pack"));

        this.game = game;

        if (!AudioManager.isMusicOff()) {
            AudioManager.playMusic("sounds/Jesús Lastra - Heatstroke.mp3");
        }

        camera = new OrthographicCamera();
        viewport = new FitViewport(ShooterGame.V_WIDTH / 75, ShooterGame.V_HEIGHT / 75, camera);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("levels/level" + ShooterGame.currentLvl +".tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 1 / ShooterGame.PPM);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        world = new World(new Vector2(0, 0), true);
        worldCreator = new Box2DWorldCreator(this);
        debugRenderer = new Box2DDebugRenderer();
        player = new Player(this);
        hud = new HUD(game.batch);
        world.setContactListener(new WorldContactListener());
    }

    public void handleInput(float dt) {

        if (Gdx.input.isKeyPressed(Input.Keys.W) ||
                Gdx.input.isKeyPressed(Input.Keys.S) ||
                Gdx.input.isKeyPressed(Input.Keys.A) ||
                Gdx.input.isKeyPressed(Input.Keys.D)) {

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                player.playerBody.applyLinearImpulse(new Vector2(0, 1f), player.playerBody.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                player.playerBody.applyLinearImpulse(new Vector2(0, -1f), player.playerBody.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                player.playerBody.applyLinearImpulse(new Vector2(-1f, 0), player.playerBody.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                player.playerBody.applyLinearImpulse(new Vector2(1f, 0), player.playerBody.getWorldCenter(), true);
            }
        } else {
            player.playerBody.setLinearVelocity(0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            if (!player.isReloading()) {
                player.reload();
            }
        }
        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    if (!player.isReloading()) {
                        player.shoot();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void update(float dt) {

        handleInput(dt);

        world.step(1 / 60f, 6, 2);

        player.update(dt);

        hud.update();

        for (Crate crate : worldCreator.getCrates()) {
            crate.update(dt);
        }
        for (FirstAid firstaid : worldCreator.getFirstAids()) {
            firstaid.update(dt);
        }
        for (Ammunition ammunition : worldCreator.getAmmunition()) {
            ammunition.update(dt);
        }

        for (Iterator<Enemy> enemies = Box2DWorldCreator.getEnemies().iterator(); enemies.hasNext(); ) {
            Enemy enemy = enemies.next();
            enemy.update(dt, player);
            if (enemy.isDead()) {
                enemies.remove();
                hud.enemyKilled();
            }
        }
        //update camera position to where the body is
        camera.position.set(player.playerBody.getPosition().x, player.playerBody.getPosition().y, 0);

        camera.update();
        tiledMapRenderer.setView(camera);
    }

    @Override
    public void render(float delta) {

        update(delta);

        //Clears the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Render game map
        tiledMapRenderer.render();

        //Render debug lines
//        debugRenderer.render(world, camera.combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Crate crate : worldCreator.getCrates()) {
            crate.draw(game.batch);
        }
        for (FirstAid firstaid : worldCreator.getFirstAids()) {
            firstaid.draw(game.batch);
        }
        for (Ammunition ammunition : worldCreator.getAmmunition()) {
            ammunition.draw(game.batch);
        }
        for (Enemy enemies : Box2DWorldCreator.getEnemies()) {
            enemies.draw(game.batch);
        }

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (gameOver()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        if (hud.isObjectiveDone()){
            if (ShooterGame.currentLvl == ShooterGame.LEVEL_NR){
                game.setScreen(new GameOverScreen(game));
                dispose();
            }else {
                game.setScreen(new LevelSummaryScreen(game));
                dispose();
            }
        }
    }

    public boolean gameOver() {
        return player.getState() == Player.State.DEAD;
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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

    public TextureAtlas getAtlas(String key) {
        return atlases.get(key);
    }

    public World getWorld() {
        return world;
    }

    public TiledMap getMap() {
        return map;
    }

    public Vector3 getMousePos() {
        Vector3 worldCoordinates = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        return viewport.unproject(worldCoordinates);
    }

    @Override
    public void dispose() {
        world.dispose();
        map.dispose();
        hud.dispose();
        debugRenderer.dispose();
        tiledMapRenderer.dispose();
    }
}
