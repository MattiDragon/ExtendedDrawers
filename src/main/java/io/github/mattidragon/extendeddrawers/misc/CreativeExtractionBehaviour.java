package io.github.mattidragon.extendeddrawers.misc;

public enum CreativeExtractionBehaviour {
    FRONT_MINE(true, true),
    ALL_MINE(false, true),
    FRONT_NO_BREAK(true, false),
    ALL_NO_BREAK(false, false),
    NORMAL(false, false);
    
    private final boolean frontOnly;
    private final boolean allowMine;
    
    CreativeExtractionBehaviour(boolean frontOnly, boolean allowMine) {
        this.frontOnly = frontOnly;
        this.allowMine = allowMine;
    }
    
    public boolean isFrontOnly() {
        return frontOnly;
    }
    
    public boolean isAllowMine() {
        return allowMine;
    }
}
