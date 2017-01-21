package com.huskygames.rekhid.slugger.actor;

import com.huskygames.rekhid.slugger.util.DoublePair;

public class HurtBox extends ActorCircle {
    private DoublePair effect;
    private Fighter parentActor;
    private Attack parentAttack;
    private double damage;
    private int lifetime;

    public HurtBox(
            DoublePair offset,
            Fighter parentActor,
            Attack parentAttack,
            double r,
            DoublePair hit,
            double damage,
            int life)
    {
        super(offset, parentActor, r);
        this.parentActor = parentActor;
        this.parentAttack = parentAttack;
        effect = hit;
        this.damage = damage;
        lifetime = life;
    }

    public DoublePair getLaunchVector() {
        return effect;
    }

    public Fighter getParentActor() {
        return parentActor;
    }

    public Attack getParentAttack() {
        return parentAttack;
    }

    public void setDamage(int newDamage){
        damage = newDamage;
    }
    public double getDamage() {
        return damage;
    }
    public boolean decrementLife() {
        return --lifetime <= 0;
    }
}
