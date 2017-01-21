package com.huskygames.rekhid.slugger.actor;

import com.huskygames.rekhid.slugger.util.DoublePair;
import com.huskygames.rekhid.slugger.util.collison.shape.Shape;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Attack {
    private DoublePair[][] offsets;
    private int[][] damages;
    private DoublePair[][] effects;
    private int[][] area;
    private int[] ticks;
    private Fighter parent;
    private int temp;
    private int cur;
    private boolean finished = false;

    protected Set<Shape> hurters = new HashSet<>();

    public Attack(DoublePair[][] boxes, int[] stages, int[][] damage, DoublePair[][] launch, int[][]areas, Fighter parent){
        offsets = boxes;
        ticks = stages;
        damages = damage;
        effects = launch;
        area = areas;
        this.parent = parent;
        cur = 0;
        temp = 0;
        genBoxes(cur);
    }

    public boolean next(){
        Iterator<Shape> i = hurters.iterator();
        Shape current;
        while (i.hasNext()) {
            current = i.next();
            if (current instanceof HurtBox) {
                if (((HurtBox) current).decrementLife()) {
                    i.remove();
                }
            }
        }
        if(!finished) {
            if (ticks[cur] - temp > 0) {
                temp++;
            } else {
                temp = 0;
                cur++;
                if (cur == ticks.length) {
                    finished = true;
                } else {
                    genBoxes(cur);
                }
            }
        }
        return finished;
    }

    public Actor getParent(){
        return parent;
    }

    public void genBoxes(int index){
        for(int i = 0; i < offsets[cur].length; i++){
            if(offsets[cur][i] != null) {
                hurters.add(new HurtBox(offsets[cur][i], parent, this, area[cur][i], effects[cur][i], damages[cur][i], ticks[i]));
            }
        }
    }

    public Set<Shape> getPain(){
        return hurters;
    }

    public boolean isFinished(){
        return finished;
    }
}
