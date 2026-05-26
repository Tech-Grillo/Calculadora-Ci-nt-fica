package view;

import javax.swing.*;

import java.awt.*;

public class CalculadoraView extends JFrame {

    public JTextField tela;

    public JTextArea historico;

    public JButton[] botoes;

    public CalculadoraView() {

        setTitle("Calculadora Científica");

        setSize(750,600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                EXIT_ON_CLOSE);

        setResizable(false);

        Color fundo =
                new Color(80,25,80);

        Color botoesCor =
                new Color(50,50,50);

        Color texto =
                Color.WHITE;

        // TELA
        tela = new JTextField();

        tela.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        30));

        tela.setBackground(Color.BLACK);

        tela.setForeground(Color.GREEN);

        tela.setCaretColor(Color.RED);

        tela.setHorizontalAlignment(
                JTextField.RIGHT);

        tela.setEditable(false);

        tela.setPreferredSize(
                new Dimension(0,80));

        // HISTÓRICO
        historico = new JTextArea();

        historico.setEditable(false);

        historico.setBackground(
                new Color(35,35,35));

        historico.setForeground(
                Color.WHITE);

        historico.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14));

        JScrollPane scroll =
                new JScrollPane(historico);

        scroll.setPreferredSize(
                new Dimension(220,0));

        // PAINEL
        JPanel painel =
                new JPanel();

        painel.setLayout(
                new GridLayout(
                        0,4,10,10));

        painel.setBackground(fundo);

        painel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,10,10,10));

        String[] nomes = {

                "7","8","9","/",
                "4","5","6","*",
                "1","2","3","-",
                "0",".","=","+",
                "C","√","sin","cos",
                "tan","(",")","^",
                "HC"
        };

        botoes =
                new JButton[nomes.length];

        for(int i=0;i<nomes.length;i++){

            botoes[i] =
                    new JButton(nomes[i]);

            botoes[i].setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            22));

            botoes[i].setBackground(
                    botoesCor);

            botoes[i].setForeground(
                    texto);

            botoes[i].setFocusPainted(false);

            painel.add(botoes[i]);
        }

        setLayout(
                new BorderLayout(15,15));

        add(tela,BorderLayout.NORTH);

        add(painel,BorderLayout.CENTER);

        add(scroll,BorderLayout.EAST);

        getContentPane()
                .setBackground(fundo);

        setVisible(true);
    }
}