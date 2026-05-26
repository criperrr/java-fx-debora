package com.template;

import com.sun.media.jfxmedia.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import com.template.ShopItemDTO;
import com.template.ShopItemDAO;

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

    private ShopItemDAO itemDAO = new ShopItemDAO();

    @FXML
    void onSave() {
        try {
            String name = txtName.getText();

        } catch(Exception e) {

        }
    }

    @FXML
    void onDelete(ActionEvent event) {
        System.out.println("Botão Excluir clicado!");
    }

    @FXML
    void onClear(ActionEvent event) {
        txtId.clear();
        txtName.clear();
        txtDescription.clear();
        txtPrice.clear();
    }

    @FXML
    public void initialize() {
    }
}