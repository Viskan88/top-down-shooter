package se.victormattsson.game.sprites;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.screens.PlayScreen;

/**
 * Created by Victor Mattsson on 2015-12-09.
 */
public class Projectile {

    private World world;
    private PlayScreen screen;
    private Player player;

    public Body getProjectile() {
        return projectile;
    }

    private Body projectile;
    private Body playerBody;
    private float lifeTime = 1.5f;
    private float stateTime = 0;
    private boolean destroy;

    public Projectile(PlayScreen screen, Body playerBody, Player player){

        this.screen = screen;
        this.world = screen.getWorld();
        this.playerBody = playerBody;
        this.player = player;

        defineProjectile();
    }

    public void update(float dt){
        stateTime += dt;
        if (stateTime > lifeTime){
            destroy = true;
            stateTime = 0;
        }
    }

    private void defineProjectile() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(playerBody.getPosition().x, playerBody.getPosition().y);
        bdef.type = BodyDef.BodyType.DynamicBody;
        projectile = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4f / ShooterGame.PPM, 2f / ShooterGame.PPM);
        fdef.shape = shape;
        fdef.restitution = 0;
        fdef.filter.categoryBits = ShooterGame.PROJECTILE_BIT;
        fdef.filter.maskBits =
                ShooterGame.CRATE_BIT |
                ShooterGame.WALL_BIT;
        projectile.createFixture(fdef).setUserData(this);

        shape.dispose();
    }

    public void setImpulse(){
        float angle = playerBody.getAngle() * MathUtils.radiansToDegrees;
        float angle2 = (angle - 180f) * MathUtils.degreesToRadians;
        projectile.setLinearVelocity(new Vector2(20f * MathUtils.cos(angle2), 20f * MathUtils.sin(angle2)));
        projectile.setTransform(projectile.getPosition().x, projectile.getPosition().y, playerBody.getAngle());
    }

    public boolean toDestroy(){
        return destroy;
    }

    public void setToDestroy(){
        destroy = true;
    }


}
