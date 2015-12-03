package se.victormattsson.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.screens.sprites.Player;

/**
 * Created by victormattsson on 2015-12-02.
 */
public class PlayScreen implements Screen {

    //Reference to our game
    private ShooterGame game;

    private Player player;

    private TextureAtlas atlas;

    //Camera and viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer debugRenderer;

    //Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public PlayScreen(ShooterGame game) {

        atlas = new TextureAtlas("shooter_handgun.pack");

        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(ShooterGame.V_WIDTH, ShooterGame.V_HEIGHT, camera);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("levels/level2.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map);

        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();

        player = new Player(this);
    }

    public void handleInput(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.W) ||
                Gdx.input.isKeyPressed(Input.Keys.S) ||
                Gdx.input.isKeyPressed(Input.Keys.A) ||
                Gdx.input.isKeyPressed(Input.Keys.D)) {

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                player.body.applyLinearImpulse(new Vector2(0, 100f), player.body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                player.body.applyLinearImpulse(new Vector2(0, -100f), player.body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                player.body.applyLinearImpulse(new Vector2(-500f, 0), player.body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                player.body.applyLinearImpulse(new Vector2(500f, 0), player.body.getWorldCenter(), true);
            }
        } else {
            player.body.setLinearDamping(10f);
        }
    }

    public void update(float dt) {

        handleInput(dt);

        world.step(1 / 60f, 6, 2);

        player.update(dt);

        //update camera position to where the body is
        camera.position.set(player.body.getPosition().x, player.body.getPosition().y, 0);

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
        debugRenderer.render(world, camera.combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        player.draw(game.batch);
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

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public World getWorld() {
        return world;
    }
}
