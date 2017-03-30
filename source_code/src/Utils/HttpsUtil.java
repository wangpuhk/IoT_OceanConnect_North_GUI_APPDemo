/*
 * Copyright Notice:
 *      Copyright  1998-2008, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

@SuppressWarnings("deprecation")
public class HttpsUtil extends DefaultHttpClient 
{
    public final static String HTTPGET = "GET";

    public final static String HTTPPUT = "PUT";

    public final static String HTTPPOST = "POST";

    public final static String HTTPDELETE = "DELETE";

    public final static String HTTPACCEPT = "Accept";

    public final static String CONTENT_LENGTH = "Content-Length";

    public final static String CHARSET_UTF8 = "UTF-8";

	// 缂侊拷鏉炴壆浼愬ù鐙呯悼閻栵拷 閻犲洣妞掗崝鐔烘崉椤栨氨绐為柕鍡曟祰閻﹀绋婇敃锟介惁鎴︽煢閵夘煈鍤為柡宥堫潐瀹撲胶锟藉湱鍋ゅ顖炲箚閸涱厼鏋岄柡鍥ㄥ瀹曪拷
	public static String SELFCERTPATH = "cert//outgoing.CertwithKey.pkcs12";

	public static String SELFCERTPWD = "IoM@1234";

	public static String TRUSTCAPATH = "cert//ca.jks";

	// 閺夆晜鐟╅崳鐑芥儍閸曨偆妲曢柣顔绘缁楀寮伴悿瀛夐悹鍥︽閸旂喖鎯冮崟顐ゆ闁活喕绶ょ槐婵嬫嚀鐏炵偓笑jks閻犲洣妞掗崝鐔哥閹惧磭姘ㄩ柣銊ュ閻︽垿鎯嶉敓锟� 闁挎稑婀慉閻犲洣妞掗崝鐔煎嫉椤掑啴鐓╁☉鎾崇Т鐎垫﹢宕ラ銈庢綄闂佸鍎荤槐婵嬪炊閻樻剚鍔冨☉鏃傚枑閻ュ懘寮垫径濠勬闁活喕绶ょ槐锟�
	public static String TRUSTCAPWD = "Huawei@123";
	
	private static HttpClient httpClient;

	/**
	 * 闁告瑥鑻幃婊呮媼閵堝牏妲堥柛锔惧劋濞咃拷 Two-Way Authentication 
	 * 闁告瑥鑻幃婊呮媼閵堝牏妲堥柛锔惧劋濞呮瑦绋夌�ｅ墎绀夐悗骞垮灪閸╂稓绮╅鐐翠粯閻熸洩鎷� 
	 * 1闁靛棔绀侀閬嶅礂閵夈劌娈扮�规瓕绮鹃惁澶嬬▕閿旇偐绀夐柟缁樺姃缁剁敻鎳涢鍕畳閻犲洣妞掗崝鐔哥瑹濞戞瑦绠涢柛鏃撶磿椤忣剟寮介敓鐘靛矗
	 * 2闁靛棔绀侀閬嶅礂閵夛附绠涢柛鏂猴拷铏彜CA閻犲洣妞掗崝鐔兼晬鐏炵厧鈻忛柣鈧妽濠�鍥礉閿涘嫷浼侰A閻犲洣妞掗崝鐔煎冀閿熺姷宕ｉ柡鍫濈Т婵喓绮╅姘岛闂侇偂娴囩换鍐级閵壯勭暠閻犲洣妞掗崝锟� 
	 * 3闁靛棔娴囬鏇犵磾椤旇崵鐟濋柡宥忕節閻涙瑩宕洪悢閿嬪�� 闁挎稑鐗撳顏堝疮閸℃瑦鏆忛柣婊庡灠椤ｃ劍绋夌�ｅ墎绀夋繛灞惧笚濠�浣规媴鐠恒劍鏆忛柛鈺冨枎閹洜鎷嬮崸妤侊紪闁挎冻鎷�
	 * */
	public void initSSLConfigForTwoWay() throws Exception {
		// 1闁靛棔绀侀閬嶅礂閵夈劌娈扮�规瓕绮鹃惁澶嬬▕閿燂拷
		KeyStore selfCert = KeyStore.getInstance("pkcs12");
		try {
			selfCert.load(new FileInputStream(SELFCERTPATH), SELFCERTPWD.toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("sunx509");
		kmf.init(selfCert, SELFCERTPWD.toCharArray());

		// 2闁靛棔绀侀閬嶅礂閵夛附绠涢柛鏂猴拷铏彜CA閻犲洣妞掗崝锟�
		KeyStore caCert = KeyStore.getInstance("jks");
		try {
			caCert.load(new FileInputStream(TRUSTCAPATH), TRUSTCAPWD.toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("sunx509");
		tmf.init(caCert);

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		// 3闁靛棔绀侀崣褔姊婚锛勬濞戞棑绠戦悡娆撳触瀹ュ棛澧″Δ鐙呮嫹
		// (闁艰鲸妫侀惃鐔访圭�ｎ厾妲搁柣婊庡灠椤ｃ劍绋夐銊х濞戞搫鎷烽柤鍓插墯閻ュ懘寮垫径灞炬殼閻犲洤鍢查悡娆撳触瀹ュ繒绀夐柤鏉挎湰濡插憡鎷呯捄銊︽殢ip閺夆晜绋栭、鎴犳媼閸ф锛栭柣銊ュ缁辨繃娼诲▎鎴綒闁革妇鍎ゅ▍娆愮▔鐎ｎ亞绠戝銈堫嚙閸櫻囨⒒椤擄紕妲堝☉鏃撳濞堟垿宕洪悢閿嬪�抽柡宥忕節閻涙瑩宕濋悢璇插幋)
		SSLSocketFactory ssf = new SSLSocketFactory(sc,
				SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		// 濠碘�冲�归悘澶愭偝椤栨凹鏆旂�规瓕灏欑划锟犳偨鐎圭媭鍤炲ù婊冩閻撴瑩宕ュ蹇曠妤犵偞婀圭粭鏍ㄧ▔鎼淬倗妲堝☉鏃撶細娣囧﹪骞侀娆掑幀闁汇劌瀚悡娆撳触瀹ュ懎鐖遍梺鏉跨▌缁辨繈骞嶅鍛濞寸姰鍎辩槐鎴﹀触椤栨繄妲堝☉鏃撶畱閻撴瑩宕ュ鍡欏ⅰ濡ょ媴鎷� 闁挎稑鐗撶划顖滄媼閵堝嫮鐦嶉柡鍕靛灡婢э箑顕ｉ敓浠嬫儍閸曞墎绀�
		// SSLSocketFactory ssf = new SSLSocketFactory(sc);

		ClientConnectionManager ccm = this.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", 8743, ssf));
		
	    httpClient = new DefaultHttpClient(ccm);
	}

	/**
	 * 闁告娲栭幃婊呮媼閵堝牏妲堥柛锔惧劋濞咃拷 One-way authentication 
	 * 闁告娲栭幃婊呮媼閵堝牏妲堥柛锔惧劋濞呮瑦绋夌�ｅ墎绀夐悗骞垮灪閸╂稓绮╅鐐翠粯閻熸洩鎷�
	 * 1闁靛棔绀侀閬嶅礂閵夛附绠涢柛鏂猴拷铏彜CA閻犲洣妞掗崝鐔兼晬鐏炵厧鈻忛柣鈧妽濠�鍥礉閿涘嫷浼侰A閻犲洣妞掗崝鐔煎冀閿熺姷宕ｉ柡鍫濈Т婵喓绮╅姘岛闂侇偂娴囩换鍐级閵壯勭暠閻犲洣妞掗崝锟�
	 * 2闁靛棔娴囬鏇犵磾椤旇崵鐟濋柡宥忕節閻涙瑩宕洪悢閿嬪�� 闁挎稑鐗撳顏堝疮閸℃瑦鏆忛柣婊庡灠椤ｃ劍绋夌�ｅ墎绀夋繛灞惧笚濠�浣规媴鐠恒劍鏆忛柛鈺冨枎閹洜鎷嬮崸妤侊紪闁挎冻鎷�
	 * */
	/*
	public void initSSLConfigForOneWay() throws Exception {
		// 闁硅埖鎸哥�垫﹢鏁嶇仦鐐級闊浄鎷�
		System.setProperty("ssl.provider",
				"com.sun.net.ssl.internal.ssl.Provider");
		System.setProperty("ssl.pkgs", "com.sun.net.ssl.internal.www.protocol");
		System.setProperty("javax.net.ssl.keyStore", "D://cert//ca.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "Huawei@123");
		System.setProperty("javax.net.debug", "all");

		// 1闁靛棔绀侀閬嶅礂閵夛附绠涢柛鏂猴拷铏彜CA閻犲洣妞掗崝锟�
		KeyStore caCert = KeyStore.getInstance("jks");
		caCert.load(new FileInputStream(TRUSTCAPATH), TRUSTCAPWD.toCharArray());
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("sunx509");
		tmf.init(caCert);

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, tmf.getTrustManagers(), null);

		// 2闁靛棔绀侀崣褔姊婚锛勬濞戞棑绠戦悡娆撳触瀹ュ棛澧″Δ鐙呮嫹
		// (闁艰鲸妫侀惃鐔访圭�ｎ厾妲搁柣婊庡灠椤ｃ劍绋夐銊х濞戞搫鎷烽柤鍓插墯閻ュ懘寮垫径灞炬殼閻犲洤鍢查悡娆撳触瀹ュ繒绀夐柤鏉挎湰濡插憡鎷呯捄銊︽殢ip閺夆晜绋栭、鎴犳媼閸ф锛栭柣銊ュ缁辨繃娼诲▎鎴綒闁革妇鍎ゅ▍娆愮▔鐎ｎ亞绠戝銈堫嚙閸櫻囨⒒椤擄紕妲堝☉鏃撳濞堟垿宕洪悢閿嬪�抽柡宥忕節閻涙瑩宕濋悢璇插幋)
		SSLSocketFactory ssf = new SSLSocketFactory(sc,
				SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		// 濠碘�冲�归悘澶愭偝椤栨凹鏆旂�规瓕灏欑划锟犳偨鐎圭媭鍤炲ù婊冩閻撴瑩宕ュ蹇曠妤犵偞婀圭粭鏍ㄧ▔鎼淬倗妲堝☉鏃撶細娣囧﹪骞侀娆掑幀闁汇劌瀚悡娆撳触瀹ュ懎鐖遍梺鏉跨▌缁辨繈骞嶅鍛濞寸姰鍎辩槐鎴﹀触椤栨繄妲堝☉鏃撶畱閻撴瑩宕ュ鍡欏ⅰ濡ょ媴鎷� 闁挎稑鐗撶划顖滄媼閵堝嫮鐦嶉柡鍕靛灡婢э箑顕ｉ敓浠嬫儍閸曞墎绀�
		// SSLSocketFactory ssf = new SSLSocketFactory(sc);

		ClientConnectionManager ccm = this.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", 8743, ssf));
		
		httpClient = new DefaultHttpClient(ccm);
	}
*/
    public  HttpResponse doPost(String url, Map<String, String> headerMap,
            StringEntity stringEntity) 
    {
        HttpPost request = new HttpPost(url);
        addRequestHeader(request, headerMap);

        request.setEntity(stringEntity);

        return executeHttpRequest(request);
    }

    public  HttpResponse doPost(String url, Map<String, String> headerMap,
            InputStream inStream) 
    {
        HttpPost request = new HttpPost(url);
        addRequestHeader(request, headerMap);

        request.setEntity(new InputStreamEntity(inStream));

        return executeHttpRequest(request);
    }

    public  HttpResponse doPostJson(String url,
            Map<String, String> headerMap, String content)
    {
        HttpPost request = new HttpPost(url);
        addRequestHeader(request, headerMap);

        request.setEntity(
                new StringEntity(content, ContentType.APPLICATION_JSON));

        return executeHttpRequest(request);
    }

    public  String doPostJsonForString(String url,
            Map<String, String> headerMap, String content)
    {
        HttpPost request = new HttpPost(url);
        addRequestHeader(request, headerMap);

        request.setEntity(
                new StringEntity(content, ContentType.APPLICATION_JSON));

        HttpResponse response = executeHttpRequest(request);
        if (null == response)
        {
        	 System.out.println("The response body is null.");
        }
        
        return ((StreamClosedHttpResponse) response).getContent();
    }

    public  String doPostJsonForString(String url, String content)
    {
        HttpPost request = new HttpPost(url);

        request.setEntity(
                new StringEntity(content, ContentType.APPLICATION_JSON));

        HttpResponse response = executeHttpRequest(request);
        if (null == response)
        {
        	 System.out.println("The response body is null.");
        }
        
        return ((StreamClosedHttpResponse) response).getContent();
    }
    
    private  List<NameValuePair> paramsConverter(Map<String, String> params)
    {
        List<NameValuePair> nvps = new LinkedList<NameValuePair>();
        Set<Map.Entry<String, String>> paramsSet = params.entrySet();
        for (Map.Entry<String, String> paramEntry : paramsSet)
        {
            nvps.add(new BasicNameValuePair(paramEntry.getKey(),
                    	paramEntry.getValue()));
        }

        return nvps;
    }
    
    public String doPostFormUrlEncodedForString(String url, Map<String, String> formParams)
                    throws Exception
    {
        HttpPost request = new HttpPost(url);

        request.setEntity(new UrlEncodedFormEntity(paramsConverter(formParams)));

        HttpResponse response = executeHttpRequest(request);
        if (null == response)
        {
        	 System.out.println("The response body is null.");
        	 throw new Exception();
        }

        return ((StreamClosedHttpResponse) response).getContent();
    }
    
    public  HttpResponse doPut(String url, Map<String, String> headerMap, InputStream inStream)
    {
        HttpPut request = new HttpPut(url);
        addRequestHeader(request, headerMap);

        request.setEntity(new InputStreamEntity(inStream));

        return executeHttpRequest(request);
    }

    public  HttpResponse doPutJson(String url,
            Map<String, String> headerMap, String content)
    {
        HttpPut request = new HttpPut(url);
        addRequestHeader(request, headerMap);

        request.setEntity(new StringEntity(content, ContentType.APPLICATION_JSON));

        return executeHttpRequest(request);
    }
    
    public  String doPutJsonForString(String url,
            Map<String, String> headerMap, String content)
    {
        HttpResponse response = doPutJson(url, headerMap, content);
        if (null == response)
        {
        	 System.out.println("The response body is null.");
        }

        return ((StreamClosedHttpResponse) response).getContent();
    }

    public  HttpResponse doGet(String url, Map<String, String> headerMap)
    {
        HttpGet request = new HttpGet(url);
        addRequestHeader(request, headerMap);

        return executeHttpRequest(request);
    }
    
    public  HttpResponse doGetWithParas(String url, Map<String, String> queryParams, Map<String, String> headerMap)
            throws Exception
    {
        HttpGet request = new HttpGet();
        addRequestHeader(request, headerMap);
        
        URIBuilder builder;
        try
        {
            builder = new URIBuilder(url);
        }
        catch (URISyntaxException e)
        {
            System.out.printf("URISyntaxException: {}", e);
            throw new Exception(e);
            
        }
        
        if (queryParams != null && !queryParams.isEmpty())
        {
            builder.setParameters(paramsConverter(queryParams));
        }
        request.setURI(builder.build());

        return executeHttpRequest(request);
    }
    
    public  String doGetWithParasForString(String url, Map<String, String> mParam, Map<String, String> headerMap)
            throws Exception
    {
        HttpResponse response = doGetWithParas(url, mParam, headerMap);
        if (null == response)
        {
        	 System.out.println("The response body is null.");
        }

        return ((StreamClosedHttpResponse) response).getContent();
    }

    public  HttpResponse doDelete(String url,
            Map<String, String> headerMap)
    {
        HttpDelete request = new HttpDelete(url);
        addRequestHeader(request, headerMap);

        return executeHttpRequest(request);
    }
    
    public  String doDeleteForString(String url,
            Map<String, String> headerMap)
    {
        HttpResponse response = doDelete(url, headerMap);
        if (null == response)
        {
        	 System.out.println("The response body is null.");
        }

        return ((StreamClosedHttpResponse) response).getContent();
    }

    private static void addRequestHeader(HttpUriRequest request,
            Map<String, String> headerMap)
    {
        if (headerMap == null)
        {
            return;
        }

        for (String headerName : headerMap.keySet())
        {
            if (CONTENT_LENGTH.equalsIgnoreCase(headerName))
            {
                continue;
            }

            String headerValue = headerMap.get(headerName);
            request.addHeader(headerName, headerValue);
        }
    }

    private  HttpResponse executeHttpRequest(HttpUriRequest request)
    {
        HttpResponse response = null;

        try
        {
            response = httpClient.execute(request);
        }
        catch (Exception e)
        {
            System.out.println("executeHttpRequest failed.");
        }
        finally
        {
            try
            {
                // 闁兼儳鍢茶ぐ鍥规担鐤幀闁告劕鎳庨鎰版晬鐏炲�熷珯闁稿繑濞婂Λ瀛樻交閻愭潙澶�
                response = new StreamClosedHttpResponse(response);
            }
            catch (IOException e)
            {
            	 System.out.println("IOException: " + e.getMessage());
            }
        }

        return response;
    }

    public  String getHttpResponseBody(HttpResponse response)
            throws UnsupportedOperationException, IOException
    {
        if (response == null)
        {
            return null;
        }
        
        String body = null;

        if (response instanceof StreamClosedHttpResponse)
        {
            body = ((StreamClosedHttpResponse) response).getContent();
        }
        else
        {
            HttpEntity entity = response.getEntity();
            if (entity != null && entity.isStreaming())
            {
                String encoding = entity.getContentEncoding() != null
                        ? entity.getContentEncoding().getValue() : null;
                body = StreamUtil.inputStream2String(entity.getContent(),
                        encoding);
            }
        }

        return body;
    }
}
