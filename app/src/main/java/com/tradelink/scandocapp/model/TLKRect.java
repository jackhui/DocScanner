package com.tradelink.scandocapp.model;

import java.util.ArrayList;

public class TLKRect {
    private ArrayList<TLKVertex> vertices;
    private boolean inRectangle;
    private int balID;
    private int groupID;

    public TLKRect() {
        inRectangle = false;
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

    public TLKVertex getLeftTop() {
        if (vertices.get(0).getX() < vertices.get(3).getX()) {
            if (vertices.get(0).getY() < vertices.get(1).getY()) {
                return vertices.get(0);
            } else {
                return vertices.get(1);
            }
        } else {
            if (vertices.get(0).getY() < vertices.get(1).getY()) {
                return vertices.get(3);
            } else {
                return vertices.get(2);
            }
        }
    }

    public int getWidth() {
        return Math.abs(vertices.get(0).getX() - vertices.get(3).getX());
    }

    public int getHeight() {
        return Math.abs(vertices.get(0).getY() - vertices.get(1).getY());
    }
}
