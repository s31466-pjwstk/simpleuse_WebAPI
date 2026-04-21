package zad1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends javax.swing.JFrame {
    private JTextField temperaturaInfo;
    private JTextField walutaKurs;
    private JTextField PLNkurs;
    private JPanel mainPanel = new JPanel(new BorderLayout());
    private JPanel rightPanel = new JPanel(new BorderLayout());
    private JPanel leftPanel = new JPanel();
    private JPanel fx;
    private JTextField newTemperaturaMiasto;
    private JTextField newTemperaturaKraj;
    private JTextField newWalutaKurs;
    private JButton zmienMiasto = new JButton("Zmień miasto");
    private JButton zmienWalute = new JButton("Zmień walutę");
    private JFXPanel jfxPanel;
    public Service s;
    private WebEngine webEngine;

    public GUI(Service s, String weather, Double rate1, Double rate2) {
        this.s = s;
        rightPanel.setPreferredSize(new Dimension(400, 600));
        leftPanel.setPreferredSize(new Dimension(500, 600));
        temperaturaInfo = new JTextField("Temperatura w "+this.s.city+" to: "+ s.weather);
        temperaturaInfo.setEnabled(false);
        temperaturaInfo.setDisabledTextColor(Color.BLACK);
        walutaKurs = new JTextField("Kurs lokalnej waluty "+this.s.localCurrency+" do podanej "+this.s.otherCurrencyCode+" to: "+rate1.toString());
        walutaKurs.setEnabled(false);
        walutaKurs.setDisabledTextColor(Color.BLACK);
        PLNkurs = new JTextField("Kurs PLN do lokalnej waluty "+this.s.localCurrency+" to: "+rate2.toString());
        PLNkurs.setEnabled(false);
        PLNkurs.setDisabledTextColor(Color.BLACK);

        newTemperaturaMiasto = new JTextField();
        addPlaceholder(newTemperaturaMiasto, "Wpisz nowe miasto...");
        newTemperaturaMiasto.setPreferredSize(new Dimension(200, 30));
        newTemperaturaKraj = new JTextField();
        addPlaceholder(newTemperaturaKraj, "Wpisz nowy kraj...");
        newTemperaturaKraj.setPreferredSize(new Dimension(200, 30));
        newWalutaKurs = new JTextField();
        addPlaceholder(newWalutaKurs, "Wpisz nową walutę...");
        newWalutaKurs.setPreferredSize(new Dimension(200, 30));
        zmienMiasto.addActionListener(new ActionListener() {

            @Override
        public void actionPerformed(ActionEvent e) {
            if(!(newTemperaturaMiasto.getText().isEmpty()|| newTemperaturaKraj.getText().isEmpty()||newTemperaturaMiasto.getText().equals("Wpisz nowe miasto...")||newTemperaturaKraj.getText().equals("Wpisz nowy kraj..."))) {
                String city = newTemperaturaMiasto.getText();
                String country = newTemperaturaKraj.getText();
                GUI.this.s = new Service(country);
                if (GUI.this.s.countryCode != null) {
                    String weather = GUI.this.s.getWeather(city);
                    Double rate = GUI.this.s.getRateFor(GUI.this.s.otherCurrencyCode);
                    Double rateNBP = GUI.this.s.getNBPRate();
                    if (GUI.this.s.city!=null) {
                        temperaturaInfo.setText("Temperatura w " + GUI.this.s.city + " to: " + s.weather);
                        walutaKurs.setText("Kurs lokalnej waluty "+GUI.this.s.localCurrency+" do podanej " + GUI.this.s.otherCurrencyCode + " to: " + rate.toString());
                        PLNkurs.setText("Kurs PLN do lokalnej waluty " + GUI.this.s.localCurrency + " to: " + rateNBP.toString());
                        newWalutaKurs.setText("Wpisz nową walutę...");
                        newWalutaKurs.setForeground(Color.GRAY);
                        newTemperaturaMiasto.setText("Wpisz nowe miasto...");
                        newTemperaturaMiasto.setForeground(Color.GRAY);
                        newTemperaturaKraj.setText("Wpisz nowy kraj...");
                        newTemperaturaKraj.setForeground(Color.GRAY);
                        Platform.runLater(() -> {
                            webEngine.load("https://en.wikipedia.org/wiki/" + GUI.this.s.city);
                        });
                    } else
                        JOptionPane.showMessageDialog(GUI.this, "Invalid city!", "Error", JOptionPane.ERROR_MESSAGE);
                } else
                    JOptionPane.showMessageDialog(GUI.this, "Invalid country!", "Error", JOptionPane.ERROR_MESSAGE);
            }else
                JOptionPane.showMessageDialog(GUI.this, "Provide both city and country", "Error", JOptionPane.ERROR_MESSAGE);
        }});

        zmienWalute.addActionListener(new ActionListener() {
            @Override
        public void actionPerformed(ActionEvent e) {
            String currency = newWalutaKurs.getText();
            Double rate = GUI.this.s.getRateFor(currency);
            if(rate!=0.0) {
                walutaKurs.setText("Kurs lokalnej waluty "+GUI.this.s.localCurrency+" do podanej " + GUI.this.s.otherCurrencyCode + " to: " + rate.toString());
                newWalutaKurs.setText("Wpisz nową walutę...");
                newWalutaKurs.setForeground(Color.GRAY);
            }else
                JOptionPane.showMessageDialog(GUI.this, "Invalid currency!", "Error", JOptionPane.ERROR_MESSAGE);
        }});


        fx = new JPanel(new BorderLayout());

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 600);
        this.add(mainPanel);
        jfxPanel = new JFXPanel();
        Platform.runLater(this::createJFXContent);
        this.fx.add(jfxPanel);

        mainPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        leftPanel.setLayout(new GridLayout(8,1));
        leftPanel.add(temperaturaInfo);
        leftPanel.add(walutaKurs);
        leftPanel.add(PLNkurs);
        leftPanel.add(newTemperaturaMiasto);
        leftPanel.add(newTemperaturaKraj);
        leftPanel.add(zmienMiasto);
        leftPanel.add(newWalutaKurs);
        leftPanel.add(zmienWalute);
        rightPanel.add(fx);

        this.setVisible(true);
    }

    private void createJFXContent() {
        WebView webView = new WebView();
        this.webEngine = webView.getEngine();
        this.webEngine.load("https://en.wikipedia.org/wiki/"+s.city);
        Scene scene = new Scene(webView);
        jfxPanel.setScene(scene);
    }

    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }


}
