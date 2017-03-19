package com.huofu.RestaurantOS.utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class BaseRequest {
    /**
     * POST请求方法
     */
    public static final String HTTP_METHOD_POST = "POST";

    /**
     * Connection超时
     */
    private static final int SET_CONNECTION_TIMEOUT = 6000;
    /**
     * Socket超时
     */
    private static final int SET_SOCKET_TIMEOUT = 6000;

    /**
     * 网络类型
     */
    public static final int NETTYPE_NULL = 0; // 没有网络
    public static final int NETTYPE_WIFI = 1; // wifi连接
    public static final int NETTYPE_MOBILE = 2; // 手机网络
    public static final int NETTYPE_NET = 3; // net接入点
    public static final int NETTYPE_CMWAP = 4; // 移动代理
    public static final int NETTYPE_UNIWAP = 5; // 联通代理
    public static final int NETTYPE_CTWAP = 6; // 电信代理
    public static final int NETTYPE_3GWAP = 7; // 联通3G代理

    /** 代理的网关地址 */
//	protected final static String PROXY_CMWAP = "10.0.0.172";
//	protected final static String PROXY_UNIWAP = "10.0.0.172";
//	protected final static String PROXY_CTWAP = "10.0.0.200";

//	private Context mContext;

//	public BaseRequest(Context context) {
//		this.mContext = context;
//	}

    /**
     * 初始化HttpClient
     *
     * @return HttpClient
     */
    public HttpClient getHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);

            // Set the default socket timeout (SO_TIMEOUT) // in
            // milliseconds which is the timeout for waiting for data.
            HttpConnectionParams.setConnectionTimeout(params, SET_CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, SET_SOCKET_TIMEOUT);
            HttpClient client = new DefaultHttpClient(ccm, params);
            client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "5WEI_RESTAURANTOS_" + LocalDataDeal.readFromLocalVersionName(MainApplication.getContext()) + "_ANDROID");

//			int networkType = searchNetworkType(mContext);
//			String proxyAddress = "";
//			if (networkType == NETTYPE_CMWAP || networkType == NETTYPE_UNIWAP
//					|| networkType == NETTYPE_3GWAP) {
//				proxyAddress = PROXY_CMWAP;
//			} else if (networkType == NETTYPE_CTWAP) {
//				proxyAddress = PROXY_CTWAP;
//			}
//			if (!"".equals(proxyAddress)) {
//				HttpHost proxy = new HttpHost(proxyAddress, 80);
//				client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
//						proxy);
//			}
            return client;
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException {
            return sslContext.getSocketFactory().createSocket(socket, host,
                    port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    /**
     * 请求网络接口
     *
     * @param url    地址
     * @param method 请求方式
     * @param params 参数
     * @return String
     * @throws NetworkException
     */
    public String requestUrl(String url, String method,
                             List<NameValuePair> params) throws NetworkException {
        String result;
        try {
            HttpUriRequest request = null;
            if (method.equals("GET")) {
                url = url + "?" + encodeUrl(params);
                request = new HttpGet(url);
            } else if (method.equals("POST") || method.equals("PUT")) {
                HttpPost post = new HttpPost(url);
                HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                post.setEntity(entity);
                request = post;
            } else if (method.equals("DELETE")) {
                request = new HttpDelete(url);
            }
            setHeader(request);
            HttpResponse response = getHttpClient().execute(request);
            StatusLine status = response.getStatusLine();
            int statusCode = status.getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                NetworkException e = new NetworkException(statusCode);
                throw new NetworkException(statusCode);
            }
            // parse content stream from response
            result = read(response);
            return result;
        } catch (IOException e) {
            CommonUtils.LogWuwei("","url is "+url);

            throw new NetworkException(e);
        }
    }

    /**
     * 设置通用消息头
     *
     * @param request
     */
    public void setHeader(HttpUriRequest request) {
        // request.setHeader("Content-Type", "application/soap+xml");
        request.setHeader("Accept-Encoding", "gzip,deflate");
    }

    /**
     * 解析HttpResponse数据
     *
     * @param response
     * @return String
     * @throws NetworkException
     */
    private static String read(HttpResponse response) throws NetworkException {
        String result = "";
        HttpEntity entity = response.getEntity();
        InputStream inputStream;
        try {
            inputStream = entity.getContent();
            ByteArrayOutputStream content = new ByteArrayOutputStream();

            Header header = response.getFirstHeader("Content-Encoding");
            if (header != null
                    && header.getValue().toLowerCase().indexOf("gzip") > -1) {
                inputStream = new GZIPInputStream(inputStream);
            }

            // Read response into a buffered stream
            int readBytes = 0;
            byte[] sBuffer = new byte[512];
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
            // Return result from buffered stream
            result = new String(content.toByteArray());
            return result;
        } catch (IllegalStateException e) {
            throw new NetworkException(e);
        } catch (IOException e) {
            throw new NetworkException(e);
        } catch (OutOfMemoryError e) {
            throw new NetworkException(e);
        }
    }

    /**
     * 解析HttpResponse数据
     *
     * @param response
     * @return byte[]
     * @throws NetworkException
     */
    private static byte[] readData(HttpResponse response)
            throws NetworkException {
        HttpEntity entity = response.getEntity();
        InputStream inputStream;
        try {
            inputStream = entity.getContent();
            ByteArrayOutputStream content = new ByteArrayOutputStream();

            Header header = response.getFirstHeader("Content-Encoding");
            if (header != null
                    && header.getValue().toLowerCase().indexOf("gzip") > -1) {
                inputStream = new GZIPInputStream(inputStream);
            }

            // Read response into a buffered stream
            int readBytes = 0;
            byte[] sBuffer = new byte[512];
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
            // Return result from buffered stream
            return content.toByteArray();
        } catch (IllegalStateException e) {
            throw new NetworkException(e);
        } catch (IOException e) {
            throw new NetworkException(e);
        }
    }

    /**
     * 拼接Url参数
     *
     * @param params
     * @return String
     */
    public static String encodeUrl(List<NameValuePair> params) {
        if (params == null || params.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int loc = 0; loc < params.size(); loc++) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            NameValuePair nvp = params.get(loc);
            sb.append(URLEncoder.encode(nvp.getName()) + "="
                    + URLEncoder.encode(nvp.getValue()));
        }
        return sb.toString();
    }

    /**
     * 请求图片资源文件
     *
     * @param url
     * @return byte[]
     */
    public byte[] requestResource(String url) throws NetworkException {
        try {
            HttpUriRequest request = new HttpGet(url);
            setHeader(request);
            HttpResponse response = getHttpClient().execute(request);
            StatusLine status = response.getStatusLine();
            int statusCode = status.getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new NetworkException(statusCode);
            }
            // parse content stream from response
            return readData(response);
        } catch (IOException e) {
            throw new NetworkException(e);
        }
    }

    /**
     * 搜索可用的网络
     *
     * @param context
     * @return int
     */
    public int searchNetworkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active = manager.getActiveNetworkInfo();
        int networkType = NETTYPE_NULL;
        if (active != null) {
            int type = active.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                networkType = NETTYPE_WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                String extraInfo = active.getExtraInfo();
                if (extraInfo != null && !"".equals(extraInfo)) {
                    extraInfo = extraInfo.toLowerCase();
                    if (extraInfo.equals("cmwap")) {
                        networkType = NETTYPE_CMWAP;
                    } else if (extraInfo.equals("uniwap")) {
                        networkType = NETTYPE_UNIWAP;
                    } else if (extraInfo.equals("3gwap")) {
                        networkType = NETTYPE_3GWAP;
                    } else if (extraInfo.equals("ctwap")) {
                        networkType = NETTYPE_CTWAP;
                    } else {
                        networkType = NETTYPE_NET;
                    }
                }
            }
        }
        return networkType;
    }
}
