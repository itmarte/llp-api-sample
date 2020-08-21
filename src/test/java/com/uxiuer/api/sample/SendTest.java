package com.uxiuer.api.sample;

import com.uxiuer.api.sample.common.domain.OpenAPIRequest;
import com.uxiuer.api.sample.common.enums.HttpMethod;
import com.uxiuer.api.sample.common.exceptions.CipherException;
import com.uxiuer.api.sample.common.exceptions.RequestException;
import com.uxiuer.api.sample.sender.MyAPISender;

import java.io.IOException;

/**
 * @Description:
 * @author: imart·deng
 * @date: 2020/8/21 15:51
 */
public class SendTest {
    public static MyAPISender sender = new MyAPISender();

    public static void main(String[] args) throws CipherException, IOException, RequestException {
        // GET request
        // /common/v1/dictionaries/district/CHN
        OpenAPIRequest request = new OpenAPIRequest("https://api.xxx.com", "/common/v1/dictionaries/district/CHN", null, "", HttpMethod.GET);
         String responseBody = sender.call(request);
        System.out.println(String.format("请求[%s],返回结果[%s]", request.getRequestUrl(), responseBody));

        /**
         * 请求[https://api.xxx.com/common/v1/dictionaries/district/CHN],
         * 返回结果[{"items":[{"code":"110000","name":"北京"},{"code":"120000","name":"天津"},{"code":"130000","name":"河北省"},{"code":"140000","name":"山西省"},{"code":"150000","name":"内蒙古自治区"},{"code":"210000","name":"辽宁省"},{"code":"220000","name":"吉林省"},{"code":"230000","name":"黑龙江省"},{"code":"310000","name":"上海"},{"code":"320000","name":"江苏省"},{"code":"330000","name":"浙江省"},{"code":"340000","name":"安徽省"},{"code":"350000","name":"福建省"},{"code":"360000","name":"江西省"},{"code":"370000","name":"山东省"},{"code":"410000","name":"河南省"},{"code":"420000","name":"湖北省"},{"code":"430000","name":"湖南省"},{"code":"440000","name":"广东省"},{"code":"450000","name":"广西壮族自治区"},{"code":"460000","name":"海南省"},{"code":"500000","name":"重庆"},{"code":"510000","name":"四川省"},{"code":"520000","name":"贵州省"},{"code":"530000","name":"云南省"},{"code":"540000","name":"西藏自治区"},{"code":"610000","name":"陕西省"},{"code":"620000","name":"甘肃省"},{"code":"630000","name":"青海省"},{"code":"640000","name":"宁夏回族自治区"},{"code":"650000","name":"新疆维吾尔自治区"},{"code":"710000","name":"台湾省"},{"code":"810000","name":"香港特别行政区"},{"code":"820000","name":"澳门特别行政区"}]}]
         */
    }
}
