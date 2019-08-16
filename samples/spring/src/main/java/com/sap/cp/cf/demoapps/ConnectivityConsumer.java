	package com.sap.cp.cf.demoapps;

	import java.io.IOException;
	import java.io.InputStream;
	import java.net.HttpURLConnection;
	import java.net.InetSocketAddress;
	import java.net.Proxy;
	import java.net.URL;

	import com.sap.cloud.security.xsuaa.token.Token;
	import org.apache.commons.io.IOUtils;
	import org.json.JSONArray;
	import org.json.JSONException;
	import org.json.JSONObject;
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.security.core.Authentication;
	import org.springframework.security.core.context.SecurityContextHolder;
	import org.springframework.security.oauth2.client.token.AccessTokenRequest;
	import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
	import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
	import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
	import org.springframework.security.oauth2.common.OAuth2AccessToken;
	//import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;


	public class ConnectivityConsumer {
		private static final Logger logger = LoggerFactory.getLogger(Application.class);

		public byte[] getImageFromBackend(String fileName) throws IOException {
			HttpURLConnection urlConnection = null;
			URL url = null;
			try {
				// Build the URL to the requested file.
				url = new URL("http://mybackend:10080/images/" + fileName);

				// Build the connectivity proxy and set up the connection.
				Proxy proxy = getProxy();
				urlConnection = (HttpURLConnection) url.openConnection(proxy);

				// Get connectivity access token and configure the proxy authorization header.
				String accessToken = null; //getAccessToken();
				urlConnection.setRequestProperty("Proxy-Authorization", "Bearer " + accessToken);

				// Set connection timeouts.
				urlConnection.setConnectTimeout(10000);
				urlConnection.setReadTimeout(60000);

				// Get the user JWT token and configure the connectivity authentication header.
				String token = getUserOAuthToken();
				urlConnection.setRequestProperty("SAP-Connectivity-Authentication", "Bearer " + token);

				// Execute request, returning the response as a byte array.
				urlConnection.connect();
				InputStream in = urlConnection.getInputStream();
				return IOUtils.toByteArray(in);

			} catch (Exception e) {
				String messagePrefix = "Connectivity operation failed with reason: ";
				logger.error(messagePrefix, e);
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
			}
			return null;
		}

		// Parse the credentials for a given service from the environment variables.
		private JSONObject getServiceCredentials(String serviceName) throws JSONException {
			JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));
			JSONArray jsonArr = jsonObj.getJSONArray(serviceName);
			return jsonArr.getJSONObject(0).getJSONObject("credentials");
		}

		// Create a HTTP proxy from the connectivity service credentials.
		private Proxy getProxy() throws JSONException {
			JSONObject credentials = getServiceCredentials("connectivity");
			String proxyHost = credentials.getString("onpremise_proxy_host");
			int proxyPort = Integer.parseInt(credentials.getString("onpremise_proxy_port"));
			return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
		}

		// Get JWT token for the connectivity service from UAA
		private OAuth2AccessToken getAccessToken() throws Exception {
			JSONObject connectivityCredentials = getServiceCredentials("connectivity");
			String clientId = connectivityCredentials.getString("clientid");
			String clientSecret = connectivityCredentials.getString("clientsecret");

			// Make request to UAA to retrieve JWT token
			JSONObject xsuaaCredentials = getServiceCredentials("xsuaa");
			String xsUaaUrl = xsuaaCredentials.getString("url");

			ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
			resource.setAccessTokenUri(xsUaaUrl + "/oauth/token");
			resource.setClientId(clientId);
			resource.setClientSecret(clientSecret);
			resource.setGrantType("client_credentials");
			AccessTokenRequest atr = new DefaultAccessTokenRequest();
			return new ClientCredentialsAccessTokenProvider().obtainAccessToken(resource,new DefaultAccessTokenRequest());
		}

		private String getUserOAuthToken() throws Exception {

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth == null) {
				throw new Exception("User not authenticated");
			}

			Token token = (Token) auth.getDetails();
			return token.getAppToken();
		}

	}
