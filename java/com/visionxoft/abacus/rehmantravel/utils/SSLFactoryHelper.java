package com.visionxoft.abacus.rehmantravel.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Helper class of SSL Factory related methods for Secure Communication over Web. (HTTPS)
 */
public class SSLFactoryHelper extends SSLSocketFactory {

    // Warning: Do not change instance names, else it will not work
    private SSLContext sslContext;
    private SSLSocketFactory delegate;

    public SSLFactoryHelper() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }};

            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            delegate = sslContext.getSocketFactory();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose)
            throws IOException {
        SSLSocket ss = (SSLSocket) delegate.createSocket(s, host, port, autoClose);
        ss.setEnabledProtocols(ss.getSupportedProtocols());
        ss.setEnabledCipherSuites(ss.getSupportedCipherSuites());
        return ss;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        SSLSocket ss = (SSLSocket) delegate.createSocket(host, port);
        ss.setEnabledProtocols(ss.getSupportedProtocols());
        ss.setEnabledCipherSuites(ss.getSupportedCipherSuites());
        return ss;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        SSLSocket ss = (SSLSocket) delegate.createSocket(host, port);
        ss.setEnabledProtocols(ss.getSupportedProtocols());
        ss.setEnabledCipherSuites(ss.getSupportedCipherSuites());
        return ss;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException {
        SSLSocket ss = (SSLSocket) delegate.createSocket(host, port, localHost, localPort);
        ss.setEnabledProtocols(ss.getSupportedProtocols());
        ss.setEnabledCipherSuites(ss.getSupportedCipherSuites());
        return ss;
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress,
                               int localPort) throws IOException {
        SSLSocket ss = (SSLSocket) delegate.createSocket(address, port, localAddress, localPort);
        ss.setEnabledProtocols(ss.getSupportedProtocols());
        ss.setEnabledCipherSuites(ss.getSupportedCipherSuites());
        return ss;
    }
}
