package dao;

import java.util.ArrayList;
import java.util.List;

import model.Operacao;

public class HistoricoDAO {

    private ArrayList<Operacao> historico =
            new ArrayList<>();

    // SALVAR
    public void salvar(Operacao op) {

        historico.add(op);
    }

    // LISTAR
    public List<Operacao> listar() {

        return new ArrayList<>(historico);
    }

    // LIMPAR
    public void limpar() {

        historico.clear();
    }
}