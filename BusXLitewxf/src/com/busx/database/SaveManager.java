//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			yangyang	2011/07/07		新規
//1.0.0			liupengfei	2011/09/14		修正
//1.0.0			zhaoyong	2011/09/20		修正
//**************************************************************
package com.busx.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import com.busx.data.VersionData;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 实现数据保存功能，本地数据读取功能 * @author yangyang
 *
 */
public class SaveManager
{
	/** 索引*/
	private static final String SID = "sid";

	/** 数据库名称*/
	public static final String DATA_NAME = "clientdata.db";

	/** 保存的数据库名称*/
	public static final String DATA_PATH = "savedata";

	/** Integer数据类型*/
	public static final String TYPE_INT = "INTEGER";

	/** 文本数据类型*/
	public static final String TYPE_TEXT = "TEXT";

	/** 二进制数据类型*/
	public static final String TYPE_BIN = "BINARY";

	/** 操作句柄*/
	private static Context s_oContext = null;

	/** 获取SDCard路径*/
	public static String getSDCardPath(){
		return android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+DATA_PATH;
	}
	
	/** 获取数据库默认路径 **/
	public static String getDataBasePath(){
		return s_oContext.getFilesDir().getPath()+ "/" + DATA_PATH;
	}
	
	/**
	 * 数据库的初期化，当想操作数据库的时候，此方法的实现是必要的。	 * @param oContext 操作句柄	 */
	public static void initDatabase(Context oContext)
	{
		s_oContext = oContext;
	}

	/**
	 * 数据库打开	 * @return SQLiteDatabase 成功的话，操作句柄返回，否则，返回空	 * @throws Exception 异常发生时，异常返回
	 */
	private static SQLiteDatabase openDatabase()throws Exception
	{
		if(s_oContext != null)
		{
			String sPath = s_oContext.getFilesDir().getPath() + "/" + DATA_PATH;
			createDir(sPath);
			return SQLiteDatabase.openOrCreateDatabase(sPath + "/" + DATA_NAME, null);
		}
		return null;
	}


	/**
	 * 创建数据库表	 * @param oDataBase 数据库的操作句柄	 * @param sTable 表名称
	 * @param oData 保存数据的操作句柄
	 * @throws Exception 异常发生时，异常返回
	 */
	private static void createTable(SQLiteDatabase oDataBase,String sTable,SaveDataListener oData)throws Exception
	{
		try
		{
			oDataBase.rawQuery("SELECT * FROM " + sTable, null);
		}
		catch(Exception e)
		{
			Hashtable<String,String> ohKey = oData.onCreateTable();
			StringBuffer osSql = new StringBuffer();
			osSql.append("CREATE TABLE " + sTable + " (" + SID + " INTEGER PRIMARY KEY,");
			String sKey;
			String sType;
			for(Enumeration<String> oEn = ohKey.keys();oEn.hasMoreElements();)
			{
				sKey = (String)oEn.nextElement();
				sType = ohKey.get(sKey);
				osSql.append(sKey + " " + sType + ",");
			}
			osSql.deleteCharAt(osSql.length() - 1);
			osSql.append(")");

			oDataBase.execSQL(osSql.toString());
		}
	}

	/**
	 * 路径创建	 * @param sPath 路径名称
	 */
	public static void createDir(String sPath)
	{
		File oDir;
		String sMid;
		if(sPath.startsWith("/"))
		{
			sPath = sPath.substring(1);
		}
		int wIndex = sPath.indexOf('/');
		while(wIndex != -1)
		{
			sMid = sPath.substring(0,wIndex);
			oDir = new File(sMid);
			if(!oDir.exists())
			{
				oDir.mkdir();
			}
			wIndex = sPath.indexOf('/', wIndex + 1);
		}
		oDir = new File(sPath);
		if(!oDir.exists())
		{
			oDir.mkdir();
		}
	}

	/**
	 * 保存数据
	 * @param sTable 表名称
	 * @param oData 保存的数据
	 * @param wMaxSize 最大数据保存件数
	 * @return wFinish 如果成功，返回1，数据不存在，返回0，保存失败，返回0
	 */
	public static int saveData(String sTable,SaveDataListener oData,int wMaxSize)
	{

		int wFinish = -1;
		Cursor oCursor = null;
		if(sTable != null && oData != null)
		{
			SQLiteDatabase oDatabase = null;
			try
			{
				oDatabase = openDatabase();
				createTable(oDatabase,sTable,oData);

				//
				String sSql = "SELECT * FROM " + sTable + " WHERE " + oData.onFilterData();

				oCursor = oDatabase.rawQuery(sSql, null);
				if(oCursor.getCount() > 0)
				{
					oCursor.moveToFirst();
					oDatabase.execSQL("DELETE FROM " + sTable + " WHERE " + SID + "=" + oCursor.getInt(oCursor.getColumnIndex(SID)));
					wFinish = 0;
				}
				else
				{
					//最大件数满足时，删除最后一件，然后再保存。					if(wMaxSize > 0)
					{
						closeCursor(oCursor);

						oCursor = oDatabase.rawQuery("SELECT * FROM " + sTable + " ORDER BY " + SID + " DESC" , null);
						if(oCursor.getCount() >= wMaxSize)
						{
							oCursor.moveToLast();
							oDatabase.execSQL("DELETE FROM " + sTable + " WHERE " + SID + "=" + oCursor.getInt(oCursor.getColumnIndex(SID)));
						}
					}
				}
				oDatabase.insert(sTable, null, oData.onSaveData());
				wFinish = 1;
			}
			catch(Exception e)
			{
					e.printStackTrace();
			}
			finally
			{
				closeCursor(oCursor);
				closeDB(oDatabase);
			}
		}
		return wFinish;
	}

	/**
	 * 指定表内容读取。	 * @param sTable 表名称
	 * @param wId 数据的id (如果没有指定的id,则置为-1)
	 * @param oData 输出参数，取得的数据	 */
	public static void readData(String sTable,int wId,SaveDataListener oData)
	{
		if(sTable != null && oData != null)
		{
			SQLiteDatabase oDatabase = null;
			Cursor oCursor = null;
			try
			{
				oDatabase = openDatabase();
				String sql = "SELECT * FROM " + sTable + " WHERE 1=1 ";// + SID + "=" + wId;
				String where = oData.onFilterData();
				if (null != where && !where.equals("")) {
					sql += " and " + where;
				} else if (wId != -1) {
					sql += " and " + SID + "=" + wId;
				}
				oCursor = oDatabase.rawQuery(sql, null);
				if(oCursor.moveToFirst())
				{
					oData.onReadData(oCursor);
				}
			}
			catch(Exception e)
			{
					e.printStackTrace();
			}
			finally
			{
				closeCursor(oCursor);

				closeDB(oDatabase);
			}
		}
	}
	
	/**
	 * 满足条件的记录取得
	 * @param sql 检索的SQL文
	 * @param oData 检索结果
	 *
	 * */
	public static void readConditionData(String sql,SaveDataListener[] oData)
	{
		if(oData != null)
		{
			SQLiteDatabase oDatabase = null;
			Cursor oCursor = null;
			try
			{
				oDatabase = openDatabase();
				oCursor = oDatabase.rawQuery(sql, null);
				if(oCursor.moveToFirst())
				{
					int wCount = oCursor.getCount();
					for(int i = 0;i < wCount;++i)
					{
						if(oData[i] != null)
						{
							oData[i].onReadData(oCursor);
						}
						oCursor.moveToNext();
					}
				}
			}
			catch(Exception e)
			{
					e.printStackTrace();
			}
			finally
			{
				closeCursor(oCursor);

				closeDB(oDatabase);
			}
		}
	}

	/**
	 * 制定表内容的全部读取。	 * @param sTable 表名称
	 * @param ozData 输出参数，为了保存读取的数据	 */
	public static void readAllData(String sTable,SaveDataListener[] ozData)
	{
		if(sTable != null && ozData != null)
		{
			SQLiteDatabase oDatabase = null;
			Cursor oCursor = null;
			try
			{
				oDatabase = openDatabase();
				oCursor = oDatabase.rawQuery("SELECT * FROM " + sTable , null);
				if(oCursor.moveToFirst())
				{
					int wCount = oCursor.getCount();
					for(int i = 0;i < wCount;++i)
					{
						if(ozData[i] != null)
						{
							ozData[i].onReadData(oCursor);
						}
						oCursor.moveToNext();
					}
				}
			}
			catch(Exception e)
			{
					e.printStackTrace();
			}
			finally
			{
				closeCursor(oCursor);

				closeDB(oDatabase);
			}
		}
	}
	
	/**
	 * 收藏夹全部信息的读取
	 * @param sTable 表名称
	 * @param ozData 全部数据对象
	 *
	 * */
	public static void readFavoritesAllData(String sTable,SaveDataListener[] ozData)
	{
		String where = "";
		if(sTable != null && ozData != null)
		{
			SQLiteDatabase oDatabase = null;
			Cursor oCursor = null;
			try
			{
				if(ozData!=null){
					where = " where "+ ozData[0].onFilterData();
				}
				oDatabase = openDatabase();
				oCursor = oDatabase.rawQuery("SELECT * FROM " + sTable + where + " ORDER BY " + SID + " DESC", null);
				if(oCursor.moveToFirst())
				{
					int wCount = oCursor.getCount();
					for(int i = 0;i < wCount;++i)
					{
						if(ozData[i] != null)
						{
							ozData[i].onReadData(oCursor);
						}
						oCursor.moveToNext();
					}
				}
			}
			catch(Exception e)
			{
					e.printStackTrace();
			}
			finally
			{
				closeCursor(oCursor);

				closeDB(oDatabase);
			}
		}
	}
	
	/**
	 * 指定表的指定数据是否存在。	 * @param sTable 表名称	 * @param oData 检索的数据	 * @return 存在返回true，否则返回false	 */
	public static boolean isExistData(String sTable,SaveDataListener oData)
	{
		boolean bExist = false;
		if(sTable != null && oData != null)
		{
			SQLiteDatabase oDatabase = null;
			Cursor oCursor = null;
			try
			{
				oDatabase = openDatabase();
				createTable(oDatabase,sTable,oData);

				//
				String sSql = "SELECT * FROM " + sTable + " WHERE " + oData.onFilterData();
				oCursor = oDatabase.rawQuery(sSql, null);
				if(oCursor.getCount() > 0)
				{
					bExist = true;
				}
			}
			catch(Exception e)
			{
					e.printStackTrace();
			}
			finally
			{
				closeCursor(oCursor);

				closeDB(oDatabase);
			}
		}
		return bExist;
	}
	
	/**
	 * 自定义sql查看指定数据是否存在。
	 * @param sql 
	 * @return 存在返回true，否则返回false
	 */
	public static boolean getConditionDataExist(String sql)
	{
		boolean bExist = false;
		if(sql != null )
		{
			SQLiteDatabase oDatabase = null;
			Cursor oCursor = null;
			try
			{
				oDatabase = openDatabase();
				//
				oCursor = oDatabase.rawQuery(sql, null);
				if(oCursor.getCount() > 0)
				{
					bExist = true;
				}
			}
			catch(Exception e)
			{
					e.printStackTrace();
			}
			finally
			{
				closeCursor(oCursor);

				closeDB(oDatabase);
			}
		}
		return bExist;
	}

	/**
	 * 指定表数据删除	 * @param sTable 表名称
	 * @param oData 制定数据	 * @return 删除操作成功返回true，否则返回false	 */
	public static boolean deleteData(String sTable,SaveDataListener oData)
	{
		boolean bFinish = false;
		if(sTable != null)
		{
			SQLiteDatabase oDatabase = null;
			try
			{
				oDatabase = openDatabase();
				String sql = "DELETE FROM " + sTable;
				if(oData != null)
				{
					sql += " WHERE " + oData.onFilterData();
				}
				oDatabase.execSQL(sql);
				bFinish = true;

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				closeDB(oDatabase);
			}
		}
		return bFinish;
	}

	/**
	 * 删除指定表
	 * @param sTable 表名称	 */
	public static void deleteAllData(String sTable)
	{
		if(sTable != null)
		{
			SQLiteDatabase oDatabase = null;
			try
			{
				oDatabase = openDatabase();
				oDatabase.execSQL("DROP TABLE " + sTable);
			}
			catch(Exception e)
			{
					e.printStackTrace();
			}
			finally
			{
				closeDB(oDatabase);
			}
		}
	}

	/**
	 * 指定表中数据总数取得
	 * @param sTable 表名称
	 * @param oData where条件(如果没有,传null)	 * @return wCount 表数据总数	 */
	public static int getCount(String sTable,SaveDataListener oData)
	{
		int wCount = 0;
		String where = "";
		SQLiteDatabase oDatabase = null;
		Cursor oCursor = null;
		try
		{
			oDatabase = openDatabase();
			if(oData!=null){
				String str = oData.onFilterData();
				if(null != str && !str.equals(""))
				{
					where = " where " + str;
				}
			}
			oCursor = oDatabase.rawQuery("SELECT * FROM " + sTable + where, null);
			if(oCursor != null){
				wCount = oCursor.getCount();
			}
		}
		catch(Exception e)
		{
			    e.printStackTrace();
		}
		finally
		{
			closeCursor(oCursor);
			closeDB(oDatabase);
		}
		return wCount;
	}
	
	/**
	 * 满足条件的条数取得
	 * @param sql 检索的SQL文
	 * @return int  件数
	 *
	 * */
	public static int getConditionCount(String sql)
	{
		int wCount = 0;
		SQLiteDatabase oDatabase = null;
		Cursor oCursor = null;
		try
		{
			oDatabase = openDatabase();
			oCursor = oDatabase.rawQuery(sql, null);
			if(oCursor != null){
				wCount = oCursor.getCount();
			}
		}
		catch(Exception e)
		{
				e.printStackTrace();
		}
		finally
		{
			closeCursor(oCursor);
			closeDB(oDatabase);
		}
		return wCount;
	}
	
	
	/**
	 * 指定文件是否存在
	 * @param sName 文件名称	 * @return 异常发生时，返回异常内容	 */
	public static boolean isExistFile(String sName)
	{
		String sPath = s_oContext.getFilesDir().getPath() + "/" + DATA_PATH + "/" + sName;
		File oFile = new File(sPath);
		if(oFile.exists())
		{
			return true;
		}
		return false;
	}

	/**
	 * 制定文件删除	 * @param sName 文件名称	 */
	public static void deleteFile(String sName)
	{
		if( s_oContext!=null && s_oContext.getFilesDir()!=null ){
			String sPath = s_oContext.getFilesDir().getPath() + "/" + DATA_PATH + "/" + sName;
			File oFile = new File(sPath);
			if(oFile.exists())
			{
				oFile.delete();
			}
		}
	}

    private static void closeCursor(Cursor cursor){
    	if(cursor != null){
    		cursor.deactivate();
    		cursor.close();
    		cursor = null;
    	}
    }

    private static void closeDB(SQLiteDatabase oDatabase){
    	if(oDatabase != null && oDatabase.isOpen()){
    		oDatabase.close();
    	}
    }

    /**
     * 将res\raw目录中数据库文件复制
     * 系统默认文件夹中
     * @param dbsource DB的ID
     */
    public static void initDataBaseFile(int dbsource) {

		String databaseFilename = null;

		try
		{
			// 获得clientdata.db文件的绝对路径
			databaseFilename = getDataBasePath()+"/"+DATA_NAME;
			File dir = new File(getDataBasePath());
			File filename = new File(databaseFilename);

			if (!dir.exists())
			{
				dir.mkdirs();
			}

			if (!filename.exists())
			{
				// 获得封装clientdata.db文件的InputStream对象R.raw.clientdata
				createClientDBFile(dbsource,databaseFilename);
			}else
			{
				//获取Client 版本号
				VersionData[] vData = getVersionData("4");
		        int vDataLength = vData == null?0:vData.length;
		        long[] versionStrs = new long[vDataLength];

		    	for (int i = 0; i < vDataLength; i++){
		    		versionStrs[i] = Long.parseLong(vData[i].m_iVersion);
		    	}
		    	
		    	long serVersion = 20120302000000l;

		    	if (vDataLength == 0 || serVersion != versionStrs[3])
		    	{
		    		filename.delete();

		    		if(!filename.exists())
		    		{
		    			createClientDBFile(dbsource,databaseFilename);
		    		}
		    	}
			}

		} catch (Exception e)
		{
		}

	}

    /**
     * 创建ClientDB文件
     * @param dbsource DB的ID
     * @param databaseFilename 数据库的名称
     */
    public static void createClientDBFile(int dbsource,String databaseFilename)
    {
    	InputStream is  = null;
    	FileOutputStream fos = null;

    	try
    	{
    		is = s_oContext.getResources().openRawResource(dbsource);
    		fos = new FileOutputStream(databaseFilename);
    		int lenght = is.available();
    		byte[] buffer = new byte[lenght];
    		int count = 0;

    		// 开始复制clientdata.db文件
    		while ((count = is.read(buffer)) > 0)
    		{
    			fos.write(buffer, 0, count);
    		}

    	}catch(Exception e){

    	}finally
    	{
    		try
    		{
        		fos.close();
        		is.close();
    		}catch(Exception e)
    		{
    		}
    	}
    }

    /**
     * 取得版本信息
     * @param id 版本的ID
     * @return VersionData[] 版本信息的对象
     */
    public static VersionData[] getVersionData(String id) {

    	VersionData[] rlData = null;
    	String table = "version";
    	//String where = "id=" + id;
    	String where = "";
		int count = SaveManager.getCount(table,null);

		if (count > 0)
		{
			rlData = new VersionData[count];
			for (int i = 0; i < rlData.length; ++i)
			{
				rlData[i] = new VersionData();
			}

			SaveManager.readVersionData(table, where, rlData);
		}

		return rlData;
	}

	/**
	 * 版本信息内容的全部读取。
	 * @param sTable 表名称
	 * @param where 检索用的条件语句
	 * @param ozData 输出参数，为了保存读取的数据
	 */
	public static void readVersionData(String sTable, String where, SaveDataListener[] ozData)
	{
		if(sTable != null && ozData != null)
		{
			SQLiteDatabase oDatabase = null;
			Cursor oCursor = null;
			try
			{
				oDatabase = openDatabase();
				if(where.length()>0)
				{
					where = " WHERE " + where;
				}
				oCursor = oDatabase.rawQuery("SELECT * FROM " + sTable + where, null);
				if(oCursor.moveToFirst())
				{
					int wCount = oCursor.getCount();
					for(int i = 0;i < wCount;++i)
					{
						if(ozData[i] != null)
						{
							ozData[i].onReadData(oCursor);
						}
						oCursor.moveToNext();
					}
				}
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
					//e.printStackTrace();
			}
			finally
			{
				closeCursor(oCursor);

				closeDB(oDatabase);
			}
		}
	}

	/**
	 * 初始化时复制文件到data文件夹下
	 * @param dbsource 复制文件在工程下面的ID
	 * @param filename 文件名
	 */
	public static void copyFileToData(int dbsource, String filename) {
		if (isExistFile(filename)) {
			deleteFile(filename);
		} else {
			createDir(SaveManager.getDataBasePath());
		}
		createClientDBFile(dbsource, SaveManager.getDataBasePath() + "/"
				+ filename);
	}

	/**
	 * 用户自定义的增删改SQL语句
	 * @param Sqldata
	 * @return
	 */
	public static int updData(String Sqldata)
	{

		int wFinish = -1;
		Cursor oCursor = null;
		if(Sqldata != null )
		{
			SQLiteDatabase oDatabase = null;
			try
			{
				oDatabase = openDatabase();
				oDatabase.execSQL(Sqldata);

			}
			catch(Exception e)
			{
					e.printStackTrace();
			}
			finally
			{
				closeCursor(oCursor);
				closeDB(oDatabase);
			}
		}
		return wFinish;
	}
	
	
	
	

}
