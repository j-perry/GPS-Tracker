package com.team2.dash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/*
 * This class has sends html off to the server specified in ServerURL to have something done with it
 * An example call to this could be:
 * JSONObject j1 = sc.ConnectAndSendHTML(null, "GetTime.php?TimeZone=England, false); 
 * This should theortically support CodeIgniter, but it's untested currently
 */	

public class ServerConnector extends AsyncTask<String, Integer, String>
{
	//Current website URL
	private String mProcessMessage = "processing ...";	
	private static final int SOCKET_TIMEOUT = 5000;	
	private static final int CONN_TIMEOUT = 3000;	
	private ProgressDialog pDlg = null;
	private static String ServerURL;
	private Context mContext = null;
	private boolean mUseCodeIgniter;	
	public String responseString;	
	private String[][] mVars;	
	
	/*
	 * This is the method that we will call to send, then receive data.
	 * PHP files should always return some form of a response, even if it's just json_decode(Array("success" => false));
	 *
	 *@vars = Data to be POSTed to the server (in SimplePHP) or for the URL in CodeIgniter (only uses vars[i][1])
	 *@webPage = Page for data to be sent to
	 *@useCodeIgniter = Are we using CodeIgniter or Simple PHP?
	 */	
	public ServerConnector (String[][] vars, boolean useCodeIgniter, Context thisContext, String processMessage)
	{			
		mVars = vars;
		mUseCodeIgniter = useCodeIgniter;
		mContext = thisContext;			
		mProcessMessage = processMessage; 				
		if(useCodeIgniter == true)
		{
			ServerURL = ((Context) thisContext).getString(R.string.webServiceEndPoint);
		}
		else
		{
			ServerURL = ((Context) thisContext).getString(R.string.webServiceEndPointNoFile);
		}
	}	
	
	@Override
	protected String doInBackground(String... params) 
	{
		String mWebPage = params[0];
		
		if(mUseCodeIgniter == true) 
		{
			responseString = ConnectAndSendUsingCodeIgniter(mVars, mWebPage);			
		}
		//Use PHP's native POST / GET rather than a framework
		else 
		{			
			responseString = ConnectAndSendSimplePHP(mVars, mWebPage);		
		}	
				
		
		// cleaning server additions
		int open=0, close=0;
		for( int i=0; i<responseString.length(); i++) {

			if( responseString.charAt(i) == '{')
				open++;
			if( responseString.charAt(i) == '}' )
				close++;
			if( (open == close) && (i>0) ) {
				responseString = responseString.substring(0, i+1);
				break;
			}
		}
		
		return responseString;
	}	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		showProgressDialog();
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		pDlg.dismiss();
	}	
	
	/*
	 * This will build a codeigniter url (as it takes GET variables differently to simple PHP) and then call that URL
	 * PHP files should always return some form of a response, even if it's just json_decode(Array("success" => false));
	 *
	 *@vars = Data to sent via URL in CodeIgniter (only uses vars[i][1])
	 *@webPage = Page for data to be sent to
	 */
	private String ConnectAndSendUsingCodeIgniter(String[][] vars, String webPage)
	{
		String temp = null;	
		HttpClient httpclient = new DefaultHttpClient();
    	String newURL = "";
		
    	//Lets build our code igniter URL here
    	if (vars != null && vars.length > 0)
		{    	       		
    	    for(String[] singleString : vars)
    	    {
    	    	if(singleString[0] == null || singleString[0].isEmpty())
    	    	{
    	    		newURL = newURL + "/" + singleString[1];
    	    	}
    	    }	    	    	    	        	    
		}    	
		
    	try 
    	{    		    		
			HttpParams httpp = new BasicHttpParams();			
			HttpConnectionParams.setConnectionTimeout(httpp,CONN_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpp, SOCKET_TIMEOUT);
			HttpPost httppost = new HttpPost(ServerURL + webPage + newURL);			
			
			if (vars != null && vars.length > 0)
			{
	    	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(vars.length);
	    	    for(String[] singleString : vars)
	    	    {
	    	    	if(singleString[0] != null && !singleString[0].isEmpty())
	    	    	{
	    	    		nameValuePairs.add(new BasicNameValuePair(singleString[0], singleString[1]));
	    	    	}
	    	    }	    	 
	    	    if(nameValuePairs.size() > 0)
	    	    {
	    	    	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    	    }
			}			
			
    	    HttpResponse response = httpclient.execute(httppost);
    	    HttpEntity entity = response.getEntity();    	    
    	    temp = EntityUtils.toString(entity);	
    	    
    	    if(entity != null)
    	    {	
    	    	return temp;
    	    } 
    	    else 
    	    {
    	    	return null;    	    	
    	    }    	
    	} 
    	catch (ClientProtocolException e) 
    	{ 
    		e.printStackTrace(); 
    		Log.e("Error", "SC ClientProtocol " + e.getMessage()); 
    		return null; 
    	}    	
    	catch (IOException e) 
    	{ 
    		e.printStackTrace(); 
    		Log.e("Error", "SC IOException " + e.getMessage());     		
    		return null;
    	} 
    	catch (Exception e) 
    	{ 
    		e.printStackTrace(); 
    		Log.e("Error", "SC Exception " + e.getMessage());        		
    		return null;
    	}  
	}
	
	/*
	 * This will build a FORM for POSTing Data and then call the URL
	 * PHP files should always return some form of a response, even if it's just json_decode(Array("success" => false));
	 *
	 *@vars = Data to sent via URL in CodeIgniter (only uses vars[i][1])
	 *@webPage = Page for data to be sent to
	 */	
	private String ConnectAndSendSimplePHP(String[][] vars, String webPage)
	{	
		String temp = null;	   	
		
    	try 
    	{
        	HttpClient httpclient = new DefaultHttpClient();
    		HttpParams httpp = new BasicHttpParams();			
    		HttpConnectionParams.setConnectionTimeout(httpp,CONN_TIMEOUT);
    		HttpConnectionParams.setSoTimeout(httpp, SOCKET_TIMEOUT);    	
        	HttpPost httppost = new HttpPost(ServerURL + webPage);
        	
    		if (vars != null && vars.length > 0)
    		{
	    	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(vars.length);
	    	    for(String[] singleString : vars)
	    	    {
	    	    	nameValuePairs.add(new BasicNameValuePair(singleString[0], singleString[1]));		    	    
	    	    }	    	    	    	    
	    	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    		}
    	 
    	    HttpResponse response = httpclient.execute(httppost);    	        	    
    	    HttpEntity entity = response.getEntity();    	    
    	    temp = EntityUtils.toString(entity);

    	    if(entity != null)
    	    {	  	    	
    	    	return temp;
    	    } 
    	    else 
    	    {    	    	
    	    	return null;    	    	
    	    }
    	
    	} 
    	catch (ClientProtocolException e) 
    	{ 
    		e.printStackTrace(); 
    		Log.e("Error", "SC ClientProtocol " + e.getMessage()); 
    		return null;
    	}    	
    	catch (IOException e) 
    	{ 
    		e.printStackTrace(); 
    		Log.e("Error", "SC IOException " + e.getMessage());     		
    		return null;
    	} 
    	catch (Exception e) 
    	{ 
    		e.printStackTrace(); 
    		Log.e("Error", "SC Exception " + e.getMessage());        		
    		return null;
    	}     	
	}	
	
	private void showProgressDialog(){
		pDlg = new ProgressDialog(mContext);
		pDlg.setMessage(mProcessMessage);
		pDlg.setProgressDrawable(WallpaperManager.getInstance(mContext).getDrawable());
		pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDlg.setCancelable(false);
		pDlg.show();
	}	
	
	public static JSONObject ConvertStringToObject(String response)
	{
		try
		{			
	    	JSONObject SimpleResults = new JSONObject(response);
    	    return SimpleResults;				
		}
		catch (JSONException e)
		{
    		e.printStackTrace(); 
    		Log.v("Error", "SC JSONException " + e.getMessage());   
		}
		return null;
	}	
}
