package com.hyd.pass.utils;

import com.hyd.pass.Logger;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class);

    public static boolean ensureFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
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
        return org.apache.commons.io.FileUtils.readFileToString(file, "UTF-8");
    }

    public static void write(File file, String content, String charset) throws IOException {
        org.apache.commons.io.FileUtils.write(file, content, charset);
    }
}
