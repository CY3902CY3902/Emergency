package io.github.cy3902.emergency.abstracts;

import io.github.cy3902.emergency.Emergency;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public abstract class FileProviderList {
    protected List<File> fileList = new ArrayList<>();
    protected YamlConfiguration yml;
    protected Emergency emergency;

    public FileProviderList(String path) {
        this.emergency = Emergency.getInstance();
        Path dir = Paths.get(path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                File file = entry.toFile();
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    this.fileList.add(file);
                    this.yml = YamlConfiguration.loadConfiguration(file);
                    readDefault(this.yml);
                }
            }
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
        }
    }

    protected abstract void readDefault(YamlConfiguration yml);

    //取值
    public <T> T getValue(YamlConfiguration yml, String path, T defaultValue) {
        Object value = yml.get(path);
        if (value != null) {
            try {
                return (T) value;
            } catch (ClassCastException ex) {
                if (defaultValue instanceof List && value instanceof String) {
                    return (T) Arrays.asList(((String) value).split(","));
                }
                this.emergency.info(emergency.getLang().readYmlError + path, Level.SEVERE);
                return defaultValue;
            }
        }
        yml.set(path, defaultValue);
        return defaultValue;
    }

    // 使用 Optional 來處理可能的空值情況
    public Optional<ConfigurationSection> getSection(String path){
        ConfigurationSection section = this.yml.getConfigurationSection(path);
        return section !=null ? Optional.of(section) : Optional.empty();
    }


}
