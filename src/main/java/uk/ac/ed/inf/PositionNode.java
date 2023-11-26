package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * The PositionNode class is used for storing information about a specific point.
 */
public class PositionNode {

    private LngLat position;
    private double f, g, h;
    private PositionNode parent;
    private double angle;

    public PositionNode(LngLat position){
        this.position = position;

        parent = null;
        f = 0;
        g = 0;
        h = 0;
        angle = 999;
    }

    public double getF(){
        return this.f;
    }
    public double getG(){
        return this.g;
    }
    public double getH(){
        return this.h;
    }
    public LngLat getPosition() {
        return this.position;
    }
    public double getAngle() {
        return angle;
    }
    public PositionNode getParent() {
        return parent;
    }

    public void setF(double f){
        this.f = f;
    }
    public void setG(double g){
        this.g = g;
    }
    public void setH(double h){
        this.h = 1.5*h;
    }
    public void setPosition(LngLat position) {
        this.position = position;
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public void setParent(PositionNode parent) {
        this.parent = parent;
    }
}
