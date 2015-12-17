package se.victormattsson.game.sprites.projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.screens.PlayScreen;

/**
 * Created by Victor Mattsson on 2015-12-09.
 */
public class PlayerProjectile extends Projectile{

    public PlayerProjectile(PlayScreen screen, Body playerBody){
        super(screen, playerBody);
        lifeTime = 1.5f;
        stateTime = 0;
        projectileTexture = new Texture(Gdx.files.internal("bullet.png"));
        setRegion(projectileTexture);
        setBounds(0, 0, 6 / ShooterGame.PPM, 4 / ShooterGame.PPM);
        defineProjectile();
    }

    public void update(float dt){
        stateTime += dt;
        if (stateTime > lifeTime){
            destroy = true;
            stateTime = 0;
        }
        setRegion(projectileTexture);
        setPosition(projectile.getPosition().x, projectile.getPosition().y);
        setRotation((body.getAngle() * MathUtils.radiansToDegrees) - 170f);
        setOriginCenter();
    }

    private void defineProjectile() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(body.getPosition().x, body.getPosition().y);
        bdef.type = BodyDef.BodyType.DynamicBody;
        projectile = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4f / ShooterGame.PPM, 2f / ShooterGame.PPM);
        fdef.shape = shape;
        fdef.restitution = 0;
        fdef.filter.categoryBits = ShooterGame.PLAYER_PROJECTILE_BIT;
        fdef.filter.maskBits =
                ShooterGame.CRATE_BIT |
                ShooterGame.WALL_BIT |
                ShooterGame.ENEMY_BIT;
        projectile.createFixture(fdef).setUserData(this);

        shape.dispose();
    }

    public void setImpulse(){
        float angle = body.getAngle() * MathUtils.radiansToDegrees;
        float angle2 = (angle - 180f) * MathUtils.degreesToRadians;
        projectile.setLinearVelocity(new Vector2(20f * MathUtils.cos(angle2), 20f * MathUtils.sin(angle2)));
        projectile.setTransform(projectile.getPosition().x, projectile.getPosition().y, body.getAngle());
    }

}
