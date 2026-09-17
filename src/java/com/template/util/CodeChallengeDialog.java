package com.template.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

import com.template.model.EmpireState;
import com.template.model.challenge.Challenge;
import com.template.model.challenge.TestResult;
import com.template.service.challenge.ChallengeCatalog;
import com.template.service.challenge.ChallengeSandboxService;

/**
 * Terminal de Desafios de Engenharia e Programação SQL / PostgreSQL do Siri Cascudo 2000.
 */
public class CodeChallengeDialog {

    public static void show(Stage parentStage, Runnable onUpdateCallback) {
        Stage stage = new Stage();
        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("💻 Terminal de Engenharia & Desafios Dev - Siri Cascudo OS 2000");

        List<Challenge> challengeList = ChallengeCatalog.getAllChallenges();
        final Challenge[] currentChallenge = { challengeList.get(0) };

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.getStyleClass().add("retro-window-panel");

        // ==========================================
        // TOPO: SELETOR DE FASES & STATUS DE CARREIRA
        // ==========================================
        VBox topBox = new VBox(6);
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label lblTitle = new Label("🛠️ TASK COMPLEXA: APAGÃO DO ALMOÇO & ATAQUE DO PLANKTON");
        lblTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #b71c1c;");

        Label lblRank = new Label("Cargo: " + EmpireState.getDeveloperRank());
        lblRank.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #01579b; -fx-background-color: #e1f5fe; -fx-padding: 3 8; -fx-border-color: #81d4fa; -fx-border-radius: 4; -fx-background-radius: 4;");

        HBox.setHgrow(lblTitle, Priority.ALWAYS);
        headerRow.getChildren().addAll(lblTitle, lblRank);

        // Barra de seleção de Desafio
        HBox selectorRow = new HBox(10);
        selectorRow.setAlignment(Pos.CENTER_LEFT);
        Label lblChoose = new Label("Selecione a Fase:");
        lblChoose.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");

        ComboBox<Challenge> cbChallenges = new ComboBox<>();
        cbChallenges.getItems().addAll(challengeList);
        cbChallenges.getSelectionModel().select(currentChallenge[0]);
        cbChallenges.setPrefWidth(420);
        cbChallenges.getStyleClass().add("retro-combo");

        cbChallenges.setCellFactory(lv -> new ListCell<Challenge>() {
            @Override
            protected void updateItem(Challenge c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    boolean done = EmpireState.isChallengeCompleted(c.getId());
                    setText((done ? "✅ " : "⏳ ") + c.getCharacterIcon() + " " + c.getTitle());
                }
            }
        });
        cbChallenges.setButtonCell(new ListCell<Challenge>() {
            @Override
            protected void updateItem(Challenge c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    boolean done = EmpireState.isChallengeCompleted(c.getId());
                    setText((done ? "✅ " : "⏳ ") + c.getCharacterIcon() + " " + c.getTitle());
                }
            }
        });

        Label lblReward = new Label("Recompensa: " + currentChallenge[0].getRewardCoins() + " 🐚 Conchas");
        lblReward.setStyle("-fx-font-weight: bold; -fx-text-fill: #e65100; -fx-font-size: 11px;");

        selectorRow.getChildren().addAll(lblChoose, cbChallenges, lblReward);
        topBox.getChildren().addAll(headerRow, selectorRow);
        root.setTop(topBox);

        // ==========================================
        // CENTRO: SPLITPANE (ENUNCIADO vs EDITOR SQL)
        // ==========================================
        SplitPane split = new SplitPane();
        split.setPadding(new Insets(8, 0, 8, 0));

        // PAINEL ESQUERDO: ENUNCIADO & CRITÉRIOS
        VBox leftPane = new VBox(8);
        leftPane.setPadding(new Insets(8));
        leftPane.setPrefWidth(380);

        Label lblSubtitle = new Label(currentChallenge[0].getSubtitle());
        lblSubtitle.setWrapText(true);
        lblSubtitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #004d40;");

        Label lblNarrativeTitle = new Label("💬 Contexto & Situação-Problema:");
        lblNarrativeTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #b71c1c;");

        Label lblNarrative = new Label(currentChallenge[0].getClientNarrative());
        lblNarrative.setWrapText(true);
        lblNarrative.setStyle("-fx-font-style: italic; -fx-font-size: 11px; -fx-background-color: #fffde7; -fx-padding: 8; -fx-border-color: #fff59d; -fx-border-radius: 4; -fx-background-radius: 4;");

        Label lblObjTitle = new Label("🎯 Objetivo de Engenharia:");
        lblObjTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");

        TextArea txtObjective = new TextArea(currentChallenge[0].getMissionObjective());
        txtObjective.setEditable(false);
        txtObjective.setWrapText(true);
        txtObjective.setPrefRowCount(4);
        txtObjective.setStyle("-fx-font-size: 11px; -fx-font-family: 'Comic Sans MS', sans-serif;");

        Label lblCritTitle = new Label("🧪 Critérios Validados pela Suíte de Testes:");
        lblCritTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1b5e20;");

        VBox criteriaBox = new VBox(3);
        Runnable updateCriteria = () -> {
            criteriaBox.getChildren().clear();
            for (String crit : currentChallenge[0].getTestCriteriaDescriptions()) {
                Label l = new Label("• " + crit);
                l.setWrapText(true);
                l.setStyle("-fx-font-size: 10px; -fx-text-fill: #37474f;");
                criteriaBox.getChildren().add(l);
            }
        };
        updateCriteria.run();

        leftPane.getChildren().addAll(lblSubtitle, lblNarrativeTitle, lblNarrative, lblObjTitle, txtObjective, lblCritTitle, criteriaBox);
        ScrollPane scrollLeft = new ScrollPane(leftPane);
        scrollLeft.setFitToWidth(true);

        // PAINEL DIREITO: EDITOR SQL & CONSOLE DE TESTES
        VBox rightPane = new VBox(6);
        rightPane.setPadding(new Insets(8));

        HBox editorTools = new HBox(8);
        editorTools.setAlignment(Pos.CENTER_LEFT);
        Label lblEditorTitle = new Label("💻 Editor SQL / PLpgSQL (PostgreSQL):");
        lblEditorTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #263238;");

        Button btnPasteTemplate = new Button("↩️ Template");
        btnPasteTemplate.getStyleClass().addAll("retro-btn", "retro-btn-clear");
        btnPasteTemplate.setStyle("-fx-font-size: 10px; -fx-padding: 2 6;");

        Button btnPasteSolution = new Button("📋 Colar Gabarito");
        btnPasteSolution.getStyleClass().addAll("retro-btn", "retro-btn-clear");
        btnPasteSolution.setStyle("-fx-font-size: 10px; -fx-padding: 2 6;");

        Button btnClearCode = new Button("🧼 Limpar");
        btnClearCode.getStyleClass().addAll("retro-btn", "retro-btn-clear");
        btnClearCode.setStyle("-fx-font-size: 10px; -fx-padding: 2 6;");

        HBox.setHgrow(lblEditorTitle, Priority.ALWAYS);
        editorTools.getChildren().addAll(lblEditorTitle, btnPasteTemplate, btnPasteSolution, btnClearCode);

        TextArea txtCode = new TextArea(currentChallenge[0].getDefaultSnippet());
        txtCode.setWrapText(false);
        txtCode.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 12px; -fx-background-color: #0d1117; -fx-text-fill: #00e676;");
        VBox.setVgrow(txtCode, Priority.ALWAYS);

        // Barra de Ação de Teste
        HBox runBar = new HBox(10);
        runBar.setAlignment(Pos.CENTER_LEFT);

        Button btnRunTests = new Button("▶ EXECUTAR E TESTAR CÓDIGO (Ctrl+Enter)");
        btnRunTests.getStyleClass().addAll("retro-btn", "retro-btn-save");
        btnRunTests.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(20, 20);
        spinner.setVisible(false);

        Label lblTestSummary = new Label("Status: Aguardando execução do código.");
        lblTestSummary.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        runBar.getChildren().addAll(btnRunTests, spinner, lblTestSummary);

        // Console de Saída / Logs do Test Runner
        Label lblConsoleTitle = new Label("📟 Console de Testes & Logs do PostgreSQL:");
        lblConsoleTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: #37474f;");

        TextArea txtConsole = new TextArea();
        txtConsole.setEditable(false);
        txtConsole.setPrefRowCount(7);
        txtConsole.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 11px; -fx-background-color: #121212; -fx-text-fill: #81c784;");

        rightPane.getChildren().addAll(editorTools, txtCode, runBar, lblConsoleTitle, txtConsole);

        split.getItems().addAll(scrollLeft, rightPane);
        split.setDividerPositions(0.42);
        root.setCenter(split);

        // ==========================================
        // RODAPÉ: BOTÃO FECHAR
        // ==========================================
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button btnClose = new Button("Fechar");
        btnClose.getStyleClass().addAll("retro-btn", "retro-btn-delete");
        btnClose.setOnAction(e -> {
            if (onUpdateCallback != null) onUpdateCallback.run();
            stage.close();
        });
        footer.getChildren().add(btnClose);
        root.setBottom(footer);

        // ==========================================
        // LÓGICA DE ATUALIZAÇÃO AO TROCAR DE FASE
        // ==========================================
        cbChallenges.setOnAction(e -> {
            Challenge selected = cbChallenges.getValue();
            if (selected != null) {
                currentChallenge[0] = selected;
                lblSubtitle.setText(selected.getSubtitle());
                lblNarrative.setText(selected.getClientNarrative());
                txtObjective.setText(selected.getMissionObjective());
                txtCode.setText(selected.getDefaultSnippet());
                lblReward.setText("Recompensa: " + selected.getRewardCoins() + " 🐚 Conchas");
                updateCriteria.run();
                txtConsole.setText("--- Fase alterada para " + selected.getTitle() + " ---\nClique em 'EXECUTAR E TESTAR' para validar seu código.");
                lblTestSummary.setText("Status: Pronto para testar.");
                lblTestSummary.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #000;");
            }
        });

        btnPasteTemplate.setOnAction(e -> txtCode.setText(currentChallenge[0].getDefaultSnippet()));
        btnPasteSolution.setOnAction(e -> txtCode.setText(currentChallenge[0].getSolutionTemplate()));
        btnClearCode.setOnAction(e -> txtCode.clear());

        // ==========================================
        // DISPARO DA SUÍTE DE TESTES ASSÍNCRONA
        // ==========================================
        Runnable executeChallenge = () -> {
            btnRunTests.setDisable(true);
            spinner.setVisible(true);
            lblTestSummary.setText("⏳ Executando suíte de testes no PostgreSQL...");
            lblTestSummary.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");

            String codeToRun = txtCode.getText();

            new Thread(() -> {
                TestResult result = ChallengeSandboxService.executeAndTest(currentChallenge[0], codeToRun);

                Platform.runLater(() -> {
                    btnRunTests.setDisable(false);
                    spinner.setVisible(false);

                    StringBuilder sb = new StringBuilder();
                    sb.append("=== RESULTADO DA SUÍTE DE TESTES (").append(result.getExecutionTimeMs()).append("ms) ===\n");
                    for (String log : result.getLogs()) {
                        sb.append(log).append("\n");
                    }
                    if (result.getErrorMessage() != null) {
                        sb.append("\nERRO: ").append(result.getErrorMessage()).append("\n");
                    }
                    txtConsole.setText(sb.toString());

                    if (result.isAllPassed()) {
                        lblTestSummary.setText("🎉 SUCESSO! 100% DOS TESTES APROVADOS! +" + currentChallenge[0].getRewardCoins() + " 🐚");
                        lblTestSummary.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");

                        SoundManager.play(SoundManager.GARY_MEOW);

                        if (!EmpireState.isChallengeCompleted(currentChallenge[0].getId())) {
                            EmpireState.markChallengeCompleted(currentChallenge[0].getId());
                            EmpireState.addCoins(currentChallenge[0].getRewardCoins());
                            lblRank.setText("Cargo: " + EmpireState.getDeveloperRank());
                            cbChallenges.setItems(null);
                            cbChallenges.setItems(javafx.collections.FXCollections.observableArrayList(challengeList));
                            cbChallenges.getSelectionModel().select(currentChallenge[0]);

                            AlertUtil.showInfo(
                                "🎉 PARABÉNS, DESENVOLVEDOR DA FENDA!\n\n" +
                                "Você completou com louvor a " + currentChallenge[0].getTitle() + "!\n" +
                                "O Seu Siriguejo depositou +" + currentChallenge[0].getRewardCoins() + " 🐚 Conchas no seu saldo corporativo!\n" +
                                "Seu novo cargo: " + EmpireState.getDeveloperRank()
                            );
                        }

                        if (onUpdateCallback != null) onUpdateCallback.run();
                    } else {
                        lblTestSummary.setText("❌ FALHA: " + result.getPassedTests() + "/" + result.getTotalTests() + " testes passaram.");
                        lblTestSummary.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #c62828;");
                        SoundManager.play(SoundManager.FAIL);
                    }
                });
            }).start();
        };

        btnRunTests.setOnAction(e -> executeChallenge.run());

        // Atalho Ctrl+Enter no editor
        txtCode.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                executeChallenge.run();
            }
        });

        Scene scene = new Scene(root, 960, 620);
        try {
            scene.getStylesheets().add(CodeChallengeDialog.class.getResource("/com/template/spongebob/spongebob.css").toExternalForm());
        } catch (Exception ignored) {}

        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }
}
