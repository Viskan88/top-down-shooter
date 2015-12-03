package se.victormattsson.game.screens.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import se.victormattsson.game.screens.PlayScreen;

/**
 * Created by victormattsson on 2015-12-02.
 */
public class Player extends Sprite {

    public enum State {IDLE, MOVE}
    public World world;
    public Body body;

    private TextureRegion playerIdle;
    private Animation playerMove;

    private float stateTimer;

    public Player(PlayScreen screen){
        world = screen.getWorld();
        stateTimer = 0;

        //Array with TextureRegions for animations
        Array<TextureRegion> frames = new Array<TextureRegion>();

        playerIdle = new TextureRegion(screen.getAtlas().findRegion("survivor-move_handgun_0_small"));

        for (int i  = 0; i < 19; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("survivor-move_handgun_" + i + "_small")));
        }
        playerMove = new Animation(0.033f, frames);
        frames.clear();

        //Set bound of sprite to ber correct size
        setBounds(0, 0, 64, 64);
        setRegion(playerMove.getKeyFrame(Gdx.graphics.getDeltaTime(), true));
        definePlayer();
    }

    public void update(float dt) {
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    private TextureRegion getFrame(float dt) {

        TextureRegion region;

        region = playerMove.getKeyFrame(stateTimer, true);

        stateTimer += Gdx.graphics.getDeltaTime();

        return region;
    }

    public void definePlayer(){
        //Body
        BodyDef bdef = new BodyDef();
        bdef.position.set(1000,500);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        //Fixture
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(20);

        fdef.shape = shape;
        body.createFixture(fdef);
    }
}
