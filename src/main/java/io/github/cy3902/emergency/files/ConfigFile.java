package io.github.cy3902.emergency.files;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.FileProvider;
import io.github.cy3902.emergency.sql.MySQL;
import io.github.cy3902.emergency.sql.SQLite;

/**
 * 用於讀取和處理配置文件的類別。
 */
public class ConfigFile extends FileProvider {

    public enum DatabaseType {
        sqlite, mysql
    }

    /**
     * 初始化 ConfigFile 實例。
     *
     * @param path 配置文件的路徑
     * @param internalPath 內部路徑
     * @param fileName 配置文件名
     */
    public ConfigFile(String path, String internalPath, String fileName) {
        // 調用父類的構造方法初始化文件路徑
        super("./plugins/Emergency", "", "config.yml");
    }

    /**
     * 讀取默認配置，設置語言和數據庫類型。
     */
    @Override
    protected void readDefault() {
        // 讀取語言設置，默認為 "zh_TW"
        String lang = getValue(this.yml, "language", "zh_TW");
        String databaseTypeString = getValue(this.yml, "database.type", "sqlite");
        DatabaseType databaseType;

        // 設置語言類型
        try {
            Emergency.setLangType(Lang.LangType.valueOf(lang));
        } catch (IllegalArgumentException e) {
            // 如果語言無效，設置為默認語言 "zh_TW"
            Emergency.setLangType(Lang.LangType.zh_TW);
        }

        // 設置數據庫類型
        try {
            databaseType = DatabaseType.valueOf(databaseTypeString);
        } catch (IllegalArgumentException e) {
            // 如果數據庫類型無效，設置為默認值 sqlite
            databaseType = DatabaseType.sqlite;
        }

        // 根據數據庫類型設置對應的數據庫連接
        if (databaseType == DatabaseType.sqlite) {
            // 讀取 SQLite 配置參數
            String DATABASE_URL = getValue(this.yml, "file_path", "plugins/Emergency/SQL/emergency.db");
            Emergency.setSql(new SQLite(DATABASE_URL));
        } else if (databaseType == DatabaseType.mysql) {
            // 讀取 MySQL 配置參數
            String host = getValue(this.yml, "database.mysql.host", "");
            String dbName = getValue(this.yml, "database.mysql.database", "");
            int port = getValue(this.yml, "database.mysql.port", 0);
            String username = getValue(this.yml, "database.mysql.username", "");
            String password = getValue(this.yml, "database.mysql.password", "");

            // 創建 MySQL 實例並設置
            Emergency.setSql(new MySQL(host, port, dbName, username, password));
        }
    }
}
