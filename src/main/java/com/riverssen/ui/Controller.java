/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Riverssen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.riverssen.ui;

import com.riverssen.core.RiverCoin;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.security.Wallet;
import com.riverssen.core.transactions.TXIList;
import com.riverssen.core.transactions.Transaction;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class Controller implements Initializable
{
    @FXML
    ToggleButton my_wallet;
    @FXML
    ToggleButton transactions;
    @FXML
    ToggleButton settings;
    @FXML
    ToggleButton dark_theme;
    @FXML
    ToggleButton light_theme;

    @FXML
    TabPane tabpane;

    @FXML
    Pane main;

    @FXML
    ListView keypair_list;

    @FXML
    ListView txlist;

    public void selectMyWallet()
    {
        my_wallet.setSelected(true);
        transactions.setSelected(false);
        settings.setSelected(false);

        tabpane.getSelectionModel().select(0);

        keypair_list.getItems().add("bro");
    }

    public void selectTransactions()
    {
        my_wallet.setSelected(false);
        transactions.setSelected(true);
        settings.setSelected(false);

        tabpane.getSelectionModel().select(1);

        txlist.getItems().add(Math.random() * 10.0 + "" + (int)(Math.random() * 100));
    }

    public void selectSettings()
    {
        my_wallet.setSelected(false);
        transactions.setSelected(false);
        settings.setSelected(true);

        tabpane.getSelectionModel().select(2);
    }

    public void selectDarkTheme()
    {
        main.getStylesheets().set(0, getClass().getResource("style.css").toExternalForm());
        dark_theme.setSelected(true);
        light_theme.setSelected(false);
    }

    public void selectLightTheme()
    {
        main.getStylesheets().set(0, getClass().getResource("light.css").toExternalForm());
        dark_theme.setSelected(false);
        light_theme.setSelected(true);
    }

    public void sendFunds(ContextI context, Wallet from, String to, String amt, String comment)
    {
        TXIList list = new TXIList();
        Transaction trxn = new Transaction(from.getPublicKey().getCompressed(), new PublicAddress(to), list, new
                RiverCoin(amt), comment);
        trxn.sign(from.getPrivateKey());
        context.getTransactionPool().addInternal(trxn);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}