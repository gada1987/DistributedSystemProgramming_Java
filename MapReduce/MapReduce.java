
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

interface MapEmitter<K2,V2> {
	
	void mapEmit(K2 k2, V2 v2); 
	
}

interface ReduceEmitter<K3,V3> {
	
	void reduceEmit(K3 k3, V3 v3);
	
}

interface MapFunction<K1,V1,K2,V2> {
	
	public void map(K1 key, V1 value, MapEmitter<K2,V2> emitter);
	
}

interface ReduceFunction<K2,V2,K3,V3> {
	
	public void reduce(K2 key, List<V2> values, ReduceEmitter<K3,V3> emitter);

}

public class MapReduce<K1,V1,K2,V2,K3,V3> implements MapEmitter<K2,V2>, ReduceEmitter<K3,V3> {

	private MapFunction<K1,V1,K2,V2> mapFunction;
	private ReduceFunction<K2,V2,K3,V3> reduceFunction;

	private Map<K2,List<V2>> interResult = new TreeMap<K2,List<V2>>();
	private Map<K3, V3> finalResult = new TreeMap<K3,V3>();
	
	public synchronized void mapEmit(K2 k2, V2 v2) {
		
		if(interResult.get(k2) == null) {
			
			List<V2> nl = new ArrayList<V2>();
			nl.add(v2);
			interResult.put(k2, nl);
			
		}
		else{
			
			interResult.get(k2).add(v2);
			
		}
	}

	public synchronized void reduceEmit(K3 k3, V3 v3) {
		
		finalResult.put(k3, v3);
		
	}
	
	public Map<K3,V3> mapReduce(Map<K1,V1> input) {
		
		List<Thread> mapWorkers = new ArrayList<Thread>();
		
		for(K1 key1 : input.keySet()) {
			
			Thread t = new Thread(() -> mapFunction.map(key1, input.get(key1), MapReduce.this));
			t.start();
			mapWorkers.add(t);
			
		}
		
		for(Thread w : mapWorkers) try { w.join(); } catch(InterruptedException ie) {}
		
		System.out.println(interResult);
		List<Thread> reduceWorkers = new ArrayList<Thread>();
		
		for(K2 key2 : interResult.keySet()) {
			
			Thread t = new Thread(() -> reduceFunction.reduce(key2, interResult.get(key2), MapReduce.this));
			t.start();
			reduceWorkers.add(t);	
			
		}
		
		for(Thread w : reduceWorkers) {
			
			try {
				
				w.join(); 
				
			} catch(InterruptedException ie) {}
			
		}
		
		return finalResult;
		
	}
	
	//Constructor
	public MapReduce(MapFunction<K1,V1,K2,V2> map, ReduceFunction<K2,V2,K3,V3> reduce) {
		
		this.mapFunction = map;
		this.reduceFunction = reduce;
		
	}
	
	//Constructor for integers
	
	public static void main(String[] args) {
		
		MapFunction<Integer, Integer, Integer, Integer> map = (key, values, emitter) -> {
			
			
			StringTokenizer integerTokenizer = new StringTokenizer(values.toString(), "\t\n");
			
			while(integerTokenizer.hasMoreTokens()){
				
				String temp = integerTokenizer.nextToken();
				int tempInt = Integer.parseInt(temp);
				
				emitter.mapEmit(tempInt, 1);
				
			}
			
		};
		
		ReduceFunction<Integer, Integer, Integer, Integer> reduce = (key, values, emitter) -> {
			
			Integer sum = 0;
			
			for(Integer i : values){
				
				sum = sum + i;
				
			}
			
			emitter.reduceEmit(key, sum);
			
			//StringTokenizer integerTokenizer = new StringTokenizer(value, "\t\n");
			
		};
		
		/*
		MapFunction<String,String,String,Integer> map = 
		(key, value, emitter) -> {
			
			StringTokenizer st = new StringTokenizer(value, " \t\n");
			
			while(st.hasMoreTokens()) emitter.mapEmit(st.nextToken(), 1);				
			
		};
		
		ReduceFunction<String,Integer,String,Integer> reduce = 
		(key, values, emitter) -> {
			
			Integer sum = 0;
			
			for(Integer i : values) sum += i;
			
			emitter.reduceEmit(key, sum);
			
		};
		*/
		
		MapReduce<Integer, Integer, Integer, Integer, Integer, Integer> integerReduce = new MapReduce<>(map, reduce);
		
		Map<Integer, Integer> integerMap = new TreeMap<Integer, Integer>();
		
		integerMap.put(1, 1);
		integerMap.put(2, 123);
		integerMap.put(3, 9);
		integerMap.put(4, 6);
		
		
		
		
		
		System.out.println(integerReduce.mapReduce(integerMap));
		
		//MapReduce<String,String,String,Integer,String,Integer> mr = new MapReduce<>(map, reduce);
		//Map<String,String> input = new TreeMap<String,String>();
		
		//Integer Map
		//Map<Integer,String> integerInput = new TreeMap<Integer,String>();
		//MapReduce integerInput = new MapReduce();
		
		//input.put("first", "most people ignore most poetry");
		//input.put("second", "most poetry ignores most people");
		//input.put("third", "abbe poetry doesn't benas most people");
		//integerInput.put(4, "hej lol");
		//System.out.println(mr.mapReduce(input));
		
	}
	
}

