package com.hyd.pass.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * User Configuration
 *
 * @author yidin
 */
public class UserConfig {

    private static final Logger LOG = LoggerFactory.getLogger(UserConfig.class);

    private static final String userConfigPath =
            System.getProperty("user.home") + "/.hydrogen-pass/user-config.properties";

    private static boolean readOnly = false;

    private static final Properties properties = new Properties();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            File file = new File(userConfigPath);
            if (!file.exists()) {

                File parentFile = file.getParentFile();
                if (!parentFile.exists() && !parentFile.mkdirs()) {
                    LOG.error("Unable to create configuration file");
                    return;
                }

                if (!file.createNewFile()) {
                    LOG.error("Unable to create configuration file");
                    return;
                }
            } else if (!file.canWrite()) {
                readOnly = true;
            }

            try (Reader reader = new FileReader(file)) {
                properties.load(reader);
            }
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }

    public static String getString(String key, String def) {
        return properties.getProperty(key, def);
    }

    public static void setString(String key, String value) {
        properties.put(key, value);

        if (!readOnly) {
            try {
                try (Writer writer = new FileWriter(new File(userConfigPath))) {
                    properties.store(writer, "");
                }
            } catch (IOException e) {
                throw new ConfigException(e);
            }
        }
    }
}
