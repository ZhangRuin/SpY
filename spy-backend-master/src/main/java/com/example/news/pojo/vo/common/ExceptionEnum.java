package com.example.news.pojo.vo.common;

public enum ExceptionEnum implements BaseErrorInfoInterface {
    // 数据操作错误定义
    SUCCESS("2000", "成功"),

    STAFF_PASSWORD_ERROR("5001", "管理员密码错误"),
    NO_STAFF_ONLINE("5002", "没有工作人员在线"),
    STAFF_POLL_ERROR("5003", "工作人员轮询错误"),
    LAW_QUESTION_RECORD_NOT_FOUND("5004", "法律问题记录不存在"),
    PARAM_ERROR("5005", "参数错误"),
    AI_ANSWER_ERROR("5006", "ai回答异常"),
    FiLE_ERROR("5007", "获取文件字节数据异常"),
    JSON_DATA_ERROR("5008", "获取json数据异常"),
    ;


    /**
     * 错误码
     */
    private final String resultCode;

    /**
     * 错误描述
     */
    private final String resultMsg;

    ExceptionEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
