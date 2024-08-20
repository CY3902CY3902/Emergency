package io.github.cy3902.emergency.files;


import io.github.cy3902.emergency.abstracts.FileProviderList;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class WorldConfig extends FileProviderList {
        public WorldConfig( String path) {
                super(path);
        }

        @Override
        protected void readDefault(YamlConfiguration yml) {
                Set<String> keys = this.yml.getKeys(false);
                for (String key : keys) {
                        this.readWorld(key);
                }
        }
        protected void readWorld(String name) {
                List<String> dayGroup = getValue(this.yml, name+".dayGroup", Arrays.asList(""));
                List<String> timeGroup = getValue(this.yml, name+".timeGroup", Arrays.asList(""));
                emergency.registerWorld(name,dayGroup,timeGroup);
        }


}
