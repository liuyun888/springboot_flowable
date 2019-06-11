package com.boot.flowable.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: ResponseUtils
 * Description:
 *
 */
public class ResponseUtils extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public ResponseUtils() {
        put("code", 0);
        put("msg", "success");
    }

    public static ResponseUtils error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static ResponseUtils error(String msg) {
        return error(500, msg);
    }

    public static ResponseUtils error(int code, String msg) {
        ResponseUtils responseUtils = new ResponseUtils();
        responseUtils.put("code", code);
        responseUtils.put("msg", msg);
        return responseUtils;
    }

    public static ResponseUtils error(String code, String msg) {
        ResponseUtils responseUtils = new ResponseUtils();
        responseUtils.put("code", code);
        responseUtils.put("msg", msg);
        return responseUtils;
    }

    public static ResponseUtils ok(String msg) {
        ResponseUtils responseUtils = new ResponseUtils();
        responseUtils.put("msg", msg);
        return responseUtils;
    }

    public static ResponseUtils ok(Map<String, Object> map) {
        ResponseUtils responseUtils = new ResponseUtils();
        responseUtils.putAll(map);
        return responseUtils;
    }

    public static ResponseUtils ok() {
        return new ResponseUtils();
    }

    @Override
    public ResponseUtils put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
