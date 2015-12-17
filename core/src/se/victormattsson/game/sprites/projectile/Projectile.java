package se.victormattsson.game.sprites.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import se.victormattsson.game.screens.PlayScreen;

/**
 * Created by Victor Mattsson on 2015-12-14.
 */
public class Projectile extends Sprite{

    protected World world;
    protected Body projectile;
    protected Body body;
    protected float lifeTime;
    protected float stateTime;
    protected boolean destroy;
    protected boolean destroyed;
    protected Texture projectileTexture;

    public Projectile(PlayScreen screen, Body body){
        this.world = screen.getWorld();
        this.body = body;
    }

    public void update(float dt){

    }

    public boolean toDestroy(){
        return destroy;
    }

    public void setToDestroy(){
        destroy = true;
    }

    public Body getProjectile() {
        return projectile;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
}
