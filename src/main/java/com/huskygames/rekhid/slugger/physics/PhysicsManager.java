package com.huskygames.rekhid.slugger.physics;

import com.huskygames.rekhid.slugger.world.World;

import java.util.HashSet;

public class PhysicsManager {
    private static PhysicsManager instance;

    private final HashSet<PhysicsObject> objects = new HashSet<>();

    private World world;

    public PhysicsManager() {
        instance = this;
    }

    public static PhysicsManager getInstance() {
        if (instance == null) {
            instance = new PhysicsManager();
        }
        return instance;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void addObject(PhysicsObject object) {
        objects.add(object);
    }

    public void removeObject(PhysicsObject object){
        objects.remove(object);
    }

    public void updateObjects() {
        for (PhysicsObject object : objects) {
            object.update(world);
        }
    }
}
