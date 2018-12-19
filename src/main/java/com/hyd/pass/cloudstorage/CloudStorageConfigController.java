package com.hyd.pass.cloudstorage;

import com.aliyun.oss.OSSClient;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.pass.conf.UserConfig;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CloudStorageConfigController {

    public TextField txtAliyunEndPoint;

    public TextField txtAliyunAccessKeyId;

    public PasswordField txtAliyunAccessKeySecret;

    public TextField txtAliyunBucket;

    public TextField txtAliyunFilePath;

    public Button btnOpenAliyunPasswordLib;

    public void saveAliyunConfig() {
        Map<String, String> aliyunConfig = new HashMap<>();
        aliyunConfig.put("cloudstorage.aliyun.end-point", txtAliyunEndPoint.getText());
        aliyunConfig.put("cloudstorage.aliyun.access-key-id", txtAliyunAccessKeyId.getText());
        aliyunConfig.put("cloudstorage.aliyun.access-key-secret", txtAliyunAccessKeySecret.getText());
        aliyunConfig.put("cloudstorage.aliyun.bucket", txtAliyunBucket.getText());
        aliyunConfig.put("cloudstorage.aliyun.file-path", txtAliyunFilePath.getText());
        UserConfig.setProperties(aliyunConfig);
    }

    private boolean validate(TextField textField, String name) {
        String value = textField.getText();

        if (StringUtils.isBlank(value)) {
            AlertDialog.error(name + " 不能为空");
            textField.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public void openAliyunFile() {

        String aliyunEndPoint = txtAliyunEndPoint.getText();
        String aliyunAccessKeyId = txtAliyunAccessKeyId.getText();
        String aliyunAccessKeySecret = txtAliyunAccessKeySecret.getText();
        final String aliyunBucket = txtAliyunBucket.getText();

        if (
                !validate(txtAliyunEndPoint, "EndPoint") ||
                        !validate(txtAliyunAccessKeyId, "AccessKeyId") ||
                        !validate(txtAliyunAccessKeySecret, "AccessKeySecret") ||
                        !validate(txtAliyunBucket, "Bucket")
        ) {
            return;
        }

        final OSSClient ossClient = new OSSClient(
                aliyunEndPoint,
                aliyunAccessKeyId,
                aliyunAccessKeySecret
        );

        btnOpenAliyunPasswordLib.setDisable(true);
        new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {

                    @Override
                    protected Void call() {
                        openAliyunPasswordLib(aliyunBucket, ossClient);
                        return null;
                    }
                };
            }
        }.start();
    }

    private void openAliyunPasswordLib(String aliyunBucket, OSSClient ossClient) {
        try {
            if (!ossClient.doesBucketExist(aliyunBucket)) {
                AlertDialog.error("Bucket 不存在");
                return;
            }

            if (!validate(txtAliyunFilePath, "文件路径")) {
                return;
            }

            String filePath = txtAliyunFilePath.getText();
            if (!ossClient.doesObjectExist(aliyunBucket, filePath)) {
                if (AlertDialog.confirmYesNo("文件不存在", "没有找到现有的密码库文件。要自动新建一个吗？")) {

                }
                return;
            }
        } catch (Exception e) {
            if (e.getMessage().contains("SocketException")) {
                AlertDialog.error("无法连接到服务器");
            } else {
                AlertDialog.error(e.getMessage());
            }
        } finally {
            btnOpenAliyunPasswordLib.setDisable(false);
        }
    }
}
