package com.mycloset.server.model;

import java.io.Serializable;

/**
 * Created by jtan on 3/5/16.
 */
public class Image implements Serializable {

    Long imageId;
    Long itemId;
    String imagePath;

    public Image(Long imageId) {
        this.imageId = imageId;
    }

    public Long getImageId() {
        return this.imageId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
