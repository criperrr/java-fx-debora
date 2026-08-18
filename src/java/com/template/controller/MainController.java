package com.template.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import com.template.model.dto.ShopItemDTO;
import com.template.service.ShopItemService;
import com.template.util.AlertUtil;
import com.template.util.FormatUtil;
import com.template.validation.ValidationException;

/**
 * Controller: focado exclusivamente no gerenciamento da View e interação com o usuário (UI/Eventos).
 * Service (ShopItemService): orquestração das regras de negócio e operações de CRUD.
 * Validator (ShopItemValidator): validações de integridade e dados de entrada.
 * Util (FormatUtil, AlertUtil): formatações de valores e exibição padronizada de diálogos/alertas.
 * DAO (ShopItemDAO): persistência e comunicação direta com o banco de dados.
 */
public class MainController {

    // Componentes visuais gerenciados pelo Controller (responsabilidade da View/UI)
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

    // Regras de negócio e persistência delegadas para a camada de serviço
    private final ShopItemService itemService;
    private final ObservableList<ShopItemDTO> masterData = FXCollections.observableArrayList();

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
        loadItems();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Responsabilidade de formatação de exibição de moeda: movida/delegada para FormatUtil
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
            || String.valueOf(item.getId()).contains(lowerFilter);
    }

    private void setupSelectionListener() {
        tableItems.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                populateForm(selected);
            }
        });
    }

    private void populateForm(ShopItemDTO item) {
        txtId.setText(String.valueOf(item.getId()));
        txtName.setText(item.getName());
        txtDescription.setText(item.getDescription());
        txtPrice.setText(item.getPrice());
        btnSave.setText("Atualizar");
        btnSave.setId("btnUpdate");
    }

    @FXML
    void onSave(ActionEvent event) {
        try {
            // Responsabilidade de negócio, validação e persistência: movido/delegado para ShopItemService
            itemService.saveItem(
                txtId.getText(),
                txtName.getText(),
                txtDescription.getText(),
                txtPrice.getText()
            );
            onClear(null);
            loadItems();
        } catch (ValidationException e) {
            // Responsabilidade de validação: tratada no ShopItemValidator e capturada aqui
            // Responsabilidade de exibição de alertas visuais: movido/delegado para AlertUtil
            AlertUtil.showError(e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    void onDelete(ActionEvent event) {
        ShopItemDTO selected = tableItems.getSelectionModel().getSelectedItem();
        if (selected == null) {
            // Responsabilidade de alerta de aviso: movido/delegado para AlertUtil
            AlertUtil.showWarning("selecione um item na tabela para excluir");
        } else {
            // Responsabilidade de diálogo modal de confirmação: movido/delegado para AlertUtil
            boolean confirmed = AlertUtil.showConfirmation(
                "confirmar exclusao",
                "excluir \"" + selected.getName() + "\"?"
            );

            if (confirmed) {
                try {
                    // Responsabilidade de exclusão e acesso a dados: movido/delegado para ShopItemService
                    itemService.deleteItem(selected.getId());
                    onClear(null);
                    loadItems();
                } catch (Exception e) {
                    AlertUtil.showError("erro ao excluir: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    void onClear(ActionEvent event) {
        // Responsabilidade da View: limpar os campos do formulário e resetar estado visual
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
            // Responsabilidade de busca de dados: delegada para ShopItemService
            masterData.setAll(itemService.getAllItems());
        } catch (Exception e) {
            AlertUtil.showError("erro ao carregar dados: " + e.getMessage());
        }
    }
}
