package main;

//避免轉換頁面時資料遺失的中繼class(用來儲存資料)
public class Middle {
	
	public static String username, password;

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
