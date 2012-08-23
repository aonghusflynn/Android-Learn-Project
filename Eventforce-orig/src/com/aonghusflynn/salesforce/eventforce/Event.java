package com.aonghusflynn.salesforce.eventforce;

import java.util.List;

/* TODO 
 * Following the example on
 * http://www.ibm.com/developerworks/xml/library/x-dataAndroid/#l7
 * 
 * 
 * */
public class Event {

	private final String title = "";
	private final String description = "";
	private final String imgUrl = "";
	private final String link = "";
	private final String id = "";
	private final double lat = 0.0;
	private final double longitude = 0.0;
	static List events ;
	

	public Event(String title, String description, String lat,
			String longitude, String id, String link, String imgUrl) {
		title = title;
		description = description;
		imgUrl = imgUrl;
		longitude = longitude;
		lat = lat;
		id = id;
	}

	protected void setTitle(String title) {
		title = title;
	}

	protected void setDescription(String description) {
		description = description;
	}

	protected void setImgUrl(String imgUrl) {
		imgUrl = imgUrl;
	}

	protected String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	protected String getImgUrl() {
		return imgUrl;
	}

}
