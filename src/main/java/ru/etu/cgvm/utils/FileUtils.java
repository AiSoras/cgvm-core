package ru.etu.cgvm.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static void saveContentToFile(final String content, final File file) throws FileNotFoundException {
        var writer = new PrintWriter(file);
        writer.println(content);
        writer.close();
    }

    public static String readContent(final File file) throws IOException {
        return org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }
}
