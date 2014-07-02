package si.noemus.boatguard.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
       HttpGet httpGet = new HttpGet(params[0]);
       String text = null;
       try {
    	   HttpResponse response = httpClient.execute(httpGet, localContext);
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
