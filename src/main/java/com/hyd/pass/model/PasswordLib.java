package com.hyd.pass.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyd.pass.App;
import com.hyd.pass.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.hyd.pass.utils.AESUtils.encode128;
import static com.hyd.pass.utils.Bytes.md5;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class PasswordLib {

    private static final String ENC_TEST_STRING = "abcdefghijklmnopqrstuvwxyz";

    private String charset = "UTF-8";

    private File saveFile;

    private String masterPasswordValidator;

    private Category rootCategory;

    private boolean changed;

    public PasswordLib(File saveFile, String masterPassword, boolean create) throws PasswordLibException {
        if (create) {
            this.saveFile = saveFile;
            this.rootCategory = new Category("我的密码库");
            this.masterPasswordValidator = generateValidator(masterPassword, rootCategory.getId());
            App.setMasterPassword(masterPassword);

        } else {

            String masterPasswordValidator;
            JSONObject jsonObject;
            try {
                String json = FileUtils.read(saveFile);
                jsonObject = JSON.parseObject(json);
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

            App.setMasterPassword(masterPassword);

            try {
                if (this.rootCategory == null) {
                    this.rootCategory = new Category("我的密码库");
                } else {
                    this.rootCategory.iterateChildren(Category::readEntries);  // 解密内容
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
        masterPasswordValidator = generateValidator(App.getMasterPassword(), rootCategory.getId());

        // 加密所有 entry
        rootCategory.iterateChildren(Category::saveEntries);

        Map<String, Object> data = new HashMap<>();
        data.put("masterPasswordValidator", masterPasswordValidator);
        data.put("rootCategory", rootCategory);

        try {
            FileUtils.write(this.saveFile, JSON.toJSONString(data, true), charset);
            setChanged(false);
        } catch (IOException e) {
            throw new PasswordLibException(e);
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


}
