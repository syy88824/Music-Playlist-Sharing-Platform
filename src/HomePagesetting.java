import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HomePagesetting {
	private Connection conn;
	private Functions func;
	private Label Searchlabel = new Label();
	private Label Colorlabel = new Label();
	private Label rankTitle = new Label();
	private Label divTitle = new Label();
	private Button btnSearch, btnLogout, btnUser, btnDiv;
	private TextArea divArea, rankArea;

	// 新增歌單的node
	private Label listTitle = new Label();
	private Label playNameLbl = new Label();
	private Label styleLabel = new Label();
	private TextField playNameFld;
	private Button btnCommit;

	// 搜索歌單的node
	private TextField textField = new TextField();
	private PreparedStatement pstmt;
	private ResultSet result;
	private ListView<String> listView;
	private ObservableList<String> searchObserv;
	private Stage playlistStage = new Stage();
	Middle mid = new Middle();

	public HomePagesetting(Connection conn) {
		this.conn = conn;
	}

	public Scene HomeScene() throws SQLException, InterruptedException {
		listView = new ListView<>(searchObserv);
		func = new Functions(conn);
		Font font = Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 18);

		Searchlabel.setText("Search:");
		Colorlabel.setText("Change Color :");
		rankTitle.setText("排行榜");
		rankTitle.setFont(font);
		divTitle.setText("每日歌曲占卜");
		divTitle.setFont(font);

		btnSearch = new Button("Find PlayList");
		btnLogout = new Button("Log out");
		btnUser = new Button("User Info");
		btnDiv = new Button("開始占卜");

		//搜索匡
		listView = new ListView<>(searchObserv);
		listView.setPrefHeight(50);
		listView.setVisible(false);

		//歌單排名
		rankArea = new TextArea();
		rankArea.setPrefWidth(500);
		rankArea.setPrefHeight(200);
		rankArea.setEditable(false);

		func.setRankUpdateCallback(new RankUpdateCallback(rankArea) {
			@Override
			public void onRankUpdate(String result) {
				Font font = new Font("Courier New", 12);
				rankArea.setText(result);
				rankArea.setFont(font);
			}
		});
		func.startRankUpdates();
		RankUpdateCallback callback = new RankUpdateCallback(rankArea);
		func.rank(conn, callback);

		playNameLbl.setText("PlayList Name:");
		styleLabel.setText("          Style:");
		listTitle.setText("創建歌單");
		listTitle.setFont(font);
		String[] genreOptions = { "K-pop", "J-pop", "Hip-Hop", "R&B", "華語流行", "Jazz", "Metal", "Classic", "Anime" };
		ComboBox<String> styleCmb = new ComboBox<>();
		styleCmb.getItems().addAll(genreOptions);
		styleCmb.setPrefWidth(200);
		btnCommit = new Button("Commit");
		playNameFld = new TextField();
		playNameFld.setPrefWidth(200);

		// 顏色
		String[] options = { "WHITE", "GAINSBORO", "AZURE", "BEIGE", "HONEYDEW", "LAVENDER", "LAVENDERBLUSH", "LINEN" };
		ComboBox<String> comboBox = new ComboBox<>();
		comboBox.getItems().addAll(options);
		if (Middle.getColor() == null) {
			comboBox.setValue("WHITE");
		} else {
			comboBox.setValue(Middle.getColor());
		}

		//新增歌單時，必須輸入歌單名稱以及選擇一個風格。歌單新增成功時，會跳出提示和該歌單的資料頁面
		btnCommit.setOnAction(event -> {
			mid.userData(conn);
			Functions func = new Functions(conn);
			try {
				if (styleCmb.getValue() == null) {
					Functions.showMessageDialog("Please choose one genre!");
					return;
				}
				if (func.search_playlist(conn, playNameFld.getText())) {
					Functions.showMessageDialog("Playlist name already existed, please rename!");
					return;
				} else
					func.add_playlist(conn, Middle.getUsername(), playNameFld.getText(), styleCmb.getValue());
				mid.setPlaylistName(playNameFld.getText());
				Pl_addPage playlistPage = new Pl_addPage(conn);
				playlistPage.newP(conn).showAndWait();
			} catch (SQLException e) {
				Functions.showMessageDialog(e.getMessage());
			}
			Functions.showMessageDialog("成功添加歌單!");
		});

		btnDiv.setOnAction(event -> {
			try {
				Font f = new Font("Courier New", 12);
				divArea.setFont(f);
				divArea.setText(func.lot_drawing(conn));
			} catch (SQLException e) {
				Functions.showMessageDialog(e.getMessage());
			}
		});

		//點擊此鍵以進入使用者資料頁面
		btnUser.setOnAction(event -> {
			try {
				UserSetting page1 = new UserSetting(conn);
				Scene scene1 = page1.sceneUser(conn);
				Stage stage = (Stage) btnUser.getScene().getWindow();
				stage.setScene(scene1);
			} catch (SQLException e) {
				Functions.showMessageDialog(e.getMessage());
			}
		});

		// 登出：回到註冊-登入頁面
		btnLogout.setOnAction(event -> {
			try {
				LoginSetting login = new LoginSetting(conn);
				Stage stage = (Stage) btnLogout.getScene().getWindow();
				stage.setScene(login.loginPage(conn));
			} catch (Exception e) {
				Functions.showMessageDialog(e.getMessage());
			}
		});

		VBox helloPanel = new VBox(10);

		//使用者選擇顏色(主題)後，將該顏色存放至Middle class
		comboBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String selectedColor = comboBox.getValue();
				helloPanel.setStyle("-fx-background-color: " + selectedColor);
				mid.setColor(selectedColor);
			}
		});

		helloPanel.setAlignment(Pos.TOP_CENTER);
		helloPanel.setPadding(new Insets(20));
		helloPanel.setPrefWidth(800);
		helloPanel.setPrefHeight(800);

		FlowPane row1Pane = new FlowPane();
		row1Pane.setAlignment(Pos.CENTER);
		row1Pane.setHgap(10);
		row1Pane.setVgap(10);
		row1Pane.getChildren().addAll(Colorlabel, comboBox, btnLogout, btnUser);
		helloPanel.getChildren().add(row1Pane);

		FlowPane row2Pane = new FlowPane();
		HBox hbox = new HBox(10);
		row2Pane.setHgap(10);
		textField.setPrefWidth(150);
		row2Pane.setAlignment(Pos.CENTER_RIGHT);

		searchObserv = FXCollections.observableArrayList();
		listView = new ListView<>(searchObserv);
		listView.setVisible(false);
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			updateSuggestions(conn, newValue);
			//當使用者點擊listView中的某列時，textField會自動變成該列的文字
			listView.setOnMouseClicked(event -> {
				int selectedIndex = listView.getSelectionModel().getSelectedIndex();
				String selectedName = searchObserv.get(selectedIndex);
				textField.setText(selectedName);
			});
			listView.setVisible(true);
		});

		//若點擊btnSearch時能找到對應的歌單，會跳出該歌單的資料頁面，否則會跳出提示
		btnSearch.setOnAction(event -> {
			String inputName = textField.getText();
			if (this.listExist(conn, inputName)) {
				mid.setPlaylistName(inputName);
				Pl_addPage playlistPage = new Pl_addPage(conn);
				playlistPage.newP(conn).showAndWait();
				textField.clear();
			} else {
				Functions.showMessageDialog("Playlist doesn't exist");
			}
			listView.setVisible(false);
		});

		hbox.getChildren().addAll(Searchlabel, textField, btnSearch);
		hbox.setStyle("-fx-alignment: center");
		listView.setPrefSize(200, 50);
		row2Pane.getChildren().addAll(hbox, listView);
		helloPanel.getChildren().add(row2Pane);

		FlowPane row4Pane = new FlowPane();
		row4Pane.setAlignment(Pos.CENTER);
		row4Pane.getChildren().addAll(listTitle);
		helloPanel.getChildren().add(row4Pane);

		FlowPane row5Pane = new FlowPane();
		row5Pane.setAlignment(Pos.CENTER);
		row5Pane.setHgap(10);
		row5Pane.setVgap(10);
		row5Pane.getChildren().addAll(playNameLbl, playNameFld);
		helloPanel.getChildren().add(row5Pane);

		FlowPane row6Pane = new FlowPane();
		row6Pane.setAlignment(Pos.CENTER);
		row6Pane.setHgap(10);
		row6Pane.setVgap(10);
		row6Pane.getChildren().addAll(styleLabel, styleCmb);
		helloPanel.getChildren().add(row6Pane);

		FlowPane row8Pane = new FlowPane();
		row8Pane.setAlignment(Pos.CENTER);
		row8Pane.getChildren().addAll(btnCommit);
		helloPanel.getChildren().add(row8Pane);

		FlowPane row13Pane = new FlowPane();
		row13Pane.setAlignment(Pos.CENTER);
		row13Pane.getChildren().addAll(rankTitle);
		helloPanel.getChildren().add(row13Pane);

		FlowPane row14Pane = new FlowPane();
		row14Pane.setAlignment(Pos.CENTER);
		row14Pane.getChildren().addAll(rankArea);
		helloPanel.getChildren().add(row14Pane);

		FlowPane row15Pane = new FlowPane();
		row15Pane.setAlignment(Pos.CENTER);
		row15Pane.getChildren().addAll(divTitle);
		helloPanel.getChildren().add(row15Pane);

		FlowPane row16Pane = new FlowPane();
		row16Pane.setAlignment(Pos.CENTER);
		row16Pane.getChildren().addAll(btnDiv);
		helloPanel.getChildren().add(row16Pane);

		FlowPane row17Pane = new FlowPane();
		row17Pane.setAlignment(Pos.CENTER);
		divArea = new TextArea();

		divArea.setPrefWidth(500);
		divArea.setPrefHeight(50);
		row17Pane.getChildren().addAll(divArea);
		helloPanel.getChildren().add(row17Pane);

		helloPanel.setPrefHeight(700);
		helloPanel.setStyle("-fx-background-color: " + Middle.getColor());
		return new Scene(helloPanel);

	}

	//依使用者輸入的keyword更新所有的可能歌單名稱
	private void updateSuggestions(Connection conn, String keyword) {
		try {
			searchObserv.clear();
			ArrayList<String> keywords = new ArrayList<>();
			result = this.getResult(conn);
			while (result.next()) {
				keywords.add(result.getString("playlist_name"));
			}
			for (String keyword1 : keywords) {
				if (keyword1.startsWith(keyword)) {
					searchObserv.add(keyword1);
				}
			}
		} catch (SQLException e) {
			Functions.showMessageDialog(e.getMessage());
		}
	}

	public boolean listExist(Connection conn, String searchText) {
		boolean exist = false;
		ResultSet result = this.getResult(conn);
		try {
			while (result.next()) {
				if (result.getString(1).equals(searchText)) {
					exist = true;
					break;
				}
			}
		} catch (SQLException e) {
			Functions.showMessageDialog(e.getMessage());
		}
		return exist;
	}

	public ResultSet getResult(Connection conn) {
		try {
			pstmt = conn.prepareStatement("SELECT `playlist_name` FROM `playlist` WHERE 1");
			result = pstmt.executeQuery();
		} catch (SQLException e) {
			Functions.showMessageDialog(e.getMessage());
		}
		return result;
	}

}
