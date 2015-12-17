package se.victormattsson.game.sprites.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.screens.PlayScreen;
import se.victormattsson.game.sprites.projectile.PlayerProjectile;

/**
 * Created by victormattsson on 2015-12-02.
 */
public class Player extends Sprite {

    public enum State {IDLE, MOVING, SHOOTING, RELOADING, DEAD}
    private final float MAX_SPEED = 6;
    private State currentState;
    private State previousState;
    private PlayScreen screen;
    public World world;
    public Body playerBody;
    public static int healthPoints;
    public static int clipAmmo;
    public static int totalAmmunition;
    public static final int CLIP_SIZE = 12;
    private Sound sound;
    private Animation playerMove, playerIdle, playerShoot, playerReload;
    private float stateTimer;
    private Array<PlayerProjectile> projectiles = new Array<PlayerProjectile>();
    private boolean shooting;
    private boolean reloading;
    private Crosshair crosshair;

    public Player(PlayScreen screen) {
        healthPoints = 100;
        currentState = State.IDLE;
        previousState = State.IDLE;
        this.screen = screen;
        world = screen.getWorld();
        stateTimer = 0;
        clipAmmo = CLIP_SIZE;
        totalAmmunition = CLIP_SIZE * 2;
        crosshair = new Crosshair(screen);

        //Array with TextureRegions for animations
        Array<TextureRegion> frames = new Array<TextureRegion>();

        //Set player idle animation
        for (int i = 0; i < 19; i++) {
            frames.add(new TextureRegion(screen.getAtlas("player_idle").findRegion("survivor-idle_handgun_" + i + "_small")));
        }
        playerIdle = new Animation(0.1f, frames);
        frames.clear();

        //Set player move animation
        for (int i = 0; i < 19; i++) {
            frames.add(new TextureRegion(screen.getAtlas("player_move").findRegion("survivor-move_handgun_" + i + "_small")));
        }
        playerMove = new Animation(0.033f, frames);
        frames.clear();

        //Set player shoot animation
        for (int i = 0; i < 3; i++) {
            frames.add(new TextureRegion(screen.getAtlas("player_shoot").findRegion("survivor-shoot_handgun_" + i + "_small")));
        }
        playerShoot = new Animation(0.033f, frames);
        frames.clear();

        //Set player reload animation
        for (int i = 0; i < 14; i++) {
            if (i == 7) continue;
            frames.add(new TextureRegion(screen.getAtlas("player_reload").findRegion("handgun_" + i + "_small")));
        }
        playerReload = new Animation(0.066f, frames);
        frames.clear();

        //Set bound of sprite to ber correct size
        setBounds(0, 0, 64 / ShooterGame.PPM, 64 / ShooterGame.PPM);
        setRegion(playerIdle.getKeyFrame(Gdx.graphics.getDeltaTime(), true));
        definePlayer();
    }

    public void update(float dt) {

        for (PlayerProjectile projectile : projectiles) {
            projectile.update(dt);
            if (projectile.toDestroy()){
                projectile.setDestroyed(true);
                world.destroyBody(projectile.getProjectile());
                projectiles.removeValue(projectile, true);
            }
        }

        Vector2 velocity = playerBody.getLinearVelocity();
        float speed = velocity.len();
        if (speed > MAX_SPEED) {
            playerBody.setLinearVelocity(velocity.scl(MAX_SPEED / speed));
        }

        setPosition(playerBody.getPosition().x - getWidth() / 2, playerBody.getPosition().y - getHeight() / 2);
        crosshair.update();
        playerBody.setTransform(playerBody.getPosition().x, playerBody.getPosition().y, getMouseDirection());
        setRotation((playerBody.getAngle() * MathUtils.radiansToDegrees) - 170f);
        setOriginCenter();
        setRegion(getFrame(dt));
    }

    private TextureRegion getFrame(float dt) {

        currentState = getState();
        TextureRegion region = null;

        switch (currentState) {
            case RELOADING:
                region = playerReload.getKeyFrame(stateTimer);
                if (playerReload.isAnimationFinished(stateTimer)) {
                    reloading = false;
                }
                break;
            case SHOOTING:
                region = playerShoot.getKeyFrame(stateTimer);
                if (playerShoot.isAnimationFinished(stateTimer)) {
                    shooting = false;
                }
                break;
            case IDLE:
                region = playerIdle.getKeyFrame(stateTimer, true);
                break;
            case MOVING:
                region = playerMove.getKeyFrame(stateTimer, true);
                break;
            case DEAD:
                region = playerIdle.getKeyFrame(stateTimer, true);
                break;

        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public void definePlayer() {
        //Body
        BodyDef bdef = new BodyDef();
        bdef.position.set(1050 / ShooterGame.PPM, 500 / ShooterGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        playerBody = world.createBody(bdef);

        //Fixture
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(20 / ShooterGame.PPM);

        fdef.shape = shape;
        fdef.filter.categoryBits = ShooterGame.PLAYER_BIT;
        fdef.filter.maskBits = ShooterGame.CRATE_BIT |
                ShooterGame.WALL_BIT |
                ShooterGame.FIRST_AID_BIT |
                ShooterGame.AMMO_BIT |
                ShooterGame.ENEMY_BIT |
                ShooterGame.ENEMY_PROJECTILE_BIT;
        playerBody.createFixture(fdef).setUserData(this);

        shape.dispose();
    }

    public void shoot() {
        if (clipAmmo > 0) {
            shooting = true;
            PlayerProjectile projectile = new PlayerProjectile(screen, playerBody);
            projectiles.add(projectile);
            projectile.setImpulse();
            sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pistol.wav"));
            long ID = sound.play();
            sound.setVolume(ID, 0.2f);
            clipAmmo--;
        }
    }

    public void reload() {
        if (clipAmmo < CLIP_SIZE && totalAmmunition > 0) {
            reloading = true;
            sound = Gdx.audio.newSound(Gdx.files.internal("sounds/Eject Clip And Re-Load.wav"));
            long ID = sound.play();
            sound.setVolume(ID, 0.5f);

            int remainder = CLIP_SIZE - clipAmmo;

            if (totalAmmunition < CLIP_SIZE && remainder >= totalAmmunition) {
                clipAmmo += totalAmmunition;
                totalAmmunition = 0;
            } else {
                clipAmmo += remainder;
                totalAmmunition -= remainder;
            }
        }
    }

    public void hit() {
        healthPoints -= 15;
        if (healthPoints < 0){
            healthPoints = 0;
        }
    }

    public void replenishHealth() {
        healthPoints += 30;
        if (healthPoints > 100) {
            healthPoints = 100;
        }
    }

    public void replenishAmmo() {
        totalAmmunition += CLIP_SIZE;
    }

    public float getMouseDirection() {
        Vector3 playerPos = getPlayerPos();
        playerPos.sub(screen.getMousePos());
        Vector2 direction = new Vector2(playerPos.x, playerPos.y);
        return direction.angleRad();
    }

    public Array<PlayerProjectile> getProjectiles() {
        return projectiles;
    }

    public State getState() {
        if ((playerBody.getLinearVelocity().y != 0 ||
                playerBody.getLinearVelocity().x != 0)
                && !reloading && !shooting) {
            return State.MOVING;
        }
        if (shooting) {
            return State.SHOOTING;
        }
        if (reloading) {
            return State.RELOADING;
        }
        if (healthPoints <= 0){
            return State.DEAD;
        }
        else {
            return State.IDLE;
        }
    }

    public boolean isReloading() {
        return reloading;
    }

    public Vector3 getPlayerPos(){
        return new Vector3(playerBody.getPosition().x, playerBody.getPosition().y, playerBody.getAngle());
    }

    @Override
    public void draw(Batch batch){
        super.draw(batch);
        crosshair.draw(batch);
        for (PlayerProjectile projectile : getProjectiles()){
            projectile.draw(batch);
        }
    }
}
