package com.fred.platform.hive.udaf;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.*;


public class TimeInterval extends UDAF {
    static final Log LOG = LogFactory.getLog(TimeInterval.class.getName());

    public static class TimeIntervalEvaluator implements UDAFEvaluator {
    	private Column col = null;
		public static Map<String, ArrayList> intervalTable = new Hashtable();

	    public static class Column {
			String id = null;
			int startTime;
			int endTime;
			Interval columnInterval;
	    }

	    public static class Interval {
			int startTime;
			int endTime;

      		public boolean isInInterval(Column col) {
      			boolean result = false;

      			if (col.startTime <= startTime && col.endTime >= startTime) {
      				result = true;
      			} else if (col.endTime >= endTime && col.startTime <= endTime) {
					result = true;
      			} else if (startTime <= col.startTime && endTime >= col.startTime) {
					result = true;
      			} else if (endTime >= col.startTime && startTime <= col.startTime) {
					result = true;
      			} else if (startTime <= col.endTime && endTime >= col.endTime) {
					result = true;
      			} else if (endTime >= col.endTime && startTime <= col.endTime) {
					result = true;
      			}

      			return result;
      		}

      		public void add(Column col) {
      			if (col.startTime < startTime) {
      				startTime = col.startTime;
      			}
      			if (col.endTime > endTime) {
      				endTime = col.endTime;
      			}
      		}      		
	    }

	    public TimeIntervalEvaluator() {
			super();
			init();
	    }

	    public void init () {
	    	col = new Column();
	    }

	    public boolean iterate(String id, String startTime, String endTime) throws HiveException {
	    	String[] time_split;
			if (col == null)
				throw new HiveException("Item is not initialized");
			col.id = id;

			// Converting the time from string to integer format
			time_split = startTime.split(":");
			col.startTime = Integer.parseInt(time_split[0])*60 + Integer.parseInt(time_split[1]);

			time_split = endTime.split(":");
			col.endTime = Integer.parseInt(time_split[0])*60 + Integer.parseInt(time_split[1]);

			Interval interval = processColumn(col);
			col.columnInterval = interval;

			return true;
	    }

	    public Interval processColumn(Column col) {
	    	ArrayList<Interval> intervalList;
	    	boolean intervalFound = false;
	    	Interval intervalSelected = null;

	    	intervalList = intervalTable.get(col.id);
	    	if (intervalList == null) {
	    		intervalList = new ArrayList<Interval>();
	    		intervalTable.put(col.id, intervalList);
	    	}

			for (Interval interval : intervalList) {				
				if (interval.isInInterval(col)) {
					intervalSelected = interval;
					interval.add(col);
					col.columnInterval = interval;
					break;
				}
			}

			if (intervalSelected == null) {
				intervalSelected = new Interval();
				intervalSelected.startTime = col.startTime;
				intervalSelected.endTime = col.endTime;
				intervalList.add(intervalSelected);
				col.columnInterval = intervalSelected;
			}
			return intervalSelected;
	    }

	    public Column terminatePartial() {
			return col;
	    }

	    public boolean merge(Column other) {
			if(other == null)
				return true;

			Interval interval = processColumn(other);

			col.startTime = interval.startTime;
			col.endTime = interval.endTime;
			col.id = other.id;
			col.columnInterval = interval;

			mergeIntervals(other.id);

			return true; 
	    }

	    public void mergeIntervals (String id) {
	    	// Do a recursive loop until merge all needed intervals
	    	int merged = -1;
	    	Column intervalColumn = new Column();

			ArrayList<Interval> intervalList = intervalTable.get(id);
			for (int i = 0; i < intervalList.size()-1; i++) {
				Interval iInterval = intervalList.get(i);
				for (int j = i+1; j < intervalList.size(); j++) {					
					Interval jInterval = intervalList.get(j);
					intervalColumn.startTime = jInterval.startTime;
					intervalColumn.endTime = jInterval.endTime;

					if (iInterval.isInInterval(intervalColumn)) {						
						iInterval.add(intervalColumn);
						merged = j;
						break;
					}
				}
				if (merged >= 0)
					break;
			}

	    	if (merged >= 0) {
	    		intervalList.remove(merged);
	    		mergeIntervals(id);
	    	}
	    }

	    public void calculate() {
    		for (String id: intervalTable.keySet()) {
	    		mergeIntervals(id);
			}
	    }

	    public String terminate() {
			return col.id + ":" + Integer.toString(col.columnInterval.startTime) + "-" + Integer.toString(col.columnInterval.endTime);
	    }
 	}
}


