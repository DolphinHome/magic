package org.pengten.magic.log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ��־�࣬���ڼ�¼��־
 *
 * @author yangwenpeng
 * @version 2021��1��22��14:07:19
 */
public class Log {

    /**
     * ʵ��
     * Single case
     */
    private static Log instants = new Log();

    /**
     * д�뻺������С
     * Write buffer size
     */
    private static final int BUFFER_SIZE = 256 * 1024;

    /**
     * д�뻺����
     * Write buffer
     */
    private BufferedWriter writer;

    /**
     * ��־�ļ�·��
     * log file path
     */
    private String logfile;

    /**
     * ��ʼ����־���ܣ�ָ����־�ļ�����·��
     * Initialize the log function and specify the absolute path of the log file
     *
     * @param logfile
     */
    public static void init(String logfile) {
        if (logfile == null || logfile.isEmpty()) {
            return;
        }
        if (instants.writer != null && instants.logfile.equals(logfile)) {
            return;
        }
        instants.logfile = logfile;
        try {
            if (instants.writer != null) {
                instants.cleanup();
            }
            File file = new File(logfile);
            if (!file.exists() && !file.createNewFile()) {
                System.err.println("create log file in " + file.getPath() + " error, please check file system!");
                return;
            }
            if (file.isDirectory()) {
                System.err.println("create log file in " + file.getPath() + " error, it can`t be a directory!");
                return;
            }
            instants.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8), BUFFER_SIZE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the value of instants.
     *
     * @return the value of instants
     */
    public static Log getInstants() {
        return instants;
    }

    @Override
    protected void finalize() throws Throwable {
        this.cleanup();
    }

    /**
     * �ͷ���Դ
     * Release resources
     *
     * @throws IOException
     */
    public static void release() throws IOException {
        if (instants != null) {
            instants.cleanup();
        }
    }

    /**
     * �ͷ���Դ
     * Release resources
     *
     * @throws IOException
     */
    public void cleanup() throws IOException {
        if (this.writer != null) {
            this.writer.flush();
            this.writer.close();
            this.writer = null;
        }
    }

    /**
     * д��һ����־
     * Write one line of log
     *
     * @param line ��־��Ϣ�������� || ���� <br/> Log information will be separated by "||"
     */
    public void writeLine(String... line) {
        if (writer == null) {
            return;
        }
        try {
            writer.newLine();
            writer.write(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.write("\t");
            writer.write(String.join(" || ", line));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * д���쳣��Ϣ
     * Write exception information
     *
     * @param msg errormsg
     * @param e   throwable
     */
    public void writeError(String msg, Throwable e) {
        writeLine(msg, e.toString());
        for (Throwable throwable : e.getSuppressed()) {
            try {
                writer.newLine();
                writer.write("\t\t");
                writer.write(throwable.toString());
                writer.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace(System.err);
            }
        }
    }
}
