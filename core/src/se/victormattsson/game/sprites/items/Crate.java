package se.victormattsson.game.sprites.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import javax.net.ssl.SNIHostName;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.screens.PlayScreen;

/**
 * Created by Victor Mattsson on 2015-12-08.
 */
public class Crate extends Sprite {

    private Texture boxTexture;
    private World world;
    private Body body;
    private Rectangle rectangle;

    public Crate(PlayScreen screen, Rectangle rectangle){
        this.rectangle = rectangle;
        this.world = screen.getWorld();
        boxTexture = new Texture(Gdx.files.internal("RTS_Crate.png"));
        setPosition(rectangle.getX() / ShooterGame.PPM, rectangle.getY() / ShooterGame.PPM);
        setBounds(getX(), getY(), 32 / ShooterGame.PPM, 32 / ShooterGame.PPM);

        defineCrate();
    }

    private void defineCrate() {
        BodyDef bdef = new BodyDef();
        bdef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / ShooterGame.PPM,
                (rectangle.getY() + rectangle.getHeight() / 2) / ShooterGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rectangle.getWidth() / 2 / ShooterGame.PPM, rectangle.getHeight() / 2 / ShooterGame.PPM);
        fdef.shape = shape;
        fdef.friction = 10f;
        fdef.density = 10f;
        fdef.restitution = 0.05f;
        fdef.filter.categoryBits = ShooterGame.CRATE_BIT;
        body.createFixture(fdef).setUserData(this);

        shape.dispose();
    }

    public void update(float dt) {
        setRegion(boxTexture);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        setOriginCenter();
        body.setLinearDamping(2f);
        body.setAngularDamping(2f);
    }
}
