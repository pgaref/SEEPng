package uk.ac.imperial.lsds.seep.comm.protocol;

public class BootstrapCommand implements CommandType {

	private String ip;
	private int port;
	private int dataPort;
	private int controlPort;
	
	public BootstrapCommand(){}
	
	public BootstrapCommand(String ip, int port, int dataPort, int controlPort){
		this.ip = ip;
		this.port = port;
		this.dataPort = dataPort;
		this.controlPort = controlPort;
	}
	
	@Override
	public short type() {
		return MasterWorkerProtocolAPI.BOOTSTRAP.type();
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getDataPort() {
		return dataPort;
	}
	
	public int getControlPort() {
		return controlPort;
	}

}
