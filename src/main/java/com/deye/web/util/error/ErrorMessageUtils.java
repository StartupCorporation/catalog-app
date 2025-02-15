package com.deye.web.util.error;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessageUtils {
    public static final String MINIO_UPLOAD_FILE_ERROR_MESSAGE = "Error during upload file to MinIO";
    public static final String MINIO_DELETE_FILE_ERROR_MESSAGE = "Error during deleting file in MinIO";
    public static final String CATEGORY_NOT_FOUND_ERROR_MESSAGE = "Category not found";
    public static final String EVENT_MESSAGE_NOT_PROCEED_ERROR_MESSAGE = "Error during event message processing";
    public static final String PRODUCT_NOT_FOUND_ERROR_MESSAGE = "Product not found";
    public static final String JSON_PARSE_ERROR_MESSAGE = "JSON parse from/to db column error";
    public static final String ATTRIBUTE_NOT_FOUND_ERROR_MESSAGE = "Attribute not found";
}
