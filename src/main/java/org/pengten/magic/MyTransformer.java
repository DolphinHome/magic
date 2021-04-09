package org.pengten.magic;

import org.apache.commons.io.IOUtils;
import org.pengten.magic.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.security.ProtectionDomain;

/**
 * ����ʱת���࣬��Ҫ�����滻����ֽ��롣
 * Load time conversion class, mainly used to replace the bytecode of the class.
 * @author yangwenpeng
 * @version 2021��1��22��14:01:53
 */
public class MyTransformer implements ClassFileTransformer {

    /**
     * �����滻�ֽ���� class �ļ���ַ��֧�� http �ͱ��ؾ���·��
     * Class file address used to replace bytecode, supporting HTTP and local absolute path
     */
    private String url;

    /**
     * ��Ҫ�滻��Ŀ�� class ȫ�޶������� java.lang.String
     * The fully qualified name of the target class to be replaced, such as java.lang.String
     */
    private String targetClass;

    public MyTransformer(String[] args) {
        targetClass = args[0];
        url = args[1];
        Log.init(args[2]);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!className.equals(targetClass.replace(".", "/"))) {
            return null;
        }
        byte[] bytes = null;
        FileInputStream fileInputStream = null;
        try {
            if (url.startsWith("http")) {
                bytes = IOUtils.toByteArray(new URL(url));
            } else {
                fileInputStream = new FileInputStream(new File(url));
                bytes = IOUtils.toByteArray(fileInputStream);
            }
        } catch (IOException e) {
            Log.getInstants().writeError("transform error", e);
            throw new IllegalClassFormatException("transform error");
        } finally {
            try {
                IOUtils.close(fileInputStream);
            } catch (IOException e) {
                Log.getInstants().writeError("filestream close error", e);
            }
        }
        return bytes;
    }
}
