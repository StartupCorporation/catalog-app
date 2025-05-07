package com.deye.web.util.error;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorCodeUtils {
    public static final Integer MINIO_UPLOAD_FILE_ERROR_CODE = 1;
    public static final Integer MINIO_DELETE_FILE_ERROR_CODE = 2;
    public static final Integer CATEGORY_NOT_FOUND_ERROR_CODE = 3;
    public static final Integer REQUEST_BODY_VALIDATION_ERROR_CODE = 4;
    public static final Integer DATABASE_ERROR_CODE = 5;
    public static final Integer EVENT_MESSAGE_NOT_PROCEED_ERROR_CODE = 6;
    public static final Integer PRODUCT_NOT_FOUND_ERROR_CODE = 7;
    public static final Integer JSON_PARSE_ERROR_CODE = 8;
    public static final Integer ATTRIBUTE_NOT_FOUND_ERROR_CODE = 9;
    public static final Integer COMMON_ERROR_CODE = 10;
    public static final Integer ATTRIBUTES_VALUES_ERROR_CODE = 11;
    public static final Integer ACTION_NOT_ALLOWED_ERROR_CODE = 12;
    public static final Integer CATEGORY_ATTRIBUTE_NOT_FOUND_ERROR_CODE = 13;
    public static final Integer JWT_TOKEN_ERROR_CODE = 14;
    public static final Integer MINIO_GET_LINK_ERROR_CODE = 15;
    public static final Integer GENERAL_ERROR_CODE = 16;
}
