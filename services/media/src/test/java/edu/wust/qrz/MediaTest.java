package edu.wust.qrz;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

@SpringBootTest
public class MediaTest {

    @Test
    public void FileChunkTest() throws FileNotFoundException {
        String filePath = "D:\\Bilibili\\Media\\testVideo.mp4";
        int chunkSize = 30 * 1024 * 1024;
        File file = new File(filePath);
        try (InputStream in = new FileInputStream(file)) {
            byte[] buffer = new byte[chunkSize];
            int len, part = 1;
            while ((len = in.read(buffer)) > 0) {
                File outFile = new File(file.getParent(), file.getName() + ".part" + part);
                try (OutputStream out = new FileOutputStream(outFile)) {
                    out.write(buffer, 0, len);
                }
                part++;
            }
            System.out.println("分块完成，共 " + part + " 块");
        } catch (IOException e) {
            System.out.println("文件分块失败: " + e.getMessage());
        }
    }
}