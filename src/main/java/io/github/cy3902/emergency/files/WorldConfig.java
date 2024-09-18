package io.github.cy3902.emergency.files;


import io.github.cy3902.emergency.abstracts.FileProviderList;
import io.github.cy3902.emergency.world.DayWorld;
import io.github.cy3902.emergency.world.TimeWorld;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 負責讀取和處理世界配置的類別。
 */
public class WorldConfig extends FileProviderList {

        /**
         * 構造函數，用於初始化 WorldConfig 對象。
         *
         * @param path 配置文件的路徑
         */
        public WorldConfig(String path) {
                super(path);
        }

        /**
         * 讀取預設的配置值並初始化世界對象。
         *
         * @param yml YAML 配置對象
         */
        @Override
        protected void readDefault(YamlConfiguration yml) {
                Set<String> keys = this.yml.getKeys(false); // 獲取根鍵集合
                for (String key : keys) {
                        this.readWorld(key); // 讀取每個世界的配置
                }
        }

        /**
         * 讀取指定名稱的世界配置並創建相應的世界對象。
         *
         * @param name 世界的名稱
         */
        protected void readWorld(String name) {
                // 從配置中獲取 "day_group" 和 "time_group" 的值
                List<String> dayGroup = getValue(this.yml, name + ".day_group", Arrays.asList("")); // 日間組
                List<String> timeGroup = getValue(this.yml, name + ".time_group", Arrays.asList("")); // 時間組

                // 創建 DayWorld 和 TimeWorld 對象
                DayWorld dayWorld = new DayWorld(name, dayGroup);
                TimeWorld timeWorld = new TimeWorld(name, timeGroup);

                // 註冊這些世界對象
                emergency.registerWorld(dayWorld);
                emergency.registerWorld(timeWorld);
        }
}