import java.sql.Connection;
import java.util.TimerTask;
import javax.security.auth.callback.Callback;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Timer;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.ArrayList;

public class Functions {
	
	private PreparedStatement pstmt;
	private ResultSet rs;
	private Connection conn;
	private RankUpdateCallback callback;
	private User user;
	private Timer timer;
	private ObservableList<String> plList= FXCollections.observableArrayList();
	private ObservableList<String> songList= FXCollections.observableArrayList();
	
	public Functions(Connection conn) {
        this.conn = conn;
        timer = new Timer();
    }
	public void setRankUpdateCallback(RankUpdateCallback callback) {
        this.callback = callback;
    }
	
	//以userName找尋userID，若查不到此userName，則會回傳-1
	public int getUserId(Connection conn, String username) throws SQLException {
       pstmt = conn.prepareStatement("SELECT userID FROM users WHERE username = ?");
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("userID");
        } else {
            return -1;
        }
    }
	
	//以userID找尋userName，若查不到此userID，則會回傳null
	public String getUserName(Connection conn, String username) throws SQLException {
	       pstmt = conn.prepareStatement("SELECT username FROM users WHERE userID = ?");
	      int id = getUserId(conn, username);
	        pstmt.setInt(1,id);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("username");
	        } else {
	            return null;
	        }
	    }
	
	//以playlistName找尋playlistID，若查不到此playlistName，則會回傳-1
	public int getPlaylistId(Connection conn, String playlistName) throws SQLException{
		pstmt = conn.prepareStatement("SELECT playlistID FROM playlist WHERE playlist_name = ?");
   	 pstmt.setString(1, playlistName);
   	 ResultSet rs = pstmt.executeQuery();
   	 if (rs.next()) {
            return rs.getInt("playlistID");
        } else {
            return -1;
        }
   }
   
	//以songName找尋songID，若查不到此songName，則會回傳-1
  public int getSongId(Connection conn, String songName) throws SQLException{
	 pstmt = conn.prepareStatement("SELECT songID FROM songs WHERE title = ?");
  	 pstmt.setString(1, songName.trim());
  	 ResultSet rs = pstmt.executeQuery();
  	if (rs.next()) {
           return rs.getInt("songID");
       } else {
           return -1;
       }
  }
   
	//註冊：以userName和userPassword註冊帳號，userName不可與其他使用者的名稱重複，也不可包含"@"符號
	public void  register(Connection conn, String userName, String userPassword) throws SQLException, UserError, PasswordError {
		boolean nameExist = false;
		pstmt = conn.prepareStatement("SELECT `username` FROM `users` WHERE 1");
		rs = pstmt.executeQuery();
		while (rs.next()) {
			if (userName.equals(rs.getString(1))) {
				nameExist = true;
				showMessageDialog("Username exist, please choose a new name.");
				break;
			}
		}
		if (!nameExist) {
			if (userName.contains("@")) {
				showMessageDialog("Username cannot contain '@', please choose a new name.");
			} else {
				pstmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
				pstmt.setString(1, userName);
				pstmt.setString(2, userPassword);
				pstmt.executeUpdate();
				showMessageDialog("User registered!");
			}
		}
	}

	//加歌曲：以playlistName和songName將一首歌曲加入特定的歌單中，若歌單中已存在該歌曲，即不會被重複加至歌單中
	public void  add_song_to_playlist(Connection conn, String playlistName, String songName) throws SQLException {
		int playlistId = getPlaylistId(conn, playlistName);
		int songId = getSongId(conn, songName);
		boolean songExist = false;
		pstmt = conn.prepareStatement("SELECT `songID` FROM `playlist_songs` WHERE `playlistID` = ?");
		pstmt.setInt(1, playlistId);
		rs = pstmt.executeQuery();
		while(rs.next()) {
			if(rs.getInt(1) == songId) {
				songExist = true;
				break;
			}
		}
		if(!songExist) {
			pstmt = conn.prepareStatement("INSERT INTO playlist_songs (playlistID, songID) VALUES (?, ?)");
			pstmt.setInt(1, playlistId);
			pstmt.setInt(2, songId);
			pstmt.executeUpdate();
		}else {
			showMessageDialog("Song already exist.");
		}
	}

	//刪除歌曲：以playlistName和songName將一首歌曲從特定歌單中刪除
	public void  delete_song_from_playlist(Connection conn, String playlistName, String songName) throws SQLException {
		int playlistId = getPlaylistId(conn, playlistName);
		if (playlistId == -1) {
			return;
		}
		int songId = getSongId(conn,songName.trim());
		if (songId == -1) {
			return;
		}
		pstmt = conn.prepareStatement("DELETE FROM playlist_songs WHERE playlistID= ? AND songID = ?");
		pstmt.setInt(1, playlistId);
		pstmt.setInt(2, songId);
		pstmt.executeUpdate();		
	}
	
	//搜尋歌單：以playlistName搜尋歌單是否存在，若歌單已存在則回傳true
	public boolean search_playlist(Connection conn, String searchTerm) throws SQLException {
	    pstmt = conn.prepareStatement("SELECT playlist_name FROM playlist WHERE playlist_name = ?");
	    pstmt.setString(1, searchTerm);
	    boolean e =false;
	    ResultSet rs = pstmt.executeQuery();
	    if (!rs.next()) {
	    	e = false;
	    }else {
	    	e = true;
	    }
	    return e;
	}

	public interface RankCallback {
	    void onRankUpdate(String result);
	}
		
	public void startRankUpdates() {
        timer.schedule(new RankTask(), 0, 5000); 
    }
	
	public void stopRankUpdates() {
        timer.cancel();
    }
	
	private class RankTask extends TimerTask {
		public void run() {
            try {
            	String result = rank(conn,callback); 
            	if (callback != null) {
            		callback.onRankUpdate(result); 
            	}
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
	}
	
	//歌單排名：將所有歌單依讚數做排名，並列出讚數前10高的歌單名稱、創作者、歌單風格
	public String rank(Connection conn, RankUpdateCallback rcb) throws SQLException, InterruptedException  {
		while (true) {
		pstmt = conn.prepareStatement("SELECT playlist_name , likes, hashtag FROM playlist ORDER BY likes DESC LIMIT 10");
		ResultSet rs = pstmt.executeQuery();
		return showResultSet(rs);
		}
	}
	
	//歌曲占卜：先從歌曲資料庫中隨機選出一首歌，再輸出其歌名和歌手
	public String lot_drawing(Connection conn) throws SQLException{
		pstmt = conn.prepareStatement("SELECT MAX(songID) FROM songs");
	    rs = pstmt.executeQuery();
	    String result = null;	   
	    int maxSongID = 0;          		    
	    if (rs.next()) {
	        maxSongID = rs.getInt(1);
	    }     		    
	    int randomSongID = (int) (Math.random() * maxSongID) + 1;
	    pstmt = conn.prepareStatement("SELECT title, artist FROM songs WHERE songID=?"); 
	    pstmt.setInt(1, randomSongID);
	    rs = pstmt.executeQuery();
	    String title = "";
	    String artist = "";
	    while(title.isBlank()) {  
	    	if (rs.next()) {
	    		title = rs.getString("title");
	    		artist = rs.getString("artist");
	    		result = "Your song of the day is: " + title + " by " + artist;
	    	}else {
	        result ="No songs found.";
	    	}
	    }
	 return result;
	}
	
	//使"歌單排名"中輸出的資料對齊
	public static String showResultSet(ResultSet result) throws SQLException {
	    ResultSetMetaData metaData = result.getMetaData();
	    int columnCount = metaData.getColumnCount();
	    int[] columnWidths = { 20, 10, 20 }; // 自訂每個欄位的固定欄寬
	    StringBuilder output = new StringBuilder();

	    // 格式化輸出並對齊欄位標籤
	    for (int i = 1; i <= columnCount; i++) {
	        String label = metaData.getColumnLabel(i);
	        output.append(String.format("%-20s", label)); // 使用固定的欄寬 20
	    }
	    output.append("\n");

	    // 格式化輸出並對齊結果
	    while (result.next()) {
	        for (int i = 1; i <= columnCount; i++) {
	            String value = result.getString(i).trim();
	            output.append(String.format("%-20s", value)); // 使用固定的欄寬 20
	        }
	        output.append("\n");
	    }
	    return output.toString();
	}

	  //以userName篩選出由一個特定使用者創作的所有歌單及其資料
	  public ResultSet show_playlist(Connection conn, String userName) throws SQLException {
			pstmt = conn.prepareStatement("SELECT `playlist_name`,`hashtag` FROM `playlist` WHERE `username` = ?");
			pstmt.setString(1, userName);
			rs = pstmt.executeQuery();
			return rs;
		}
	 
	  //更新密碼
	  public void change_password(Connection conn, String password, String newPassword) throws SQLException {
			pstmt = conn.prepareStatement("UPDATE `users` SET `password`=? WHERE `password`=?");
			pstmt.setString(1, newPassword);
			pstmt.setString(2, password);
			pstmt.executeUpdate();
		}

	  //更新或新增信箱
		public void change_email(Connection conn, String userName, String email) throws SQLException {
			pstmt = conn.prepareStatement("UPDATE `users` SET `email`=? WHERE `username`=?");
			pstmt.setString(1, email);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		}
		
		//更新使用者名稱
		public void change_userName(Connection conn, String userName, String newUserName) throws SQLException {
			pstmt = conn.prepareStatement("UPDATE `users` SET `username`=? WHERE `username`=?");
			pstmt.setString(1, newUserName);
			pstmt.setString(2, userName);		
			pstmt.executeUpdate();
			pstmt = conn.prepareStatement(
					"UPDATE `playlist` INNER JOIN `users` ON playlist.userID = users.userID  SET playlist.username = users.username");
			pstmt.execute();
		}

		//更新歌單名稱
		public void change_playlistName(Connection conn, String originalName, String newName) throws SQLException {
			pstmt = conn.prepareStatement("UPDATE `playlist_name`= ? WHERE `playlist_name`= ?");
			pstmt.setString(1, newName);
			pstmt.setString(2, originalName);
			pstmt.executeUpdate();
		}
		
		//喜歡歌單：以playlistName讓使用者對特定歌單點喜歡
		public void like_playlist(Connection conn, String playlistName) throws SQLException {
			int playlistId = getPlaylistId(conn, playlistName);
			if (playlistId == -1) {
				return;
			}
			pstmt = conn.prepareStatement("UPDATE playlist SET likes = likes + 1 WHERE playlistID = ?;");
			pstmt.setInt(1, playlistId);
			pstmt.executeUpdate();
			showMessageDialog("playlist liked!");
		}
		
		//刪除歌單：以userName和playlistName將特定使用者的某個歌單刪除
		public void delete_playlist(Connection conn, String userName, String playlistName) throws SQLException {
			PreparedStatement pstmt = conn.prepareStatement("DELETE `playlist` FROM `playlist` INNER JOIN `users` ON playlist.userID = users.userID WHERE playlist.playlist_name = ? AND users.username = ?");
			pstmt.setString(1, playlistName);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		}

		//新增歌單：以userName, playlistName, genre新增某個特定風格的歌單
		public void add_playlist(Connection conn, String userName, String playlistName, String genre) throws SQLException {
			PreparedStatement pstmt = conn. prepareStatement("INSERT INTO playlist (playlist_name, userID, hashtag, username) VALUES (?, ?, ?, ?)");
			pstmt.setString(1, playlistName);
			pstmt.setInt(2, getUserId(conn, userName));
			pstmt.setString(3, genre);
			pstmt.setString(4, userName);
			pstmt.executeUpdate();
						
		}
		
		//跳出提示框
		public static void showMessageDialog(String message) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Message");
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.showAndWait();
		}

		
}
	

