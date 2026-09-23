package com.template.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import com.template.model.dto.ShopItemDTO;
import com.template.service.IShopItemService;
import com.template.service.ShopItemService;
import com.template.util.AlertUtil;
import com.template.util.FormatUtil;
import com.template.validation.IShopItemValidator;
import com.template.validation.ShopItemValidator;

// controller cuida so da tela e dos eventos do usuario. nada de enfiar sql ou validacao pesada aqui dentro.
public class MainController implements Initializable {

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

    // depende sempre das interfaces pro codigo nao ficar engessado
    private final IShopItemService itemService;
    private final IShopItemValidator itemValidator;

    private final ObservableList<ShopItemDTO> masterData = FXCollections.observableArrayList();

    public MainController() {
        this(new ShopItemService(), new ShopItemValidator());
    }

    // injeta servico e validador prontos pra testar ou trocar depois sem dor de cabeca
    public MainController(IShopItemService itemService, IShopItemValidator itemValidator) {
        this.itemService = itemService;
        this.itemValidator = itemValidator;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialize();
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

        // formata moeda na celula sem sujar o objeto original
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
        // se os dados de entrada sao lixo, aborta logo antes de mexer com banco
        if (!itemValidator.validarItem(txtName.getText(), txtPrice.getText())) {
            return;
        }

        // validou certinho, empacota no dto e despacha pro servico se virar
        try {
            String idStr = txtId.getText();
            String normalizedPrice = FormatUtil.normalizePrice(txtPrice.getText());
            String name = txtName.getText() != null ? txtName.getText().trim() : "";
            String desc = txtDescription.getText() != null ? txtDescription.getText().trim() : "";

            if (idStr != null && !idStr.trim().isEmpty()) {
                int id = Integer.parseInt(idStr.trim());
                ShopItemDTO objItem = new ShopItemDTO(id, name, desc, normalizedPrice);
                itemService.atualizarItem(objItem);
                onClear(null);
                loadItems();
                AlertUtil.showInformation("Item atualizado com sucesso!");
            } else {
                ShopItemDTO objItem = new ShopItemDTO(name, desc, normalizedPrice);
                itemService.cadastrarItem(objItem);
                onClear(null);
                loadItems();
                AlertUtil.showInformation("Item cadastrado com sucesso!");
            }
        } catch (Exception e) {
            AlertUtil.showError("erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    void onDelete(ActionEvent event) {
        ShopItemDTO selected = tableItems.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("selecione um item na tabela para excluir");
            return;
        }

        boolean confirmed = AlertUtil.showConfirmation(
            "confirmar exclusao",
            "excluir \"" + selected.getName() + "\"?"
        );

        if (confirmed) {
            try {
                itemService.deletarItem(selected.getId());
                onClear(null);
                loadItems();
                AlertUtil.showInformation("Item excluído com sucesso!");
            } catch (Exception e) {
                AlertUtil.showError("erro ao excluir: " + e.getMessage());
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
            masterData.setAll(itemService.listarItens());
        } catch (Exception e) {
            AlertUtil.showError("erro ao carregar dados: " + e.getMessage());
        }
    }

    public void limparCampos() {
        onClear(null);
    }

    public void listarItens() {
        loadItems();
    }

    public void btnCadastrarAction(ActionEvent event) {
        onSave(event);
    }

    public void btnAtualizarAction(ActionEvent event) {
        onSave(event);
    }

    public void btnExcluirAction(ActionEvent event) {
        onDelete(event);
    }
}
