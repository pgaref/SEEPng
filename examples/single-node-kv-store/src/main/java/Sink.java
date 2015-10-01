import uk.ac.imperial.lsds.seep.api.API;
import uk.ac.imperial.lsds.seep.api.SeepTask;
import uk.ac.imperial.lsds.seep.api.data.ITuple;


public class Sink implements SeepTask {

	
	int id = 0;
	// time control variables
	long init = 0;
	int sec = 0;
	int c = 0;
	long latency = 0;
	
	
	@Override
	public void setUp() {
		// TODO Auto-generated method stub
	}

	@Override
	public void processData(ITuple data, API api) {
		
		String k = data.getString("k");
		int v = data.getInt("v");
		
		//TODO: Check if the functionality existis in new SEEP
		/*
		long ts = arg0.getPayload().instrumentation_ts;
		long now = System.currentTimeMillis();
		latency = latency + (now-ts);
		*/
		
		// TIME CONTROL
		c++;
		if((System.currentTimeMillis() - init) > 1000){
			System.out.println("[Sink] e/s: "+sec+" "+c+" ");
			//System.out.println("[Sink] avg-lat: "+(latency/c));
			c = 0;
			//latency = 0;
			sec++;
			init = System.currentTimeMillis();
		}
		
	}

	@Override
	public void processDataGroup(ITuple dataBatch, API api) {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

}
