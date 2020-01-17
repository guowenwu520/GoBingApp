package com.example.gobang_app.bean;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by lenov0 on 2016/1/25.
 */
@JsonObject
public class Point {

    @JsonField
    public int x;
    @JsonField
    public int y;

    public void setXY(int x, int y){
        this.x = x;
        this.y = y;
    }
}

