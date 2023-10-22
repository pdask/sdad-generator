package gr.petros.core;

import com.x5.template.Chunk;
import com.x5.template.Theme;
import gr.petros.model.JavaObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChunkFactory {

    public static File createFile(JavaObject obj, String templateName) {
        Chunk chunk = createChunk(obj, templateName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("temp");
            fileOutputStream.write(chunk.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new File("temp");
    }

    public static Chunk createChunk(JavaObject obj, String templateName) {
        Theme theme = new Theme("examples");
        Chunk chunk = theme.makeChunk(templateName);
        chunk.set("obj", obj);
        return chunk;
    }

    public static String create(JavaObject obj, String templateName) {
        return createChunk(obj, templateName).toString();
    }

}
