package edu.clemson.tanapasafari.model;

import org.json.JSONException;
import org.json.JSONObject;

import edu.clemson.tanapasafari.R;
import edu.clemson.tanapasafari.constants.Constants;
import edu.clemson.tanapasafari.webservice.Response;
import edu.clemson.tanapasafari.webservice.ResponseHandler;
import edu.clemson.tanapasafari.webservice.WebServiceClientHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class User {
	
	private static Integer id;

	public static void getId(Context context, final UserIdListener listener) {
		if (id == null || id < 0) {
			Log.d(Constants.LOGGING_TAG, "No user id set, checking the shared preferences.");
			// First look in local preferences file.
			final SharedPreferences prefs = context.getSharedPreferences("userpreferences", Context.MODE_PRIVATE);
			int userId = prefs.getInt("user_id", -1);
			if (userId != -1) {
				Log.d(Constants.LOGGING_TAG, "User id found in shared preferences.");
				// Since we found a user id in the local preferences, let's update the static id to the this value for future lookups.
				id = userId;
				// Now we can call the user id listener back with the id.
				listener.onUserId(id);
			} else {
				Log.d(Constants.LOGGING_TAG, "No user id found in shared preferences, calling the webservice for a new one.");
				// If there isn't a user id in the local preferences, retrieve a new one from the server.
				String url = context.getString(R.string.base_url) + "/user.php";
				WebServiceClientHelper.doGet(url, new ResponseHandler() {

					@Override
					public void onResponse(Response r) {
						try {
							Log.d(Constants.LOGGING_TAG, "Response received from the webservice: " + r.getData());
							// Parse out the user id returned from the server.
							JSONObject results = new JSONObject(r.getData());
							JSONObject jsonObject = results.getJSONArray("results").getJSONObject(0);
							int userId = jsonObject.getInt("id");
							// Save off this new user id to shared preferences.
							prefs.edit().putInt("user_id", userId);
							// Since we found a user id in the local preferences, let's update the static id to the this value for future lookups.
							id = userId;
							// Call the listener back with the user id.
							listener.onUserId(userId);
						} catch (JSONException e) {
							Log.e(Constants.LOGGING_TAG, "Error occurred while handling user id response from webservice", e);
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				});
			}			
		} else {
			Log.d(Constants.LOGGING_TAG, "User ID was already set, no use looking elsewhere.");
			listener.onUserId(id);
		}
	}

}
