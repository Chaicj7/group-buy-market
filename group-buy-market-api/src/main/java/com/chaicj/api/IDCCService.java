package com.chaicj.api;

import com.chaicj.api.response.Response;

public interface IDCCService {

    Response<String> updateConfig(String key, String value);
}
