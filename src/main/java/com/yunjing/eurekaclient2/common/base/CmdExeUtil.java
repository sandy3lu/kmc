package com.yunjing.eurekaclient2.common.base;

import java.io.IOException;

/**
 * @ClassName CmdExeUtil
 * @Description 指令执行工具类
 * @Author scyking
 * @Date 2019/1/11 10:59
 * @Version 1.0
 */
public class CmdExeUtil {

    /**
     * 判断系统是否是windows系统
     *
     * @return
     */
    private static boolean isWindowsSystem() {
        String systemName = System.getProperty("os.name");
        return systemName.toLowerCase().contains("windows");
    }

    /**
     * 执行指令（目前仅区别windows系统与linux系统）
     *
     * @param cmd
     */
    public static void exeCmd(String cmd) throws IOException, InterruptedException {
        if (isWindowsSystem()) {
            winCmd(cmd);
        } else {
            linuxCmd(cmd);
        }
    }

    /**
     * windows下执行指令（异步）
     */
    public static void winCmd(String cmd) throws IOException, InterruptedException {
        String[] myCmd = {"cmd", "/C", cmd};
        Process proc = Runtime.getRuntime().exec(myCmd);
        proc.waitFor();
    }

    /**
     * linux下执行指令（异步）
     */
    public static void linuxCmd(String cmd) throws IOException, InterruptedException {
        String[] myCmd = {"/bin/sh", "-c", cmd};
        Process proc = Runtime.getRuntime().exec(myCmd);
        proc.waitFor();
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        winCmd("mysqldump -u root -proot sm_sign dict_constant > D:/mysql.sql");
    }
}

