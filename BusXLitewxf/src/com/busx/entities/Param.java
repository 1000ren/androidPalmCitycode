package com.busx.entities;

import java.io.Serializable;
import java.text.Collator;

public class Param  implements Comparable<Param>, Serializable
{
	private static final long serialVersionUID = 900L;

	public String key;
	public String value;

	public Param(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	@Override
	public int compareTo(Param another)
	{
		Collator cmp = Collator.getInstance(java.util.Locale.US);
		return cmp.compare(this.key, another.key);
	}
	
}
