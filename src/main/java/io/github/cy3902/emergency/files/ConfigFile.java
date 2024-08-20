package io.github.cy3902.emergency.files;

import io.github.cy3902.emergency.abstracts.FileProvider;

public class ConfigFile extends FileProvider {

    public ConfigFile(String path, String internalPath, String fileName) {
        super("./plugins/Emergency","", "config.yml");
    }



    @Override
    protected void readDefault() {
        String lang = getValue(this.yml, "language", "zh_TW");
        try{
            this.emergency.setLangType(Lang.LangType.valueOf(lang));
        }catch (IllegalArgumentException e){
            //this.emergency.info(emergency.getLang().readLangError, Level.SEVERE);
            this.emergency.setLangType(Lang.LangType.zh_TW);
        }

    }



}
