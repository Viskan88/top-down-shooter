package se.victormattsson.game.sprites.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.screens.PlayScreen;
import se.victormattsson.game.sprites.player.Player;
import se.victormattsson.game.sprites.projectile.EnemyProjectile;
import se.victormattsson.game.sprites.projectile.PlayerProjectile;

/**
 * Created by Victor Mattsson on 2015-12-14.
 */
public class Enemy extends Sprite {

    private World world;
    public Body body;
    private Ellipse ellipse;
    private boolean toDestroy;
    private boolean destroyed;
    private int healthPoints = 100;
    private PlayScreen screen;
    private Array<EnemyProjectile> projectiles = new Array<EnemyProjectile>();
    private float fireDelay;
    private Animation enemyIdle;

    public Enemy(PlayScreen screen, Ellipse ellipse) {
        this.world = screen.getWorld();
        this.ellipse = ellipse;
        this.screen = screen;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < 19; i++) {
            frames.add(new TextureRegion(screen.getAtlas("enemy_idle").findRegion("survivor-idle_handgun_" + i + "_small")));
        }
        enemyIdle = new Animation(0.1f, frames);
        frames.clear();

        setBounds(ellipse.x, ellipse.y, 64 / ShooterGame.PPM, 64 / ShooterGame.PPM);
        setRegion(enemyIdle.getKeyFrame(Gdx.graphics.getDeltaTime(), true));

        defineEnemy();
    }

    private void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set((ellipse.x + ellipse.width / 2) / ShooterGame.PPM,
                (ellipse.y + ellipse.height / 2) / ShooterGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius((ellipse.width / 2) / ShooterGame.PPM);
        fdef.shape = shape;
        fdef.restitution = 0;
        fdef.density = 20f;
        fdef.filter.categoryBits = ShooterGame.ENEMY_BIT;
        fdef.filter.maskBits = ShooterGame.WALL_BIT |
                ShooterGame.PLAYER_BIT |
                ShooterGame.CRATE_BIT |
                ShooterGame.PLAYER_PROJECTILE_BIT |
                ShooterGame.ENEMY_BIT;
        body.createFixture(fdef).setUserData(this);

        shape.dispose();
    }

    public void update(float dt, Player player) {

        for (Iterator<EnemyProjectile> projectiles = getProjectiles().iterator(); projectiles.hasNext(); ) {
            EnemyProjectile projectile = projectiles.next();
            projectile.update(dt);
            if (projectile.toDestroy()) {
                world.destroyBody(projectile.getProjectile());
                projectiles.remove();
            }
        }

        if (getHypotenuse(player) < 9){
            fireDelay -= dt;
            if (fireDelay <= 0) {
                shoot();
                fireDelay += 1;
            }

            if (getHypotenuse(player) <= 4){
                body.setLinearVelocity(0, 0);
            }else {
            body.setLinearVelocity(2 * (getxDistance(player.playerBody) / getHypotenuse(player)),
                    2 * (getyDistance(player.playerBody) / getHypotenuse(player)));
            }
        }else {
            body.setLinearVelocity(0, 0);
        }
        body.setTransform(body.getPosition().x, body.getPosition().y, getAngleToPlayer(player));

        if (toDestroy && !destroyed) {
            world.destroyBody(body);
            for (EnemyProjectile projectile : projectiles) {
                world.destroyBody(projectile.getProjectile());
            }
            destroyed = true;
        }

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRotation((body.getAngle() * MathUtils.radiansToDegrees) + 20);
        setOriginCenter();
    }

    private void shoot() {
        EnemyProjectile projectile = new EnemyProjectile(screen, body);
        projectile.setImpulse();
        projectiles.add(projectile);
    }

    public void hit() {
        healthPoints -= 25;
        if (isDead()) toDestroy = true;
    }

    public boolean isDead(){
        return (healthPoints <= 0);
    }

    private float getHypotenuse(Player player) {
        float xDistance = getxDistance(player.playerBody);
        float yDistance = getyDistance(player.playerBody);
        return (float) Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    }

    private float getxDistance(Body player) {
        return player.getPosition().x - body.getPosition().x;
    }

    private float getyDistance(Body player) {
        return player.getPosition().y - body.getPosition().y;
    }

    private float getAngleToPlayer(Player player){
        Vector3 playerPos = player.getPlayerPos();
        playerPos.sub(body.getPosition().x, body.getPosition().y, body.getAngle());
        Vector2 direction = new Vector2(playerPos.x, playerPos.y);
        return direction.angleRad();
    }

    public Array<EnemyProjectile> getProjectiles() {
        return projectiles;
    }

    @Override
    public void draw(Batch batch){
        super.draw(batch);
        for (EnemyProjectile projectile : getProjectiles()){
            projectile.draw(batch);
        }
    }
}
