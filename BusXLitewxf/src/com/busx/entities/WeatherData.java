package com.busx.entities;

import java.io.Serializable;

/**
 * 天气相关数据
 * @author 
 *
 */
public class WeatherData implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public String id = "";
	public String weather_date = "";
	public String city_name = "";
	public String first_temperature = "";
	public String second_temperature = "";
	public String third_temperature = "";
	public String first_weather = "";
	public String second_weather = "";
	public String third_weather = "";
	public String first_weather_img="";

	public String second_weather_img="";
	public String third_weather_img="";
	public String emperature="";
	public String direction="";
	public String humidity="";
	public String pressure="";
	public String quality="";
	public String ultraviol="";
	public String dress="";
	public String cold="";
	public String exercise="";
	public String car_wash="";
	public String air_cure="";
	public String journey="";
	public String traffic_case="";
	public String comfort="";
	public String first_direction="";
	public String second_direction="";
	public String third_direction="";
}
