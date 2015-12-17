package se.victormattsson.game.sprites.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.screens.PlayScreen;

/**
 * Created by Victor Mattsson on 2015-12-08.
 */
public class FirstAid extends Sprite{

    private Texture boxTexture;
    private World world;
    private Body body;
    private Rectangle rectangle;
    private boolean toDestroy;
    private boolean destroyed;

    public FirstAid(PlayScreen screen, Rectangle rectangle){
        this.rectangle = rectangle;
        this.world = screen.getWorld();
        boxTexture = new Texture(Gdx.files.internal("firstaid.png"));
        setPosition(rectangle.getX() / ShooterGame.PPM, rectangle.getY() / ShooterGame.PPM);
        setBounds(getX(), getY(), 64 / ShooterGame.PPM, 33 / ShooterGame.PPM);

        defineFirstAid();
    }

    private void defineFirstAid() {
        BodyDef bdef = new BodyDef();
        bdef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / ShooterGame.PPM,
                (rectangle.getY() + rectangle.getHeight() / 2) / ShooterGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((rectangle.getWidth() / 2 / ShooterGame.PPM), rectangle.getHeight() / 2 / ShooterGame.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = ShooterGame.FIRST_AID_BIT;
        fdef.filter.maskBits = ShooterGame.PLAYER_BIT;
        body.createFixture(fdef).setUserData(this);

        shape.dispose();
    }

    public void update(float dt) {
        if (toDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
        }else {
            setRegion(boxTexture);
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            setOriginCenter();
        }
    }

    public void setToDestroy() {
        toDestroy = true;
    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed) {
            super.draw(batch);
        }
    }
}
