package com.uxiuer.api.sample;

import com.uxiuer.api.sample.common.domain.OpenAPIRequest;
import com.uxiuer.api.sample.common.exceptions.CipherException;
import com.uxiuer.api.sample.common.exceptions.RequestException;
import com.uxiuer.api.sample.common.utils.RSA;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author: imart·deng
 * @date: 2020/6/30 11:19
 */
public abstract class AbstractOpenApiSender {

    /**
     * 全局编码格式
     */
    public static final String ENCODE = "UTF-8";

    /**
     * 连接字符串
     */
    public static final String CONCAT_STR = "&";

    /**
     * 授权头
     */
    public static final String AUTHORIZATION = "Authorization";

    /**
     * 签名头
     */
    public static final String SIGNATURE_HEADER = "LLPAY-Signature";

    /**
     * 签名内容格式化
     */
    public static final String SIGN_FORMAT = "t=%s,v=%s";

    /**
     * okhttp 客户端
     */
    public static final OkHttpClient client = new OkHttpClient.Builder().writeTimeout(180, TimeUnit.SECONDS).build();

    /**
     * 默认文件名称
     */
    public static final String DEFAULT_FILE_ALIAS = "file";

    /**
     * 获取商户自己的RSA私钥，格式为PKCS8的
     *
     * @return
     */
    public abstract String getPrivateKey();

    /**
     * 获取XXX 提供的验签公钥
     *
     * @return
     */
    public abstract String getLLPPublicKey();

    /**
     * 获取认证token
     *
     * @return
     */
    public abstract String getToken();

    /**
     * 处理业务失败的请求
     *
     * @param responseBody 响应包体
     * @param request      请求信息
     */
    public void dealBadRequest(String responseBody, OpenAPIRequest request) {
        // do something...
    }

    /**
     * 处理资源不存在请求
     *
     * @param responseBody 响应包体
     * @param request      请求信息
     */
    public void dealNotFundRequest(String responseBody, OpenAPIRequest request) {
        // do something...
    }

    /**
     * 处理未授权请求
     *
     * @param responseBody 响应包体
     * @param request      请求信息
     */
    public void dealUnauthorized(String responseBody, OpenAPIRequest request) {
        // do something...
    }

    /**
     * 处理方法不支持
     *
     * @param responseBody 响应包体
     * @param request      请求信息
     */
    public void dealMethodNotAllowed(String responseBody, OpenAPIRequest request) {
        // do something...
    }

    /**
     * 处理异常请求，比如：找xx寻求帮助
     *
     * @param responseBody 响应包体
     * @param request      请求信息
     */
    public void dealInnerServerError(String responseBody, OpenAPIRequest request) {
        // do something...
    }

    /**
     * 发起OpenAPI请求
     *
     * @param request 请求内容
     * @return 可能的返回:
     * <ul>
     *     <li>实际响应包体</li>
     *     <li>null（IoException 或者验签不通过）</li>
     * </ul>
     * @throws UnsupportedEncodingException
     * @throws CipherException
     */
    public String call(OpenAPIRequest request) throws IOException, CipherException, RequestException {
        // 1.获取当前请求epoch
        String epoch = getEpoch();

        // 2.拼装签名格式内容：{method}&{URI}&{epoch}&{requestBody}[&{queryString}]
        StringBuilder sBuilder = new StringBuilder("");
        sBuilder
                .append(request.getMethod().name())
                .append(CONCAT_STR)
                .append(request.getUri())
                .append(CONCAT_STR)
                .append(epoch)
                .append(CONCAT_STR)
                .append(null == request.getRequestBody() ? "" : request.getRequestBody());
        if (isNotEmpty(request.getQueryString())) {
            // 存在queryString的情况
            sBuilder.append(CONCAT_STR)
                    .append(URLEncoder.encode(request.getQueryString(), ENCODE));
        }

        // 3.准备签名
        String sign = RSA.sign(RSA.Mode.SHA256withRSA, sBuilder.toString(), getPrivateKey());
        String signHeader = String.format(SIGN_FORMAT, epoch, sign);

        // 4.构建http请求实例
        Request.Builder builder = new Request.Builder()
                .url(request.getRequestUrl())
                // 确认请求方法
                .method(request.getMethod().name(), getRequestBody(request))
                // 添加身份认证头
                .addHeader(AUTHORIZATION, getToken())
                // 添加签名头
                .addHeader(SIGNATURE_HEADER, signHeader);

        // 5.处理请求
        try (Response response = client.newCall(builder.build()).execute()) {
            // 5.1得到响应码
            int responseCode = response.code();

            // 5.2 得到响应包体
            String responseBody = response.body().string();

            switch (responseCode) {
                case ResponseHttpCode.OK:
                    // 5.3 得到响应签名头，安全性考虑，建议您每次得到响应时验证一下响应头，响应头签名内容为 xx-Signature: t=epoch,v=RSA.sign(RSA.Mode.SHA256withRSA, epoch+"&"+responseBody, MERCHANT_PRIVATE_KEY)
                    if(verifyResponse(response, responseBody)) {
                        //正常业务逻辑
                        return responseBody;
                    } else {
                        // 验证不通过,do something
                        throw new RequestException("unsafe request.");
                    }
                case ResponseHttpCode.BAD_REQUEST:
                    // 业务逻辑失败,do something
                    dealBadRequest(responseBody, request);
                    break;
                case ResponseHttpCode.NOT_FOUND:
                    // 资源不存在,do something
                    dealNotFundRequest(responseBody, request);
                    break;
                case ResponseHttpCode.UNAUTHORIZED:
                    // 客户端未授权或验签失败,do something
                    dealUnauthorized(responseBody, request);
                    break;
                case ResponseHttpCode.METHOD_NOT_ALLOWED:
                    // 方法不支持,do something
                    dealMethodNotAllowed(responseBody, request);
                    break;
                case ResponseHttpCode.INTERNAL_SERVER_ERROR:
                    // 内部服务器异常，联系xx以获得支持
                    dealInnerServerError(responseBody, request);
                    break;
                default:
                    break;
            }
            return responseBody;
        } catch (IOException e) {
            // do something...
            throw e;
        }
    }

    @Nullable
    private RequestBody getRequestBody(OpenAPIRequest request) {
        return getRequestBody(request, null);
    }

    private RequestBody getRequestBody(OpenAPIRequest request, Consumer<MultipartBody.Builder> builderConsumer) {

        if (null != request.getFile()) {
            // 文件类型请求
            MultipartBody.Builder multipartRequestBuilder = new MultipartBody
                    .Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(DEFAULT_FILE_ALIAS, request.getFile().getName(), RequestBody.create(request.getFile(), request.getMediaType()));

            // 默认只有一个文件，bound只有一个，可以根据接口定义 addFormDataPart
            if (null != builderConsumer) {
                builderConsumer.accept(multipartRequestBuilder);
            }
            return multipartRequestBuilder.build();

        } else {
            // 普通类型请求
            return isEmpty(request.getRequestBody()) ? null : RequestBody.create(request.getRequestBody(), request.getMediaType());
        }

    }


    /**
     * 检验xx返回的真实性
     *
     * @param response
     * @param responseBody
     * @return
     */
    private boolean verifyResponse(Response response, String responseBody) {
        try {
            String llpSignHeader = response.header(SIGNATURE_HEADER).trim();
            String[] arr = llpSignHeader.split(",");
            String responseEpoch = arr[0].substring("t=".length());
            String responseSign = arr[1].substring("v=".length());
            return RSA.verify(RSA.Mode.SHA256withRSA, responseEpoch + "&" + responseBody, responseSign, getLLPPublicKey());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * xx可能返回的http状态码
     */
    public static class ResponseHttpCode {
        /**
         * 成功
         */
        public static final int OK = 200;

        /**
         * 业务失败
         */
        public static final int BAD_REQUEST = 400;

        /**
         * 客户端未授权或验签失败
         */
        public static final int UNAUTHORIZED = 401;

        /**
         * 资源不存在，在定义REST api时我们把所有的请求当作为资源
         * 例如：https://api.XXX.com/collections/accounts/account/{accountId}接口
         * 当accountId = accountA的时候正常返回200，accountId = accountB的时候可能返回404
         */
        public static final int NOT_FOUND = 404;

        /**
         * 不支持方法类型
         */
        public static final int METHOD_NOT_ALLOWED = 405;

        /**
         * 服务器内部异常
         */
        public static final int INTERNAL_SERVER_ERROR = 500;
    }

    protected boolean isNotEmpty(String requestBody) {
        return !isEmpty(requestBody);
    }

    protected boolean isEmpty(String requestBody) {
        return null == requestBody || "".equals(requestBody);
    }

    /**
     * 获取epoch时间，秒
     *
     * @return
     */
    protected String getEpoch() {
        return System.currentTimeMillis() / 1000 + "";
    }

}
