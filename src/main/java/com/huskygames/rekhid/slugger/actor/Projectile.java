package com.huskygames.rekhid.slugger.actor;

import com.huskygames.rekhid.Rekhid;
import com.huskygames.rekhid.slugger.resource.Resource;
import com.huskygames.rekhid.slugger.resource.sprite.SpriteSheet;
import com.huskygames.rekhid.slugger.util.DoublePair;
import com.huskygames.rekhid.slugger.util.collison.shape.Shape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

/**
 * Projectile demo class, can be extended and overridden to do different things as need be.
 * Created by Kyle on 11/5/2016.
 */
public abstract class Projectile extends Fighter {

    private Projectiles proj;
    private SpriteSheet sprite;
    private Set<Shape> colliders;
    private boolean facingleft;
    private Fighter parent;
    private int TTL; // ticks to live

    private static final Logger logger = LogManager.getLogger(Projectile.class.getName());

    public Projectile(DoublePair pos, DoublePair vel, Fighter par, Projectiles type, int lifetime){
        super(pos, vel);
        this.proj = type;
        parent = par;

        setGravity(false);
        this.lifetime = lifetime;
        TTL = lifetime;

        this.sprite = (SpriteSheet) Rekhid.getInstance().getResourceManager()
                .requestResource(Resource.TEST_PROJECTILE);

        colliders = new HashSet<Shape>();

        logger.info("I've been created!!");
    }

    public void addCollider(ActorCircle circle){
        colliders.add(circle);
    }

    public int getHeight() {
        return 10;
    }

    @Override
    public Set<Shape> getCollisions() {
        return colliders;
    }

    @Override
    public BufferedImage getSprite() {
        return sprite.getSprite(0, 0, facingleft);
    }

    @Override
    public double getAspectRatio() {
        return 0;
    }

    public Fighter getParent(){return parent;}

    public boolean decrementTime(){
        TTL--;
        if(TTL == 0){
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return proj.toString();
    }

    public boolean isLeft(){
        return facingleft;
    }
}
