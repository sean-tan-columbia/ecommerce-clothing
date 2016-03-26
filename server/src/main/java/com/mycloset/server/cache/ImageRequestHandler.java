package com.mycloset.server.cache;

import com.mycloset.server.cache.FileRequestHandler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by jtan on 3/11/16.
 */
public class ImageRequestHandler extends FileRequestHandler {

    private final static Logger logger = LoggerFactory.getLogger(ImageRequestHandler.class);

    private static ImageRequestHandler imageRequestHandler;

    private ImageRequestHandler() {
        super(ImageRequestHandler.class.getName());
    }

    public static ImageRequestHandler getInstance() {
        if (imageRequestHandler == null) {
            imageRequestHandler = new ImageRequestHandler();
        }
        return imageRequestHandler;
    }

    public String handle(String imagePath) throws Exception {
        return Base64.encodeBase64String(this.get(imagePath));
    }

    @Override
    protected byte[] getFileByteStream(File file) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(ImageIO.read(file), "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}
