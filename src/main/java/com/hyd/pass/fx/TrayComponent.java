package com.hyd.pass.fx;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.app.AppThread;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.pass.App;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author yiding.he
 */
public class TrayComponent {

    private SystemTray tray;

    private TrayIcon trayIcon;

    private MenuItem showOrHideMenu;

    public void init() {
        if (SystemTray.isSupported()) {
            try {
                startSystemTray();
            } catch (Exception e) {
                AlertDialog.error("无法显示托盘图标。不影响其余功能使用。", e);
            }
        }
    }

    public void updateMenu() {

    }

    private void startSystemTray() throws AWTException {
        tray = SystemTray.getSystemTray();
        BufferedImage trayIconImage = SwingFXUtils.fromFXImage(Icons.Logo16.getImage(), null);
        PopupMenu popupMenu = new PopupMenu();

        trayIcon = new TrayIcon(trayIconImage, App.APP_NAME, popupMenu);
        trayIcon.addActionListener(e -> {
            togglePrimaryStage();
        });

        tray.add(trayIcon);

        showOrHideMenu = new MenuItem("关闭窗口");
        popupMenu.add(showOrHideMenu);
    }

    private void togglePrimaryStage() {
        Stage stage = AppPrimaryStage.getPrimaryStage();
        if (stage == null) {
            return;
        }

        if (stage.isShowing()) {
            AppThread.runUIThread(stage::hide);
        } else {
            AppThread.runUIThread(() -> {
                stage.show();
                stage.setIconified(false);
            });
        }
    }

    public void close() {
        this.tray.remove(trayIcon);
    }
}
