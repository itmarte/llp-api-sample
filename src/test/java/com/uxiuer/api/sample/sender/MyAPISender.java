package com.uxiuer.api.sample.sender;

import com.uxiuer.api.sample.AbstractOpenApiSender;

/**
 * @Description:
 * @author: imart·deng
 * @date: 2020/8/21 15:48
 */
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
        return "Bearer <<access token>>";
//        return "Basic <<user token>>";
    }
}
