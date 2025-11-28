/*
 * FrmLoginNuevo - Login con redirección según rol:
 *  - ADMIN    -> FrmMenuAdmin
 *  - MESERO   -> FrmPedidosNuevo
 *  - COCINERO -> FrmCocineroPedidos
 */
package srexwingsapp.interfaces;

import javax.swing.ImageIcon;

import srexwingsapp.conexion.Conexion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FrmLoginNuevo extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegistrar;
    private JButton btnSalir;

    // Paleta de colores
    private final Color COLOR_BG_MAIN     = new Color(4, 46, 90);   // azul fondo
    private final Color COLOR_HEADER     = new Color(220, 53, 69);  // rojo barra
    private final Color COLOR_PANEL      = new Color(255, 239, 204);// crema
    private final Color COLOR_PRIMARY    = new Color(247, 201, 71); // amarillo
    private final Color COLOR_SECONDARY  = new Color(0, 82, 156);   // azul botón
    private final Color COLOR_TEXT_DARK  = new Color(40, 40, 40);
    private final Color COLOR_TEXT_MUTED = new Color(90, 90, 90);

    public FrmLoginNuevo() {
        inicializarVentana();
        inicializarComponentes();
        inicializarEventos();

        // Icono de la ventana (login)
        setIconImage(
            new ImageIcon(
                getClass().getResource("/assets/logo.png")
            ).getImage()
        );
    }

    // =============== ESTILO BOTONES =================
    private void estiloBoton(JButton btn, Color bg, Color fg) {
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));

        btn.setBorder(new javax.swing.border.LineBorder(bg.darker(), 2, true));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bg.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
            }
        });
    }

    // ================= VENTANA =================
    private void inicializarVentana() {
        setTitle("EssRexx_Wings - Login");

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setBackground(COLOR_BG_MAIN);
        getContentPane().setLayout(new GridBagLayout());
    }

    // ================= COMPONENTES =================
    private void inicializarComponentes() {

        JPanel panelForm = new JPanel(null);
        panelForm.setBackground(COLOR_PANEL);
        panelForm.setPreferredSize(new Dimension(460, 240));
        panelForm.setBorder(BorderFactory.createLineBorder(new Color(230, 210, 170)));

        JPanel header = new JPanel(null);
        header.setBackground(COLOR_HEADER);
        header.setBounds(0, 0, 460, 60);
        panelForm.add(header);

        JLabel lblTitulo = new JLabel("EssRexx_Wings – Inicio de sesión");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(20, 15, 420, 30);
        header.add(lblTitulo);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setForeground(COLOR_TEXT_DARK);
        lblUsuario.setBounds(40, 80, 120, 20);
        panelForm.add(lblUsuario);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setForeground(COLOR_TEXT_DARK);
        lblPassword.setBounds(40, 115, 120, 20);
        panelForm.add(lblPassword);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(160, 80, 250, 24);
        txtUsuario.setForeground(COLOR_TEXT_DARK);
        txtUsuario.setBackground(Color.WHITE);
        txtUsuario.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        panelForm.add(txtUsuario);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(160, 115, 250, 24);
        txtPassword.setForeground(COLOR_TEXT_DARK);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        panelForm.add(txtPassword);

        btnLogin = new JButton("Iniciar sesión");
        btnLogin.setBounds(40, 155, 170, 34);
        estiloBoton(btnLogin, COLOR_PRIMARY, COLOR_TEXT_DARK);
        panelForm.add(btnLogin);

        btnRegistrar = new JButton("Registrar usuario");
        btnRegistrar.setBounds(230, 155, 180, 34);
        estiloBoton(btnRegistrar, COLOR_SECONDARY, Color.WHITE);
        panelForm.add(btnRegistrar);

        JLabel lblHint = new JLabel("Usuarios con rol ADMIN pueden gestionar inventario y reportes.");
        lblHint.setForeground(COLOR_TEXT_MUTED);
        lblHint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblHint.setBounds(30, 195, 400, 20);
        panelForm.add(lblHint);

        btnSalir = new JButton("Salir");
        estiloBoton(btnSalir, COLOR_SECONDARY.darker(), Color.WHITE);
        btnSalir.setPreferredSize(new Dimension(90, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        getContentPane().add(panelForm, gbc);

        gbc.gridy = 1;
        getContentPane().add(btnSalir, gbc);
    }

    // ================= EVENTOS =================
    private void inicializarEventos() {

        btnLogin.addActionListener((ActionEvent e) -> iniciarSesion());

        btnRegistrar.addActionListener((ActionEvent e) -> {
            FrmRegistrarUsuario frm = new FrmRegistrarUsuario();
            frm.setVisible(true);
            dispose();
        });

        btnSalir.addActionListener((ActionEvent e) -> System.exit(0));

        txtPassword.addActionListener((ActionEvent e) -> iniciarSesion());
    }

    // ================= LÓGICA LOGIN =================
    private void iniciarSesion() {
        String user = txtUsuario.getText().trim();
        String pass = String.valueOf(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa usuario y contraseña");
            return;
        }

        String sql = "SELECT rol, nombre_completo FROM usuarios WHERE usuario=? AND password=?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String rol = rs.getString("rol");
                String nombre = rs.getString("nombre_completo");
                String mostrar = (nombre != null && !nombre.isEmpty()) ? nombre : user;

                // ====== LOGO PEQUEÑO EN MENSAJE DE BIENVENIDA ======
                ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/assets/logo.png"));
                Image img = iconoOriginal.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                ImageIcon icono = new ImageIcon(img);

                JOptionPane.showMessageDialog(
                        this,
                        "Bienvenido " + mostrar + "\nRol: " + rol,
                        "EssRexx_Wings",
                        JOptionPane.INFORMATION_MESSAGE,
                        icono
                );

                // Redirección según rol
                switch (rol.toUpperCase()) {
                    case "ADMIN":
                        new FrmMenuAdmin(mostrar).setVisible(true);
                        break;
                    case "MESERO":
                        new FrmPedidosNuevo(rol, mostrar).setVisible(true);
                        break;
                    case "COCINERO":
                        new FrmCocineroPedidos(mostrar).setVisible(true);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Rol no reconocido: " + rol);
                        return;
                }

                this.dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al iniciar sesión: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmLoginNuevo().setVisible(true));
    }
}
