
import java.util.List;

//import models.User;
import uk.ac.imperial.lsds.seep.api.API;
import uk.ac.imperial.lsds.seep.api.SeepTask;
import uk.ac.imperial.lsds.seep.api.data.ITuple;
import uk.ac.imperial.lsds.seep.api.data.OTuple;
import uk.ac.imperial.lsds.seep.api.data.Schema;
import uk.ac.imperial.lsds.seep.api.data.Schema.SchemaBuilder;
import uk.ac.imperial.lsds.seep.api.data.Type;
import utils.CassandraQueryController;


public class Source implements SeepTask {

	private Schema schema = SchemaBuilder.getInstance().newField(Type.INT, "userId").newField(Type.LONG, "rating").newField(Type.STRING, "song").build();
	private boolean working = true;
//	private List<User> allusers;
	
	@Override
	public void setUp() {
		// TODO Auto-generated method stub
		//allusers = CassandraQueryController.listAllUsers();
	}

	
	
	@Override
	public void processData(ITuple data, API api) {
		int userId = 0;
		long ts = 0;
		
		while(working){
			//ensuring index sanity with modulo
			byte[] d = OTuple.create(schema, new String[]{"userId", "rating", "song"}, new Object[]{userId, ts, "pg-test"});//allusers.get(userId % allusers.size()).getUsername()});
			api.send(d);
			
			userId++;
			ts++;
			
			waitHere(1000);
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
	
//	public static void main(String[] args) {
//		System.out.println("OK");
//		allusers = CassandraQueryController.listAllUsers();
//	}
}
