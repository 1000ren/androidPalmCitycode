package com.busx.entities;

import java.io.Serializable;

public class BusStop implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String id;
	public String stopid;
	public String stopname;
	public String tonextlen;
	public GPoint gPoint;
}
