package sample;

public class Cue {

    public int  strength, angle, xCue, yCue;
    public boolean visible = false;

    public Cue(int strength, int angle, int xCue, int yCue) {
        this.strength = strength;
        this.angle = angle;
        this.xCue = xCue;
        this.yCue = yCue;
    }

    public boolean isVisible(){
        return visible;
    }
}