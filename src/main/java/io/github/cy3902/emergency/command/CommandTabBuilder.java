package io.github.cy3902.emergency.command;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 用於構建指令自動完成選項的幫助類別。
 * 可以根據指令參數和先前參數的狀態來生成適當的自動完成選項。
 */
public class CommandTabBuilder {

    private final Set<TabList> list;

    /**
     * 初始化 CommandTabBuilder 實例，創建一個空的 TabList 集合。
     */
    public CommandTabBuilder() {
        list = new HashSet<>();
    }

    /**
     * 添加自動完成選項的配置到 Builder 中。
     *
     * @param returnList 自動完成選項列表
     * @param position 目前處於自動完成的參數位置
     * @param previousArg 先前參數的選項列表
     * @param previousPosition 先前參數的位置
     * @return 本身，以便鏈式調用
     */
    public CommandTabBuilder addTab(List<String> returnList, int position, List<String> previousArg, int previousPosition) {
        list.add(new TabList(returnList, position, previousArg, previousPosition));
        return this;
    }

    /**
     * 根據指令參數生成自動完成選項列表。
     *
     * @param args 指令參數
     * @return 自動完成選項列表
     */
    public List<String> build(String[] args) {
        List<String> returnList = new ArrayList<>();
        int length = args.length;
        if (length > 0) {
            for (TabList tabList : list) {
                if (tabList.getPosition() == length - 1) {
                    // 確保先前參數的位置小於當前參數長度
                    if (tabList.getPreviousPosition() >= length) {
                        continue;
                    }
                    // 確保先前參數不為 null
                    if (tabList.getPreviousArg() == null) {
                        continue;
                    }
                    String previousArg = args[tabList.getPreviousPosition()];

                    // 如果先前參數匹配，則返回對應的自動完成選項
                    if (tabList.getPreviousArg().contains(previousArg)) {
                        returnList = tabList.getReturnList();
                        break;
                    }
                }
            }
        }
        return returnList;
    }

    /**
     * 用於存儲自動完成選項的內部類別。
     */
    public class TabList {

        private final List<String> returnList;
        private final int position;
        private final List<String> previousArg;
        private final int previousPosition;

        /**
         * 初始化 TabList 實例。
         *
         * @param returnList 自動完成選項列表
         * @param position 目前處於自動完成的參數位置
         * @param previousArg 先前參數的選項列表
         * @param previousPosition 先前參數的位置
         */
        public TabList(List<String> returnList, int position, List<String> previousArg, int previousPosition) {
            this.returnList = returnList;
            this.position = position;
            this.previousArg = previousArg;
            this.previousPosition = previousPosition;
        }

        public List<String> getReturnList() {
            return returnList;
        }

        public int getPosition() {
            return position;
        }

        public List<String> getPreviousArg() {
            return previousArg;
        }

        public int getPreviousPosition() {
            return previousPosition;
        }
    }
}