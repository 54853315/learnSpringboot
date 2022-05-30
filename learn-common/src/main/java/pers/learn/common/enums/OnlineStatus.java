package pers.learn.common.enums;

/**
 * 用户会话在线状态
 */
public enum OnlineStatus {
    online("在线"), offline("离线");
    private final String info;

    private OnlineStatus(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}