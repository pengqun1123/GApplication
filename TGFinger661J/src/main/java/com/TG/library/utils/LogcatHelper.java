package com.TG.library.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * log日志统计保存
 *
 * @author xiaoming
 */

public class LogcatHelper {

    private static LogcatHelper INSTANCE = new LogcatHelper();
    private static String PATH_LOGCAT = "";
    private LogDumper mLogDumper = null;
    private int mPId;

    /**
     * 初始化目录
     */
    public LogcatHelper init(String filePath) {
        PATH_LOGCAT = filePath;
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
        return INSTANCE;
    }

    public static LogcatHelper getInstance() {
        return INSTANCE;
    }

    private LogcatHelper() {
        mPId = android.os.Process.myPid();
    }

    /**
     * 开始记录日志信息
     */
    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
        mLogDumper.start();
    }

    /**
     * 停止记录日志信息
     */
    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid, String dir) {
            mPID = pid;
            try {
                File fileDir = new File(dir);
                if (fileDir.exists()) {
                    File[] files = fileDir.listFiles();//这里为空
//                    if (files != null) {
                        for (File file : files) {
                            String fileName = file.getName();
                            Matcher matcher = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}").matcher(fileName);
                            if (matcher.find()) {
                                long msDate = DateFormat.getInstance().getMSDate("yyyy-MM-dd", matcher.group());
                                if (System.currentTimeMillis() - msDate > 1000 * 60 * 60 * 24 * 7) {
                                    file.delete();
                                }
                            }
                        }
                    }
//                }
                File file = new File(dir, "log_" + DateFormat.getInstance().getStrDate(System.currentTimeMillis(), "yyyy-MM-dd HH_mm_ss") + ".txt");
                out = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s 
             *
             * 显示当前mPID程序的 E和W等级的日志. 
             *
             * */
            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息  
            // cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";  //打印e、w级

        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((DateFormat.getInstance().getDateEN() + "  " + line + "\n").getBytes());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }

            }

        }

    }

}   
