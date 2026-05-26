package controller;

import static spark.Spark.*;

public class ApiServer {

    public static void main(String[] args) {

        port(8080);

        // Permitir requisições do frontend (CORS)
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.type("application/json");
        });

        options("/*", (req, res) -> {
            res.status(200);
            return "OK";
        });

        // ENDPOINT: calcular expressão
        post("/calcular", (req, res) -> {
            String body = req.body();
            String expressao = body.replaceAll(".*\"expressao\":\"([^\"]+)\".*", "$1");

            CalculadoraController calc = CalculadoraController.getInstance();
            String resultado;

            // RAIZ QUADRADA
            if (expressao.startsWith("√")) {
                String numero = expressao.substring(1);
                resultado = calc.calcularRaiz(numero);

            // SENO
            } else if (expressao.startsWith("sin(")) {
                String numero = expressao.substring(4, expressao.length() - 1);
                resultado = calc.calcularTrig("sin", numero);

            // COSSENO
            } else if (expressao.startsWith("cos(")) {
                String numero = expressao.substring(4, expressao.length() - 1);
                resultado = calc.calcularTrig("cos", numero);

            // TANGENTE
            } else if (expressao.startsWith("tan(")) {
                String numero = expressao.substring(4, expressao.length() - 1);
                resultado = calc.calcularTrig("tan", numero);

            // EXPRESSÃO NORMAL
            } else {
                resultado = calc.calcularExpressao(expressao);
            }

            return "{\"resultado\":\"" + resultado + "\"}";
        });

        // ENDPOINT: listar histórico
        get("/historico", (req, res) -> {
            CalculadoraController calc = CalculadoraController.getInstance();
            return calc.getHistoricoJson();
        });

        // ENDPOINT: limpar histórico
        delete("/historico", (req, res) -> {
            CalculadoraController calc = CalculadoraController.getInstance();
            calc.limparHistorico();
            return "{\"msg\":\"Histórico apagado\"}";
        });
    }
}