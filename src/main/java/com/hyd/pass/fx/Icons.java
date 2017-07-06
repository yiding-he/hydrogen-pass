package com.hyd.pass.fx;

import javafx.scene.image.Image;

/**
 * (description)
 * created at 2017/7/6
 *
 * @author yidin
 */
public enum Icons {

    Logo("/logo.png"),

    //////////////////////////////////////////////////////////////

    ;

    private Image image;

    Icons(String imagePath) {
        this.image = new Image(getClass().getResourceAsStream(imagePath));
    }

    public Image getImage() {
        return image;
    }
}
