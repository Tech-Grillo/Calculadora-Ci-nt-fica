package controller;

import view.CalculadoraView;
import dao.HistoricoDAO;
import model.Operacao;
import model.ModelException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculadoraController {

    private CalculadoraView view;
    private HistoricoDAO dao;

    private static CalculadoraController instance;

    public CalculadoraController() {
        view = new CalculadoraView();
        dao = new HistoricoDAO();
        instance = this;
        iniciarEventos();
    }

    public CalculadoraController(boolean comView) {
        if (comView) {
            view = new CalculadoraView();
            iniciarEventos();
        }
        dao = new HistoricoDAO();
        instance = this;
    }

    public static CalculadoraController getInstance() {
        if (instance == null) instance = new CalculadoraController(false);
        return instance;
    }

    private void iniciarEventos() {
        for (JButton botao : view.botoes) {
            botao.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        clicar(botao.getText());
                    }
                });
        }
    }

    private void clicar(String valor) {
        switch (valor) {
            case "=":
                calcular();
                break;
            case "C":
                view.tela.setText("");
                break;
            case "HC":
                limparHistoricoSwing();
                break;
            case "√":
                raizQuadrada();
                break;
            case "sin":
                trigonometria("sin");
                break;
            case "cos":
                trigonometria("cos");
                break;
            case "tan":
                trigonometria("tan");
                break;
            default:
                view.tela.setText(view.tela.getText() + valor);
        }
    }

    private void limparHistoricoSwing() {
        int resposta = JOptionPane.showConfirmDialog(
                null,
                "Deseja apagar o histórico?",
                "Limpar Histórico",
                JOptionPane.YES_NO_OPTION
        );
        if (resposta == JOptionPane.YES_OPTION) {
            dao.limpar();
            view.historico.setText("");
            JOptionPane.showMessageDialog(null, "Histórico apagado!");
        }
    }

    private void raizQuadrada() {
        try {
            String texto = view.tela.getText();
            if (texto.isEmpty()) {
                throw new ModelException("Digite um número!");
            }
            double numero = Double.parseDouble(texto);
            if (numero < 0) {
                throw new ModelException("Raiz negativa inválida!");
            }
            double resultado = Math.sqrt(numero);
            view.tela.setText(String.valueOf(resultado));
        } catch (ModelException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (Exception e) {
            view.tela.setText("Erro");
        }
    }

    private void trigonometria(String funcao) {
        try {
            String texto = view.tela.getText();
            if (texto.isEmpty()) {
                throw new ModelException("Digite um número!");
            }
            double numero = Double.parseDouble(texto);
            double resultado = 0;
            switch (funcao) {
                case "sin":
                    resultado = Math.sin(Math.toRadians(numero));
                    break;
                case "cos":
                    resultado = Math.cos(Math.toRadians(numero));
                    break;
                case "tan":
                    resultado = Math.tan(Math.toRadians(numero));
                    break;
            }
            view.tela.setText(String.valueOf(resultado));
        } catch (ModelException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (Exception e) {
            view.tela.setText("Erro");
        }
    }

    private void calcular() {
        try {
            String expressao = view.tela.getText();
            if (expressao.isEmpty()) {
                throw new ModelException("Digite uma operação!");
            }
            expressao = expressao.replaceAll("(\\d)\\(", "$1*(");
            double resultado = resolverExpressao(expressao);
            if (Double.isInfinite(resultado)) {
                throw new ModelException("Divisão por zero!");
            }
            view.tela.setText(String.valueOf(resultado));
            Operacao op = new Operacao(expressao, String.valueOf(resultado));
            dao.salvar(op);
            atualizarHistorico();
        } catch (ModelException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (ArithmeticException e) {
            JOptionPane.showMessageDialog(null, "Erro matemático!");
        } catch (Exception e) {
            view.tela.setText("Erro");
        }
    }

    private double resolverExpressao(String expressao) {
        while (expressao.contains("(")) {
            int inicio = expressao.lastIndexOf("(");
            int fim = expressao.indexOf(")", inicio);
            String dentro = expressao.substring(inicio + 1, fim);
            double resultado = calcularSimples(dentro);
            expressao = expressao.substring(0, inicio) + resultado + expressao.substring(fim + 1);
        }
        return calcularSimples(expressao);
    }

    private double calcularSimples(String expressao) {
        expressao = expressao.replace(" ", "");
        for (int i = expressao.length() - 1; i > 0; i--) {
            char c = expressao.charAt(i);
            if (c == '+' || c == '-') {
                switch (c) {
                    case '+':
                        return calcularSimples(expressao.substring(0, i))
                                + calcularSimples(expressao.substring(i + 1));
                    case '-':
                        return calcularSimples(expressao.substring(0, i))
                                - calcularSimples(expressao.substring(i + 1));
                }
            }
        }
        for (int i = expressao.length() - 1; i > 0; i--) {
            char c = expressao.charAt(i);
            if (c == '*' || c == '/') {
                switch (c) {
                    case '*':
                        return calcularSimples(expressao.substring(0, i))
                                * calcularSimples(expressao.substring(i + 1));
                    case '/':
                        double divisor = calcularSimples(expressao.substring(i + 1));
                        if (divisor == 0) {
                            throw new ArithmeticException("Divisão por zero");
                        }
                        return calcularSimples(expressao.substring(0, i)) / divisor;
                }
            }
        }
        int pot = expressao.lastIndexOf("^");
        if (pot != -1) {
            return Math.pow(
                    calcularSimples(expressao.substring(0, pot)),
                    calcularSimples(expressao.substring(pot + 1))
            );
        }
        return Double.parseDouble(expressao);
    }

    private void atualizarHistorico() {
        view.historico.setText("");
        for (Operacao op : dao.listar()) {
            view.historico.append(op + "\n");
        }
    }

    // MÉTODOS PARA A API
    public String calcularExpressao(String expressao) {
        try {
            expressao = expressao.replaceAll("(\\d)\\(", "$1*(");
            double resultado = resolverExpressao(expressao);
            if (Double.isInfinite(resultado))
                return "Erro: Divisão por zero";
            Operacao op = new Operacao(expressao, String.valueOf(resultado));
            dao.salvar(op);
            return String.valueOf(resultado);
        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }

    public void limparHistorico() {
        dao.limpar();
    }

    public String getHistoricoJson() {
        StringBuilder sb = new StringBuilder("[");
        var lista = dao.listar();
        for (int i = 0; i < lista.size(); i++) {
            Operacao op = lista.get(i);
            sb.append("{\"expressao\":\"").append(op.getExpressao())
              .append("\",\"resultado\":\"").append(op.getResultado()).append("\"}");
            if (i < lista.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
    public String calcularRaiz(String texto) {
        try {
            double numero = Double.parseDouble(texto);
            if (numero < 0) return "Erro: Raiz negativa inválida!";
            double resultado = Math.sqrt(numero);
            Operacao op = new Operacao("√" + texto, String.valueOf(resultado));
            dao.salvar(op);
            return String.valueOf(resultado);
        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }

    public String calcularTrig(String funcao, String texto) {
        try {
            double numero = Double.parseDouble(texto);
            double resultado = 0;
            switch (funcao) {
                case "sin":
                    resultado = Math.sin(Math.toRadians(numero));
                    break;
                case "cos":
                    resultado = Math.cos(Math.toRadians(numero));
                    break;
                case "tan":
                    resultado = Math.tan(Math.toRadians(numero));
                    break;
            }
            Operacao op = new Operacao(funcao + "(" + texto + ")", String.valueOf(resultado));
            dao.salvar(op);
            return String.valueOf(resultado);
        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        new CalculadoraController();
    }
}