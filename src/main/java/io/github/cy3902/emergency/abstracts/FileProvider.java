package io.github.cy3902.emergency.abstracts;

import io.github.cy3902.emergency.Emergency;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public abstract class FileProvider {
    protected File file;
    protected YamlConfiguration yml;
    protected Emergency emergency;

    public FileProvider( String path, String internalPath, String fileName){
        this.emergency = Emergency.getInstance();
        this.file = new File(path,fileName);
        if (!this.file.exists()){
            emergency.saveResource(internalPath+fileName,false);
        }
        this.yml = YamlConfiguration.loadConfiguration(file);

        readDefault();
    }



    protected abstract void readDefault();

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

    //保存文件
//    public void saveYml(){
//        try {
//            this.yml.save(file);
//        } catch (IOException e) {
//            this.emergency.info("儲存文件 "+file.getName() +" 錯誤",Level.SEVERE);
//        }
//    }
}
