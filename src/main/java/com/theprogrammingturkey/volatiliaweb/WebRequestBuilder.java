package com.theprogrammingturkey.volatiliaweb;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebRequestBuilder
{
	private String url;
	private RequestType type;
	private List<HeaderProperty> headers;
	private List<URLProperty> urlProps;
	private boolean urlParametersAsBody;

	/**
	 * Initiates a web request to later be executed from the given url as a GET Request
	 * 
	 * @param url
	 *            to make the request to
	 */
	public WebRequestBuilder(String url)
	{
		this(url, RequestType.GET);
	}

	/**
	 * Initiates a web request to later be executed from the given url and request type
	 * 
	 * @param url
	 *            to make the request to
	 * @param type
	 *            of web request to make
	 */
	public WebRequestBuilder(String url, RequestType type)
	{
		this.url = url;
		setRequestType(type);
		this.headers = new ArrayList<>();
		this.urlProps = new ArrayList<>();
	}

	/**
	 * Set the request type of this web request
	 * 
	 * @param type
	 *            to be set to
	 */
	public void setRequestType(RequestType type)
	{
		this.type = type;
		urlParametersAsBody = type != RequestType.GET;
	}

	/**
	 * Configures whether or not the url parameters passed should be sent as body text instead of in
	 * the url
	 * 
	 * @param asBody
	 */
	public void setURLPropertiesAsBody(boolean asBody)
	{
		urlParametersAsBody = asBody;
	}

	/**
	 * Adds a header property to the web request being constructed
	 * 
	 * @param key
	 *            of the header property
	 * @param value
	 *            of the header property
	 */
	public void addHeaderProp(String key, String value)
	{
		addHeaderProp(new HeaderProperty(key, value));
	}

	/**
	 * Adds a header property to the web request being constructed
	 * 
	 * @param prop
	 *            the header property
	 */
	public void addHeaderProp(HeaderProperty prop)
	{
		this.headers.add(prop);
	}

	/**
	 * Adds a url property to the web request being constructed
	 * 
	 * @param key
	 *            of the url property
	 * @param value
	 *            of the url property
	 */
	public void addURLProp(String key, String value)
	{
		addURLProp(new URLProperty(key, value));
	}

	/**
	 * Adds a url property to the web request being constructed
	 * 
	 * @param prop
	 *            the URL property
	 */
	public void addURLProp(URLProperty prop)
	{
		this.urlProps.add(prop);
	}

	/**
	 * Gets the URL that this Builder currently holds
	 * 
	 * @return
	 */
	public String getURL()
	{
		StringBuilder builder = new StringBuilder(url);
		if(!this.urlParametersAsBody)
		{
			builder.append("?");

			for(URLProperty property : urlProps)
			{
				builder.append(property.toString());
				builder.append("&");
			}
		}
		return builder.toString();
	}

	/**
	 * Gets the body content that the builder currently has
	 * 
	 * @return
	 */
	public String getBody()
	{
		StringBuilder builder = new StringBuilder();

		for(URLProperty property : urlProps)
		{
			builder.append(property.toString());
			builder.append("&");
		}

		if(builder.length() > 0)
			builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}

	/**
	 * Executes the currently constructed web request and returns the result
	 * 
	 * @return result of the web request
	 * @throws IOException
	 */
	public String executeRequest() throws IOException
	{
		HttpURLConnection con = (HttpURLConnection) new URL(getURL()).openConnection();
		con.setUseCaches(false);

		if(this.urlParametersAsBody)
			con.setDoOutput(true);

		con.setDoInput(true);
		con.setReadTimeout(5000);
		con.setRequestProperty("Connection", "keep-alive");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:16.0) Gecko/20100101 Firefox/16.0");

		for(HeaderProperty headProp : headers)
		{
			con.setRequestProperty(headProp.getKey(), headProp.getValue());
		}

		con.setRequestMethod(type.name());
		con.setConnectTimeout(5000);

		if(this.urlParametersAsBody)
		{
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(getBody());
			wr.flush();
			wr.close();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

		StringBuilder buffer = new StringBuilder();
		String line;
		while((line = reader.readLine()) != null)
			buffer.append(line);

		return buffer.toString();
	}

}
