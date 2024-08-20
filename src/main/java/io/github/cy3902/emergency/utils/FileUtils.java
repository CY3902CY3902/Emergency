package io.github.cy3902.emergency.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtils {

    public static void copyResourceFolder(JavaPlugin plugin, String resourcePath, File destination) throws IOException {
        if (!destination.exists()) {
            destination.mkdirs();
        }

        String jarPath = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        jarPath = java.net.URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                // 检查资源路径是否匹配
                if (entryName.startsWith(resourcePath) && !entry.isDirectory()) {
                    File destFile = new File(destination, entryName.substring(resourcePath.length()));
                    // 创建目标文件的父目录
                    destFile.getParentFile().mkdirs();
                    // 复制文件
                    try (InputStream in = jar.getInputStream(entry);
                         FileOutputStream out = new FileOutputStream(destFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = in.read(buffer)) > 0) {
                            out.write(buffer, 0, length);
                        }
                    }
                }
            }
        }
    }

    public static void copyResourceFile(JavaPlugin plugin, String resourcePath, File destination) throws IOException {
        try (InputStream resourceStream = plugin.getResource(resourcePath)) {
            if (resourceStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            try (FileOutputStream outStream = new FileOutputStream(destination)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = resourceStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, length);
                }
            }
        }
    }
}