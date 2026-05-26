package model;

public class Operacao {

    private String expressao;

    private String resultado;

    public Operacao(String expressao,
                     String resultado) {

        this.expressao = expressao;

        this.resultado = resultado;
    }

    public String getExpressao() {

        return expressao;
    }

    public String getResultado() {

        return resultado;
    }

    @Override
    public String toString() {

        return expressao + " = " + resultado;
    }
}