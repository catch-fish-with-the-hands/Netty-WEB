package com.hgy.filecopy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * copy file with NIO
 */
public class NIOFileCopy {

    public static void copyFile(String srcFile, String destFile) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(srcFile);
        FileChannel srcChannel = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream(destFile);
        FileChannel destChannel = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        while (true) {
            byteBuffer.clear();
            int read = srcChannel.read(byteBuffer);
            if (read == -1) break;
            byteBuffer.flip();
            destChannel.write(byteBuffer);
        }
    }

    public static void main(String[] args) throws Exception {
        copyFile("E:\\workSpace\\czProject\\csdn.zip",
                "E:\\workSpace\\czProject\\csdn-1.zip");
    }
}
