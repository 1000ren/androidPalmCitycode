package com.busx.entities;

import java.io.Serializable;
import java.util.List;

public class NaviBusStation implements Serializable
{

	private static final long serialVersionUID = 1L;
	
	public String lineid;
	public String linename;
	public String startstop;
	public String endstop;
	public List<NaviPassStop> passstops;
	
}
