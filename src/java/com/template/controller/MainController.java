package com.template.controller;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.template.model.EmpireState;
import com.template.model.dao.DatabaseConnection;
import com.template.model.dto.ShopItemDTO;
import com.template.service.ShopItemService;
import com.template.util.AlertUtil;
import com.template.util.AssistantTerminal;
import com.template.util.CodeChallengeDialog;
import com.template.util.FormatUtil;
import com.template.util.SkillsDialog;
import com.template.util.SoundManager;
import com.template.util.ThemeContext;
import com.template.validation.ValidationException;

/**
 * Controller: Gerencia a interface gráfica, operações CRUD, easter eggs
 * e camada de mini-game / atendimento aos clientes da Fenda do Biquíni.
 */
public class MainController {

    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private TextField txtDescription;
    @FXML private TextField txtPrice;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbCategory;
    @FXML private ComboBox<String> cbRarity;
    @FXML private Button btnSave;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;
    @FXML private TableView<ShopItemDTO> tableItems;
    @FXML private TableColumn<ShopItemDTO, Integer> colId;
    @FXML private TableColumn<ShopItemDTO, String> colName;
    @FXML private TableColumn<ShopItemDTO, String> colCategory;
    @FXML private TableColumn<ShopItemDTO, String> colDescription;
    @FXML private TableColumn<ShopItemDTO, String> colRarity;
    @FXML private TableColumn<ShopItemDTO, String> colPrice;

    // Componentes interativos do Bob Esponja e Gary (Mini-Game & Tycoon HUD)
    @FXML private Pane overlayPane;
    @FXML private Label lblClock;
    @FXML private Label lblCoins;
    @FXML private Label lblCps;
    @FXML private Label lblDevRank;
    @FXML private ImageView imgGary;
    @FXML private ImageView imgBob;
    @FXML private ImageView imgSquidward;
    @FXML private ImageView imgPatricia;
    @FXML private Label lblSquidwardQuote;
    @FXML private Label lblPatriciaQuote;
    @FXML private Pane gameArea;
    @FXML private Label lblGameStatus;

    private final ShopItemService itemService;
    private final ObservableList<ShopItemDTO> masterData = FXCollections.observableArrayList();

    private Image garyNormalImg;
    private Image garySmileImg;
    private Image garyShellLessImg;

    private double garyDragDeltaX, garyDragDeltaY;
    private double bobDragDeltaX, bobDragDeltaY;
    private double garyVelocityX = 0.6;
    private boolean garyAutoWalk = true;
    private AnimationTimer gameLoop;

    private Timeline tycoonTimeline;
    private int gameHours = 9;
    private int gameMinutes = 0;

    private int squidwardClickCount = 0;
    private int patriciaClickCount = 0;

    public MainController() {
        this(new ShopItemService());
    }

    public MainController(ShopItemService itemService) {
        this.itemService = itemService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSearchFilter();
        setupSelectionListener();
        setupSpongeBobExtras();
        loadItems();
        setupTycoonLoop();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        if (colCategory != null) {
            colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        }
        if (colRarity != null) {
            colRarity.setCellValueFactory(new PropertyValueFactory<>("rarity"));
        }

        colPrice.setCellFactory(tc -> new TableCell<ShopItemDTO, String>() {
            @Override
            protected void updateItem(String price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(FormatUtil.formatCurrency(price));
                }
            }
        });
    }

    private void setupSearchFilter() {
        FilteredList<ShopItemDTO> filteredData = new FilteredList<>(masterData, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(item -> matchesFilter(item, newVal));
        });

        SortedList<ShopItemDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableItems.comparatorProperty());
        tableItems.setItems(sortedData);
    }

    private boolean matchesFilter(ShopItemDTO item, String filter) {
        if (filter == null || filter.trim().isEmpty()) {
            return true;
        }
        String lowerFilter = filter.toLowerCase().trim();
        return (item.getName() != null && item.getName().toLowerCase().contains(lowerFilter))
            || (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerFilter))
            || (item.getCategory() != null && item.getCategory().toLowerCase().contains(lowerFilter))
            || (item.getRarity() != null && item.getRarity().toLowerCase().contains(lowerFilter))
            || String.valueOf(item.getId()).contains(lowerFilter);
    }

    private void setupSelectionListener() {
        tableItems.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                populateForm(selected);
            }
        });
    }

    private void setupSpongeBobExtras() {
        if (!ThemeContext.isBobEsponja()) {
            return;
        }

        if (cbCategory != null) {
            cbCategory.getItems().addAll(
                "Siri Cascudo 🍔",
                "Campo das Águas-Vivas 🪼",
                "Casa do Bob Esponja 🍍",
                "Casa do Lula Molusco 🗿",
                "Domo da Árvore 🐿️",
                "Balde de Lixo 🪣",
                "Lagoa Goo 🏖️"
            );
            cbCategory.getSelectionModel().selectFirst();
        }

        if (cbRarity != null) {
            cbRarity.getItems().addAll(
                "Comum 📦",
                "Raro 💎",
                "Lendário ⭐",
                "Ultra Secreto 🔒",
                "Proibido ☠️"
            );
            cbRarity.getSelectionModel().selectFirst();
        }

        setupMiniGame();
        setupEasterEggs();
    }

    private void setupMiniGame() {
        if (imgGary == null) return;

        try {
            garyNormalImg = new Image(getClass().getResourceAsStream("/com/template/spongebob/images/garry_normal.png"));
            garySmileImg = new Image(getClass().getResourceAsStream("/com/template/spongebob/images/garry_sorrindo.png"));
            garyShellLessImg = new Image(getClass().getResourceAsStream("/com/template/spongebob/images/garry_sem_casca.png"));
        } catch (Exception ignored) {}

        // Configura arraste livre com setTranslate para o Gary
        imgGary.setOnMousePressed(event -> {
            garyAutoWalk = false;
            garyDragDeltaX = event.getSceneX() - imgGary.getTranslateX();
            garyDragDeltaY = event.getSceneY() - imgGary.getTranslateY();
            imgGary.toFront();
        });

        imgGary.setOnMouseDragged(event -> {
            imgGary.setTranslateX(event.getSceneX() - garyDragDeltaX);
            imgGary.setTranslateY(event.getSceneY() - garyDragDeltaY);
            SoundManager.playWalkThrottled();
            if (lblGameStatus != null) {
                lblGameStatus.setText("🚶 Passeando com o Gary pela Fenda do Biquíni!");
            }
        });

        imgGary.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                if (garyShellLessImg != null) {
                    imgGary.setImage(garyShellLessImg);
                    if (lblGameStatus != null) lblGameStatus.setText("😱 Gary perdeu a casca de vergonha!");
                    new Thread(() -> {
                        try {
                            Thread.sleep(1200);
                            Platform.runLater(() -> imgGary.setImage(garyNormalImg));
                        } catch (InterruptedException ignored) {}
                    }).start();
                }
            } else {
                jumpGary();
            }
        });

        // Configura arraste livre com setTranslate para o Bob Esponja (pode descer pra passear com Gary!)
        if (imgBob != null) {
            imgBob.setOnMousePressed(event -> {
                bobDragDeltaX = event.getSceneX() - imgBob.getTranslateX();
                bobDragDeltaY = event.getSceneY() - imgBob.getTranslateY();
                imgBob.toFront();
            });

            imgBob.setOnMouseDragged(event -> {
                imgBob.setTranslateX(event.getSceneX() - bobDragDeltaX);
                imgBob.setTranslateY(event.getSceneY() - bobDragDeltaY);
                SoundManager.playWalkThrottled();

                if (imgGary != null) {
                    double bobX = imgBob.getLayoutX() + imgBob.getTranslateX();
                    double bobY = imgBob.getLayoutY() + imgBob.getTranslateY();
                    double garyX = imgGary.getLayoutX() + imgGary.getTranslateX();
                    double garyY = imgGary.getLayoutY() + imgGary.getTranslateY();
                    double dist = Math.hypot(bobX - garyX, bobY - garyY);

                    if (dist < 120) {
                        if (garySmileImg != null) imgGary.setImage(garySmileImg);
                        if (lblGameStatus != null) {
                            lblGameStatus.setText("🍍❤️🐚 Bob Esponja e Gary estão passeando juntos na areia!");
                        }
                    } else {
                        if (lblGameStatus != null) {
                            lblGameStatus.setText("🍍 Bob Esponja passeando livremente pela tela!");
                        }
                    }
                }
            });

            imgBob.setOnMouseClicked(event -> spinBob());
        }

        // Loop de caminhada suave e SILENCIOSA do Gary (sem som irritante constante)
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!garyAutoWalk || imgGary == null) return;

                double currentX = imgGary.getLayoutX();
                double maxX = (overlayPane != null && overlayPane.getWidth() > 100) ?
                        overlayPane.getWidth() - 90 : 900;

                if (currentX <= 15) {
                    garyVelocityX = Math.abs(garyVelocityX);
                    imgGary.setScaleX(-1);
                } else if (currentX >= maxX) {
                    garyVelocityX = -Math.abs(garyVelocityX);
                    imgGary.setScaleX(1);
                }

                imgGary.setLayoutX(currentX + garyVelocityX);
            }
        };
        gameLoop.start();

        // Teclado para controlar o Gary
        Platform.runLater(() -> {
            if (tableItems != null && tableItems.getScene() != null) {
                tableItems.getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::handleGameKeys);
            }
        });
    }

    private void handleGameKeys(KeyEvent event) {
        if (!ThemeContext.isBobEsponja() || imgGary == null) return;

        if (txtName.isFocused() || txtDescription.isFocused() || txtPrice.isFocused() || txtSearch.isFocused()) {
            return;
        }

        double step = 15;
        garyAutoWalk = false;

        if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) {
            imgGary.setTranslateX(imgGary.getTranslateX() - step);
            imgGary.setScaleX(1);
            SoundManager.playWalkThrottled();
            event.consume();
        } else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) {
            imgGary.setTranslateX(imgGary.getTranslateX() + step);
            imgGary.setScaleX(-1);
            SoundManager.playWalkThrottled();
            event.consume();
        } else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W || event.getCode() == KeyCode.SPACE) {
            jumpGary();
            event.consume();
        } else if (event.getCode() == KeyCode.B) {
            onBlowBubbles(null);
            event.consume();
        }
    }

    // ==========================================
    // 4 EASTER EGGS TEMÁTICOS DA FENDA DO BIQUÍNI
    // ==========================================
    private void setupEasterEggs() {
        // Easter Egg 2: Recital Desafinado do Lula Molusco
        if (imgSquidward != null) {
            imgSquidward.setOnMouseClicked(event -> {
                squidwardClickCount++;
                if (squidwardClickCount >= 2) {
                    squidwardClickCount = 0;
                    SoundManager.play(SoundManager.SAD_SONG);
                    if (lblSquidwardQuote != null) {
                        lblSquidwardQuote.setText("🎵 [TOCANDO CLARINETE DESAFINADO]: Fom... foonk! Ninguém valoriza minha arte!");
                    }
                    if (lblGameStatus != null) {
                        lblGameStatus.setText("🗿 Lula Molusco começou a tocar clarinete desafinado!");
                    }
                } else {
                    SoundManager.play(SoundManager.BOOWOMP);
                    if (lblSquidwardQuote != null) {
                        lblSquidwardQuote.setText("\"Não me toque! Estou em horário de trabalho... infelizmente.\"");
                    }
                }
            });
        }

        // Easter Egg 3: O Disfarce da Patrícia (Patrick Secreto)
        if (imgPatricia != null) {
            imgPatricia.setOnMouseClicked(event -> {
                patriciaClickCount++;
                if (patriciaClickCount >= 2) {
                    patriciaClickCount = 0;
                    SoundManager.play(SoundManager.GARY_MEOW);
                    if (lblPatriciaQuote != null) {
                        lblPatriciaQuote.setText("🤫 \"Psiu! Sou o Patrick! Só botei peruca pra comer 50 hambúrgueres grátis!\"");
                    }
                    boolean register = AlertUtil.showConfirmation(
                        "🎀 Segredo Revelado da Patrícia!",
                        "A Patrícia revelou que é o Patrick Estrela disfarçado!\nDeseja cadastrar o 'Super Combo Secreto do Patrick' no banco de dados?"
                    );
                    if (register) {
                        itemService.saveItem(null, "Combo Secreto do Patrick", "Siri Cascudo 🍔", "Balde com 50 Hambúrgueres de Siri e refrigerante tamanho banheira.", "Lendário ⭐", "49.90");
                        loadItems();
                    }
                } else {
                    SoundManager.play(SoundManager.WALK);
                    if (lblPatriciaQuote != null) {
                        lblPatriciaQuote.setText("\"A Patrícia acha os rapazes daqui tão educados! Hihihi!\"");
                    }
                }
            });
        }
    }

    // Easter Egg 1: Cofre Secreto do Sr. Siriguejo
    @FXML
    void onOpenSecretSafe(ActionEvent event) {
        SoundManager.play(SoundManager.STANK_NOISE);
        boolean confirmed = AlertUtil.showConfirmation(
            "🦀 Cofre Secreto do Sr. Siriguejo",
            "ARGH ARGH ARGH! Você encontrou o cofre do chefe!\n" +
            "A primeira moeda de um centavo do Seu Siriguejo está brilhando aqui dentro.\n" +
            "Deseja registrar a 'Moeda Número 1 do Siriguejo' no inventário do PostgreSQL?"
        );
        if (confirmed) {
            itemService.saveItem(null, "Primeira Moeda de Um Centavo do Siriguejo", "Siri Cascudo 🍔", "A moeda mais valiosa do mundo para Eugene H. Siriguejo. Proibido gastar!", "Lendário ⭐", "1000000.00");
            loadItems();
            SoundManager.play(SoundManager.GARY_MEOW);
        }
    }

    // Easter Egg 4: Técnica de Soprar Bolhas da Lagoa Goo
    @FXML
    void onBlowBubbles(ActionEvent event) {
        spinBob();
        SoundManager.play(SoundManager.GARY_MEOW);
        if (lblGameStatus != null) {
            lblGameStatus.setText("🫧 Técnica das Bolhas: Dobrar o joelho, girar 360° e soprar! Bolhas subindo na Fenda!");
        }
    }

    // Abertura do Terminal do Peixe Assistente (Situação-Problema)
    @FXML
    void onOpenAssistantTerminal(ActionEvent event) {
        Stage stage = (Stage) tableItems.getScene().getWindow();
        AssistantTerminal.show(stage, itemToRegister -> {
            itemService.saveItem(
                null,
                itemToRegister.getName(),
                itemToRegister.getCategory(),
                itemToRegister.getDescription(),
                itemToRegister.getRarity(),
                itemToRegister.getPrice()
            );
            loadItems();
        });
    }

    @FXML
    void onGaryMoveLeft(ActionEvent event) {
        if (imgGary == null) return;
        garyAutoWalk = false;
        imgGary.setTranslateX(imgGary.getTranslateX() - 25);
        imgGary.setScaleX(1);
        SoundManager.playWalkThrottled();
    }

    @FXML
    void onGaryMoveRight(ActionEvent event) {
        if (imgGary == null) return;
        garyAutoWalk = false;
        imgGary.setTranslateX(imgGary.getTranslateX() + 25);
        imgGary.setScaleX(-1);
        SoundManager.playWalkThrottled();
    }

    @FXML
    void onGaryJump(ActionEvent event) {
        jumpGary();
    }

    @FXML
    void onGaryFeed(ActionEvent event) {
        if (imgGary == null) return;
        garyAutoWalk = false;
        SoundManager.play(SoundManager.GARY_MEOW);
        if (garySmileImg != null) imgGary.setImage(garySmileImg);

        if (lblGameStatus != null) {
            lblGameStatus.setText("🥫 Gary devorou uma lata de Snail-Po! +10 Felicidade Marinha! 🌟");
        }

        TranslateTransition bounce = new TranslateTransition(Duration.millis(250), imgGary);
        bounce.setByY(-20);
        bounce.setCycleCount(4);
        bounce.setAutoReverse(true);
        bounce.play();

        new Thread(() -> {
            try {
                Thread.sleep(1800);
                Platform.runLater(() -> {
                    if (garyNormalImg != null) imgGary.setImage(garyNormalImg);
                    garyAutoWalk = true;
                });
            } catch (InterruptedException ignored) {}
        }).start();
    }

    @FXML
    void onBobDance(ActionEvent event) {
        spinBob();
        jumpGary();
        if (lblGameStatus != null) {
            lblGameStatus.setText("💃 Dança do Siri Cascudo! Bob Esponja e Gary estão comemorando!");
        }
    }

    private void jumpGary() {
        if (imgGary == null) return;
        SoundManager.play(SoundManager.GARY_MEOW);
        if (garySmileImg != null) imgGary.setImage(garySmileImg);

        TranslateTransition jump = new TranslateTransition(Duration.millis(220), imgGary);
        jump.setByY(-30);
        jump.setCycleCount(2);
        jump.setAutoReverse(true);
        jump.setInterpolator(Interpolator.EASE_OUT);
        jump.setOnFinished(e -> {
            if (garyNormalImg != null) imgGary.setImage(garyNormalImg);
        });
        jump.play();

        if (lblGameStatus != null) {
            lblGameStatus.setText("🐚 Gary: \"Miau!\" (O caracol agradece pelo carinho)");
        }
    }

    private void spinBob() {
        if (imgBob == null) return;
        SoundManager.playWalkThrottled();

        RotateTransition rotate = new RotateTransition(Duration.millis(500), imgBob);
        rotate.setByAngle(360);
        rotate.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition scale = new ScaleTransition(Duration.millis(250), imgBob);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.25);
        scale.setToY(1.25);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);

        rotate.play();
        scale.play();

        if (lblGameStatus != null) {
            lblGameStatus.setText("🍍 Bob Esponja: \"Estou pronto! Estou pronto!\"");
        }
    }

    private void populateForm(ShopItemDTO item) {
        txtId.setText(String.valueOf(item.getId()));
        txtName.setText(item.getName());
        txtDescription.setText(item.getDescription());
        txtPrice.setText(item.getPrice());

        if (cbCategory != null && item.getCategory() != null) {
            cbCategory.getSelectionModel().select(item.getCategory());
        }
        if (cbRarity != null && item.getRarity() != null) {
            cbRarity.getSelectionModel().select(item.getRarity());
        }

        btnSave.setText(ThemeContext.isBobEsponja() ? "Atualizar 🍍" : "Atualizar");
        btnSave.setId("btnUpdate");
    }

    @FXML
    void onSave(ActionEvent event) {
        try {
            String category = (cbCategory != null) ? cbCategory.getValue() : "Siri Cascudo";
            String rarity = (cbRarity != null) ? cbRarity.getValue() : "Comum";

            itemService.saveItem(
                txtId.getText(),
                txtName.getText(),
                category,
                txtDescription.getText(),
                rarity,
                txtPrice.getText()
            );

            if (ThemeContext.isBobEsponja()) {
                SoundManager.play(SoundManager.GARY_MEOW);
                if (lblGameStatus != null) {
                    lblGameStatus.setText("✨ Item gravado com sucesso no cofre do Seu Siriguejo!");
                }
            }
            onClear(null);
            loadItems();
        } catch (ValidationException e) {
            AlertUtil.showError(e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    void onDelete(ActionEvent event) {
        ShopItemDTO selected = tableItems.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning(ThemeContext.isBobEsponja() ?
                "Selecione um item da Fenda do Biquíni para jogar no lixo!" :
                "selecione um item na tabela para excluir");
        } else {
            // O som de tremor (shiver) toca exatamente ao abrir esta janela com item selecionado
            if (ThemeContext.isBobEsponja()) {
                SoundManager.play(SoundManager.SHIVER);
            }
            boolean confirmed = AlertUtil.showConfirmation(
                ThemeContext.isBobEsponja() ? "Confirmar Demolição / Exclusão" : "confirmar exclusao",
                ThemeContext.isBobEsponja() ?
                    "Tem certeza que deseja deletar \"" + selected.getName() + "\"? O Seu Siriguejo vai ficar furioso!" :
                    "excluir \"" + selected.getName() + "\"?"
            );

            if (confirmed) {
                try {
                    itemService.deleteItem(selected.getId());
                    if (ThemeContext.isBobEsponja()) {
                        SoundManager.play(SoundManager.STANK_NOISE);
                        if (lblGameStatus != null) {
                            lblGameStatus.setText("💥 Item deletado! Som de buzina do Siriguejo acionado!");
                        }
                    }
                    onClear(null);
                    loadItems();
                    updateHud();
                } catch (Exception e) {
                    AlertUtil.showError("erro ao excluir: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    void onClear(ActionEvent event) {
        if (event != null && ThemeContext.isBobEsponja()) {
            SoundManager.play(SoundManager.BOOWOMP);
        }
        txtId.clear();
        txtName.clear();
        txtDescription.clear();
        txtPrice.clear();
        txtSearch.clear();
        if (cbCategory != null && !cbCategory.getItems().isEmpty()) cbCategory.getSelectionModel().selectFirst();
        if (cbRarity != null && !cbRarity.getItems().isEmpty()) cbRarity.getSelectionModel().selectFirst();
        tableItems.getSelectionModel().clearSelection();
        btnSave.setText(ThemeContext.isBobEsponja() ? "Salvar 🍔" : "Salvar");
        btnSave.setId("btnSave");
    }

    private void loadItems() {
        try {
            masterData.setAll(itemService.getAllItems());
            updateHud();
        } catch (Exception e) {
            AlertUtil.showError("erro ao carregar dados: " + e.getMessage());
        }
    }

    // ==========================================
    // TYCOON & ACTIVE MINI-GAME DO SIRI CASCUDO
    // ==========================================

    @FXML
    void onGrillBurger(ActionEvent event) {
        if (!ThemeContext.isBobEsponja()) return;

        EmpireState.incrementBurgersCooked();
        long gain = 10;
        if (EmpireState.hasConnectionPool()) gain *= 2;
        if (EmpireState.hasFirewallPlankton()) gain = (long) (gain * 1.5);
        if (EmpireState.hasBtreeIndex()) gain += 5;

        EmpireState.addCoins(gain);
        updateHud();

        SoundManager.play(SoundManager.GARY_MEOW);
        if (lblGameStatus != null) {
            lblGameStatus.setText("🔥 Hambúrguer de Siri Deluxe grelhado no ponto! +" + gain + " 🐚!");
        }

        // Pulo animado do Bob Esponja ao fritar
        if (imgBob != null) {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), imgBob);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(1.3);
            st.setToY(1.3);
            st.setCycleCount(2);
            st.setAutoReverse(true);
            st.play();
        }

        spawnFloatingCoinText("+" + gain + " 🐚");
    }

    @FXML
    void onOpenCodeChallenges(ActionEvent event) {
        Stage stage = (Stage) tableItems.getScene().getWindow();
        CodeChallengeDialog.show(stage, this::updateHud);
    }

    @FXML
    void onOpenSkills(ActionEvent event) {
        Stage stage = (Stage) tableItems.getScene().getWindow();
        SkillsDialog.show(stage, this::updateHud);
    }

    @FXML
    void onResetGame(ActionEvent event) {
        boolean confirmed = AlertUtil.showConfirmation(
            "💥 Resetar Restaurante e Banco de Dados",
            "ATENÇÃO: Deseja realmente zerar o banco de dados PostgreSQL e o progresso das Conchas?\n" +
            "Todas as mercadorias cadastradas e habilidades serão reiniciadas!"
        );
        if (confirmed) {
            try {
                DatabaseConnection.resetSpongeBobDatabase();
                EmpireState.reset();
                onClear(null);
                loadItems();
                updateHud();
                SoundManager.play(SoundManager.STANK_NOISE);
                if (lblGameStatus != null) {
                    lblGameStatus.setText("💥 Jogo resetado! Banco PostgreSQL zerado e conchas reiniciadas.");
                }
            } catch (Exception e) {
                AlertUtil.showError("Erro ao resetar jogo: " + e.getMessage());
            }
        }
    }

    private void spawnFloatingCoinText(String text) {
        if (overlayPane == null) return;
        Label floatLabel = new Label(text);
        floatLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffd700; " +
                            "-fx-effect: dropshadow(gaussian, #b71c1c, 4, 0.8, 1, 1);");
        double startX = (imgBob != null) ? imgBob.getLayoutX() + imgBob.getTranslateX() - 30 : 500;
        double startY = (imgBob != null) ? imgBob.getLayoutY() + imgBob.getTranslateY() + 40 : 100;
        floatLabel.setLayoutX(Math.max(50, Math.min(startX, 900)));
        floatLabel.setLayoutY(Math.max(50, startY));
        floatLabel.setMouseTransparent(true);

        overlayPane.getChildren().add(floatLabel);

        TranslateTransition tt = new TranslateTransition(Duration.millis(900), floatLabel);
        tt.setByY(-45);
        FadeTransition ft = new FadeTransition(Duration.millis(900), floatLabel);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> overlayPane.getChildren().remove(floatLabel));

        tt.play();
        ft.play();
    }

    private void setupTycoonLoop() {
        if (!ThemeContext.isBobEsponja()) return;

        updateHud();

        tycoonTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            // Avança relógio do jogo
            gameMinutes += 10;
            if (gameMinutes >= 60) {
                gameMinutes = 0;
                gameHours++;
                if (gameHours >= 22) {
                    gameHours = 8; // Novo expediente
                }
            }

            int itemCount = masterData.size();
            long passive = itemCount * 2L;

            if (EmpireState.hasBtreeIndex()) {
                passive = (long) (passive * 1.4);
            }
            if (EmpireState.hasConnectionPool()) {
                passive *= 2;
            }
            if (EmpireState.hasAutoVacuum()) {
                passive += 5;
                EmpireState.incrementBurgersCooked();
            }
            if (EmpireState.hasFirewallPlankton()) {
                passive = (long) (passive * 1.5);
            }

            if (passive > 0) {
                EmpireState.addCoins(passive);
            }

            updateHud();
        }));
        tycoonTimeline.setCycleCount(Timeline.INDEFINITE);
        tycoonTimeline.play();
    }

    private void updateHud() {
        if (!ThemeContext.isBobEsponja()) return;

        int itemCount = masterData.size();
        long passive = itemCount * 2L;
        if (EmpireState.hasBtreeIndex()) passive = (long) (passive * 1.4);
        if (EmpireState.hasConnectionPool()) passive *= 2;
        if (EmpireState.hasAutoVacuum()) passive += 5;
        if (EmpireState.hasFirewallPlankton()) passive = (long) (passive * 1.5);

        if (lblClock != null) {
            String period = (gameHours >= 12) ? "PM" : "AM";
            int displayHour = (gameHours > 12) ? gameHours - 12 : (gameHours == 0 ? 12 : gameHours);
            lblClock.setText(String.format("⏰ %02d:%02d %s", displayHour, gameMinutes, period));
        }
        if (lblCoins != null) {
            lblCoins.setText("🐚 " + EmpireState.getCoins() + " Conchas");
        }
        if (lblCps != null) {
            lblCps.setText("+" + passive + "/s");
        }
        if (lblDevRank != null) {
            lblDevRank.setText("🎓 Cargo: " + EmpireState.getDeveloperRank());
        }
    }
}
