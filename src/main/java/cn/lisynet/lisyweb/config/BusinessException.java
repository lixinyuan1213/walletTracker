package cn.lisynet.lisyweb.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lisy
 * @date 2023/8/20 11:02
 */
@Getter
@Setter
public class BusinessException extends RuntimeException{
    private int code;

    private String errMsg;

    public BusinessException(CommonResult.CodeNum code,String msg){
        this.code = code.getCode();
        this.errMsg = msg;
    }

    public static BusinessException error(CommonResult.CodeNum code,String msg){
        return new BusinessException(code, msg);
    }

    public static BusinessException error(String msg){
        return error(CommonResult.CodeNum.ERROR_CODE,msg);
    }
}
