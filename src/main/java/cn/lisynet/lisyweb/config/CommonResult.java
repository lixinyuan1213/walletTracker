package cn.lisynet.lisyweb.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lisy
 * @date 2023/8/20 10:45
 */
@Getter
@Setter
public class CommonResult<T> {
    private int code;
    private String msg;
    private T data;

    enum CodeNum{
        /**
         * 错误状态
         */
        ERROR_CODE(-1),
        /**
         * 成功状态
         */
        SUCCESS_CODE(0);

        private int code;

        CodeNum(int code){
            this.code = code;
        }
        public int getCode() {
            return code;
        }
    }


    public static <T> CommonResult<T> success(T data) {
        return success("success",data);
    }

    public static <T> CommonResult<T> success(String msg,T data) {
        CommonResult<T> rs = new CommonResult<>();
        rs.setCode(CodeNum.SUCCESS_CODE.getCode());
        rs.setMsg(msg);
        rs.setData(data);
        return rs;
    }

    public static CommonResult<String> error(CodeNum code,String msg) {
        CommonResult<String> rs = new CommonResult<>();
        rs.setCode(code.getCode());
        rs.setMsg(msg);
        rs.setData("");
        return rs;
    }

    public static CommonResult<String> error(String msg) {
        return error(CodeNum.ERROR_CODE,msg);
    }
}
