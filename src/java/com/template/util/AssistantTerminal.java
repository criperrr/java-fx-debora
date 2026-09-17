package com.template.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.template.model.EmpireState;
import com.template.model.dto.ShopItemDTO;

/**
 * Terminal do Peixe Assistente:
 * Simula a situação-problema dos desenvolvedores do Siri Cascudo para coletar
 * demandas e cadastrar produtos das necessidades reais dos cidadãos da Fenda do Biquíni.
 */
public class AssistantTerminal {

    public static class ClientRequest {
        public final String clientName;
        public final String clientRole;
        public final String imagePath;
        public final String problemDescription;
        public final String dialogQuote;
        public final ShopItemDTO itemToRegister;
        public final String soundEffect;

        public ClientRequest(String clientName, String clientRole, String imagePath,
                             String problemDescription, String dialogQuote,
                             ShopItemDTO itemToRegister, String soundEffect) {
            this.clientName = clientName;
            this.clientRole = clientRole;
            this.imagePath = imagePath;
            this.problemDescription = problemDescription;
            this.dialogQuote = dialogQuote;
            this.itemToRegister = itemToRegister;
            this.soundEffect = soundEffect;
        }
    }

    public static void show(Stage parentStage, Consumer<ShopItemDTO> onRegisterCallback) {
        List<ClientRequest> requests = getClients();

        Stage stage = new Stage();
        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("🐟 Terminal do Peixe Assistente - Central de Demandas da Fenda");

        int[] currentIndex = {0};

        BorderPane root = new BorderPane();
        root.getStyleClass().add("retro-window-panel");
        root.setPadding(new Insets(15));

        // Topo: Cabeçalho com contexto de engenharia de software
        VBox header = new VBox(4);
        Label lblTitle = new Label("📋 SISTEMA DE COLETA DE CLIENTES REAIS (SIRI CASCUDO OS 2000)");
        lblTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #b71c1c;");
        Label lblSubtitle = new Label("Desafio dos Desenvolvedores: Coletar necessidades e registrar no PostgreSQL.");
        lblSubtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #01579b; -fx-font-weight: bold;");
        header.getChildren().addAll(lblTitle, lblSubtitle);
        root.setTop(header);

        // Centro: Perfil do Cliente + Avatar + Diálogo + Demanda
        HBox center = new HBox(15);
        center.setPadding(new Insets(12, 0, 12, 0));
        center.setAlignment(Pos.CENTER_LEFT);

        ImageView imgAvatar = new ImageView();
        imgAvatar.setFitHeight(180);
        imgAvatar.setPreserveRatio(true);
        imgAvatar.setSmooth(true);

        VBox infoBox = new VBox(6);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label lblClient = new Label();
        lblClient.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #b71c1c;");

        Label lblRole = new Label();
        lblRole.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #00695c;");

        Label lblQuote = new Label();
        lblQuote.setWrapText(true);
        lblQuote.setStyle("-fx-font-style: italic; -fx-font-size: 12px; -fx-text-fill: #3e2723; -fx-background-color: #fff9c4; -fx-padding: 6; -fx-border-color: #fbc02d; -fx-border-radius: 4; -fx-background-radius: 4;");

        Label lblProblemTitle = new Label("⚙️ Necessidade de Sistema / Demanda de Dados:");
        lblProblemTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");

        TextArea txtProblem = new TextArea();
        txtProblem.setEditable(false);
        txtProblem.setWrapText(true);
        txtProblem.setPrefRowCount(3);
        txtProblem.setStyle("-fx-font-size: 11px; -fx-font-family: 'Comic Sans MS', sans-serif;");

        Label lblItemPreview = new Label();
        lblItemPreview.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");

        infoBox.getChildren().addAll(lblClient, lblRole, lblQuote, lblProblemTitle, txtProblem, lblItemPreview);
        center.getChildren().addAll(imgAvatar, infoBox);
        root.setCenter(center);

        // Rodapé: Navegação e Ações
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button btnPrev = new Button("◀ Cliente Anterior");
        btnPrev.getStyleClass().addAll("retro-btn", "retro-btn-clear");

        Button btnNext = new Button("Próximo Cliente ▶");
        btnNext.getStyleClass().addAll("retro-btn", "retro-btn-clear");

        Button btnProcess = new Button("💾 Processar e Salvar no Banco");
        btnProcess.getStyleClass().addAll("retro-btn", "retro-btn-save");

        Button btnClose = new Button("Fechar");
        btnClose.getStyleClass().addAll("retro-btn", "retro-btn-delete");

        footer.getChildren().addAll(btnPrev, btnNext, btnProcess, btnClose);
        root.setBottom(footer);

        Runnable updateView = () -> {
            ClientRequest req = requests.get(currentIndex[0]);
            try {
                imgAvatar.setImage(new Image(AssistantTerminal.class.getResourceAsStream(req.imagePath)));
            } catch (Exception ignored) {}
            lblClient.setText(req.clientName);
            lblRole.setText("Setor: " + req.clientRole);
            lblQuote.setText("\"" + req.dialogQuote + "\"");
            txtProblem.setText(req.problemDescription);
            lblItemPreview.setText("📦 Pedido: " + req.itemToRegister.getName() + " | " + req.itemToRegister.getRarity() + " | R$ " + req.itemToRegister.getPrice());
        };

        btnPrev.setOnAction(e -> {
            if (currentIndex[0] > 0) {
                currentIndex[0]--;
                updateView.run();
                SoundManager.play(SoundManager.WALK);
            }
        });

        btnNext.setOnAction(e -> {
            if (currentIndex[0] < requests.size() - 1) {
                currentIndex[0]++;
                updateView.run();
                SoundManager.play(SoundManager.WALK);
            }
        });

        btnProcess.setOnAction(e -> {
            ClientRequest req = requests.get(currentIndex[0]);
            if (onRegisterCallback != null) {
                onRegisterCallback.accept(req.itemToRegister);
            }
            long reward = 50;
            if (EmpireState.hasConnectionPool()) reward *= 2;
            if (EmpireState.hasFirewallPlankton()) reward = (long)(reward * 1.5);
            EmpireState.addCoins(reward);
            EmpireState.incrementOrdersFulfilled();
            SoundManager.play(req.soundEffect);
            AlertUtil.showInfo("Demanda de " + req.clientName + " processada com sucesso!\n" +
                "Item '" + req.itemToRegister.getName() + "' cadastrado no PostgreSQL!\n" +
                "💰 Você recebeu +" + reward + " 🐚 Conchas de Ouro pelo atendimento!");
        });

        btnClose.setOnAction(e -> stage.close());

        updateView.run();

        Scene scene = new Scene(root, 720, 480);
        try {
            scene.getStylesheets().add(AssistantTerminal.class.getResource("/com/template/spongebob/spongebob.css").toExternalForm());
        } catch (Exception ignored) {}

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private static List<ClientRequest> getClients() {
        List<ClientRequest> list = new ArrayList<>();

        list.add(new ClientRequest(
            "Sr. Siriguejo (Eugene H. Krabs)",
            "Diretoria do Siri Cascudo 🦀",
            "/com/template/spongebob/images/siriguejo_sem_fundo.png",
            "REQUISITO: Corte de custos extremo e cobrança de taxa de respiração. Demanda registrar um produto ultra lucrativo com margem de 300%.",
            "Menino estagiário! Coloque no sistema agora o Hambúrguer Econômico do Chefe! Não gaste papel nem guardanapo!",
            new ShopItemDTO("Hambúrguer Econômico do Siriguejo", "Siri Cascudo 🍔", "Hambúrguer com pão aerado para maximizar 300% de margem de lucro sem gastar siri.", "Lendário ⭐", "9.99"),
            SoundManager.STANK_NOISE
        ));

        list.add(new ClientRequest(
            "Sandy Bochechas (Sandy Cheeks)",
            "Domo da Árvore / Texas 🐿️",
            "/com/template/spongebob/images/sandy_sem_fundo.png",
            "REQUISITO: Provisões para atletas espaciais. Embalagem hermética para suportar a câmara de ar seco do Domo sem murchar.",
            "Como vão, parceiros! Preciso de comida com alto teor proteico pro meu treino bruto de caratê e ciência!",
            new ShopItemDTO("Nozes do Texas Desidratadas", "Domo da Árvore 🐿️", "Nozes hipercalóricas com tempero texano para suportar treinos pesados de caratê.", "Raro 💎", "28.50"),
            SoundManager.GARY_MEOW
        ));

        list.add(new ClientRequest(
            "Sheldon J. Plankton",
            "Laboratório do Balde de Lixo 🔬",
            "/com/template/spongebob/images/plankton_sem_fundo.png",
            "REQUISITO: Tentativa de injeção de pedido fraudulento para tentar extrair a lista secreta de condimentos do Siri Cascudo.",
            "Ei, peixe estagiário inútil! Me dê um hambúrguer para viagem... e certifique-se de listar todos os 27 ingredientes na nota!",
            new ShopItemDTO("Isca Radioativa do Plankton", "Balde de Lixo 🪣", "Substância verde brilhante criada para espionagem industrial e controle mental.", "Proibido ☠️", "0.05"),
            SoundManager.FAIL
        ));

        list.add(new ClientRequest(
            "Sra. Puff",
            "Escola de Pilotagem de Barcos 🐡",
            "/com/template/spongebob/images/sra_puff_sem_fundo.png",
            "REQUISITO: Alimento anti-estresse e calmante para evitar inflar e destruir a sala de exames após as aulas de trânsito do Bob Esponja.",
            "Oh céus... Minha pressão está nas alturas! Me dê algo bem doce e relaxante antes que eu infle como um dirigível!",
            new ShopItemDTO("Bolo Calmante de Algas da Sra. Puff", "Lagoa Goo 🏖️", "Receita terapêutica relaxante para instrutores de direção à beira de um colapso.", "Raro 💎", "16.00"),
            SoundManager.SHIVER
        ));

        list.add(new ClientRequest(
            "Peixe Fred (\"Minha Perna!\")",
            "Hospital Geral da Fenda 🩹",
            "/com/template/spongebob/images/bob_esponja_sem_fundo.png",
            "REQUISITO: Entrega expressa na ala ortopédica. Refeição leve com canudo articulado que possa ser consumida em repouso absoluto.",
            "MINHA PERNA! A porta do restaurante bateu no meu joelho de novo! Me entreguem um caldo na cama 4 do hospital!",
            new ShopItemDTO("Caldo de Siri Restaurador Ortopédico", "Siri Cascudo 🍔", "Sopa nutritiva servida com canudo flexível para clientes com pernas engessadas.", "Comum 📦", "12.00"),
            SoundManager.BOOWOMP
        ));

        return list;
    }
}
