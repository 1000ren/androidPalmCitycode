package com.busx.entities;

import java.io.Serializable;

public class BusRouteUserInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String recid;//唯一ID
	public String usrname;//此条推荐的用户名
	public String time;//推荐时间
	public String nickname;//此条推荐的用户昵称
	public String pathcomid;//推荐信息的id
	public String reason;//推荐理由
	public int approve;//赞同数目
	public int opposition;//反对数目

}
