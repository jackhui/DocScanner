package com.tradelink.scandocapp.model;

import java.util.ArrayList;

public class TLKRect {
    private ArrayList<TLKVertex> vertices;
    private boolean collided;
    private int balID;
    private int groupID;

    public TLKRect() {
        collided = false;
        vertices = new ArrayList<>();
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

    public void setCollided(boolean collided) {
        this.collided = collided;
    }

    public boolean isCollided() {
        return collided;
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
}
