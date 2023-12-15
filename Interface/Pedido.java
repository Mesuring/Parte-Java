import java.sql.Timestamp;

public class Pedido {
    private int cpfCliente;
    private int idProd;
    private int qtd_Comprada;
    private Timestamp data;
    private String CEP;
    private int numCasa;
    private int idFunc;
    private int idVenda;

    // Construtor
    public Pedido(int cpfCliente,int idProd, int qtd_Comprada, Timestamp data, String CEP, int numCasa, int idFunc, int idVenda) {
        this.cpfCliente= cpfCliente;
        this.idProd= idProd;
        this.qtd_Comprada = qtd_Comprada;
        this.data = data;
        this.CEP = CEP;
        this.numCasa = numCasa;
        this.idFunc = idFunc;
        this.idVenda = idVenda;
    }

    // Outros métodos, getters e setters

    public Object[] toArray() {
        // Retornar um array com os dados da consulta para ser usado na tabela
        return new Object[]{cpfCliente,idProd, qtd_Comprada, data, CEP, numCasa, idFunc,idVenda};
    }
}
