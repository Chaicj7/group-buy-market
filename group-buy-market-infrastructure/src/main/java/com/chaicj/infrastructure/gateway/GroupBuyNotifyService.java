package com.chaicj.infrastructure.gateway;

import com.chaicj.types.enums.ResponseCode;
import com.chaicj.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class GroupBuyNotifyService {

    @Resource
    private OkHttpClient okHttpClient;

    public String groupBuyNotify(String notifyUrl, String notifyData) throws Exception {
        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, notifyData);
            Request request = new Request.Builder()
                    .url(notifyUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            // 调用接口
            Response response = okHttpClient.newCall(request).execute();
            // 返回结果
            return response.body().string();
        } catch (Exception e) {
            log.error("拼团回调 HTTP 接口服务异常 {}", notifyUrl, e);
            throw new AppException(ResponseCode.HTTP_EXCEPTION);
        }
    }
}
