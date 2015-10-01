import java.util.Map;
import java.util.Random;

import uk.ac.imperial.lsds.seep.api.API;
import uk.ac.imperial.lsds.seep.api.SeepTask;
import uk.ac.imperial.lsds.seep.api.data.ITuple;
import uk.ac.imperial.lsds.seep.api.data.OTuple;
import uk.ac.imperial.lsds.seep.api.data.Schema;
import uk.ac.imperial.lsds.seep.api.data.Type;
import uk.ac.imperial.lsds.seep.api.data.Schema.SchemaBuilder;
import uk.ac.imperial.lsds.seep.api.state.stateimpl.SeepMap;


public class Source implements SeepTask {

	
	private Schema srcSchema = SchemaBuilder.getInstance().newField(Type.STRING, "type").newField(Type.STRING, "url").newField(Type.STRING, "id").build();
	private Schema snkSchema = SchemaBuilder.getInstance().newField(Type.STRING, "k").newField(Type.INT, "v").build();
	
	private boolean working = true;
	
	//KVStore data structure
	public SeepMap<String, Integer> kv = new SeepMap<String, Integer>();
	
	int id = 0;
	// time control variables
	long init = 0;
	int sec = 0;
	int c = 0;
	
	
	@Override
	public void setUp() {
		// TODO Auto-generated method stub
		//id = api.getOperatorId();
	}

	@Override
	public void processData(ITuple data, API api) {

		waitHere(2000);
		String baseUrl = "http://www.example.com/ga/";
		Random gen = new Random(5555);
		
		int aux = 0;
		long latency = 0;

		while(working){
			
			// Source, build queries
			int requestId = gen.nextInt();
			String url = baseUrl + requestId;
			// 0 -> update, 1 -> request
			int type = aux++ % 2;

			// store, process queries
			String k = null;
			int v = 0;
			// Update
			if (type == 0) {
				if (kv.containsKey(url)) {
					int newCount = (Integer) kv.get(url) + 1;
					kv.put(url, newCount);
				} else {
					kv.put(url, 0);
				}
			}
			// Get
			else if (type == 1) {
				if (kv.containsKey(url)) {
					int counter = (Integer) kv.get(url);
					k = url;
					v = counter;
				}
			}

			// sink, here I have k and v.
			k = (k != null) ? k : "";
			
			byte[] d = OTuple.create(snkSchema, new String[]{"k", "v"}, new Object[]{k, v});
			api.send(d);
			// TIME CONTROL
			c++;
			if ((System.currentTimeMillis() - init) > 1000) {
				System.out.println("[Source] e/s: " + sec + " " + c + " ");
				System.out.println("[Source] avg-lat: " + (latency / c));
				c = 0;
				latency = 0;
				sec++;
				init = System.currentTimeMillis();
			}
			
		}

	}
	
	private void waitHere(int time){
		try {
			Thread.sleep(time);
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void processDataGroup(ITuple dataBatch, API api) {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() {
		this.working = false;
	}
}
