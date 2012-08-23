package com.aonghusflynn.salesforce.eventforce;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends HeadlinesFragment   {
	public MatrixCursor eventCursor;
	public final static String EVENT_TITLE = "com.aonghusflynn.salesforce.eventforce.EVENT_TITLE";
	public final static String EVENT_DESCRIPTION = "com.aonghusflynn.salesforce.eventforce.EVENT_DESCRIPTION";
	public final static String LINK = "com.aonghusflynn.salesforce.eventforce.LINK";
	public final static String LAT = "com.aonghusflynn.salesforce.eventforce.LAT";
	public final static String LONGITUDE = "com.aonghusflynn.salesforce.eventforce.LONGITUDE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new EventJsonParser().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		eventCursor.moveToPosition(position);

		Intent intent = new Intent(MainActivity.this, Detail.class);
		intent.putExtra(EVENT_TITLE, eventCursor.getString(1));
		intent.putExtra(EVENT_DESCRIPTION, eventCursor.getString(2));
		startActivity(intent);

	}

	public abstract class BaseEventClass extends
			AsyncTask<String, Integer, MatrixCursor> {
		String urlString = "http://www.salesforce.com/uk/assets/js/events.json";

		protected InputStream getData() throws Exception {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(new URI(urlString));

			HttpResponse response = client.execute(request);
			return response.getEntity().getContent();
		}

		@Override
		protected void onPostExecute(MatrixCursor eventCursor) {
			String[] menuCols = new String[] { "title", "description", "link" };
			int[] to = new int[] { R.id.text1, R.id.textField2, R.id.textField3 };
			// TODO Create SimpleCursorAdapter here
			ListAdapter adapter = new SimpleCursorAdapter(MainActivity.this,
					R.layout.event, eventCursor, menuCols, to);

			setListAdapter(adapter);

		}

	}

	private class EventJsonParser extends BaseEventClass {
		public EventJsonParser() {
			super();
		}

		@Override
		protected MatrixCursor doInBackground(String... strings) {
			String[] COLUMN_NAMES = { "_id", "title", "description", "lat",
					"longitude", "id", "link", "imgUrl" };
			Event[] events = new Event[0];

			eventCursor = new MatrixCursor(COLUMN_NAMES);
			startManagingCursor(eventCursor);
			try {
				StringBuilder json = new StringBuilder();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(getData()));
				String line = reader.readLine();
				while (line != null) {
					json.append(line);
					line = reader.readLine();
				}
				JSONObject jsonObj = new JSONObject(json.toString());

				JSONArray eventArray = jsonObj.getJSONArray("events");

				events = new Event[eventArray.length()];
				for (int i = 0; i < eventArray.length(); i++) {
					JSONObject object = eventArray.getJSONObject(i);
					String title = object.getString("Event_Display_Name__c");
					String description = object
							.getString("Event_Description__c");
					String link = object.getString("Event_Link__c");
					String imgUrl = object.getString("Venue_Image_Location__c");
					String lat = object.getString("venue_lat");
					String longitude = object.getString("venue_lon");
					String id = object.getString("id");
					eventCursor.addRow(new Object[] { i, title, description,
							lat, longitude, id, link, imgUrl });
					Log.d("Main activity",
							object.getString("Event_Display_Name__c"));
					events[i] = new Event(title, description, lat, longitude,
							id, link, imgUrl);
				}

			} catch (Exception e) {
				Log.e("Eventforce", "Exception getting JSON data", e);
			}
			return eventCursor;
		}
	}

}
