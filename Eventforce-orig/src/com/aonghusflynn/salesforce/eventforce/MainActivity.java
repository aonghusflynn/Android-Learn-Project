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

import com.aonghusflynn.salesforce.eventforce.HeadlinesFragment;
import com.aonghusflynn.salesforce.eventforce.ArticleFragment;
import com.aonghusflynn.salesforce.eventforce.R;


import android.content.Intent;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends FragmentActivity implements
		HeadlinesFragment.OnHeadlineSelectedListener {
	public MatrixCursor eventCursor;
	public final static String EVENT_TITLE = "com.aonghusflynn.salesforce.eventforce.EVENT_TITLE";
	public final static String EVENT_DESCRIPTION = "com.aonghusflynn.salesforce.eventforce.EVENT_DESCRIPTION";
	public final static String LINK = "com.aonghusflynn.salesforce.eventforce.LINK";
	public final static String LAT = "com.aonghusflynn.salesforce.eventforce.LAT";
	public final static String LONGITUDE = "com.aonghusflynn.salesforce.eventforce.LONGITUDE";
/*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new EventJsonParser().execute();
	}
	
	*/
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_articles);

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            HeadlinesFragment firstFragment = new HeadlinesFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
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
			// ListAdapter adapter = new SimpleCursorAdapter(MainActivity.this,
			// R.layout.event, eventCursor, menuCols, to);

			// setListAdapter(adapter);

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

    public void onArticleSelected(int position) {
        // The user selected the headline of an article from the HeadlinesFragment

        // Capture the article fragment from the activity layout
        ArticleFragment articleFrag = (ArticleFragment)
                getSupportFragmentManager().findFragmentById(R.id.article);

        if (articleFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            articleFrag.updateArticleView(position);

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            ArticleFragment newFragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putInt(ArticleFragment.ARG_POSITION, position);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }

}
