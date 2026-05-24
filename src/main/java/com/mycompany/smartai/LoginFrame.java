package com.mycompany.smartai;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
 
public class LoginFrame extends JFrame {
 
    // ── Fields ────────────────────────────────────────────────────────────────
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JCheckBox      showPasswordBox;
    private JButton        loginButton;
    private JButton        registerButton;
 
    // ── Colour Palette (GitHub dark theme) ───────────────────────────────────
    private static final Color BG_DARK      = new Color(13,  17,  23);
    private static final Color CARD_BG      = new Color(22,  27,  34);
    private static final Color BORDER_CLR   = new Color(48,  54,  61);
    private static final Color ACCENT       = new Color(88, 166, 255);
    private static final Color ACCENT_HOVER = new Color(58, 140, 230);
    private static final Color TEXT_PRIMARY = new Color(230, 237, 243);
    private static final Color TEXT_MUTED   = new Color(139, 148, 158);
    private static final Color INPUT_BG     = new Color(13,  17,  23);
    private static final Color INPUT_FOCUS  = new Color(31,  111, 235);
 
    // ── Constructor ───────────────────────────────────────────────────────────
    public LoginFrame() {
        setTitle("SmartAI — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(460, 570);
        setLocationRelativeTo(null);
        buildUI();
    }
 
    // ── Build UI ──────────────────────────────────────────────────────────────
    private void buildUI() {
 
        // Root — dark background
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_DARK);
        setContentPane(root);
 
        // Card
        RoundedPanel card = new RoundedPanel(16, CARD_BG, BORDER_CLR);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 42, 36, 42));
        card.setPreferredSize(new Dimension(390, 490));
 
        // ── Title block ───────────────────────────────────────────────────────
        JLabel logo = centeredLabel("✦", 34, Font.PLAIN, ACCENT);
        JLabel title = centeredLabel("SmartAI", 28, Font.BOLD, TEXT_PRIMARY);
        JLabel sub   = centeredLabel("Sign in to your account", 13, Font.PLAIN, TEXT_MUTED);
 
        // ── Username ──────────────────────────────────────────────────────────
        JLabel userLbl = fieldLabel("Username");
        usernameField  = new PlaceholderTextField("Enter your username");
        styleInput(usernameField);
 
        // ── Password ──────────────────────────────────────────────────────────
        JLabel passLbl = fieldLabel("Password");
        passwordField  = new PlaceholderPasswordField("Enter your password");
        styleInput(passwordField);
 
        // ── Show password ─────────────────────────────────────────────────────
        showPasswordBox = new JCheckBox("Show password");
        showPasswordBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordBox.setForeground(TEXT_MUTED);
        showPasswordBox.setBackground(CARD_BG);
        showPasswordBox.setAlignmentX(LEFT_ALIGNMENT);
        showPasswordBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        showPasswordBox.addActionListener(e -> {
            if (showPasswordBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
                showPasswordBox.setText("Hide password");
            } else {
                passwordField.setEchoChar('\u2022');
                showPasswordBox.setText("Show password");
            }
        });
 
        // ── Login button ──────────────────────────────────────────────────────
        loginButton = accentButton("Login", ACCENT, ACCENT_HOVER);
        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
 
        // ── Divider ───────────────────────────────────────────────────────────
        JPanel div = divider();
 
        // ── Register button ───────────────────────────────────────────────────
        registerButton = outlineButton("Create a new account");
        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
 
        // ── Footer ────────────────────────────────────────────────────────────
        JLabel footer = centeredLabel("Powered by Google Gemini 2.5 Flash", 11, Font.PLAIN, TEXT_MUTED);
 
        // ── Assemble ──────────────────────────────────────────────────────────
        card.add(logo);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(userLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(16));
        card.add(passLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(showPasswordBox);
        card.add(Box.createVerticalStrut(22));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(16));
        card.add(div);
        card.add(Box.createVerticalStrut(16));
        card.add(registerButton);
        card.add(Box.createVerticalStrut(22));
        card.add(footer);
 
        root.add(card);
    }
 
    // ── Login logic ───────────────────────────────────────────────────────────
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
 
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter username and password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (DatabaseManager.loginUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            new ChatFrame(username).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Wrong username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            usernameField.requestFocus();
        }
    }
 
    // ── Style helpers ─────────────────────────────────────────────────────────
    private JLabel centeredLabel(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(color);
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }
 
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT_PRIMARY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
 
    /** Applies shared input styling to any JTextComponent */
    private void styleInput(javax.swing.text.JTextComponent f) {
        f.setBackground(INPUT_BG);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(
            new RoundedLineBorder(BORDER_CLR, 1, 8),
            new EmptyBorder(8, 12, 8, 12)
        ));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    new RoundedLineBorder(INPUT_FOCUS, 2, 8),
                    new EmptyBorder(7, 11, 7, 11)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    new RoundedLineBorder(BORDER_CLR, 1, 8),
                    new EmptyBorder(8, 12, 8, 12)));
            }
        });
    }
 
    private JButton accentButton(String text, Color normal, Color hover) {
        JButton btn = new JButton(text) {
            private Color cur = normal;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e){ cur=hover; repaint(); }
                public void mouseExited (MouseEvent e){ cur=normal; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cur);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
 
    private JButton outlineButton(String text) {
        JButton btn = new JButton(text) {
            private boolean h = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e){ h=true;  repaint(); }
                public void mouseExited (MouseEvent e){ h=false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(h ? new Color(30,37,45) : CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10));
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,10,10));
                g2.setColor(TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
 
    private JPanel divider() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CARD_BG);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        p.setAlignmentX(LEFT_ALIGNMENT);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;
        JSeparator s1 = new JSeparator(); s1.setForeground(BORDER_CLR);
        JSeparator s2 = new JSeparator(); s2.setForeground(BORDER_CLR);
        JLabel or = new JLabel("  or  ");
        or.setFont(new Font("Segoe UI", Font.PLAIN, 12)); or.setForeground(TEXT_MUTED);
        p.add(s1, gc); gc.weightx=0; p.add(or,gc); gc.weightx=1; p.add(s2,gc);
        return p;
    }
 
    // ── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext","true");
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
 
    // ═════════════════════════════════════════════════════════════════════════
    // Inner classes
    // ═════════════════════════════════════════════════════════════════════════
 
    /** TextField with a grey placeholder that disappears on focus */
    static class PlaceholderTextField extends JTextField {
        private final String hint;
        PlaceholderTextField(String hint) { this.hint = hint; }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(139,148,158));
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                g2.drawString(hint, 13, getHeight()/2 + 5);
                g2.dispose();
            }
        }
    }
 
    /** PasswordField with a grey placeholder that disappears on focus */
    static class PlaceholderPasswordField extends JPasswordField {
        private final String hint;
        PlaceholderPasswordField(String hint) { this.hint = hint; setEchoChar('\u2022'); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getPassword().length == 0 && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(139,148,158));
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                g2.drawString(hint, 13, getHeight()/2 + 5);
                g2.dispose();
            }
        }
    }
 
    /** Panel with rounded corners drawn in paintComponent */
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill, border;
        RoundedPanel(int radius, Color fill, Color border) {
            this.radius=radius; this.fill=fill; this.border=border;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),radius*2,radius*2));
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0.5f,0.5f,getWidth()-1,getHeight()-1,radius*2,radius*2));
            g2.dispose();
            super.paintComponent(g);
        }
    }
 
    /** Border with rounded corners */
    static class RoundedLineBorder extends AbstractBorder {
        private final Color color; private final int thick, radius;
        RoundedLineBorder(Color color, int thick, int radius) {
            this.color=color; this.thick=thick; this.radius=radius;
        }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thick));
            g2.draw(new RoundRectangle2D.Float(x+thick/2f, y+thick/2f,
                w-thick, h-thick, radius*2, radius*2));
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) {
            return new Insets(thick+2, thick+2, thick+2, thick+2);
        }
    }
}