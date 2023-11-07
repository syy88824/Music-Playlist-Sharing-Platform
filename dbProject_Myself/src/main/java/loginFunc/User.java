package loginFunc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
	private PreparedStatement pstmt;
	private ResultSet result;
	
	//註冊新的使用者帳號時，使用者名稱不可為空白，且密碼必須為8個字
	public void checkAdd(Connection conn,String name, String pw, String pwAgain) throws PasswordError, UserError, SQLException{
		pstmt = conn.prepareStatement("INSERT INTO users (userName, userPW) VALUES (?, ?)");
		if (name.length() == 0) throw new UserError("Username can't be empty");
		if (pw.length() != 8) throw new PasswordError("Password should be 8 letter");
		if(!pw.equals(pwAgain)) throw new PasswordError("Reentered password is different to password");
		return;
	}
	
	//以使用者名稱檢查此使用者是否存在
	public void checkUserExist(Connection conn,String name) throws UserError, SQLException { 
		pstmt = conn.prepareStatement("SELECT `userName` FROM `users` WHERE 1");
		result = pstmt.executeQuery();
		Boolean userCheck = false;
		while(result.next()) {
				if (result.getString("username").equals(name)) {
					userCheck = true;
				}
		}
		if(userCheck) return;
		throw new UserError("Can't find the user");
	}
	
	//以使用者名稱找到該帳號的密碼，再確認使用者input與其設定密碼是否一致
	public void checkPassword(Connection conn,String name, String PW) throws PasswordError, SQLException {		
		pstmt = conn.prepareStatement("SELECT * FROM users WHERE userName = ?");
		pstmt.setString(1, name);
		result = pstmt.executeQuery();
		Boolean pwCheck = false;
		while(result.next()) {
			if (result.getString("userPW").equals(PW)) {
				pwCheck = true;
			}
		}
		if(pwCheck) return;
		throw new PasswordError("Password is wrong");
	}

	
}
	
class UserError extends Exception { 
	public UserError(String Error){
		super(Error);
	}
}
class PasswordError extends Exception {	
	public PasswordError(String Error){ 
		super(Error);
	}
}
