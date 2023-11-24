package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class PositionNode {

    LngLat position;
    private double f, g, h;
    PositionNode parent;

    double angle;

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

    public void setF(double f){
        this.f = f;
    }
    public void setG(double g){
        this.g = g;
    }
    public void setH(double h){
        this.h = h;
    }


}
