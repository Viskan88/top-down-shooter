package se.victormattsson.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
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
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.screens.PlayScreen;

/**
 * Created by victormattsson on 2015-12-02.
 */
public class Player extends Sprite {

    public enum State {IDLE, MOVING, SHOOTING, RELOADING}

    private final float MAX_SPEED = 6;
    private State currentState;
    private State previousState;
    private PlayScreen screen;
    public World world;
    public Body playerBody;
    public Body cursorBody;
    private int healthPoints = 100;
    private int ammunition = 20;
    private Sound sound;
    private Animation playerMove, playerIdle, playerShoot, playerReload;
    private float stateTimer;
    private List<Projectile> projectiles = new ArrayList<Projectile>();
    private boolean shooting;
    private boolean reloading;

    public Player(PlayScreen screen) {
        this.screen = screen;
        world = screen.getWorld();
        stateTimer = 0;

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
//        setRegion(playerMove.getKeyFrame(Gdx.graphics.getDeltaTime(), true));
        setRegion(playerIdle.getKeyFrame(Gdx.graphics.getDeltaTime(), true));
        definePlayer();
        defineCursor();
        defineMouseJoint();
    }

    public void update(float dt) {
        for (Iterator<Projectile> projectiles = getProjectiles().iterator(); projectiles.hasNext(); ) {
            Projectile projectile = projectiles.next();
            projectile.update(dt);
            if (projectile.toDestroy()) {
                world.destroyBody(projectile.getProjectile());
                projectiles.remove();
            }
        }

        Vector2 velocity = playerBody.getLinearVelocity();
        float speed = velocity.len();
        if (speed > MAX_SPEED) {
            playerBody.setLinearVelocity(velocity.scl(MAX_SPEED / speed));
        }

        setPosition(playerBody.getPosition().x - getWidth() / 2, playerBody.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
        cursorBody.setTransform(screen.getMousePos().x, screen.getMousePos().y, 0);
        playerBody.setTransform(playerBody.getPosition().x, playerBody.getPosition().y, getMouseDirection());
        setRotation((playerBody.getAngle() * MathUtils.radiansToDegrees) - 170f);
        setOriginCenter();
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

        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;

        return region;
    }

    public void definePlayer() {
        //Body
        BodyDef bdef = new BodyDef();
        bdef.position.set(1000 / ShooterGame.PPM, 500 / ShooterGame.PPM);
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
                ShooterGame.FIRST_AID_BIT;
        playerBody.createFixture(fdef).setUserData(this);

        shape.dispose();
    }

    private void defineCursor() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.KinematicBody;
        cursorBody = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(20 / ShooterGame.PPM);

        fdef.shape = shape;
        fdef.filter.categoryBits = ShooterGame.CURSOR_BIT;
        cursorBody.createFixture(fdef).setUserData(this);
        cursorBody.setActive(false);

        shape.dispose();
    }

    private void defineMouseJoint() {

//        WheelJointDef wheelJointDef = new WheelJointDef();
//        wheelJointDef.bodyA = playerBody;
//        wheelJointDef.bodyB = cursorBody;
//        wheelJointDef.dampingRatio = 0;
//        wheelJointDef.frequencyHz = 0;
//        wheelJointDef.motorSpeed = 0;
//        wheelJointDef.maxMotorTorque = 0;
//
//        world.createJoint(wheelJointDef);
    }

    public void shoot() {
        shooting = true;
        Projectile projectile = new Projectile(screen, playerBody, this);
        projectile.setImpulse();
        projectiles.add(projectile);
        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/pistol.wav"));
        long ID = sound.play();
        sound.setVolume(ID, 0.2f);
        ammunition--;
    }

    public void reload() {
        reloading = true;
        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/Eject Clip And Re-Load.wav"));
        long ID = sound.play();
        sound.setVolume(ID, 0.5f);
        ammunition = 20;
    }


    public void replenishHealth() {
        healthPoints += 30;
        if (healthPoints >= 100) {
            healthPoints = 100;
        }
    }

    public float getMouseDirection() {
        cursorBody.setTransform(screen.getMousePos().x, screen.getMousePos().y, 0);
        Vector3 playerPos = new Vector3(playerBody.getPosition().x, playerBody.getPosition().y, playerBody.getAngle());
        playerPos.sub(screen.getMousePos());
        Vector2 direction = new Vector2(playerPos.x, playerPos.y);
        return direction.angleRad();
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public State getState() {
        if ((playerBody.getLinearVelocity().y != 0 ||
                playerBody.getLinearVelocity().x != 0)
                && !reloading && !shooting) {
            return State.MOVING;
        } if (shooting) {
            return State.SHOOTING;
        } if (reloading) {
            return State.RELOADING;
        } else {
            return State.IDLE;
        }
    }

    public boolean isReloading() {
        return reloading;
    }

    public int getAmmunition() {
        return ammunition;
    }


}
