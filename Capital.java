package Dijkstra;

public class Capital {
	
	private String CapitalName;
	private double longitude;
	private double latitude;
	
	private double x;
	private double y;

	public Capital(String CapitalName, double longitude, double latitude) {
		setCapitalName(CapitalName);
		setLatitude(latitude);
		setLongitude(longitude);
	}
	
	
	public String getCapitalName() {
		return CapitalName;
	}
	public void setCapitalName(String CapitalName) {
		this.CapitalName = CapitalName;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	
}

