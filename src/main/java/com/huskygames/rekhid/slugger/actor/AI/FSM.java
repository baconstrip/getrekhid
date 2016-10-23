package com.huskygames.rekhid.slugger.actor.AI;

import com.huskygames.rekhid.actor.StickMan;
import com.huskygames.rekhid.slugger.input.ButtonEvent;
import com.huskygames.rekhid.slugger.util.DoublePair;

import java.util.LinkedList;
import java.util.Queue;

import static com.huskygames.rekhid.slugger.input.ButtonType.ATTACK_BUTTON;
import static com.huskygames.rekhid.slugger.input.ButtonType.JUMP_BUTTON;

/**
 * Created by brian on 10/22/2016.
 */
    public class FSM{

        protected int difficulty;
        protected String activeState;
        protected LinkedList<String> queuedStates;
        protected StickMan meAIPlayer;
        protected StickMan firstPlayer;

        //TODO: Eventually change FPlayer to list of enemy players
        public FSM(int Difficulty, StickMan AIPlayer, StickMan FPlayer) {
            this.difficulty = Difficulty;
            this.queuedStates = new LinkedList<String>();
            activeState = "idle";
            firstPlayer = FPlayer;
            meAIPlayer = AIPlayer;
        }

        public void pushState(String addState) {
            queuedStates.addLast(addState);
        }

        public void update(){

            if( firstPlayer.getPosition().getX() -meAIPlayer.getPosition().getX() > 30 && firstPlayer.getDamage() < 2.0){
                this.pushState("Attack");
            }else if(firstPlayer.getPosition().getX() -meAIPlayer.getPosition().getX()< 30 && firstPlayer.getDamage() > 2.0){
                this.pushState("Retreat");
            }else {
                this.pushState("Attack");
            }
            popState();
        }

        public void popState(){
            Queue<ButtonEvent> buttonEvents = new LinkedList<ButtonEvent>();

            String statecase = queuedStates.pop();
           // System.out.print(statecase);
            switch(statecase){
                case "Attack":


                    break;
                case "Retreat":
                    ButtonEvent AIattack = new ButtonEvent(JUMP_BUTTON, meAIPlayer, 69);
                    buttonEvents.add(AIattack);
                    meAIPlayer.AIreadController(buttonEvents);
                    break;
                case "Approach":

                    break;
                default:
                    ButtonEvent AIattack2 = new ButtonEvent(JUMP_BUTTON, meAIPlayer, 69);
                    buttonEvents.add(AIattack2);
                    meAIPlayer.AIreadController(buttonEvents);
                    break;
            }

        }

        public String activeState(){
            return activeState;
        }
}
