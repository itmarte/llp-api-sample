# llp-api-sample

API 示例代码,请先根据流程获***取到开发者信息之后***使用如下代码示例。

### 一、创建RSA公私钥对:

```shell
#creating rsa private key
openssl genrsa -out rsa_private_key.pem 2048
 
#convert private key into PKCS#8 format
openssl pkcs8 -topk8 -inform PEM -in rsa_private_key.pem -outform PEM -nocrypt -out private_pkcs8.pem
 
#get public key from private key (X509 format)
openssl rsa -in rsa_private_key.pem -pubout -out rsa_public_key.pem
```

### 二、引入代码依赖包：

```java
<!-- 如果公网找不到该包，可以直接打包源码install到自己私库 -->
		<dependency>
			<groupId>com.itmarte</groupId>
			<artifactId>llp-api-sample</artifactId>
			<version>${llp-api-sample.version}</version>
		</dependency>
```

### 三、构建自己的sender

```java
public class MyAPISender extends AbstractOpenApiSender {
    @Override
    public String getPrivateKey() {
        // merchant RSA public key
        /**
         * create the key by openssl
         * # first：creating rsa private key
         *  openssl genrsa -out rsa_private_key.pem 2048
         * #second：convert private key into PKCS#8 format
         *  openssl pkcs8 -topk8 -inform PEM -in rsa_private_key.pem -outform PEM -nocrypt -out private_pkcs8.pem
         * #third：get public key from private key (X509 format)
         *  openssl rsa -in rsa_private_key.pem -pubout -out rsa_public_key.pem
         */
        return "<<your RSA public key>>";
    }

    @Override
    public String getLLPPublicKey() {
        return "<<llp public key>>";
    }

    @Override
    public String getToken() {
        // 基于auth2.0 的第三方应用模式
        return "Bearer <<access token>>";
        // 合作商户直接对接模式
//        return "Basic <<user token>>";
    }
}
```

### 四、测试API资源

```java
public class SendTest {
    public static MyAPISender sender = new MyAPISender();

    public static void main(String[] args) throws CipherException, IOException, RequestException {
        // GET request
        // /common/v1/dictionaries/district/CHN
        OpenAPIRequest request = new OpenAPIRequest("https://api.xxx.com", "/common/v1/dictionaries/district/CHN", null, "", HttpMethod.GET);
         String responseBody = sender.call(request);
        System.out.println(String.format("请求[%s],返回结果[%s]", request.getRequestUrl(), responseBody));
}
```

![59799906899](/images/1597999068999.png)