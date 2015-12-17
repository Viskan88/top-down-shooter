package se.victormattsson.game.sprites.player;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.screens.PlayScreen;

/**
 * Created by Victor Mattsson on 2015-12-15.
 */
public class Crosshair extends Sprite {

    private Body crosshairBody;
    private World world;
    private PlayScreen screen;
    private Texture crosshair;

    public Crosshair(PlayScreen screen){
        this.world = screen.getWorld();
        this.screen = screen;
        crosshair = new Texture(Gdx.files.internal("crosshair.png"));
        setRegion(crosshair);
        setBounds(0, 0, 43 / ShooterGame.PPM, 36 / ShooterGame.PPM);
        defineCrosshair();
    }

    public void update(){

        crosshairBody.setTransform(screen.getMousePos().x, screen.getMousePos().y, 0);
        setBounds(crosshairBody.getPosition().x - getWidth() / 2, crosshairBody.getPosition().y - getHeight() / 2, 43 / ShooterGame.PPM, 36 / ShooterGame.PPM);
    }

    private void defineCrosshair() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.KinematicBody;
        crosshairBody = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(20 / ShooterGame.PPM);

        fdef.shape = shape;
        fdef.filter.categoryBits = ShooterGame.CURSOR_BIT;
        crosshairBody.createFixture(fdef).setUserData(this);
        crosshairBody.setActive(false);

        shape.dispose();
    }

}
