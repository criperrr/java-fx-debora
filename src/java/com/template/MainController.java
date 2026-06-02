package com.template;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txtPrice;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnClear;
    @FXML
    private TableView<ShopItemDTO> tableItems;
    @FXML
    private TableColumn<ShopItemDTO, Integer> colId;
    @FXML
    private TableColumn<ShopItemDTO, String> colName;
    @FXML
    private TableColumn<ShopItemDTO, String> colDescription;
    @FXML
    private TableColumn<ShopItemDTO, String> colPrice;
    @FXML
    private VBox mainVBox;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ComboBox<String> themeComboBox;
    @FXML
    private StackPane rootStackPane;

    private ShopItemDAO itemDAO = new ShopItemDAO();
    private ObservableList<ShopItemDTO> masterData = FXCollections.observableArrayList();

    public void setInitialData(List<ShopItemDTO> initialData) {
        masterData.setAll(initialData);
        progressIndicator.setVisible(false);
        mainVBox.setDisable(false);
        mainVBox.setOpacity(1.0);
    }

    @FXML
    void onSave(ActionEvent event) {
        try {
            String name = txtName.getText();
            String description = txtDescription.getText();
            String priceStr = txtPrice.getText();

            if (name == null || name.trim().isEmpty()) {
                showError("O campo Nome é obrigatório.");
                return;
            }
            if (priceStr == null || priceStr.trim().isEmpty()) {
                showError("O campo Preço é obrigatório.");
                return;
            }
            
            priceStr = priceStr.replace(",", ".");
            try {
                Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                showError("O Preço deve ser um valor numérico válido.");
                return;
            }

            String idStr = txtId.getText();

            if (idStr != null && !idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                ShopItemDTO item = new ShopItemDTO(id, name, description, priceStr);
                itemDAO.updateShopItem(item);
            } else {
                ShopItemDTO item = new ShopItemDTO(name, description, priceStr);
                itemDAO.createShopItem(item);
            }

            onClear(null);
            loadItems();

        } catch(Exception e) {
            showError("Ocorreu um erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro de Validação");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void onDelete(ActionEvent event) {
        ShopItemDTO selectedItem = tableItems.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Exclusão");
            alert.setHeaderText("Tem certeza que deseja excluir este item?");
            alert.setContentText("Item: " + selectedItem.getName());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    itemDAO.deleteShopItem(selectedItem.getId());
                    loadItems();
                    onClear(null);
                } catch (Exception e) {
                    showError("Erro ao deletar: " + e.getMessage());
                }
            }
        } else {
             Alert alert = new Alert(Alert.AlertType.WARNING);
             alert.setTitle("Nenhum item selecionado");
             alert.setHeaderText(null);
             alert.setContentText("Por favor, selecione um item na tabela para excluir.");
             alert.showAndWait();
        }
    }

    @FXML
    void onClear(ActionEvent event) {
        txtId.clear();
        txtName.clear();
        txtDescription.clear();
        txtPrice.clear();
        tableItems.getSelectionModel().clearSelection();
        btnSave.setId("btnSave");
        btnSave.setText("Salvar");
    }

    @FXML
    public void initialize() {
        mainVBox.setDisable(true);
        mainVBox.setOpacity(0.5);
        progressIndicator.setVisible(true);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        colPrice.setCellFactory(tc -> new TableCell<ShopItemDTO, String>() {
            @Override
            protected void updateItem(String price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    try {
                        double val = Double.parseDouble(price.replace(",", "."));
                        setText(String.format("R$ %.2f", val));
                    } catch (NumberFormatException e) {
                         setText(price);
                    }
                }
            }
        });

        FilteredList<ShopItemDTO> filteredData = new FilteredList<>(masterData, p -> true);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } else if (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } else if (String.valueOf(item.getId()).contains(lowerCaseFilter)) {
                     return true;
                }
                return false;
            });
        });

        SortedList<ShopItemDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableItems.comparatorProperty());
        tableItems.setItems(sortedData);

        tableItems.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtId.setText(String.valueOf(newSelection.getId()));
                txtName.setText(newSelection.getName());
                txtDescription.setText(newSelection.getDescription());
                txtPrice.setText(newSelection.getPrice());
                btnSave.setText("Atualizar");
                btnSave.setId("btnUpdate");
            }
        });

        themeComboBox.getItems().addAll("Claro", "Escuro");
        themeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldTheme, newTheme) -> {
            if (newTheme != null) {
                applyTheme(newTheme);
            }
        });
    }

    // Novo método para configurações que dependem da Scene estar disponível
    public void postInitialize() {
        themeComboBox.getSelectionModel().select("Claro");
        applyTheme("Claro");
    }

    private void applyTheme(String themeName) {
        if (rootStackPane.getScene() == null) {
            System.err.println("Erro: Scene é nula ao tentar aplicar o tema.");
            return;
        }
        ObservableList<String> stylesheets = rootStackPane.getScene().getStylesheets();
        stylesheets.clear();
        stylesheets.add(getClass().getResource("/com/template/base.css").toExternalForm());
        if ("Claro".equals(themeName)) {
            stylesheets.add(getClass().getResource("/com/template/light.css").toExternalForm());
        } else if ("Escuro".equals(themeName)) {
            stylesheets.add(getClass().getResource("/com/template/dark.css").toExternalForm());
        }
    }

    private void loadItems() {
        progressIndicator.setVisible(true);
        mainVBox.setDisable(true);
        mainVBox.setOpacity(0.5);

        Task<List<ShopItemDTO>> task = new Task<List<ShopItemDTO>>() {
            @Override
            protected List<ShopItemDTO> call() throws Exception {
                return itemDAO.getAllShopItems();
            }
        };

        task.setOnSucceeded(e -> {
            masterData.setAll(task.getValue());
            progressIndicator.setVisible(false);
            mainVBox.setDisable(false);
            mainVBox.setOpacity(1.0);
        });

        task.setOnFailed(e -> {
            showError("Falha ao recarregar dados do banco.");
            progressIndicator.setVisible(false);
            mainVBox.setDisable(false);
            mainVBox.setOpacity(1.0);
        });

        new Thread(task).start();
    }
}
