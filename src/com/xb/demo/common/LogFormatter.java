package com.xb.demo.common;

import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class LogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        Date date = new Date();
        String sDate = date.toString();
        return "[" + sDate + "]" + "[" + record.getLevel() + "]"
                + record.getClass() + record.getMessage() + "\n";
    }

    public static void setLog(Logger log) throws IOException {
        log.setLevel(Level.ALL);

        FileHandler fileHandler = new FileHandler(log.getName() + ".log");
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new LogFormatter());
        log.addHandler(fileHandler);
    }

    public static Logger getLog(Class<?> clazz){
        Logger log = Logger.getLogger(clazz.getName());
        try {
            setLog(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return log;
    }

}