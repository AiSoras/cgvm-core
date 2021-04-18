package ru.etu.cgvm.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static void saveContentToFile(final String content, final File file) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(file);
        writer.println(content);
        writer.close();
    }

    public static String readContent(final File file) throws IOException {
        return org.apache.jena.util.FileUtils.readWholeFileAsUTF8(file.getAbsolutePath());
    }
}
