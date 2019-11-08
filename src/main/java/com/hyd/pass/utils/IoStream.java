package com.hyd.pass.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class IoStream {

    public static String toString(InputStream inputStream, Charset charset) throws IOException {
        try (
            InputStreamReader reader = new InputStreamReader(inputStream, charset);
            BufferedReader bufferedReader = new BufferedReader(reader)
        ) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        }
    }

    public static String toString(File file, Charset charset) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return toString(fileInputStream, charset);
        }
    }

    public static void write(File file, String content, String charset) throws IOException {
        Files.write(file.toPath(), content.getBytes(charset));
    }
}
