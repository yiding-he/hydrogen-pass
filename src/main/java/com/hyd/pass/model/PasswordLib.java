package com.hyd.pass.model;

import static com.hyd.pass.utils.AESUtils.encode128;
import static com.hyd.pass.utils.Bytes.md5;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.hyd.pass.utils.FileUtils;
import com.hyd.pass.utils.IoStream;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class PasswordLib {

    private static final String ENC_TEST_STRING = "abcdefghijklmnopqrstuvwxyz";

    private Charset charset = StandardCharsets.UTF_8;

    private File saveFile;

    private String masterPasswordValidator;

    private Category rootCategory;

    @JSONField(serialize = false)
    private boolean changed;

    @JSONField(serialize = false)
    private String masterPassword;

    /**
     * 创建或打开密码库
     *
     * @param saveFile       文件路径
     * @param masterPassword 主密码
     * @param create         true 表示是创建新的密码库，false 表示是打开现有的密码库
     *
     * @throws PasswordLibException 如果创建或打开密码库失败
     */
    public PasswordLib(File saveFile, String masterPassword, boolean create) throws PasswordLibException {
        if (create) {
            this.saveFile = saveFile;
            this.rootCategory = new Category("我的密码库");
            this.masterPasswordValidator = generateValidator(masterPassword, rootCategory.getId());
            this.masterPassword = masterPassword;

        } else {

            String masterPasswordValidator;
            JSONObject jsonObject;
            try {
                String content = readContent(saveFile);
                jsonObject = JSON.parseObject(content);
                masterPasswordValidator = jsonObject.getString("masterPasswordValidator");
            } catch (IOException e) {
                throw new PasswordLibException("无法打开文件", e);
            }

            try {
                long rootId = jsonObject.getJSONObject("rootCategory").getLong("id");
                String userValue = generateValidator(masterPassword, rootId);
                if (!userValue.equals(masterPasswordValidator)) {
                    throw new PasswordLibException("密码不正确");
                }
            } catch (PasswordLibException e) {
                throw e;
            } catch (Exception e) {
                throw new PasswordLibException("密码不正确", e);
            }

            this.saveFile = saveFile;
            this.rootCategory = jsonObject.getObject("rootCategory", Category.class);
            this.masterPasswordValidator = masterPasswordValidator;
            this.masterPassword = masterPassword;

            try {
                if (this.rootCategory == null) {
                    this.rootCategory = new Category("我的密码库");
                } else {
                    this.rootCategory.iterateChildren(category -> {
                        category.readEntries(masterPassword);
                    });  // 解密内容
                }
            } catch (Exception e) {
                throw new PasswordLibException(e);
            }
        }
    }

    private String generateValidator(String masterPassword, long rootId) {
        // 转为 MD5 是为了防止同样的 masterPassword 产生外观相似的校验字符串
        return md5(encode128(ENC_TEST_STRING + rootId, masterPassword));
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public String filePath() {
        return this.saveFile.getAbsolutePath();
    }

    public Category getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(Category rootCategory) {
        this.rootCategory = rootCategory;
    }

    public String getMasterPasswordValidator() {
        return masterPasswordValidator;
    }

    public void setMasterPasswordValidator(String masterPasswordValidator) {
        this.masterPasswordValidator = masterPasswordValidator;
    }

    public void save() {

        // 保证校验字符串与最新的主密码一致
        masterPasswordValidator = generateValidator(this.masterPassword, rootCategory.getId());

        // 加密所有 entry
        rootCategory.iterateChildren(category -> {
            category.saveEntries(this.masterPassword);
        });

        Map<String, Object> data = new HashMap<>();
        data.put("masterPasswordValidator", masterPasswordValidator);
        data.put("rootCategory", rootCategory);

        try {
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(saveFile))) {
                saveContent(data, zos);
            }
            setChanged(false);
        } catch (IOException e) {
            throw new PasswordLibException(e);
        }
    }

    private void saveContent(Map<String, Object> data, ZipOutputStream zos) throws IOException {
        String content = JSON.toJSONString(data, JSONWriter.Feature.PrettyFormat);

        zos.putNextEntry(new ZipEntry("content.json"));
        zos.write(content.getBytes(charset));
        zos.closeEntry();
    }

    private String readContent(File saveFile) throws IOException {
        try {
            try (ZipFile f = new ZipFile(saveFile)) {
                ZipEntry entry = f.getEntry("content.json");
                if (entry != null) {
                    try (InputStream is = f.getInputStream(entry)) {
                        return IoStream.toString(is, charset);
                    }
                } else {
                    throw new IOException("文件内容不存在");
                }
            }
        } catch (ZipException e) {
            // 读取时兼容最初的纯文本格式，但保存时以 zip 格式保存
            return FileUtils.read(saveFile);
        }
    }

    public void deleteCategory(Category c) {
        boolean[] removed = {false};

        this.rootCategory.iterateChildren(category -> {
            if (category.getChildren().contains(c)) {
                category.getChildren().remove(c);
                removed[0] = true;
                return false;
            } else {
                return true;
            }
        });

        if (removed[0]) {
            setChanged(true);
        }
    }

    public boolean validatePassword(String password) {
        return Objects.equals(this.masterPassword, password);
    }
}
