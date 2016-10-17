package com.example.sharepricechart;

import java.util.GregorianCalendar;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.ContainerDataSeries;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("sharepricechart")
public class SharepricechartUI extends UI {

	static final String OpenCurlyBracket = "%7B";
	static final String CloseCurlyBracket = "%7D";
	static final String OpenSquareBracket = "%5B";
	static final String CloseSquareBracket = "%5D";
	static final String Colon = "%3A";
	static final String Comma = "%2C";
	static final String Quote = "%22";

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SharepricechartUI.class, widgetset = "com.example.sharepricechart.widgetset.SharepricechartWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		
		String Symbol = "AMZN";
		
		String Resource = "http://dev.markitondemand.com/MODApis/Api/v2/InteractiveChart/json?parameters=" + OpenCurlyBracket;
		Resource = Resource + Quote + "Normalized" + Quote + Colon + "false" + Comma;
		Resource = Resource + Quote + "NumberOfDays" + Quote + Colon + "365" + Comma;
		Resource = Resource + Quote + "DataPeriod" + Quote + Colon + Quote + "Day" + Quote + Comma;
		Resource = Resource + Quote + "Elements" + Quote + Colon + OpenSquareBracket + OpenCurlyBracket + Quote + "Symbol" + Quote + Colon + Quote + Symbol + Quote + Comma;
		Resource = Resource + Quote + "Type" + Quote + Colon + Quote + "price" + Quote + Comma;
		Resource = Resource + Quote + "Params" + Quote + Colon + OpenSquareBracket + Quote + "c" + Quote;
		Resource = Resource + CloseSquareBracket + CloseCurlyBracket + CloseSquareBracket + CloseCurlyBracket;
		
		//System.out.println(Resource);
		 
		Client c = Client.create();
		WebResource resource = c.resource(Resource);
		
		Symbol = "";
		String Currency = "";
		JSONArray Values = null;
		JSONArray Dates = null;
		
		String response = null;
		try{
			response = resource.get(String.class);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		JSONObject JSONData = null;
		try {
			JSONData = new JSONObject(response);
			//System.out.println(JSONData.toString());
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			Dates = JSONData.getJSONArray("Dates");
			//System.out.println("Dates (" + Dates.length() + "): " + Dates.toString());
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
/*
		JSONArray Positions = null;
		try {
			Positions = JSONData.getJSONArray("Positions");
			System.out.println("Positions (" + Positions.length() + "): " + Positions.toString());
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
*/		
		JSONObject Elements;
		try {
			Elements = JSONData.getJSONArray("Elements").getJSONObject(0);
			//System.out.println("Elements: " + Elements.toString());
/*			
			String Type;
			try {
				Type = Elements.getString("Type");
				System.out.println("Type: " + Type);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}
*/			
			try {
				Symbol = Elements.getString("Symbol");
				//System.out.println("Symbol: " + Symbol);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}

			try {
				Currency = Elements.getString("Currency");
				//System.out.println("Currency: " + Currency);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}
			
			try {
				Values = Elements.getJSONObject("DataSeries").getJSONObject("close").getJSONArray("values");
				//System.out.println("Values (" + Values.length() + ") : " + Values.toString());
			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}

		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}

		// OK now we have the data in the format we want it, time to create a chart
	
		Chart chart = new Chart(ChartType.LINE);
		chart.setWidth("100%");
		chart.setHeight("400px");
		
		// Modify the default configuration a bit
		
		Configuration conf = chart.getConfiguration();
		conf.setTitle("Share Price for " + Symbol);
		conf.getLegend().setEnabled(false); // Disable legend

		// Store the data supplied by the RESTful web service in a bean item container
		
		BeanItemContainer<SharePriceHist> SharePriceHist = new BeanItemContainer<SharePriceHist>(SharePriceHist.class);
		
		SharePriceHist ShareBean;
		String DateStr = null;
		GregorianCalendar cal = new GregorianCalendar();
		long EpochVal;
		
		try {
			String FirstDate = Dates.getString(0);
			FirstDate = FirstDate.substring(8, 10) + "/" + FirstDate.substring(5, 7) + "/" + FirstDate.substring(0, 4);
			String LastDate = Dates.getString(Values.length() - 1);
			LastDate = LastDate.substring(8, 10) + "/" + LastDate.substring(5, 7) + "/" + LastDate.substring(0, 4);
			conf.setSubTitle("between " + FirstDate + " and " + LastDate);
		} catch (JSONException e1) {
			System.out.println(e1.getMessage());
		}

		for (int ele = 0; ele < Values.length(); ele++) {
			try {
				DateStr = Dates.getString(ele);
				cal.set(new Integer(DateStr.substring(0, 4)), (new Integer(DateStr.substring(5, 7))) - 1, new Integer(DateStr.substring(8, 10)));
				EpochVal = cal.getTimeInMillis();
				
				ShareBean = new SharePriceHist(ele);
				ShareBean.setEpoch(EpochVal);
				ShareBean.setPrice(Values.getDouble(ele));
				SharePriceHist.addBean(ShareBean);
				
				//System.out.println("" + ShareBean.getId() + " : " + DateStr + " : " + ShareBean.getEpoch() + " : " + ShareBean.getPrice());
				
				ShareBean = null;

			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}
		}
		
		ContainerDataSeries price = new ContainerDataSeries(SharePriceHist);
		price.setName("Price");
		price.setXPropertyId("epoch");
		price.setYPropertyId("price");
		 
		conf.addSeries(price);
		XAxis xaxis = conf.getxAxis();
		xaxis.setTitle("Date Range");
		xaxis.setType(AxisType.DATETIME);

		// Set the Y axis title
		
		YAxis yaxis = conf.getyAxis();
		yaxis.setTitle("Price / " + Currency);
		
		layout.addComponent(chart);
		
		Label Message = new Label();
		Message.setCaptionAsHtml(true);
		Message.setCaption("<br/>Data supplied by Markit On Demand. Commercial use <a href=\"http://dev.markitondemand.com/MODApis/\">prohibited</a>.");
		layout.addComponent(Message);
	}
}