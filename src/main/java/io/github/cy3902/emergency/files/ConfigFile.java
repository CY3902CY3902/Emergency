package io.github.cy3902.emergency.files;

import io.github.cy3902.emergency.abstracts.FileProvider;
import io.github.cy3902.emergency.sql.MySQL;
import io.github.cy3902.emergency.sql.SQLite;

public class ConfigFile extends FileProvider {
    public enum DatabaseType {sqlite,mysql};

    public ConfigFile(String path, String internalPath, String fileName) {
        super("./plugins/Emergency","", "config.yml");
    }



    @Override
    protected void readDefault() {
        String lang = getValue(this.yml, "language", "zh_TW");
        String databaseTypeString = getValue(this.yml, "database.type", "sqlite");
        DatabaseType databaseType;
        try{
            this.emergency.setLangType(Lang.LangType.valueOf(lang));
        }catch (IllegalArgumentException e){
            this.emergency.setLangType(Lang.LangType.zh_TW);
        }

        try{
            databaseType = DatabaseType.valueOf(databaseTypeString);
        }catch (IllegalArgumentException e){
            databaseType = DatabaseType.sqlite;
        }

        if (databaseType == DatabaseType.sqlite){
            String DATABASE_URL = getValue(this.yml, "file_path", "plugins/Emergency/SQL/emergency.db");
            emergency.setSql(new SQLite(DATABASE_URL));
        }

        if (databaseType == DatabaseType.mysql){
            String host = getValue(this.yml, "database.mysql.host", "");
            String dbName = getValue(this.yml, "database.mysql.database", "");
            int port = getValue(this.yml, "database.mysql.port", 0);
            String username = getValue(this.yml, "database.mysql.username", "");
            String password = getValue(this.yml, "database.mysql.password", "");

            emergency.setSql(new MySQL(host, port, dbName, username, password));

        }
    }



}
