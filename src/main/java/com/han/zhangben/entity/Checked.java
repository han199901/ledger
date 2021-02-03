package com.han.zhangben.entity;

import android.widget.CheckBox;

public class Checked {
    CheckBox checkBox;
    int position;
    int id;


    public Checked(CheckBox checkBox, int position, int id) {
        this.checkBox = checkBox;
        this.position = position;
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
