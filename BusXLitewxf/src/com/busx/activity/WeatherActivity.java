package com.busx.activity;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.busx.R;
import com.busx.activity.base.BaseActivity;
import com.busx.utils.Utils;

/**
 * 天气
 * @author Administrator
 *
 */
public class WeatherActivity extends BaseActivity 
{
	private int[] i_img=new int[]{R.drawable.weather_number_0,R.drawable.weather_number_1,R.drawable.weather_number_2,R.drawable.weather_number_3,R.drawable.weather_number_4,R.drawable.weather_number_5,R.drawable.weather_number_6,R.drawable.weather_number_7,R.drawable.weather_number_8,R.drawable.weather_number_9};

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weather);
		showFirstDay();
		showSecondDay();
		showThirdDay();
	}
	private void showFirstDay()
	{

		Toast.makeText(getApplicationContext(), mCommonApplication.mWeatherData.city_name, Toast.LENGTH_SHORT);
		//城市名称
		TextView oWeatherCity = (TextView)this.findViewById(R.id.TextView_WeatherCity);
		oWeatherCity.setText(mCommonApplication.mWeatherData.city_name);
		//日期-年月日
		TextView oWeatherDate =  (TextView)this.findViewById(R.id.TextView_date);
		oWeatherDate.setText(mCommonApplication.mWeatherData.weather_date);
		//日期-星期
		TextView oWeatherWeek =  (TextView)this.findViewById(R.id.TextView_week);
		oWeatherWeek.setText(Utils.ymdToWeek(mCommonApplication.mWeatherData.weather_date));
		//天气
		TextView oFirstWeather =  (TextView)this.findViewById(R.id.TextView_first_weather);
		oFirstWeather.setText(mCommonApplication.mWeatherData.first_weather);
		//天气-图片
		ImageView imgFirstWeather = (ImageView)this.findViewById(R.id.ImageView_first_weather);
		String reqWeatherIcons = mCommonApplication.mWeatherData.first_weather_img;
		String todayIcon = reqWeatherIcons.substring(0, reqWeatherIcons.indexOf("."));
		int todayIcon_day=0;
		try {
			todayIcon_day = Integer.parseInt(todayIcon);
		} catch (Exception e) {
		}
		imgFirstWeather.setImageResource(getTodayIcon(todayIcon_day));

		//温度-图片
		LinearLayout layout = (LinearLayout)this.findViewById(R.id.TextView_first_temperature);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        String str = mCommonApplication.mWeatherData.first_temperature;//"23℃/23℃";
        for(int i=0;i<str.length();i++)
        {
        	ImageView img = new ImageView(this);
        	char c = str.charAt(i);
        	img.setPadding(0, 0, 0, 0);
        	img.setImageResource(getWeatherIcon(c));
        	layout.addView(img,i,params);
        }
		
		//出行提示
		TextView oTrafficCase =  (TextView)this.findViewById(R.id.TextView_traffic_case);
		oTrafficCase.setText(mCommonApplication.mWeatherData.traffic_case);

		//穿衣提示
		TextView oDress =  (TextView)this.findViewById(R.id.TextView_dress);
		oDress.setText(mCommonApplication.mWeatherData.dress);

		//运动指数
		TextView oExercise =  (TextView)this.findViewById(R.id.TextView_exercise);
		oExercise.setText(mCommonApplication.mWeatherData.exercise);

		//洗车指数
		TextView oCarWash =  (TextView)this.findViewById(R.id.TextView_car_wash);
		oCarWash.setText(mCommonApplication.mWeatherData.car_wash);
	}
	
	private void showSecondDay() {
		TextView oDateSecond = (TextView)this.findViewById(R.id.TextView_date_second);
		oDateSecond.setText(Utils.getDateOfYear(mCommonApplication.mWeatherData.weather_date, 1));
		TextView oWeekSecond = (TextView)this.findViewById(R.id.TextView_week_second);

		oWeekSecond.setText(Utils.ymdToWeek(mCommonApplication.mWeatherData.weather_date, 1));
		TextView oSecondWeather = (TextView)this.findViewById(R.id.TextView_second_weather);

		oSecondWeather.setText(mCommonApplication.mWeatherData.second_weather);
		TextView oSecondTemperature = (TextView)this.findViewById(R.id.TextView_second_temperature);

		oSecondTemperature.setText(mCommonApplication.mWeatherData.second_temperature);

		ImageView imgSccondWeather = (ImageView)this.findViewById(R.id.ImageView_second_weather);

		String reqWeatherIcons = mCommonApplication.mWeatherData.second_weather_img;
		String secondIcon = reqWeatherIcons.substring(0, reqWeatherIcons.indexOf("."));
		int secondIcon_day = 0;
		try {
			secondIcon_day=Integer.parseInt(secondIcon);
		} catch (Exception e) {
		}
				
		imgSccondWeather.setImageResource(getOtherDayIcon(secondIcon_day));

	}


	private void showThirdDay() {
		TextView oDateThird = (TextView)this.findViewById(R.id.TextView_date_third);

		oDateThird.setText(Utils.getDateOfYear(mCommonApplication.mWeatherData.weather_date, 2));
		TextView oWeekThird = (TextView)this.findViewById(R.id.TextView_week_third);
		oWeekThird.setText(Utils.ymdToWeek(mCommonApplication.mWeatherData.weather_date, 2));

		TextView oThirdWeather = (TextView)this.findViewById(R.id.TextView_third_weather);

		oThirdWeather.setText(mCommonApplication.mWeatherData.third_weather);
		TextView oThirdTemperature = (TextView)this.findViewById(R.id.TextView_third_temperature);

		oThirdTemperature.setText(mCommonApplication.mWeatherData.third_temperature);
		//添加天气小图标
		ImageView imgThirdWeather = (ImageView)this.findViewById(R.id.ImageView_third_weather);


		String reqWeatherIcons = mCommonApplication.mWeatherData.third_weather_img;
		String thirdIcon = reqWeatherIcons.substring(0, reqWeatherIcons.indexOf("."));
		int thirdIcon_day=0;
		try {
			thirdIcon_day=Integer.parseInt(thirdIcon);
		} catch (Exception e) {
		}
		imgThirdWeather.setImageResource(getOtherDayIcon(thirdIcon_day));

	}
	private int getWeatherIcon(char c){
    	int num = 0;
    	if(c >= 48 && c <=57){
    		num = i_img[(int)(c-48)];
    	}else if(c == '-'){
    		num = R.drawable.weather_number_negative;
    	}else if(c == '/'){
    		num = R.drawable.weather_number_symbol;
    	}else{
    		num = R.drawable.weather_number_celsius;
    	}
    	return num;
    }
	
	private int getTodayIcon(int num){
		int icon = R.drawable.weather_sunny;
		switch(num){
		case 0:
			icon = R.drawable.weather_sunny;//晴
			break;
		case 1:
			icon = R.drawable.weather_mostlycloudy;//多云
			break;
		case 2:
			icon = R.drawable.weather_cloudy;//阴
			break;
		case 3:
			icon = R.drawable.weather_chancerain;//阵雨
			break;
		case 4:
		case 5:
			icon = R.drawable.weather_chancestorm;//雷阵雨
			break;
		case 6:
			icon = R.drawable.weather_icyrain;//雨夹雪
			break;
		case 7:
		case 8:
		case 9:
		case 21:
		case 22:
			icon = R.drawable.weather_lightrain;//小到大雨
			break;
		case 10:
		case 11:
		case 12:
		case 23:
		case 24:
		case 25:
			icon = R.drawable.weather_rain;//暴到特大暴雨
			break;
		case 13:
		case 14:
		case 26:
			icon = R.drawable.weather_chancesnow;//阵雪到小雪
			break;
		case 15:
		case 16:
		case 17:
		case 27:
		case 28:
			icon = R.drawable.weather_snow;//中到暴雪
			break;
		case 18:
			icon = R.drawable.weather_fog;
			break;
		}
		return icon;
	}

 private int getOtherDayIcon(int num){
		int icon = R.drawable.weather_sunny_s;
		switch(num){
		case 0:
			icon = R.drawable.weather_sunny_s;//晴
			break;
		case 1:
			icon = R.drawable.weather_mostlycloudy_s;//多云
			break;
		case 2:
			icon = R.drawable.weather_cloudy_s;//阴
			break;
		case 3:
			icon = R.drawable.weather_chancerain_s;//阵雨
			break;
		case 4:
		case 5:
			icon = R.drawable.weather_chancestorm_s;//雷阵雨
			break;
		case 6:
			icon = R.drawable.weather_icyrain_s;//雨夹雪
			break;
		case 7:
		case 8:
		case 9:
		case 21:
		case 22:
			icon = R.drawable.weather_lightrain_s;//小到大雨
			break;
		case 10:
		case 11:
		case 12:
		case 23:
		case 24:
		case 25:
			icon = R.drawable.weather_rain_s;//暴到特大暴雨
			break;
		case 13:
		case 14:
		case 26:
			icon = R.drawable.weather_chancesnow_s;//阵雪到小雪
			break;
		case 15:
		case 16:
		case 17:
		case 27:
		case 28:
			icon = R.drawable.weather_snow_s;//中到暴雪
			break;
		case 18:
			icon = R.drawable.weather_fog_s;
			break;
		}
		return icon;
	}
 
 	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			mCommonApplication.mIsBack = true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
