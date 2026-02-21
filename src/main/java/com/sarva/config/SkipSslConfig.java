package com.sarva.config;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.X509Certificate;

@Configuration
public class SkipSslConfig {

    @Bean
    public RestClientCustomizer sslIgnoreCustomizer() {
        return (restClientBuilder) -> {
            restClientBuilder.requestFactory(new TrustAllClientHttpRequestFactory());
        };
    }

    static class TrustAllClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
        @Override
        protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
            if (connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) connection).setHostnameVerifier((hostname, session) -> true);
                ((HttpsURLConnection) connection).setSSLSocketFactory(trustAllSslSocketFactory());
            }
            super.prepareConnection(connection, httpMethod);
        }

        private SSLSocketFactory trustAllSslSocketFactory() {
            try {
                TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }

                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }
                };
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                return sc.getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
