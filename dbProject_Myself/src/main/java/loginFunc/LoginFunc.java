package loginFunc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import main.Middle;

public class LoginFunc {
	
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	User user = new User();
	Middle userData = new Middle();
	
	public LoginFunc(Connection conn) {
		this.conn = conn;
	}
			
	public LoginFunc() {};
	
	// 以userName找尋userID，若查不到此userName，則會回傳-1
	public int getUserId(Connection conn, String username) throws SQLException {
		pstmt = conn.prepareStatement("SELECT userID FROM users WHERE userName = ?");
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			return rs.getInt("userID");
		} else {
			return -1;
		}
	}

	// 以userID找尋userName，若查不到此userID，則會回傳null
	public String getUserName(Connection conn, String username) throws SQLException {
		pstmt = conn.prepareStatement("SELECT userName FROM users WHERE userID = ?");
		int id = getUserId(conn, username);
		pstmt.setInt(1, id);
		rs = pstmt.executeQuery();
		if (rs.next()) {
			return rs.getString("userName");
		} else {
			return null;
		}
	}
	
	public void register(Connection conn, String userName, String userPassword) throws SQLException, UserError, PasswordError {
		boolean nameExist = false;
		pstmt = conn.prepareStatement("SELECT `userName` FROM `users` WHERE 1");
		rs = pstmt.executeQuery();
		while (rs.next()) {
			if (userName.equals(rs.getString(1))) {
				nameExist = true;
				showMessageDialog("Username exist, please choose a new name.");
				break;
			}
		}
		if (!nameExist) {
				pstmt = conn.prepareStatement("INSERT INTO users (userName, password) VALUES (?, ?)");
				pstmt.setString(1, userName);
				pstmt.setString(2, userPassword);
				pstmt.executeUpdate();
				showMessageDialog("User registered!");
		}
	}
	
	public void btnRegister(String inputName, String inputPW, String againPW) throws SQLException {
		conn = main.Main.getConn();
		try {
			user.checkAdd(conn, inputName, inputPW, againPW);
			register(conn, inputName, inputPW);
		} catch (PasswordError e1) {
			showMessageDialog("Password should be 8 words!");
		} catch (UserError e2) {
			showMessageDialog("User name can't be empty!");
		} catch (SQLException e) {
			showMessageDialog(e.getMessage());
		}
	}
	
	public void btnLogin(String inputName, String inputPW) throws SQLException {
		conn = main.Main.getConn();
		try {
			user.checkUserExist(conn, inputName);
			try {
				user.checkPassword(conn, inputName, inputPW);
				try {
					userData.setUsername(inputName);
					userData.setPassword(inputPW);
					//task : 成功登入後將畫面連接回主頁
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (PasswordError e1) {
				showMessageDialog("Worng Password!");
			}
		} catch (UserError | SQLException e2) {
			showMessageDialog("User doesn't exist!");
		}
	}
	
	public void showMessageDialog(String str) {
		
	}
	
}
