package com.acdroid.giphyapi;

import com.acdroid.giphyapi.data.GiphyInfo;
import com.acdroid.giphyapi.data.GiphyInfo.GifInfo;
import com.acdroid.giphyapi.utils.FetchInfoTask;

import android.util.Log;

import java.util.ArrayList;

/**
 * 
 * @author Marcos Trujillo
 *
 */
public class Giphy {
	private static final String TAG = "Giphy";
	private static final String BETA_KEY = "dc6zaTOxFJmzC";
	private static final String HOST = "http://api.giphy.com/";
	private static final String RECENT_END_POINT = "/v1/gifs/recent";
	
	private static final String APIKEY_KEY = "?api_key=";
	private static final String TAG_KEY = "&tag=";
	private static final String LIMIT_KEY = "&limit=";
	
	
	
	private static String mApiKey;
	
	/**
	 * Inits the Giphy library
	 * @param apiKey 
	 */
	public static void init(String apiKey){
		if (apiKey == null || "".equals(apiKey))
			throw new NullPointerException("Invalid ApiKey");
		
		mApiKey  = apiKey;
	}
	
	/**
	 * Inits the Giphy library with a betaapikey
	 */
	public static void initBeta(){
		mApiKey = BETA_KEY;
	}
	
	/**
	 * Fetch most recent gifs, optionally limited by tag. Returns 10 results. 
	 * Additional GIF size data can be looked up by using the get GIF by id.
	 * 
	 * @param tag (optional) limits recent GIFs to a specific tag. null or empty to disable
	 * @param limit (optional) limits the number of results returned. -1 to disable
	 * @param callback to get the result
	 */
	public static void getRecentGifs(String tag, int limit, GiphyCallback callback){
		
		StringBuilder url = new StringBuilder(HOST);
		url.append(RECENT_END_POINT);
		url.append(APIKEY_KEY);
		url.append(mApiKey);
		if (tag != null && !"".equals(tag)){
			url.append(TAG_KEY);
			url.append(tag);
		}
		if (limit != -1){
			url.append(LIMIT_KEY);
			url.append(limit);
		}
	
		new FetchInfo(url.toString(), callback).executeFetch();
	}
	
	
	/**
	 * Fetch Info from the server, parse it and return it
	 * 
	 * 
	 * @author Marcos Trujillo
	 *
	 */
	private static class FetchInfo extends FetchInfoTask<GiphyInfo>{
	    private static final int TRAFFIC_STATS_TAG = 0xBBBB; //For DDMS debug
		private final GiphyCallback mCallback;
				
		private FetchInfo(String url, GiphyCallback callback){
			super(url);
            enableJsonParsing(GiphyInfo.class);
            setTrafficStatsTag(TRAFFIC_STATS_TAG);
			mCallback = callback;
		}

		@Override
		protected void onResult(GiphyInfo result) {
		    if (mCallback == null){
		        Log.w(TAG, "Empty callback, imposible to return the GIFs");
		        return;
		    }
		    
		    if (result == null){
		        Log.e(TAG, "Empty response");
		        mCallback.onResult(new ArrayList<GifInfo>());
		    }
		    
		    if(result.meta.status != 200){
		        Log.e(TAG, "Empty Response. Status:" + result.meta.status + " message: " + result.meta.message);
                mCallback.onResult(new ArrayList<GifInfo>());
		    }
		    
		    if (result.meta.code != null){
                Log.e(TAG, "Empty Response. Code:" + result.meta.code + " message: " + result.meta.errorMessage);
                mCallback.onResult(new ArrayList<GifInfo>());
            }
		    
		    mCallback.onResult(result.gifList);			
		}

		@Override
		protected void onError(int type, String message) {
			Log.e(TAG, "Error fetching GIFs. Message: " + message);			
			if (mCallback != null)
				mCallback.onResult(new ArrayList<GifInfo>());
		}
		
	}

	
	/**
	 * Callback to get the results of the petitions
	 * @author marcos
	 *
	 */
	public static interface GiphyCallback{		
		public void onResult(ArrayList<GifInfo> result);
	}
}