package se.victormattsson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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

import javax.net.ssl.SNIHostName;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.sprites.Crate;
import se.victormattsson.game.sprites.FirstAid;
import se.victormattsson.game.sprites.Player;
import se.victormattsson.game.util.Box2DWorldCreator;
import se.victormattsson.game.util.WorldContactListener;

/**
 * Created by victormattsson on 2015-12-02.
 */
public class PlayScreen implements Screen {

    //Reference to our game
    private ShooterGame game;

    private Player player;

    private TextureAtlas atlas;
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

        atlases.put("player_move", new TextureAtlas("player/shooter_handgun.pack"));
        atlases.put("player_idle", new TextureAtlas("player/handgun_idle.pack"));
        atlases.put("player_shoot", new TextureAtlas("player/handgun_shoot.pack"));
        atlases.put("player_reload", new TextureAtlas("player/handgun_reload.pack"));

        this.game = game;

        if (game.music != null) {
            float volume = game.music.getVolume();
            game.music = Gdx.audio.newMusic(Gdx.files.internal("sounds/Jesús Lastra - Heatstroke.mp3"));
            game.music.setLooping(true);
            game.music.setVolume(volume);
            game.music.play();
        }

        camera = new OrthographicCamera();
        viewport = new FitViewport(ShooterGame.V_WIDTH / ShooterGame.PPM, ShooterGame.V_HEIGHT / ShooterGame.PPM, camera);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("levels/level1.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 1 / ShooterGame.PPM);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        world = new World(new Vector2(0, 0), true);
        worldCreator = new Box2DWorldCreator(this);
        debugRenderer = new Box2DDebugRenderer();
        player = new Player(this);
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
            player.playerBody.setLinearVelocity(0,0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.R)){
            System.out.println(player.getAmmunition());
            if (!player.isReloading() && player.getAmmunition() < 20)
            player.reload();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (player.getAmmunition() > 0) {
                player.shoot();
                System.out.println(player.getAmmunition());
            }
        }
    }

    public void update(float dt) {

        handleInput(dt);

        world.step(1 / 60f, 6, 2);

        player.update(dt);


        for (Crate crate : worldCreator.getCrates()) {
            crate.update(dt);
        }
        for (FirstAid firstaid : worldCreator.getFirstAids()) {
            firstaid.update(dt);
        }

        //update camera position to where the playerBody is
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
        game.batch.end();

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

    @Override
    public void dispose() {
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
        return camera.unproject(worldCoordinates);
    }
}
