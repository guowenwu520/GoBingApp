package com.example.gobang_app.bean;


/**
 * Created by lenov0 on 2016/1/25.
 */
public class Point {

    public int x;
    public int y;
    //权重
    public int inf;
    //黑白方
    public boolean mIsWhiteFirst;

    public void setXY(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Point() {
    }

    public Point(int x, int y, int inf, boolean mIsWhiteFirst) {
        this.x = x;
        this.y = y;
        this.inf = inf;
        this.mIsWhiteFirst = mIsWhiteFirst;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean ismIsWhiteFirst() {
        return mIsWhiteFirst;
    }

    public void setmIsWhiteFirst(boolean mIsWhiteFirst) {
        this.mIsWhiteFirst = mIsWhiteFirst;
    }

    public int getInf() {
        return inf;
    }

    public void setInf(int inf) {
        this.inf = inf;
    }
}

