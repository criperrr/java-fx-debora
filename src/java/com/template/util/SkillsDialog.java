package com.template.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.template.model.EmpireState;

/**
 * Diálogo de Árvore de Habilidades do Banco de Dados PostgreSQL (Tech Tree da Fenda).
 */
public class SkillsDialog {

    public static void show(Stage parentStage, Runnable onUpdateCallback) {
        Stage stage = new Stage();
        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("⚡ Árvore de Habilidades PostgreSQL - Siri Cascudo Empire");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.getStyleClass().add("retro-window-panel");

        // Topo: Saldo de Conchas
        VBox top = new VBox(4);
        Label lblTitle = new Label("🛠️ INFRAESTRUTURA & SKILLS DO BANCO POSTGRESQL");
        lblTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #b71c1c;");

        Label lblCoins = new Label();
        lblCoins.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #e65100;");
        top.getChildren().addAll(lblTitle, lblCoins);
        root.setTop(top);

        // Centro: Grid com as 4 Skills
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(15, 5, 15, 5));

        // Skill 1: B-Tree
        VBox s1Box = createSkillCard(
            "⚡ 1. Índices B-Tree Submarinos",
            "Custo: 100 🐚 | Acelera queries e reduz tempo de pedidos em 40%.",
            EmpireState.hasBtreeIndex(),
            100,
            () -> {
                if (EmpireState.spendCoins(100)) {
                    EmpireState.setBtreeIndex(true);
                    SoundManager.play(SoundManager.GARY_MEOW);
                    return true;
                }
                return false;
            }
        );

        // Skill 2: HikariCP Connection Pool
        VBox s2Box = createSkillCard(
            "🏊 2. Connection Pool Siri-Hikari",
            "Custo: 250 🐚 | Dobra os ganhos de conchas de cada pedido!",
            EmpireState.hasConnectionPool(),
            250,
            () -> {
                if (EmpireState.spendCoins(250)) {
                    EmpireState.setConnectionPool(true);
                    SoundManager.play(SoundManager.GARY_MEOW);
                    return true;
                }
                return false;
            }
        );

        // Skill 3: Auto-Vacuum
        VBox s3Box = createSkillCard(
            "🧹 3. Auto-Vacuum & Trigger Bob Autônomo",
            "Custo: 500 🐚 | Bob Esponja vira 1 hambúrguer e fatura sozinho por segundo!",
            EmpireState.hasAutoVacuum(),
            500,
            () -> {
                if (EmpireState.spendCoins(500)) {
                    EmpireState.setAutoVacuum(true);
                    SoundManager.play(SoundManager.GARY_MEOW);
                    return true;
                }
                return false;
            }
        );

        // Skill 4: Anti-Plankton Firewall
        VBox s4Box = createSkillCard(
            "🛡️ 4. Firewall PL/pgSQL Anti-Plankton",
            "Custo: 1.000 🐚 | Protege o banco de sabotagens e concede +50% de bônus corporativo!",
            EmpireState.hasFirewallPlankton(),
            1000,
            () -> {
                if (EmpireState.spendCoins(1000)) {
                    EmpireState.setFirewallPlankton(true);
                    SoundManager.play(SoundManager.GARY_MEOW);
                    return true;
                }
                return false;
            }
        );

        grid.add(s1Box, 0, 0);
        grid.add(s2Box, 1, 0);
        grid.add(s3Box, 0, 1);
        grid.add(s4Box, 1, 1);
        root.setCenter(grid);

        // Rodapé: Botão Fechar
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button btnClose = new Button("Fechar");
        btnClose.getStyleClass().addAll("retro-btn", "retro-btn-save");
        btnClose.setOnAction(e -> {
            if (onUpdateCallback != null) onUpdateCallback.run();
            stage.close();
        });
        footer.getChildren().add(btnClose);
        root.setBottom(footer);

        Runnable refreshCoins = () -> {
            lblCoins.setText("Saldo Atual: " + EmpireState.getCoins() + " 🐚 Conchas de Ouro");
        };
        refreshCoins.run();

        Scene scene = new Scene(root, 650, 420);
        try {
            scene.getStylesheets().add(SkillsDialog.class.getResource("/com/template/spongebob/spongebob.css").toExternalForm());
        } catch (Exception ignored) {}

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private interface SkillBuyer {
        boolean buy();
    }

    private static VBox createSkillCard(String title, String desc, boolean alreadyHas, int cost, SkillBuyer buyer) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #f57f17; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-radius: 4;");
        card.setPrefWidth(290);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #b71c1c;");

        Label lblDesc = new Label(desc);
        lblDesc.setWrapText(true);
        lblDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: #37474f;");

        Button btnBuy = new Button(alreadyHas ? "✅ Adquirido" : "Comprar (" + cost + " 🐚)");
        btnBuy.getStyleClass().addAll("retro-btn", alreadyHas ? "retro-btn-clear" : "retro-btn-save");
        btnBuy.setDisable(alreadyHas);

        btnBuy.setOnAction(e -> {
            if (buyer.buy()) {
                btnBuy.setText("✅ Adquirido");
                btnBuy.setDisable(true);
                btnBuy.getStyleClass().setAll("retro-btn", "retro-btn-clear");
            } else {
                AlertUtil.showWarning("Conchas insuficientes! Atenda clientes ou frite hambúrgueres para ganhar mais conchas.");
            }
        });

        card.getChildren().addAll(lblTitle, lblDesc, btnBuy);
        return card;
    }
}
