package com.huskygames.rekhid.slugger.input;

import net.java.games.input.Component;

public abstract class ControlMapper {
    public abstract ButtonType translate(Component.Identifier iden);
}
