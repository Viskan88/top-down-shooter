package se.victormattsson.game.util;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.sprites.Crate;
import se.victormattsson.game.sprites.FirstAid;
import se.victormattsson.game.sprites.Player;
import se.victormattsson.game.sprites.Projectile;

/**
 * Created by Victor Mattsson on 2015-12-10.
 */
public class WorldContactListener implements ContactListener {


    @Override
    public void beginContact(Contact contact) {

        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int categoryDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch (categoryDef) {
            case ShooterGame.PROJECTILE_BIT | ShooterGame.WALL_BIT:
                if (fixtureA.getFilterData().categoryBits == ShooterGame.PROJECTILE_BIT) {
                    ((Projectile) fixtureA.getUserData()).setToDestroy();
                } else {
                    ((Projectile) fixtureB.getUserData()).setToDestroy();
                }
                break;
            case ShooterGame.PROJECTILE_BIT | ShooterGame.CRATE_BIT:
                if (fixtureA.getFilterData().categoryBits == ShooterGame.PROJECTILE_BIT) {
                    ((Projectile) fixtureA.getUserData()).setToDestroy();
                } else {
                    ((Projectile) fixtureB.getUserData()).setToDestroy();
                }
                break;
            case ShooterGame.PLAYER_BIT | ShooterGame.FIRST_AID_BIT:
                if (fixtureA.getFilterData().categoryBits == ShooterGame.PLAYER_BIT){
                    ((FirstAid)fixtureB.getUserData()).setToDestroy();
                    ((Player)fixtureA.getUserData()).replenishHealth();
                } else {
                    ((FirstAid)fixtureA.getUserData()).setToDestroy();
                    ((Player)fixtureB.getUserData()).replenishHealth();
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
