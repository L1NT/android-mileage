package com.evancharlton.mileage.dao;

import java.util.ArrayList;

import android.database.Cursor;

public class FillupSeries extends ArrayList<Fillup> {
	private static final long serialVersionUID = 5304523564485608182L;

	public FillupSeries(Fillup... fillups) {
		final int length = fillups.length;
		Fillup previous = null;
		Fillup current = null;
		for (int i = 0; i < length; i++) {
			current = fillups[i];
			if (previous != null) {
				previous.setNext(current);
			}
			current.setPrevious(previous);
			super.add(current);
			previous = current;
		}
	}

	@Override
	public boolean add(Fillup fillup) {
		if (size() > 0) {
			Fillup last = get(size() - 1);
			last.setNext(fillup);
			fillup.setPrevious(last);
		}
		super.add(fillup);
		return true;
	}

	public double getTotalDistance() {
		final int size = size();
		if (size >= 2) {
			// TODO: will this work for partials? check the edge case here
			return get(size - 1).getOdometer() - get(0).getOdometer();
		}
		return 0D;
	}

	public double getTotalVolume() {
		return getEconomyVolume() + get(0).getVolume();
	}

	/**
	 * Gets the sum of all the volume values except for the first one, since
	 * it's not used in the calculation of fuel economy.
	 * 
	 * @return
	 */
	public double getEconomyVolume() {
		final int size = size();
		double total = 0D;
		for (int i = 1; i < size; i++) {
			total += get(i).getVolume();
		}
		return total;
	}

	/**
	 * In order for this to work, it's expected that the fillups are sorted in
	 * ascending order by odometer.
	 * 
	 * @param cursor
	 * @return
	 */
	public static ArrayList<FillupSeries> load(Cursor cursor) {
		ArrayList<FillupSeries> output = new ArrayList<FillupSeries>();

		FillupSeries series = new FillupSeries();
		while (cursor.moveToNext()) {
			Fillup fillup = new Fillup(cursor);
			if (fillup.isRestart()) {
				output.add(series);
				series = new FillupSeries();
			}
			series.add(fillup);
		}
		output.add(series);

		return output;
	}
}