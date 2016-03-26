package com.mycloset.server.model;

import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by jtan on 2/17/16.
 */
public class Item implements Serializable {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private int itemInStock;
    private List<String> imagePaths = new ArrayList<>();

    private BufferedImage coverImage;

    public Item(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setItemInStock(int itemInStock) {
        this.itemInStock = itemInStock;
    }

    public int getItemInStock() {
        return this.itemInStock;
    }

    public void addImagePath(String imagePath) {
        this.imagePaths.add(imagePath);
    }

    public String getCoverImagePath() {
        return this.imagePaths.get(0);
    }

    public List<String> getImagePaths() {
        return this.imagePaths;
    }


    // following for testing
    public void setCoverImage(File file) throws IOException {
        this.coverImage = ImageIO.read(file);
    }

    private byte[] coverImageToByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(this.coverImage, "jpg", baos);
        return baos.toByteArray();
    }

    public Map<String, String> getShortSerializable() throws Exception {
        Map<String, String> shortSerializable = new HashMap<>();
        shortSerializable.put("id", this.id.toString());
        shortSerializable.put("route", "");
        shortSerializable.put("source", Base64.encodeBase64String(coverImageToByteArray()));
        shortSerializable.put("description", this.description);
        return shortSerializable;
    }

}
