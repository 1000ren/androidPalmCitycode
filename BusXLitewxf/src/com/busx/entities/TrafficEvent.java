package com.busx.entities;

import java.io.Serializable;

public class TrafficEvent implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	public String id = "";
	//事件题目
	public String title = "";
	//作者
	public String author = "";
	//作者id
	public String pubtime = "";
	//开始时间
	public String starttime = "";
	//结束时间
	public String endtime = "";
	//所属分类
	public String cat = "";
	//事件内容
	public String content = "";
	
	
}
