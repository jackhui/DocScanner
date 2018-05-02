package com.tradelink.scandocapp.model;

import java.util.ArrayList;

public class TLKRect {
    private ArrayList<TLKVertex> vertices;
    private boolean inRectangle;
    private int balID;
    private int groupID;
    private boolean movable;

    public TLKRect() {
        inRectangle = false;
        vertices = new ArrayList<>();
        movable = false;
    }

    public TLKRect(ArrayList<TLKVertex> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<TLKVertex> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<TLKVertex> vertices) {
        this.vertices = vertices;
    }

    public void setInRectangle(boolean inRectangle) {
        this.inRectangle = inRectangle;
    }

    public boolean isInRectangle() {
        return inRectangle;
    }

    public int getBalID() {
        return balID;
    }

    public void setBalID(int balID) {
        this.balID = balID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public boolean isMovable() {
        return this.movable;
    }
}
