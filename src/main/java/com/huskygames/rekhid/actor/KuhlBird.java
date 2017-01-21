package com.huskygames.rekhid.actor;

import com.huskygames.rekhid.slugger.actor.*;
import com.huskygames.rekhid.slugger.util.DoublePair;
import com.huskygames.rekhid.slugger.util.collison.shape.Shape;

import java.util.HashSet;

/**
 * Created by Kyle on 11/12/2016.
 */
public class KuhlBird extends Projectile {


    public KuhlBird(DoublePair pos, DoublePair vel, Fighter par, Projectiles type, int lifetime) {
        super(pos, vel, par, type, lifetime);
        createBoxes();
    }

    private void createBoxes(){
        int multiplier = isLeft() ? -1 : 1;
        DoublePair [][] offsets = new DoublePair [][]{new DoublePair[]{new DoublePair(0.0, 5.0)}};
        DoublePair [][] directions = new DoublePair [][]{new DoublePair[]{new DoublePair(multiplier * 20, -5)}};
        int [] ticks = new int[]{20};
        int [][] damages = new int[][]{new int[]{30}};
        int [][] areas = new int[][]{new int[]{10}};


        addCollider(new ActorCircle(new DoublePair(0.0, 5.0), this, 6.0));
        theAttack = new Attack(offsets, ticks, damages, directions, areas, this);
    }

    @Override
    public void tick() {
        if (decrementTime()) {
            getParent().removeProjectile();
            return;
        }
    }
}
