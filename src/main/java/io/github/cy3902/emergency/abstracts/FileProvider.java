package io.github.cy3902.emergency.abstracts;

import io.github.cy3902.emergency.Emergency;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * 抽象類別，用於處理文件配置和 YAML 配置檔的讀取。
 * 提供了初始化、讀取預設值、取得配置值和處理 YAML 配置段的方法。
 */
public abstract class FileProvider {
    protected File file;
    protected YamlConfiguration yml;
    protected Emergency emergency;

    /**
     * 建立 FileProvider 物件並初始化文件和 YAML 配置。
     *
     * @param path 文件的目錄路徑
     * @param internalPath 內部資源的路徑
     * @param fileName 文件名稱
     */
    public FileProvider(String path, String internalPath, String fileName) {
        this.emergency = Emergency.getInstance();
        this.file = new File(path, fileName);
        if (!this.file.exists()) {
            emergency.saveResource(internalPath + fileName, false);
        }
        this.yml = YamlConfiguration.loadConfiguration(file);
        readDefault();
    }

    /**
     * 讀取 YAML 文件的預設值。
     * 具體實現由子類別定義。
     */
    protected abstract void readDefault();

    /**
     * 根據指定路徑從 YAML 配置中取得值。
     *
     * @param yml YAML 配置對象
     * @param path 配置路徑
     * @param defaultValue 預設值
     * @param <T> 預設值的類型
     * @return 取得的值或預設值
     */
    public <T> T getValue(YamlConfiguration yml, String path, T defaultValue) {
        Object value = yml.get(path);
        if (value != null) {
            try {
                return (T) value;
            } catch (ClassCastException ex) {
                if (defaultValue instanceof List && value instanceof String) {
                    return (T) Arrays.asList(((String) value).split(","));
                }
                this.emergency.info(Emergency.getLang().readYmlError + path, Level.SEVERE);
                return defaultValue;
            }
        }
        yml.set(path, defaultValue);
        return defaultValue;
    }

    /**
     * 根據指定路徑從 YAML 配置中取得配置段。
     *
     * @param path 配置段路徑
     * @return 配置段的 Optional 對象，若路徑對應的配置段不存在則為空
     */
    public Optional<ConfigurationSection> getSection(String path) {
        ConfigurationSection section = this.yml.getConfigurationSection(path);
        return section != null ? Optional.of(section) : Optional.empty();
    }
}
