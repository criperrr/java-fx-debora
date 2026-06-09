package com.template;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private TextField txtDescription;
    @FXML private TextField txtPrice;
    @FXML private TextField txtSearch;
    @FXML private Button btnSave;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;
    @FXML private TableView<ShopItemDTO> tableItems;
    @FXML private TableColumn<ShopItemDTO, Integer> colId;
    @FXML private TableColumn<ShopItemDTO, String> colName;
    @FXML private TableColumn<ShopItemDTO, String> colDescription;
    @FXML private TableColumn<ShopItemDTO, String> colPrice;

    private final ShopItemDAO itemDAO = new ShopItemDAO();
    private final ObservableList<ShopItemDTO> masterData = FXCollections.observableArrayList();

    // css do tema escuro para aplicar nos dialogs
    private static final String DARK_CSS = "/com/template/dark.css";

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        // formata preco como R$
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

        // filtro de busca em tempo real
        FilteredList<ShopItemDTO> filteredData = new FilteredList<>(masterData, p -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(item -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return item.getName().toLowerCase().contains(filter)
                    || (item.getDescription() != null && item.getDescription().toLowerCase().contains(filter))
                    || String.valueOf(item.getId()).contains(filter);
            });
        });

        SortedList<ShopItemDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableItems.comparatorProperty());
        tableItems.setItems(sortedData);

        // selecao preenche o formulario para edicao
        tableItems.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                txtId.setText(String.valueOf(selected.getId()));
                txtName.setText(selected.getName());
                txtDescription.setText(selected.getDescription());
                txtPrice.setText(selected.getPrice());
                btnSave.setText("Atualizar");
                btnSave.setId("btnUpdate");
            }
        });

        loadItems();
    }

    @FXML
    void onSave(ActionEvent event) {
        String name     = txtName.getText().trim();
        String desc     = txtDescription.getText().trim();
        String priceStr = txtPrice.getText().trim().replace(",", ".");

        if (name.isEmpty())    { showAlert(Alert.AlertType.ERROR, "nome e obrigatorio");  return; }
        if (priceStr.isEmpty()) { showAlert(Alert.AlertType.ERROR, "preco e obrigatorio"); return; }

        try {
            Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "preco invalido - use ponto ou virgula como separador decimal");
            return;
        }

        try {
            String idStr = txtId.getText().trim();
            if (!idStr.isEmpty()) {
                itemDAO.updateShopItem(new ShopItemDTO(Integer.parseInt(idStr), name, desc, priceStr));
            } else {
                itemDAO.createShopItem(new ShopItemDTO(name, desc, priceStr));
            }
            onClear(null);
            loadItems();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    void onDelete(ActionEvent event) {
        ShopItemDTO selected = tableItems.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "selecione um item na tabela para excluir");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("confirmar exclusao");
        confirm.setHeaderText(null);
        confirm.setContentText("excluir \"" + selected.getName() + "\"?");
        applyDarkTheme(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                itemDAO.deleteShopItem(selected.getId());
                onClear(null);
                loadItems();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "erro ao excluir: " + e.getMessage());
            }
        }
    }

    @FXML
    void onClear(ActionEvent event) {
        txtId.clear();
        txtName.clear();
        txtDescription.clear();
        txtPrice.clear();
        txtSearch.clear();
        tableItems.getSelectionModel().clearSelection();
        btnSave.setText("Salvar");
        btnSave.setId("btnSave");
    }

    private void loadItems() {
        try {
            List<ShopItemDTO> items = itemDAO.getAllShopItems();
            masterData.setAll(items);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "erro ao carregar dados: " + e.getMessage());
        }
    }

    // cria alert com tema escuro aplicado
    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.WARNING ? "aviso" : "erro");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        applyDarkTheme(alert);
        alert.showAndWait();
    }

    // aplica o css escuro no dialog (dialogs nao herdam o css da cena principal)
    private void applyDarkTheme(Alert alert) {
        String css = getClass().getResource(DARK_CSS).toExternalForm();
        alert.getDialogPane().getStylesheets().add(css);
    }
}
