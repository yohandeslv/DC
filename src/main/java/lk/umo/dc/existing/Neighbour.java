package lk.umo.dc.existing;

class Neighbour {
	private String ip;
	private int port;

	public Neighbour(String ip, int port){
		this.ip = ip;
		this.port = port;
	}	

	public String getIp(){
		return this.ip;
	}

	public int getPort(){
		return this.port;
	}
}
