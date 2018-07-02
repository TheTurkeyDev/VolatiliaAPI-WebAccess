package com.theprogrammingturkey.volatiliaweb;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
		this.type = type;
		this.headers = new ArrayList<>();
		this.urlProps = new ArrayList<>();
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
	 * Executes the currently constructed web request and returns the result
	 * 
	 * @return result of the web request
	 * @throws IOException
	 */
	public String executeRequest() throws IOException
	{
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setUseCaches(false);
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setReadTimeout(5000);
		con.setRequestProperty("Connection", "keep-alive");

		boolean userAgentSet = false;
		for(HeaderProperty headProp : headers)
		{
			if(headProp.getKey().equalsIgnoreCase("User-Agent"))
				userAgentSet = true;
			con.setRequestProperty(headProp.getKey(), headProp.getValue());
		}

		if(!userAgentSet)
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:16.0) Gecko/20100101 Firefox/16.0");

		((HttpURLConnection) con).setRequestMethod(type.name());
		con.setConnectTimeout(5000);

		StringBuilder builder = new StringBuilder();
		builder.append("?");

		for(URLProperty property : urlProps)
		{
			builder.append(property.toString());
			builder.append("&");
		}

		if(builder.length() > 0)
			builder.deleteCharAt(builder.length() - 1);

		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(builder.toString());
		wr.flush();
		wr.close();

		BufferedInputStream in = new BufferedInputStream(con.getInputStream());

		StringBuilder buffer = new StringBuilder();
		int chars_read;
		while((chars_read = in.read()) != -1)
			buffer.append((char) chars_read);

		return buffer.toString();
	}

}
