import uk.ac.imperial.lsds.seep.api.API;
import uk.ac.imperial.lsds.seep.api.StatefulSeepTask;
import uk.ac.imperial.lsds.seep.api.data.ITuple;
import uk.ac.imperial.lsds.seep.api.data.OTuple;
import uk.ac.imperial.lsds.seep.api.data.Schema;
import uk.ac.imperial.lsds.seep.api.data.Schema.SchemaBuilder;
import uk.ac.imperial.lsds.seep.api.data.Type;
import uk.ac.imperial.lsds.seep.api.state.stateimpl.SeepMap;


public class Processor implements StatefulSeepTask<SeepMap<Integer, String>> {

	private SeepMap<Integer, String> map;
	private Schema schema = SchemaBuilder.getInstance().newField(Type.INT, "userId").newField(Type.LONG, "rating").newField(Type.STRING, "song").build();
	
	@Override
	public void setState(SeepMap<Integer, String> map) {
		this.map = map;
	}
	
	@Override
	public void setUp() {
		// TODO Auto-generated method stub
	}

	@Override
	public void processData(ITuple data, API api) {
		int userId = data.getInt("userId");
		long ts = data.getLong("rating");
		String text = data.getString("song");
		text = text + "_processed";
		userId = userId + userId;
		map.put(userId, text);
		ts = ts - 1;
		
		byte[] processedData = OTuple.create(schema, new String[]{"userId", "rating", "song"},  new Object[]{userId, ts, text});
		api.send(processedData);
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