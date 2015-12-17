package se.victormattsson.game.util;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import se.victormattsson.game.ShooterGame;
import se.victormattsson.game.sprites.enemies.Enemy;
import se.victormattsson.game.sprites.items.Ammunition;
import se.victormattsson.game.sprites.items.FirstAid;
import se.victormattsson.game.sprites.player.Player;
import se.victormattsson.game.sprites.projectile.EnemyProjectile;
import se.victormattsson.game.sprites.projectile.PlayerProjectile;

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
            case ShooterGame.PLAYER_PROJECTILE_BIT | ShooterGame.WALL_BIT:
                if (fixtureA.getFilterData().categoryBits == ShooterGame.PLAYER_PROJECTILE_BIT) {
                    ((PlayerProjectile) fixtureA.getUserData()).setToDestroy();
                } else {
                    ((PlayerProjectile) fixtureB.getUserData()).setToDestroy();
                }
                break;
            case ShooterGame.PLAYER_PROJECTILE_BIT | ShooterGame.CRATE_BIT:
                if (fixtureA.getFilterData().categoryBits == ShooterGame.PLAYER_PROJECTILE_BIT) {
                    ((PlayerProjectile) fixtureA.getUserData()).setToDestroy();
                } else {
                    ((PlayerProjectile) fixtureB.getUserData()).setToDestroy();
                }
                break;
            case ShooterGame.PLAYER_BIT | ShooterGame.FIRST_AID_BIT:
                if (fixtureA.getFilterData().categoryBits == ShooterGame.PLAYER_BIT) {
                    ((FirstAid) fixtureB.getUserData()).setToDestroy();
                    ((Player) fixtureA.getUserData()).replenishHealth();
                } else {
                    ((FirstAid) fixtureA.getUserData()).setToDestroy();
                    ((Player) fixtureB.getUserData()).replenishHealth();
                }
                break;
            case ShooterGame.PLAYER_BIT | ShooterGame.AMMO_BIT:
                if (fixtureA.getFilterData().categoryBits == ShooterGame.AMMO_BIT) {
                    ((Player) fixtureB.getUserData()).replenishAmmo();
                    ((Ammunition) fixtureA.getUserData()).setToDestroy();
                } else {
                    ((Player) fixtureB.getUserData()).replenishAmmo();
                    ((Ammunition) fixtureA.getUserData()).setToDestroy();
                }
                break;
            case ShooterGame.PLAYER_PROJECTILE_BIT | ShooterGame.ENEMY_BIT:
                if (fixtureA.getFilterData().categoryBits == ShooterGame.ENEMY_BIT) {
                    ((Enemy) fixtureA.getUserData()).hit();
                    ((PlayerProjectile) fixtureB.getUserData()).setToDestroy();
                } else {
                    ((Enemy) fixtureB.getUserData()).hit();
                    ((PlayerProjectile) fixtureA.getUserData()).setToDestroy();
                }
                break;
            case ShooterGame.ENEMY_PROJECTILE_BIT | ShooterGame.PLAYER_BIT:
                if (fixtureA.getFilterData().categoryBits == ShooterGame.PLAYER_BIT) {
                    ((Player) fixtureA.getUserData()).hit();
                    ((EnemyProjectile) fixtureB.getUserData()).setToDestroy();
                } else {
                    ((Player) fixtureB.getUserData()).hit();
                    ((EnemyProjectile) fixtureA.getUserData()).setToDestroy();
                }
                break;
            case ShooterGame.ENEMY_PROJECTILE_BIT | ShooterGame.WALL_BIT:
                if (fixtureA.getFilterData().categoryBits == ShooterGame.ENEMY_PROJECTILE_BIT) {
                    ((EnemyProjectile) fixtureA.getUserData()).setToDestroy();
                } else {
                    ((EnemyProjectile) fixtureB.getUserData()).setToDestroy();
                }

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
