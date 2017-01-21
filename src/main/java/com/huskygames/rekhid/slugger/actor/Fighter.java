package com.huskygames.rekhid.slugger.actor;

import com.huskygames.rekhid.slugger.physics.PhysicsManager;
import com.huskygames.rekhid.slugger.util.DoublePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public abstract class Fighter extends Actor {

    private final static Logger logger = LogManager.getLogger(Fighter.class);
    private int lives = 2;
    protected int jumps = 0;
    protected double damage;
    protected boolean executing;
    protected boolean dead;
    protected boolean disabled;
    protected boolean facingLeft;
    protected Projectile projectile;
    /*
        While this fighter is attacking, this set keeps track of the other Fighters that have been
        hit already, so that they aren't repeatedly hit each tick by the same attack
        TODO: refactor this down to an "attack" class
     */
    protected Set<Fighter> fightersHit = new HashSet<>();

    public Fighter(DoublePair pos, DoublePair vel) {
        super(pos, vel);
        damage = 4.00;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void addDamage(double amount) {
        this.damage += amount;
    }

    public void healDamage(double amount) {
        this.damage -= amount;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void removeLife() {
        this.lives--;

    }

    public void die() {
        this.dead = true;
        this.removeLife();
    }

    public int getLives() {
        return this.lives;
    }

    public void setJumps(int jumps) {
        this.jumps = jumps;
    }

    public int getJumps() {
        return this.jumps;
    }
    public abstract String getName();

    public void takeDamage(HurtBox hit) {
        // determine if we're dealing with a projectile, and handle accordingly
        Fighter parent;
        if(hit.getParentActor() instanceof Projectile){
            parent = ((Projectile) hit.getParentActor()).getParent();
        }
        else{
            parent = hit.getParentActor();
        }

        // double check we're not hurting ourselves
        if (parent.equals(this)) {
            return;
        }
        // if we've already been hit by this attacker, don't hurt us
        if (parent.getDamaged().contains(this)) {
            return;
        }
        parent.getDamaged().add(this);
        damage -= hit.getDamage() * 0.01;
        velocity.addInPlace(hit.getLaunchVector());
    }

    public Set<Fighter> getDamaged() {
        return fightersHit;
    }

    public void clearDamaged() {
        fightersHit.clear();
    }

    public boolean attacking(){
        return theAttack != null;
    }

    public void endAnimation(){
        executing = false;

    }

    public boolean hasProjectile(){
        if(projectile != null){
            return true;
        }
        return false;
    }

    public Projectile getProjectile(){
        return projectile;
    }

    public void removeProjectile(){
        PhysicsManager.getInstance().removeObject(projectile);
        projectile = null;
    }
}
