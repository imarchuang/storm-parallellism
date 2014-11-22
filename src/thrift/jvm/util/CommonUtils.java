package thrift.jvm.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import backtype.storm.generated.GlobalStreamId;

public class CommonUtils {
	
	/*
	 * Utility method to parse a Map<>
	 */
	  public static Long getStatValueFromMap(Map<String, Map<String, Long>> map, String statName) {
			  Long statValue = null;
			  Map<String, Long> intermediateMap = map.get(statName);
			  statValue = intermediateMap.get("default");
			  return statValue;
	  }
	
	/*
	 * Utility method to parse a Map<> as a special case for Bolts
	 */
	  public static Double get_boltStatDoubleValueFromMap(Map<String, Map<GlobalStreamId, Double>> map, String statName) {
			  Double statValue = 0.0;
			  Map<GlobalStreamId, Double> intermediateMap = map.get(statName);
			  Set<GlobalStreamId> key = intermediateMap.keySet();
			  if(key.size() > 0) {
				   Iterator<GlobalStreamId> itr = key.iterator();
				   statValue = intermediateMap.get(itr.next());
			  }
			  return statValue;
	  }

	/*
	 * Utility method for Bolts
	 */
	  public static Long get_boltStatLongValueFromMap(Map<String, Map<GlobalStreamId, Long>> map, String statName) {
			  Long statValue = null;
			  Map<GlobalStreamId, Long> intermediateMap = map.get(statName);
			  Set<GlobalStreamId> key = intermediateMap.keySet();
			  if(key.size() > 0) {
				   Iterator<GlobalStreamId> itr = key.iterator();
				   statValue = intermediateMap.get(itr.next());
			  }
			  return statValue;
	  }

}
