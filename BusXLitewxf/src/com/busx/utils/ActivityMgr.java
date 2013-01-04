package com.busx.utils;

import java.util.Stack;

import android.app.Activity;

/**
 * 
 * @author gyx
 * 
 * 功能：activity 栈管理
 *
 */
public class ActivityMgr
{
  private static Stack<Activity> activityStack;
  private static ActivityMgr   instance;

  private ActivityMgr()
  {
    if (activityStack == null)
    {
      activityStack = new Stack<Activity>();
    }
  }

  public static ActivityMgr getActivityManager()
  {
    if (instance == null)
    {
      instance = new ActivityMgr();
    }
    return instance;
  }

  public int size()
  {
	  return activityStack.size();
  }

  public void clear()
  {
	  activityStack.clear();
  }

  public void clear(Activity activity)
  {
	  if (activity != null)
	  {
	      activityStack.remove(activity);
	  }
  }
  
  public void popActivity()
  {
    Activity activity = activityStack.lastElement();
    if (activity != null)
    {
      activity.finish();
      activity = null;
    }
  }

  public void popActivity(Activity activity)
  {
    if (activity != null)
    {
      activity.finish();
      activityStack.remove(activity);
      activity = null;
    }
  }

  public Activity currentActivity()
  {
    if(activityStack.size() > 0) {
      Activity activity = activityStack.lastElement();
      return activity;
    } else {
      return null;
    }
  }

  public void pushActivity(Activity activity)
  {
    activityStack.add(activity);
  }

  @SuppressWarnings("rawtypes")
  public void popAllActivityExceptOne(Class cls)
  {
    while (true)
    {
      Activity activity = currentActivity();
      if (activity == null)
      {
        break;
      }
      if (activity.getClass().equals(cls))
      {
        break;
      }
      popActivity(activity);
    }
  }
  
  public void popAllActivity()
  {
    while (true)
    {
      Activity activity = currentActivity();
      if (activity == null)
      {
        break;
      }
      
      popActivity(activity);
    }
  }
}
