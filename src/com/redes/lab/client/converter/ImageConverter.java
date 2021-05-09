package com.redes.lab.client.converter;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
public class ImageConverter {

    private static String OUTPUT_FORMAT = "jpg";
    private final File file = new File("src/com/redes/lab/client/disk/send/");

    public byte[] getImageBytes(String name) throws IOException {

        String fileName = file.getAbsoluteFile() + File.separator + name;
        File imageFile = new File(fileName);

        try {
            var bufferedImage = ImageIO.read(imageFile);

            var baos = new ByteArrayOutputStream();

            ImageIO.write(bufferedImage, OUTPUT_FORMAT, baos);
            return baos.toByteArray();
        } catch (IIOException e ) {
            System.out.println("Imagem não encontrada");
        }

        return new byte[0];
    }
}
