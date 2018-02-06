package com.hyd.pass.fx;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * (description)
 * created at 2017/7/6
 *
 * @author yidin
 */
public enum Icons {

    Logo("/logo.png"),
    Folder("/icons/folder.png"),

    //////////////////////////////////////////////////////////////

    ;

    private Image image;

    Icons(String imagePath) {
        this.image = new Image(getClass().getResourceAsStream(imagePath));
    }

    public Image getImage() {
        return image;
    }

    public ImageView getIconImageView() {
        ImageView imageView = new ImageView(getImage());
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        return imageView;
    }
}
