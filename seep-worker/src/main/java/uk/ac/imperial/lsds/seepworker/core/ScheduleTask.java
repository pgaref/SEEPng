package uk.ac.imperial.lsds.seepworker.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import uk.ac.imperial.lsds.seep.api.API;
import uk.ac.imperial.lsds.seep.api.SeepTask;
import uk.ac.imperial.lsds.seep.api.data.ITuple;
import uk.ac.imperial.lsds.seep.api.data.Schema;
import uk.ac.imperial.lsds.seep.api.operator.LogicalOperator;
import uk.ac.imperial.lsds.seep.api.operator.SeepLogicalQuery;
import uk.ac.imperial.lsds.seep.scheduler.Stage;

public class ScheduleTask implements SeepTask {

	private int stageId;
	private int euId;
	private Deque<LogicalOperator> operators;
	private Iterator<LogicalOperator> opIt;
	private List<SeepTask> tasks;
	private Iterator<SeepTask> taskIterator;
	private SchedulePipelineCollector api;
	
//	private WorkerMasterAPIImplementation masterApi;
//	private Connection masterConn;
	
	private ScheduleTask(int euId, int stageId, Deque<LogicalOperator> operators) {
		this.stageId = stageId;
		this.euId = euId;
		this.operators = operators;
		this.tasks = new ArrayList<>();
		this.opIt = operators.iterator();
		while(opIt.hasNext()) {
			tasks.add(opIt.next().getSeepTask());
		}
		this.taskIterator = tasks.iterator();
//		this.masterApi = masterApi;
//		this.masterConn = masterConn;
	}
	
	public static ScheduleTask buildTaskFor(int id, Stage s, SeepLogicalQuery slq) {
		Deque<Integer> wrappedOps = s.getWrappedOperators();
		Deque<LogicalOperator> operators = new ArrayDeque<>();
		while(! wrappedOps.isEmpty()) {
			LogicalOperator lo = slq.getOperatorWithId(wrappedOps.poll());
			operators.addLast(lo);
		}
		return new ScheduleTask(id, s.getStageId(), operators);
	}
	
	public int getStageId() {
		return stageId;
	}
	
	public int getEuId() {
		return euId;
	}
	
	@Override
	public void setUp() {
		if(taskIterator.hasNext()){
			taskIterator.next().setUp();
		}
		else{
			taskIterator = tasks.iterator();
		}
	}

	@Override
	public void processData(ITuple data, API api) {
		while(taskIterator.hasNext()) {
			SeepTask next = taskIterator.next();
			next.processData(data, api);
			byte[] o = ((SchedulePipelineCollector)api).collect();
			
			LogicalOperator nextOp = opIt.next();
			Schema schema = nextOp.downstreamConnections().get(0).getSchema(); // 0 cause there's only 1
			data = new ITuple(schema);
			data.setData(o);
		}
		taskIterator = tasks.iterator();
	}
	
	public boolean hasMoreTasks() {
		return taskIterator.hasNext();
	}

	@Override
	public void processDataGroup(ITuple dataBatch, API api) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		if(taskIterator.hasNext()){
			taskIterator.next().close();
		}
		else{
			taskIterator = tasks.iterator();
		}
	}
	
//	public void configureScheduleTaskLazily(List<OutputAdapter> outputAdapters) {
//		this.api = new SchedulePipelineCollector(operators, outputAdapters);
//	}
//	
//	public void triggerProcessingPipeline(ITuple iTuple) {
//		api.processData(iTuple);
//		api.rewindPipeline();
//	}

//	public void notifyStatusOk(Map<Integer, Set<DataReference>> producedOutput) {
//		masterApi.scheduleTaskStatus(masterConn, stageId, euId, StageStatusCommand.Status.OK, producedOutput);
//	}

}
