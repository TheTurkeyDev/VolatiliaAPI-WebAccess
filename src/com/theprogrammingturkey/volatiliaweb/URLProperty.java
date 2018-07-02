package com.theprogrammingturkey.volatiliaweb;

public class URLProperty
{
	private String key;
	private String value;

	public URLProperty(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	public String getKey()
	{
		return key;
	}

	public String getValue()
	{
		return value;
	}

	public String toString()
	{
		return this.key + "=" + this.value;
	}
}
