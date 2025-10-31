/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package githubclient.ui;

import githubclient.domain.User;
import githubclient.domain.Repo;
import githubclient.util.ApiException;
import githubclient.service.GitHubApi;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import java.util.concurrent.CompletableFuture;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static java.util.Collections.list;
/**
 * FXML Controller class
 *
 * @author Kenneth
 */


public class MainController {
    
    @FXML 
    private TextField usernameField;
    @FXML 
    private Button searchButton;
    @FXML 
    private ProgressIndicator loadingIndicator;
    @FXML 
    private Label errorLabel;

    @FXML 
    private ImageView avatarImage;
    @FXML 
    private Label nameLabel;
    @FXML 
    private Label loginLabel;
    @FXML 
    private Label bioLabel;
    @FXML 
    private Label followersLabel;
    @FXML 
    private Label followingLabel;
    @FXML 
    private Label locationLabel;
    @FXML 
    private Label blogLabel;

    @FXML 
    private TabPane tabPane;
    @FXML 
    private Tab reposTab;
    @FXML 
    private Tab detailTab;
    @FXML 
    private TextField filterField;
    @FXML 
    private TableView<Repo> repoTable;
    @FXML 
    private TableColumn<Repo, String> colName;
    @FXML 
    private TableColumn<Repo, String> colLang;
    @FXML 
    private TableColumn<Repo, String> colUpdated;
    @FXML 
    private TableColumn<Repo, Number> colStars;
    @FXML 
    private TableColumn<Repo, Number> colForks;


    @FXML 
    private Label repoNameLabel;
    @FXML 
    private Label repoDescLabel;
    @FXML 
    private Label repoLangLabel;
    @FXML 
    private Label repoStarsLabel;
    @FXML 
    private Label repoForksLabel;
    @FXML 
    private Label repoUpdatedLabel;
    @FXML 
    private PieChart languagesPie;
    @FXML 
    private Label languagesEmptyLabel;

    private static final ZoneId LOCAL_ZONE = ZoneId.of("America/Costa_Rica");
    private static final DateTimeFormatter ABSOLUTE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final GitHubApi api = new GitHubApi();

    private final ObservableList<Repo> repos = FXCollections.observableArrayList();
    private final FilteredList<Repo> filteredRepos = new FilteredList<>(repos, r -> true);

    private String currentUsername = "";

    public void initialize() {
        colName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        colLang.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("language"));
        colStars.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("stargazersCount"));
        colForks.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("forksCount"));
        colUpdated.setCellValueFactory(cell -> new SimpleStringProperty(formatAbsolute(cell.getValue().getUpdatedAt())));

        repoTable.setItems(filteredRepos);

        filterField.textProperty().addListener((obs, oldVal, val) -> {
            String needle = val == null ? "" : val.trim().toLowerCase();
            filteredRepos.setPredicate(r -> r.getName() != null && r.getName().toLowerCase().contains(needle));
        });

        repoTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                loadRepoDetail(selected);
            }
        });

        detailTab.setDisable(true);
        setLoading(false);
        clearError();
        repoTable.setPlaceholder(new Label("No hay repos públicos o no se pudieron cargar."));

        colName.setCellFactory(col -> new TableCell<Repo, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : or(item, "Sin nombre"));
            }
        });

        colLang.setCellFactory(col -> new TableCell<Repo, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : or(item, "Sin lenguaje"));
            }
        });

    }

    @FXML
    public void onSearch() {
        final String username = (usernameField.getText() == null) ? "" : usernameField.getText().trim();
        
        if (username.isEmpty()) {
            setError("Ingresa un nombre de usuario.");
            return;
        }
        System.out.println("DEBUG onSearch fired user=" + username);

        setLoading(true);
        clearError();
        currentUsername = username;
        languagesPie.getData().clear();
        languagesEmptyLabel.setVisible(false);
        detailTab.setDisable(true);
        repoTable.getSelectionModel().clearSelection();
        tabPane.getSelectionModel().select(reposTab);

        CompletableFuture<User> fUser  = CompletableFuture.supplyAsync(() -> api.getUser(username));
        CompletableFuture<List<Repo>> fRepos = CompletableFuture.supplyAsync(() -> api.getRepos(username));

        CompletableFuture.allOf(fUser, fRepos).handle((v, ex) -> {
                Platform.runLater(() -> setLoading(false));

                if (ex != null) {
                    System.out.println("DEBUG EX=" + ex);
                    handleError(ex);
                    return null;
                }
                try {
                    User u = fUser.join();
                    List<Repo> list = fRepos.join();
                    System.out.println("DEBUG repos=" + list.size());

                    Platform.runLater(() -> {
                        populateUserHeader(u);
                        repos.setAll(list);
                        repoTable.getSelectionModel().clearSelection();
                        detailTab.setDisable(true);
                        tabPane.getSelectionModel().select(reposTab);
                    });
                } catch (Exception e) {
                    System.out.println("DEBUG join EX=" + e);
                    handleError(e);
                }
                return null;
            });
    }

    private void loadRepoDetail(Repo repo) {
        detailTab.setDisable(false);
        tabPane.getSelectionModel().select(detailTab);

        repoNameLabel.setText("Nombre: " + or(repo.getName(), "No hay datos"));
        repoDescLabel.setText("Descripción: " + or(repo.getDescription(), "No hay datos"));
        repoLangLabel.setText("Lenguaje: " + or(repo.getLanguage(), "Sin lenguaje"));
        repoStarsLabel.setText("★ " + repo.getStargazersCount());
        repoForksLabel.setText("Forks: " + repo.getForksCount());
        repoUpdatedLabel.setText("Actualizado: " + formatAbsolute(repo.getUpdatedAt()));

        setLoading(true);
        CompletableFuture.supplyAsync(() -> api.getRepoLanguages(currentUsername, repo.getName()))
                .whenComplete((map, ex) -> Platform.runLater(() -> setLoading(false)))
                .thenAccept(map -> Platform.runLater(() -> populatePie(map)))
                .exceptionally(ex -> {
                    handleError(ex);
                    return null;
                });
    }

    private void populateUserHeader(User u) {
        if (u == null) return;
        if (u.getAvatarUrl() != null && !u.getAvatarUrl().isBlank()) {
            avatarImage.setImage(new Image(u.getAvatarUrl(), true));
        } else {
            avatarImage.setImage(null);
        }
        nameLabel.setText(or(u.getName(), "No hay datos"));
        loginLabel.setText("@" + or(u.getLogin(), "No hay datos"));
        bioLabel.setText(or(u.getBio(), "No hay datos"));
        followersLabel.setText("Seguidores: " + u.getFollowers());
        followingLabel.setText("Siguiendo: " + u.getFollowing());
        locationLabel.setText("Ubicación: " + or(u.getLocation(), "No hay datos"));
        blogLabel.setText("Blog: " + or(u.getBlog(), "No hay datos"));

    }

    private void populatePie(Map<String, Integer> langBytes) {
        languagesPie.getData().clear();
        if (langBytes == null || langBytes.isEmpty()) {
            languagesEmptyLabel.setVisible(true);
            return;
        }
        languagesEmptyLabel.setVisible(false);

        double total = langBytes.values().stream().mapToDouble(Integer::doubleValue).sum();
        if (total <= 0) {
            languagesEmptyLabel.setVisible(true);
            return;
        }
        for (Map.Entry<String, Integer> e : langBytes.entrySet()) {
            double pct = (e.getValue() / total) * 100.0;
            String label = String.format("%s (%.2f%%)", e.getKey(), pct);
            languagesPie.getData().add(new PieChart.Data(label, e.getValue()));
        }
    }

    private String formatAbsolute(String githubIsoInstant) {
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(githubIsoInstant);
            return zdt.withZoneSameInstant(LOCAL_ZONE).format(ABSOLUTE_FMT);
        } catch (Exception e) {
            return githubIsoInstant == null ? "—" : githubIsoInstant;
        }
    }

    private void setLoading(boolean on) {
        loadingIndicator.setVisible(on);
        searchButton.setDisable(on);
    }

    private void setError(String msg) {
        errorLabel.setText(msg == null ? "" : msg);
    }

    private void clearError() {
        errorLabel.setText("");
    }

    private String or(String s, String placeholder) {
        return (s == null || s.isBlank()) ? placeholder : s;
    }

    private void handleError(Throwable t) {
        Throwable cause = (t instanceof java.util.concurrent.CompletionException) ? t.getCause() : t;
        String msg;
        if (cause instanceof ApiException) {
            msg = cause.getMessage();
        } else {
            msg = "Error: " + (cause == null ? "desconocido" : cause.getMessage());
        }
        Platform.runLater(() -> setError(msg));
    }

}
