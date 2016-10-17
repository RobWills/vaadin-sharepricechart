package com.example.sharepricechart;

public class SharePriceHist {
	
	private long Epoch;
	private double Price;
	private int Id;
	
	public SharePriceHist(int id) {
		setId(id);
	}

	public long getEpoch() {
		return Epoch;
	}

	public void setEpoch(long epoch) {
		Epoch = epoch;
	}

	public double getPrice() {
		return Price;
	}

	public void setPrice(double price) {
		Price = price;
	}

	public double getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}
}
