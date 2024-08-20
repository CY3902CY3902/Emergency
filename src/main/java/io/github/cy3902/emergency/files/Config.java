package io.github.cy3902.emergency.files;

import io.github.cy3902.emergency.abstracts.FileProvider;


import java.util.logging.Level;

public class Config extends FileProvider {

    public Config( String path, String internalPath, String fileName) {
        super("./plugins/Emergency","", "config.yml");
    }


    @Override
    protected void readDefault() {
        String lang = getValue(this.yml, "Language", "zh_TW");
        try{
            this.emergency.setLangType(Lang.LangType.valueOf(lang));
        }catch (IllegalArgumentException e){
            //this.emergency.info(emergency.getLang().readLangError, Level.SEVERE);
            this.emergency.setLangType(Lang.LangType.zh_TW);
        }

    }



}
