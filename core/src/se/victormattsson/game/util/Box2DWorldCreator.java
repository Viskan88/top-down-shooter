package se.victormattsson.game.util;


import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.screens.PlayScreen;
import se.victormattsson.game.sprites.Crate;
import se.victormattsson.game.sprites.FirstAid;

/**
 * Created by victormattsson on 2015-12-06.
 */
public class Box2DWorldCreator {

    private Array<FirstAid> firstaids = new Array<FirstAid>();
    private Array<Crate> crates = new Array<Crate>();

    public Box2DWorldCreator(PlayScreen screen){

        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        Body body;

        //Generate wall playerBody/fixtures
        for (MapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / ShooterGame.PPM,
                    (rectangle.getY() + rectangle.getHeight() / 2) / ShooterGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rectangle.getWidth() / 2 / ShooterGame.PPM, rectangle.getHeight() / 2 / ShooterGame.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //create box body/fixtures
        for (MapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
            crates.add(new Crate(screen, rectangle));
        }

        //create first aid body/fixtures
        for (MapObject object: map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
            firstaids.add(new FirstAid(screen, rectangle));
        }
    }

    public Array<Crate> getCrates() {
        return crates;
    }

    public Array<FirstAid> getFirstAids() {
        return firstaids;
    }

}
