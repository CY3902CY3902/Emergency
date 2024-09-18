package io.github.cy3902.emergency.abstracts;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractsSQL {
    protected Connection connection;

    /**
     * 建立與資料庫的連接。
     * 具體的連接邏輯由子類別實現。
     */
    public abstract void connect();

    /**
     * 創建資料表，如果資料表不存在。
     * 具體的表格創建邏輯由子類別實現。
     */
    public abstract void createTableIfNotExists();

    /**
     * 檢查資料庫連接是否有效。
     *
     * @return 如果連接有效且未關閉，則返回 true，否則返回 false
     */
    public boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * 關閉與資料庫的連接。
     * 會嘗試關閉連接並處理可能的 SQL 異常。
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清除指定的資料表中的所有資料。
     *
     * @param table 要清除的資料表名稱
     */
    public void clearTables(String table) {
    }
}
