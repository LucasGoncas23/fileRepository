//package project;

public class User {
	
	String username;
	String password;
	String phone;
	String address;
	String expiration;
	String college;
	
	public User(String username, String pass) {
		super();
		this.username = username;
		this.password = pass;
	}
	
	public User(String username, String password, String phone, String address, String expiration, String college) {
		super();
		this.username = username;
		this.password = password;
		this.phone = phone;
		this.address = address;
		this.expiration = expiration;
		this.college = college;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	
}
