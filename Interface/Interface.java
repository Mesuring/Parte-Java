
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Interface {

    private static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String DB_URL = "jdbc:sqlserver://regulus.cotuca.unicamp.br:1433;databaseName=BD23341;trustServerCertificate=true";
    private static final String USER = "BD23341";
    private static final String PASS = "BD23341";

    private Connection conn;
    private JFrame frame;
    private JTextField userField;
    private JPasswordField passField;
    private JButton btnLogin;
    private JTable table;
    private DefaultTableModel tableModel;

    JTextField cpfClienteField = new JTextField();
    JTextField idProdField = new JTextField();
    JTextField qtdCompradaField = new JTextField();
    JTextField dataField = new JTextField();
    JTextField CEPField = new JTextField();
    JTextField numCasaField = new JTextField();
    JTextField idFuncField = new JTextField();
    JTextField idVendaField = new JTextField();



    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Interface window = new Interface();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Interface() {
        initialize();
        connectToDatabase();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        JPanel loginPanel = createLoginPanel();
        frame.getContentPane().add(loginPanel, BorderLayout.NORTH);

        JPanel pedidoPanel = createPedidoPanel();
        frame.getContentPane().add(pedidoPanel, BorderLayout.CENTER);
        pedidoPanel.setVisible(false);
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new FlowLayout());

        JLabel lblUser = new JLabel("User:");
        loginPanel.add(lblUser);

        userField = new JTextField(15);
        loginPanel.add(userField);

        JLabel lblPass = new JLabel("Password:");
        loginPanel.add(lblPass);

        passField = new JPasswordField(15);
        loginPanel.add(passField);

        btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fazerLogin();
            }
        });
        loginPanel.add(btnLogin);

        return loginPanel;
    }

    private JPanel createPedidoPanel() {
        JPanel pedidoPanel = new JPanel(new BorderLayout());


        tableModel = new DefaultTableModel();
        tableModel.addColumn("CPF_Cliente");
        tableModel.addColumn("idProd");
        tableModel.addColumn("qtd_Comprada");
        tableModel.addColumn("data");
        tableModel.addColumn("CEP");
        tableModel.addColumn("numCasa");
        tableModel.addColumn("idFunc");
        tableModel.addColumn("idVenda");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        pedidoPanel.add(scrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        JButton btnAdicionarPedido = new JButton("Adicionar Pedido");
        btnAdicionarPedido.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirInserirPedido();
            }
        });

        JButton btnAlterarPedido = new JButton("Alterar Pedido");
        btnAlterarPedido.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirAlterarPedido();
            }
        });



        buttonPanel.add(btnAdicionarPedido);
        buttonPanel.add(btnAlterarPedido);
        pedidoPanel.add(buttonPanel, BorderLayout.SOUTH);

        return pedidoPanel;
    }

    private void connectToDatabase() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Conexão com o banco de dados estabelecida com sucesso.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erro ao conectar ao banco de dados. Verifique o console para mais detalhes: " + e.getMessage());
        }
    }

    private void fazerLogin() {
        // Utilizaremos as suas credenciais diretamente
        String user = USER;
        String pass = PASS;

        if (verificarLogin(user, pass)) {
            mostrarPedidos();
        } else {
            JOptionPane.showMessageDialog(frame, "Login falhou. Verifique suas credenciais.");
        }
    }

    private boolean verificarLogin(String user, String pass) {
        String query =  "SELECT * FROM pra.Funcionario WHERE fFirNome = ? and senha = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, pass);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erro ao verificar login. Verifique suas credenciais.");
        }

        return false;
    }

    private void mostrarPedidos() {
        frame.getContentPane().getComponent(0).setVisible(false); // Oculta o painel de login
        frame.getContentPane().getComponent(1).setVisible(true); // Exibe o painel de consultas

        // Carregar consultas do banco de dados
        List<Pedido> pedidos = obterPedidosDoBanco();

        // Limpar tabela
        tableModel.setRowCount(0);

        // Adicionar consultas à tabela
        for (Pedido pedido : pedidos) {
            tableModel.addRow(pedido.toArray());
        }
    }

    private List<Pedido> obterPedidosDoBanco() {
        List<Pedido> pedidos = new ArrayList<>();

        String sql = "SELECT CPF_Cliente, IdProd, qtd_Comprada, data, CEP, numCasa, idFunc, idVenda FROM pra.pedido";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String cpfCliente = resultSet.getString("CPF_Cliente");
                int idProd = resultSet.getInt("IdProd");
                int qtd_Comprada = resultSet.getInt("qtd_Comprada");

                // Formatando a data usando SimpleDateFormat
                Timestamp dataPedidoTimestamp = resultSet.getTimestamp("data"); // Correção aqui
                String dataPedido = formatarData(dataPedidoTimestamp);

                String CEP = resultSet.getString("CEP");
                int numCasa = resultSet.getInt("numCasa");
                int idFunc = resultSet.getInt("idFunc");
                int idVenda = resultSet.getInt("idVenda");


                Pedido pedido = new Pedido (cpfCliente,idProd, qtd_Comprada, dataPedido, CEP, numCasa, idFunc,idVenda);
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pedidos;
    }

    private String formatarData(Timestamp dataTimestamp) {
        // Formatando a data usando SimpleDateFormat
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(dataTimestamp);
    }

    private void abrirInserirPedido() {
        // Interface para adicionar consulta
        JFrame inserirFrame = new JFrame("Adicionar Pedido");
        inserirFrame.setBounds(100, 100, 400, 300);
        inserirFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inserirFrame.getContentPane().setLayout(new BorderLayout());


        JTextField cpfClienteField = new JTextField();
        JTextField idProdField = new JTextField();
        JTextField qtdCompradaField = new JTextField();
        JTextField dataField = new JTextField();
        JTextField CEPField = new JTextField();
        JTextField numCasaField = new JTextField();
        JTextField idFuncField = new JTextField();

        JButton btnSalvarPedido = new JButton("Salvar Pedido");
         btnSalvarPedido.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int idProd = Integer.valueOf(idProdField.getText());
                int qtdComprada = Integer.valueOf(qtdCompradaField.getText());
                int numCasa = Integer.valueOf(numCasaField.getText());
                adicionarPedido(
                        cpfClienteField.getText(),
                        idProd,
                        qtdComprada,
                        dataField.getText(),
                        CEPField.getText(),
                        numCasa,
                        idFuncField.getText()
                );
                inserirFrame.dispose();
            }
        });

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inserirFrame.dispose();
            }
        });

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.add(new JLabel("CPF Cliente:"));
        formPanel.add(cpfClienteField);
        formPanel.add(new JLabel("Produto ID:"));
        formPanel.add(idProdField);
        formPanel.add(new JLabel("Qtd Comprada:"));
        formPanel.add(qtdCompradaField);
        formPanel.add(new JLabel("Data:"));
        formPanel.add(dataField);
        formPanel.add(new JLabel("CEP:"));
        formPanel.add(CEPField);
        formPanel.add(new JLabel("Num Casa:"));
        formPanel.add(numCasaField);
        formPanel.add(new JLabel("Funcionário ID:"));
        formPanel.add(idFuncField);


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSalvarPedido);
        buttonPanel.add(btnVoltar);

        inserirFrame.add(formPanel, BorderLayout.CENTER);
        inserirFrame.add(buttonPanel, BorderLayout.SOUTH);

        inserirFrame.setVisible(true);
    }

    private void abrirAlterarPedido() {
        // Interface para alterar consulta
        JFrame alterarFrame = new JFrame("Alterar Pedido");
        alterarFrame.setBounds(100, 100, 400, 300);
        alterarFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        alterarFrame.getContentPane().setLayout(new BorderLayout());

        JTextField cpfClienteField = new JTextField();
        JTextField idProdField = new JTextField();
        JTextField qtdCompradaField = new JTextField();
        JTextField dataField = new JTextField();
        JTextField CEPField = new JTextField();
        JTextField numCasaField = new JTextField();
        JTextField idFuncField = new JTextField();
        JTextField idVendaField = new JTextField();

        JButton btnBuscarPedido = new JButton("Buscar Pedido");
        btnBuscarPedido.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarPedido(idProdField.getText());
            }
        });

        JButton btnAlterarPedido = new JButton("Alterar Pedido");
        btnAlterarPedido.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int idProd = Integer.valueOf(idProdField.getText());
                int qtdComprada = Integer.valueOf(qtdCompradaField.getText());
                int numCasa = Integer.valueOf(numCasaField.getText());
                int idVenda = Integer.valueOf(idVendaField.getText());
                alterarPedido(
                        cpfClienteField.getText(),
                        idProd,
                        qtdComprada,
                        dataField.getText(),
                        CEPField.getText(),
                        numCasa,
                        idFuncField.getText(),
                        idVenda

                );
                alterarFrame.dispose();
            }
        });

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                alterarFrame.dispose();
            }
        });

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.add(new JLabel("CPF Cliente:"));
        formPanel.add(cpfClienteField);
        formPanel.add(new JLabel("Pedido ID:"));
        formPanel.add(idProdField);
        formPanel.add(new JLabel("Qtd Comprada:"));
        formPanel.add(qtdCompradaField);
        formPanel.add(new JLabel("Data:"));
        formPanel.add(dataField);
        formPanel.add(new JLabel("CEP:"));
        formPanel.add(CEPField);
        formPanel.add(new JLabel("Num Casa:"));
        formPanel.add(numCasaField);
        formPanel.add(new JLabel("Func ID:"));
        formPanel.add(idFuncField);
        formPanel.add(new JLabel("Venda ID:"));
        formPanel.add(idVendaField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnBuscarPedido);
        buttonPanel.add(btnAlterarPedido);
        buttonPanel.add(btnVoltar);

        alterarFrame.add(formPanel, BorderLayout.CENTER);
        alterarFrame.add(buttonPanel, BorderLayout.SOUTH);

        alterarFrame.setVisible(true);
    }


    private void buscarPedido(String idProd) {
        if (pedidoExiste(idProd)) {
            String sql = "SELECT * FROM pra.pedido WHERE IdProd = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(idProd));
                ResultSet resultSet = pstmt.executeQuery();

                if (resultSet.next()) {
                    String cpfCliente = resultSet.getString("CPF_Cliente");
                    idProd = String.valueOf(resultSet.getInt("IdProd"));
                    int qtdComprada = resultSet.getInt("qtd_Comprada");
                    int numCasa = resultSet.getInt("numCasa");
                    String CEP = resultSet.getString("CEP");

                    // Formatando a data usando SimpleDateFormat
                    Timestamp dataPedidoTimestamp = resultSet.getTimestamp("DataPedido");
                    String dataPedido = formatarData(dataPedidoTimestamp);

                    int idFunc = resultSet.getInt("idFunc");
                    int idVenda = resultSet.getInt("idVenda");

                    cpfClienteField.setText(String.valueOf(cpfCliente));
                    idProdField.setText(String.valueOf(idProd));
                    qtdCompradaField.setText(String.valueOf(qtdComprada));
                    dataField.setText(String.valueOf(dataPedido));
                    CEPField.setText(String.valueOf(CEP));
                    numCasaField.setText(String.valueOf(numCasa));
                    idFuncField.setText(String.valueOf(idFunc));
                    idVendaField.setText(String.valueOf(idVenda));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao buscar pedido. Verifique o console para mais detalhes.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Pedido não encontrado.");
        }
    }

    private void alterarPedido(String cpfCliente, int idProd, int qtdComprada, String data , String CEP, int numCasa, String idFunc,int idVenda) {
        if (pedidoExiste(String.valueOf(idProd))) {
            String sql = "UPDATE pra.pedido SET CPF_Cliente = '?', idProd = ? ,qtd_Comprada = ?, data = '?', CEP = '?', numCasa = ?, idFunc = ? WHERE idVenda = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, cpfCliente);
                pstmt.setInt(2, idProd);
                pstmt.setInt(3, qtdComprada);

                // Convertendo a string de dados para um objeto Timestamp usando SimpleDateFormat
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedDate = dateFormat.parse(data);
                Timestamp dataPedidoTimestamp = new Timestamp(parsedDate.getTime());

                pstmt.setTimestamp(4, dataPedidoTimestamp);
                pstmt.setString(5, CEP);
                pstmt.setInt(6, numCasa);


                pstmt.setString(7, idFunc);
                pstmt.setInt(8,idVenda);

                pstmt.executeUpdate();

                // Atualizar a tabela com as consultas após a alteração
                mostrarPedidos();

                JOptionPane.showMessageDialog(frame, "Pedido alterado com sucesso.");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao alterar o pedido. Verifique o console para mais detalhes.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Pedido não encontrado.");
        }
    }

    private void adicionarPedido(String cpfCliente, int idProd, int qtdComprada, String data , String CEP, int numCasa, String idFunc) {
        // Implemente a lógica para adicionar uma nova consulta no banco de dados
        // Exemplo: Inserir os dados no banco usando uma instrução SQL INSERT

        String sql = "EXEC pra.fazerPedido '?',?,?,'?','?',?,'?'";
       // String sql = "Exec pra.fazerPedido "+"'"+cpfCliente+"'"+","+idProd+qtdComprada+"'"+dataCerta+"'"+"'"+CEP+"'"+numCasa+"'"++"'";



        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cpfCliente);
            pstmt.setInt(2, idProd);
            pstmt.setInt(3, qtdComprada);

            // Convertendo a string de dados para um objeto Timestamp usando SimpleDateFormat
            //Date dataCerta=java.sql.Date.valueOf(data);

            // Convertendo a string de dados para um objeto Timestamp usando SimpleDateFormat
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = dateFormat.parse(data);

            pstmt.setDate(4, parsedDate);

            //pstmt.setDate(4, dataCerta);
            pstmt.setString(5, CEP);
            pstmt.setInt(6, numCasa);
            pstmt.setString(7, idFunc);

            pstmt.executeUpdate();

            // Atualizar a tabela com as consultas após a adição
            mostrarPedidos();

            JOptionPane.showMessageDialog(frame, "Pedido adicionado com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erro ao adicionar o pedido. Verifique o console para mais detalhes.");
        }
    }



    private boolean pedidoExiste(String idProd) {
        try {
            String sql = "SELECT 1 FROM pra.pedido WHERE IdProd = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(idProd));

            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // O restante do seu código...

    public static class Pedido {
        private String cpfCliente;
        private int idProd;
        private int qtdComprada;
        private String data;
        private String CEP;
        private int numCasa;
        private int idFunc;
        private int idVenda;

        public Pedido(String cpfCliente,int idProd, int qtdComprada, String data, String CEP, int numCasa, int idFunc,int idVenda) {
            this.cpfCliente = cpfCliente;
            this.idProd = idProd;
            this.qtdComprada = qtdComprada;
            this.data = data;
            this.CEP = CEP;
            this.numCasa = numCasa;
            this.idFunc = idFunc;
            this.idVenda = idVenda;

        }

        public Object[] toArray() {
            return new Object[]{cpfCliente,idProd, qtdComprada, data, CEP, numCasa, idFunc,idVenda};
        }
    }
}
