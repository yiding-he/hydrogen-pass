package com.hyd.pass.controllers;

import com.hyd.fx.dialog.AlertDialog;
import com.hyd.pass.Logger;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public abstract class BaseController {

    private static final Logger logger = Logger.getLogger(BaseController.class);

    protected interface Task {
        void run() throws Exception;
    }

    protected void runSafe(Task task) {
        try {
            task.run();
        } catch (Exception e) {
            logger.error("", e);
            AlertDialog.error("错误", e.toString());
        }
    }
}
