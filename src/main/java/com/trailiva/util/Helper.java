package com.trailiva.util;

import com.trailiva.web.exceptions.BadRequestException;

public class Helper {
    public  static boolean isNullOrEmpty(String  value){
        return value == null || value.length() == 0 ;
    }

    public static void validatePageNumberAndSize(int page, int size) throws BadRequestException {
        if (page < 0){
            throw new BadRequestException("Page number cannot be less than zero.");
        }
        if (size > AppConstants.MAX_PAGE_SIZE){
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }
}
