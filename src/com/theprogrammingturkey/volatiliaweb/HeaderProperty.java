package com.theprogrammingturkey.volatiliaweb;

public class HeaderProperty
{
	private String key;
	private String value;

	public HeaderProperty(String key, String value)
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
}
