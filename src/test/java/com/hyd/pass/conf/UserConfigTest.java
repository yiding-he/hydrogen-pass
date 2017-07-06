package com.hyd.pass.conf;

/**
 * (description)
 * created at 2017/7/6
 *
 * @author yidin
 */
public class UserConfigTest {

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.home"));
        UserConfig.setString("name", "heyiding");
        System.out.println(UserConfig.getString("name", ""));
    }
}
