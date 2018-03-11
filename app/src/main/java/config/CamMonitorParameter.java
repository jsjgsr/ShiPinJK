package config;

public class CamMonitorParameter {

	public final static String FIELD_ID = "ID";
	public final static String FIELD_UID = "UID";
	public final static String FIELD_NAME = "NAME";
	public final static String FIELD_IP = "IP";
	public final static String FIELD_PORT = "PORT";
	public final static String FIELD_USERNAME = "USERNAME";
	public final static String FIELD_PASSWORD = "PASSWORD";
	public final static String FIELD_LOCALDIR = "LOCAL_DOR";
	public final static String FIELD_CONNECTTYPE= "CONNECT_TYPE";

	private int time_out = 2000;
	private int id;
	private int uid;
	private String name;
	private String ip;
	private int port;
	private String port1;
	private String password;
	private String local_dir;
	private int connectType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPort1() {
		return port1;
	}

	public void setPort1(String port1) {
		this.port1 = port1;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLocal_dir() {
		return local_dir;
	}

	public void setLocal_dir(String local_dir) {
		this.local_dir = local_dir;
	}


	public int getTime_out() {
		return time_out;
	}

	public void setTime_out(int time_out) {
		this.time_out = time_out;
	}

	public int getConnectType() {
		return connectType;
	}

	public void setConnectType(int connectType) {
		this.connectType = connectType;
	}
	
	
	
}
