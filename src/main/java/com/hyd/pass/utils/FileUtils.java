package com.hyd.pass.utils;

import com.hyd.pass.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class);

    public static boolean ensureFile(String filePath) {
        if (Str.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }

        try {
            return file.createNewFile();
        } catch (IOException e) {
            logger.error("创建文件失败", e);
            return false;
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static String read(File file) throws IOException {
        return IoStream.toString(file, StandardCharsets.UTF_8);
    }

    public static void write(File file, String content, String charset) throws IOException {
        IoStream.write(file, content, charset);
    }
}
