package cn.lisynet.lisyweb.handlers;

import cn.lisynet.lisyweb.config.CommonResult;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 返回json的处理逻辑示例
 *
 * 参数相关
 *
 * String name = ctx.queryParam("name");
 * String username = ctx.formParam("username");
 * String userId = ctx.pathParam("userId");
 * User user = ctx.bodyAsClass(User.class);
 * String auth = ctx.header("Authorization");
 * UploadedFile file = ctx.uploadedFile("file");
 *
 *
 * @author lisy
 * @date 2023/8/20 10:40
 */
public class JsonHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        Map<String, String> data = new HashMap<>(2);
        data.put("show", "1");
        data.put("page", "2");
        CommonResult<Map<String, String>> success = CommonResult.success(data);
        ctx.json(success);
    }
}
