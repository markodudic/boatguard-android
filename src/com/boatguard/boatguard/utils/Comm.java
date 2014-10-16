package com.boatguard.boatguard.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;

public class Comm extends AsyncTask<String, String, String> {
	
	OnTaskCompleteListener mListener;
	
	@Override
    protected String doInBackground(String... params) {
       HttpClient httpClient = new DefaultHttpClient();
       HttpContext localContext = new BasicHttpContext();
       HttpPost httpPost = new HttpPost(params[0]);
       if (params[1] != null && params[1].equals("json")) {
    	   StringEntity postingString = null;
			try {
				postingString = new StringEntity(params[2]);
			} catch (UnsupportedEncodingException e) {
				e.getLocalizedMessage();
			}
			System.out.println("postingString="+params[2]);
			httpPost.setEntity(postingString);
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
       }
       
       String text = null;
       try {
    	   HttpResponse response = httpClient.execute(httpPost, localContext);
    	   HttpEntity entity = response.getEntity();
    	   text = getASCIIContentFromEntity(entity);
       } catch (Exception e) {
    	   return e.getLocalizedMessage();
       }
       
       return text;
	}
	
	
    public void setCallbackListener(OnTaskCompleteListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void onPostExecute(String text) {
        super.onPostExecute(text);
        if (mListener != null) mListener.onComplete(text);
    }
    
    public interface OnTaskCompleteListener {
       public void onComplete(String text);
    }
    
	protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
		InputStream in = entity.getContent();

		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n>0) {
			byte[] b = new byte[4096];
			n =  in.read(b);
	
			if (n>0) out.append(new String(b, 0, n));
		}
		return out.toString();
	}	

}
