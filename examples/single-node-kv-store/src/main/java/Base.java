import uk.ac.imperial.lsds.seep.api.LogicalOperator;
import uk.ac.imperial.lsds.seep.api.LogicalSeepQuery;
import uk.ac.imperial.lsds.seep.api.QueryComposer;
import uk.ac.imperial.lsds.seep.api.data.Schema;
import uk.ac.imperial.lsds.seep.api.data.Schema.SchemaBuilder;
import uk.ac.imperial.lsds.seep.api.data.Type;



//public class Base implements QueryComposer{
//
//	public QueryPlan compose() {
//		/** Declare operators **/
//		
//		// Declare Source
//		List<String> srcFields = new ArrayList<String>();
//		srcFields.add("type");
//		srcFields.add("url");
//		srcFields.add("id");
//		Connectable src = QueryBuilder.newStatelessSource(new Source(), -1, srcFields);
//		
//		List<String> snkFields = new ArrayList<String>();
//		snkFields.add("k");
//		snkFields.add("v");
//		Connectable snk = QueryBuilder.newStatelessSink(new Sink(), -2, snkFields);
//
//		src.connectTo(snk, true, 0);
//		
//		return QueryBuilder.build();
//	}
//}


public class Base implements QueryComposer {

	@Override
	public LogicalSeepQuery compose() {
		
		Schema srcSchema = SchemaBuilder.getInstance().newField(Type.STRING, "type")
													.newField(Type.STRING, "url")
													.newField(Type.STRING, "id").build();
		
		Schema snkSchema = SchemaBuilder.getInstance().newField(Type.STRING, "k")
													.newField(Type.INT, "v").build();
		
		LogicalOperator src = queryAPI.newStatelessSource(new Source(), 0);
		LogicalOperator snk = queryAPI.newStatelessSink(new Sink(), 1);
		
		src.connectTo(snk, 0, snkSchema);
		//processor.connectTo(snk, 0, snkFields);
		
		return queryAPI.build();
	}

}
