package com.hyd.pass.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyd.pass.utils.AESUtils;
import com.hyd.pass.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class PasswordLib {

    private static final String ENC_TEST_STRING = "abcdefghijklmnopqrstuvwxyz";

    private File saveFile;

    private String masterPasswordValidator;

    private String charset = "UTF-8";

    public PasswordLib(File saveFile, String masterPassword, boolean create) throws PasswordLibException {
        if (create) {
            this.saveFile = saveFile;
            this.masterPasswordValidator = AESUtils.encode128(ENC_TEST_STRING, masterPassword);

        } else {

            String v;
            try {
                String json = FileUtils.read(saveFile);
                JSONObject jsonObject = JSON.parseObject(json);
                v = jsonObject.getString("masterPasswordValidator");
            } catch (IOException e) {
                throw new PasswordLibException("无法打开文件", e);
            }

            try {
                if (!AESUtils.decode128(v, masterPassword).equals(ENC_TEST_STRING)) {
                    throw new PasswordLibException("密码不正确");
                }
            } catch (PasswordLibException e) {
                throw e;
            } catch (Exception e) {
                throw new PasswordLibException("密码不正确", e);
            }

            this.saveFile = saveFile;
            // 解开其他内容
        }
    }

    public String getMasterPasswordValidator() {
        return masterPasswordValidator;
    }

    public void setMasterPasswordValidator(String masterPasswordValidator) {
        this.masterPasswordValidator = masterPasswordValidator;
    }

    public void save() {
        Map<String, Object> data = new HashMap<>();
        data.put("masterPasswordValidator", masterPasswordValidator);

        try {
            FileUtils.write(this.saveFile, JSON.toJSONString(data), charset);
        } catch (IOException e) {
            throw new PasswordLibException(e);
        }
    }
}
