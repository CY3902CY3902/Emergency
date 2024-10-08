package io.github.cy3902.emergency.files;


import io.github.cy3902.emergency.abstracts.FileProvider;


import java.util.Arrays;
import java.util.List;

/**
 * 用於讀取和管理插件語言配置的類別。
 */
public class Lang extends FileProvider {

    /**
     * 語言類型的枚舉
     */
    public enum LangType {
        zh_TW, en_US
    }

    // 配置中的各種語言字符串
    public String plugin;
    public String pluginEnable;
    public String pluginDisable;
    public List<String> helpPlayer;
    public String unknownCommand;
    public String noPermission;
    public String reload;
    public String reloadError;
    public String conflictingPlaceholders;
    public String worldNotFoundMessage;
    public String emergencyNotFoundMessage;
    public String startCommandComplete;
    public String emergencyNotFoundGroup;
    public String duplicateGroupName;
    public String duplicateEmergencyMessage;
    public String readYmlError;
    public String readLangError;
    public String pauseCommandComplete;
    public String resumeCommandComplete;
    public String groupAlreadyPaused;
    public String groupAlreadyRunning;

    /**
     * 構造函數，用於初始化 Lang 對象。
     *
     * @param path 配置文件的路徑
     * @param internalPath 配置文件的內部路徑
     * @param fileName 配置文件的名稱
     */
    public Lang(String path, String internalPath, String fileName) {
        super(path, internalPath, fileName);
    }

    /**
     * 讀取並設置預設的語言配置。
     */
    @Override
    protected void readDefault() {
        // 預設的幫助玩家列表
        List<String> helpPlayer = Arrays.asList(
                "&f------ &b&lEmergency &f------",
                "&a指令: &e/emergency",
                "&a子命令:",
                "&8 - reload &7重新載入此插件的所有設定檔(會強制停止當前所有事件)",
                "&8 - start <事件群組> <事件名> <世界名> &7指定世界下執行指定緊急事件(會強制停止當前同事件群組與世界名的事件)",
                "&8 - pause <事件群組>  <世界名> &7暫停指定緊急事件群組在指定世界下運行",
                "&8 - resume <事件群組> <世界名> &7恢復指定緊急事件群組在指定世界下運行"
        );

        // 從配置文件中讀取並顯示語言字符串
        this.plugin = emergency.color(getValue(this.yml, "plugin", "&b&lEmergency &f"));
        this.pluginEnable = emergency.color(getValue(this.yml, "plugin_enable", "&b&l Emergency 插件已啟用"));
        this.pluginDisable = emergency.color(getValue(this.yml, "plugin_disable", "&b&l Emergency 插件已停用"));
        this.helpPlayer = emergency.color(getValue(this.yml, "help_player", helpPlayer));
        this.unknownCommand = emergency.color(getValue(this.yml, "unknown_command", "&c指令輸入錯誤，請使用/emergency查詢"));
        this.noPermission = emergency.color(getValue(this.yml, "no_permission", "&c你沒有權限使用此指定"));
        this.reloadError = emergency.color(getValue(this.yml, "reload_error", "&c載入此插件的所有設定檔時出現錯誤"));
        this.readYmlError = emergency.color(getValue(this.yml, "read_yml_error", "&cYML 文件配置錯誤! 檔案路徑: "));
        this.conflictingPlaceholders = emergency.color(getValue(this.yml, "conflicting_placeholders", "&c佔位符錯誤，(%online_player%、 %player in world% 不可在同一指令中出現) 指令: "));
        this.duplicateEmergencyMessage = emergency.color(getValue(this.yml, "duplicate_emergency_message", "&c緊急事件名稱出現重複，請修改緊急事件設定檔"));
        this.worldNotFoundMessage = emergency.color(getValue(this.yml, "world_not_found_message", "&c指定世界不存在"));
        this.emergencyNotFoundMessage = emergency.color(getValue(this.yml, "emergency_not_found_message", "&c指定事件不存在"));
        this.emergencyNotFoundGroup = emergency.color(getValue(this.yml, "emergency_not_found_group", "&c指定緊急事件群組不存在"));
        this.duplicateGroupName = emergency.color(getValue(this.yml, "duplicate_group_name", "&c緊急事件名稱重複，重複值:"));
        this.readLangError = emergency.color(getValue(this.yml, "read_lang_error", "&c文件配置錯誤，默認繁體中文"));

        this.reload = emergency.color(getValue(this.yml, "reload", "&f重新載入此插件的所有設定檔"));
        this.startCommandComplete = emergency.color(getValue(this.yml, "start_command_complete", "&f指定緊急事件已觸發"));
        this.pauseCommandComplete = emergency.color(getValue(this.yml, "pause_command_complete", "&f指定緊急事件已停止"));
        this.resumeCommandComplete = emergency.color(getValue(this.yml, "resume_command_complete", "&f指定緊急事件已恢復"));
        this.groupAlreadyPaused = emergency.color(getValue(this.yml, "group_already_paused", "&c指定緊急事件群組已在該世界停止倒數"));
        this.groupAlreadyRunning = emergency.color(getValue(this.yml, "group_already_running", "&c指定緊急事件群組已在該世界運行"));
    }
}
