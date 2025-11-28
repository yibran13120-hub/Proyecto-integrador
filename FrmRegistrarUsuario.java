package srexwingsapp.interfaces;

import srexwingsapp.conexion.Conexion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FrmRegistrarUsuario extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JTextField txtNombreCompleto;
    private JButton btnRegistrar;
    private JButton btnCerrar;
    private JButton btnRegresarLogin;

    // Paleta EssRexx_Wings
    private final Color COLOR_BG_MAIN    = new Color(4, 46, 90);     // azul fondo
    private final Color COLOR_HEADER    = new Color(220, 53, 69);    // rojo barra
    private final Color COLOR_PANEL     = new Color(255, 239, 204);  // crema
    private final Color COLOR_PRIMARY   = new Color(247, 201, 71);   // amarillo
    private final Color COLOR_SECONDARY = new Color(0, 82, 156);     // azul botón
    private final Color COLOR_TEXT_DARK = new Color(40, 40, 40);
    private final Color COLOR_TEXT_MUT  = new Color(90, 90, 90);

    public FrmRegistrarUsuario() {
        inicializarVentana();
        inicializarComponentes();
        inicializarEventos();
    }

    // ================= VENTANA =================
    private void inicializarVentana() {
        setTitle("Registrar usuario - EssRexx_Wings");
        setExtendedState(JFrame.MAXIMIZED_BOTH);  
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(COLOR_BG_MAIN);
        getContentPane().setLayout(new GridBagLayout());  
    }

    // ================= COMPONENTES =================
    private void inicializarComponentes() {

        // CONTENEDOR PRINCIPAL (tarjeta centrada)
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.setPreferredSize(new Dimension(520, 420));

        // ======= Header rojo =======
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HEADER);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("Registrar nuevo usuario", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo, BorderLayout.CENTER);

        contenedor.add(header, BorderLayout.NORTH);

        // ======= Panel crema del formulario =======
        JPanel panelFormWrapper = new JPanel(new BorderLayout());
        panelFormWrapper.setBackground(COLOR_PANEL);
        panelFormWrapper.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel panelForm = new JPanel(null);
        panelForm.setBackground(COLOR_PANEL);
        panelForm.setPreferredSize(new Dimension(460, 220));

        // Labels
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setForeground(COLOR_TEXT_DARK);
        lblUsuario.setBounds(40, 30, 150, 20);
        panelForm.add(lblUsuario);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setForeground(COLOR_TEXT_DARK);
        lblPassword.setBounds(40, 70, 150, 20);
        panelForm.add(lblPassword);

        JLabel lblRol = new JLabel("Rol:");
        lblRol.setForeground(COLOR_TEXT_DARK);
        lblRol.setBounds(40, 110, 150, 20);
        panelForm.add(lblRol);

        JLabel lblNombreCompleto = new JLabel("Nombre completo:");
        lblNombreCompleto.setForeground(COLOR_TEXT_DARK);
        lblNombreCompleto.setBounds(40, 150, 150, 20);
        panelForm.add(lblNombreCompleto);

        // Campos
        txtUsuario = new JTextField();
        txtUsuario.setBounds(200, 30, 220, 24);
        txtUsuario.setForeground(COLOR_TEXT_DARK);
        txtUsuario.setBackground(Color.WHITE);
        txtUsuario.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 150)));
        panelForm.add(txtUsuario);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(200, 70, 220, 24);
        txtPassword.setForeground(COLOR_TEXT_DARK);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 150)));
        panelForm.add(txtPassword);

        cmbRol = new JComboBox<>(new String[]{"ADMIN", "MESERO", "COCINERO"});
        cmbRol.setBounds(200, 110, 220, 24);
        cmbRol.setForeground(COLOR_TEXT_DARK);
        cmbRol.setBackground(Color.WHITE);
        panelForm.add(cmbRol);

        txtNombreCompleto = new JTextField();
        txtNombreCompleto.setBounds(200, 150, 220, 24);
        txtNombreCompleto.setForeground(COLOR_TEXT_DARK);
        txtNombreCompleto.setBackground(Color.WHITE);
        txtNombreCompleto.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 150)));
        panelForm.add(txtNombreCompleto);

        // ===== Botones dentro del panel =====
        btnRegistrar = crearBoton("Registrar", COLOR_PRIMARY, COLOR_TEXT_DARK);
        btnRegistrar.setBounds(200, 190, 105, 30);
        panelForm.add(btnRegistrar);

        btnCerrar = crearBoton("Cerrar", new Color(120, 120, 120), Color.WHITE);
        btnCerrar.setBounds(315, 190, 105, 30);
        panelForm.add(btnCerrar);

        panelFormWrapper.add(panelForm, BorderLayout.CENTER);
        contenedor.add(panelFormWrapper, BorderLayout.CENTER);

        // ===== Zona inferior: texto + volver al login =====
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(COLOR_PANEL);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 25, 15, 25));

        JLabel lblHint = new JLabel("Los usuarios se crean con rol ADMIN, MESERO o COCINERO.");
        lblHint.setForeground(COLOR_TEXT_MUT);
        lblHint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        panelInferior.add(lblHint, BorderLayout.NORTH);

        JPanel panelBotonVolver = new JPanel();
        panelBotonVolver.setBackground(COLOR_PANEL);

        btnRegresarLogin = crearBoton("⬅ Volver al login", COLOR_SECONDARY, Color.WHITE);
        btnRegresarLogin.setPreferredSize(new Dimension(180, 32));
        panelBotonVolver.add(btnRegresarLogin);

        panelInferior.add(panelBotonVolver, BorderLayout.CENTER);

        contenedor.add(panelInferior, BorderLayout.SOUTH);

        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        getContentPane().add(contenedor, gbc);
    }

    private JButton crearBoton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createLineBorder(bg.darker(), 2, true));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.brighter()); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(bg); }
        });

        return btn;
    }

    // ================= EVENTOS =================
    private void inicializarEventos() {
        btnRegistrar.addActionListener((ActionEvent e) -> registrarUsuario());

        btnCerrar.addActionListener((ActionEvent e) -> dispose());

        btnRegresarLogin.addActionListener((ActionEvent e) -> {
            dispose();
            new FrmLoginNuevo().setVisible(true);
        });
    }

    // ================= LÓGICA MYSQL =================
    private void registrarUsuario() {
        String usuario = txtUsuario.getText().trim();
        String password = String.valueOf(txtPassword.getPassword()).trim();
        String rol = (String) cmbRol.getSelectedItem();
        String nombreCompleto = txtNombreCompleto.getText().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Faltan campos obligatorios");
            return;
        }

        String sql = "INSERT INTO usuarios(usuario, password, rol, nombre_completo) VALUES (?,?,?,?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, password);
            ps.setString(3, rol);
            ps.setString(4, nombreCompleto.isEmpty() ? null : nombreCompleto);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Usuario registrado correctamente");
                limpiarCampos();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error SQL: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        txtUsuario.setText("");
        txtPassword.setText("");
        txtNombreCompleto.setText("");
        cmbRol.setSelectedIndex(0);
    }

    // MAIN de prueba opcional
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmRegistrarUsuario().setVisible(true));
    }
}
