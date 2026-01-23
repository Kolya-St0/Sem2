package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Converter extends JFrame {
    private JComboBox<String> source_currency_ComboBox;
    private JComboBox<String> target_currency_ComboBox;
    private JTextField amount_field;
    private JButton convert_button;
    private JLabel result_label;
    private Properties exchange_rates;

    public Converter() {
        setTitle("Currency converter");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        loadExchangeRates();

        initComponents();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadExchangeRates() {
        exchange_rates = new Properties();
        try {
            FileInputStream fis = new FileInputStream("rates.properties");
            exchange_rates.load(fis);
            fis.close();
            System.out.println("Currency's courses loaded successfully");
        }
        catch (IOException e) {JOptionPane.showMessageDialog(this,
                "Error loading file rates.properties: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Value:"), gbc);

        gbc.gridx = 1;
        amount_field = new JTextField(10);
        add(amount_field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("From:"), gbc);

        gbc.gridx = 1;
        String[] currencies = {"USD", "EUR", "RUB"};
        source_currency_ComboBox = new JComboBox<>(currencies);
        add(source_currency_ComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("To:"), gbc);

        gbc.gridx = 1;
        target_currency_ComboBox = new JComboBox<>(currencies);
        add(target_currency_ComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        convert_button = new JButton("Convert");
        add(convert_button, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        result_label = new JLabel("Result: ");
        result_label.setFont(new Font("Arial", Font.BOLD, 14));
        add(result_label, gbc);

        convert_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });
    }

    private void convertCurrency() {
        try {
            String source_сurrency = (String) source_currency_ComboBox.getSelectedItem();
            String target_сurrency = (String) target_currency_ComboBox.getSelectedItem();
            double amount = Double.parseDouble(amount_field.getText());

            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    Thread.sleep(1000);

                    String rateKey = source_сurrency + "_TO_" + target_сurrency;
                    String rateStr = exchange_rates.getProperty(rateKey);

                    if(amount <= 0) {
                        return "Enter positive value";
                    }

                    if (source_сurrency.equals(target_сurrency)) {
                        rateStr = "1.0";
                    }

                    if (rateStr == null) {
                        return "Failed to found course";
                    }

                    double rate = Double.parseDouble(rateStr);
                    double result = amount * rate;

                    return String.format("%.2f %s = %.2f %s",
                            amount, source_сurrency, result, target_сurrency);
                }

                @Override
                protected void done() {
                    try {
                        String result = get();
                        result_label.setText("Result: " + result);
                    } catch (InterruptedException | ExecutionException e) {
                        result_label.setText("Error: " + e.getMessage());
                    }
                }
            };

            worker.execute();
            convert_button.setEnabled(false);
            result_label.setText("Converting...");

            worker.addPropertyChangeListener(evt -> {
                if ("state".equals(evt.getPropertyName())
                        && SwingWorker.StateValue.DONE == evt.getNewValue()) {
                    convert_button.setEnabled(true);
                }
            });

        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input error, repeat",
                    "Input error", JOptionPane.ERROR_MESSAGE);
        }
    }
}