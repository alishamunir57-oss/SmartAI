
 package com.mycompany.smartai;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
 
public class RegisterFrame extends JFrame {
 
    private JTextField     fullNameField;
    private JTextField     usernameField;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JCheckBox      showPass1;
    private JCheckBox      showPass2;
 
    // Same dark palette as LoginFrame
    private static final Color BG_DARK     = new Color(13,  17,  23);
    private static final Color CARD_BG     = new Color(22,  27,  34);
    private static final Color BORDER_CLR  = new Color(48,  54,  61);
    private static final Color ACCENT      = new Color(88, 166, 255);
    private static final Color ACCENT_HOV  = new Color(58, 140, 230);
    private static final Color TEXT_PRI    = new Color(230, 237, 243);
    private static final Color TEXT_MUT    = new Color(139, 148, 158);
    private static final Color INPUT_BG    = new Color(13,  17,  23);
    private static final Color INPUT_FOC   = new Color(31,  111, 235);
 
    public RegisterFrame() {
        setTitle("SmartAI — Register");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(480, 680);
        setLocationRelativeTo(null);
        buildUI();
    }
 
    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_DARK);
        setContentPane(root);
 
        RoundedPanel card = new RoundedPanel(16, CARD_BG, BORDER_CLR);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 42, 36, 42));
        card.setPreferredSize(new Dimension(400, 600));
 
        // Title
        JLabel logo  = cLabel("✦", 30, Font.PLAIN, ACCENT);
        JLabel title = cLabel("Create Account", 26, Font.BOLD, TEXT_PRI);
        JLabel sub   = cLabel("Fill in your details to register", 13, Font.PLAIN, TEXT_MUT);
 
        // Fields
        fullNameField = new PlaceholderTextField("Enter your full name");
        usernameField = new PlaceholderTextField("Choose a username");
        emailField    = new PlaceholderTextField("Enter your email");
        passwordField = new PlaceholderPasswordField("Min. 6 characters");
        confirmField  = new PlaceholderPasswordField("Re-enter password");
 
        styleInput(fullNameField);
        styleInput(usernameField);
        styleInput(emailField);
        styleInput(passwordField);
        styleInput(confirmField);
 
        // Show password checkboxes
        showPass1 = mkCheckbox("Show password");
        showPass1.addActionListener(e -> {
            passwordField.setEchoChar(showPass1.isSelected() ? (char)0 : '\u2022');
            showPass1.setText(showPass1.isSelected() ? "Hide password" : "Show password");
        });
        showPass2 = mkCheckbox("Show password");
        showPass2.addActionListener(e -> {
            confirmField.setEchoChar(showPass2.isSelected() ? (char)0 : '\u2022');
            showPass2.setText(showPass2.isSelected() ? "Hide password" : "Show password");
        });
 
        // Buttons
        JButton registerBtn = accentButton("Register", ACCENT, ACCENT_HOV);
        registerBtn.addActionListener(e -> handleRegister());
        confirmField.addActionListener(e -> handleRegister());
 
        JPanel divider = divider();
 
        JButton backBtn = outlineButton("Back to Login");
        backBtn.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
 
        JLabel footer = cLabel("SmartAI © 2026", 11, Font.PLAIN, TEXT_MUT);
 
        // Assemble
        card.add(logo);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(22));
        card.add(fLabel("Full Name")); card.add(Box.createVerticalStrut(5)); card.add(fullNameField);
        card.add(Box.createVerticalStrut(12));
        card.add(fLabel("Username")); card.add(Box.createVerticalStrut(5)); card.add(usernameField);
        card.add(Box.createVerticalStrut(12));
        card.add(fLabel("Email")); card.add(Box.createVerticalStrut(5)); card.add(emailField);
        card.add(Box.createVerticalStrut(12));
        card.add(fLabel("Password")); card.add(Box.createVerticalStrut(5)); card.add(passwordField);
        card.add(Box.createVerticalStrut(4)); card.add(showPass1);
        card.add(Box.createVerticalStrut(12));
        card.add(fLabel("Confirm Password")); card.add(Box.createVerticalStrut(5)); card.add(confirmField);
        card.add(Box.createVerticalStrut(4)); card.add(showPass2);
        card.add(Box.createVerticalStrut(20));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(divider);
        card.add(Box.createVerticalStrut(14));
        card.add(backBtn);
        card.add(Box.createVerticalStrut(18));
        card.add(footer);
 
        root.add(card);
    }
 
    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirm  = new String(confirmField.getPassword()).trim();
 
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            msg("Please fill in all fields!", "Error"); return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            msg("Invalid email address!", "Error"); return;
        }
        if (password.length() < 6) {
            msg("Password must be at least 6 characters!", "Error"); return;
        }
        if (!password.equals(confirm)) {
            msg("Passwords do not match!", "Error"); return;
        }
 
        if (DatabaseManager.registerUser(username, email, password)) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            msg("Registration failed. Username may already exist.", "Error");
        }
    }
 
    private void msg(String m, String t) {
        JOptionPane.showMessageDialog(this, m, t, JOptionPane.ERROR_MESSAGE);
    }
 
    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel cLabel(String t, int sz, int style, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", style, sz));
        l.setForeground(c); l.setAlignmentX(CENTER_ALIGNMENT); return l;
    }
    private JLabel fLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT_PRI); l.setAlignmentX(LEFT_ALIGNMENT); return l;
    }
    private JCheckBox mkCheckbox(String t) {
        JCheckBox c = new JCheckBox(t);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        c.setForeground(TEXT_MUT); c.setBackground(CARD_BG);
        c.setAlignmentX(LEFT_ALIGNMENT);
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return c;
    }
    private void styleInput(javax.swing.text.JTextComponent f) {
        f.setBackground(INPUT_BG); f.setForeground(TEXT_PRI); f.setCaretColor(ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new RoundedLineBorder(BORDER_CLR,1,8), new EmptyBorder(8,12,8,12)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(new RoundedLineBorder(INPUT_FOC,2,8), new EmptyBorder(7,11,7,11)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(new RoundedLineBorder(BORDER_CLR,1,8), new EmptyBorder(8,12,8,12)));
            }
        });
    }
    private JButton accentButton(String text, Color n, Color h) {
        JButton btn = new JButton(text) {
            private Color cur = n;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e){cur=h;repaint();}
                public void mouseExited(MouseEvent e){cur=n;repaint();}
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cur); g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10));
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI",Font.BOLD,14));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,44)); btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return btn;
    }
    private JButton outlineButton(String text) {
        JButton btn = new JButton(text) {
            private boolean h=false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e){h=true;repaint();}
                public void mouseExited(MouseEvent e){h=false;repaint();}
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(h?new Color(30,37,45):CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10));
                g2.setColor(BORDER_CLR); g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,10,10));
                g2.setColor(TEXT_PRI); g2.setFont(new Font("Segoe UI",Font.PLAIN,14));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,44)); btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return btn;
    }
    private JPanel divider() {
        JPanel p=new JPanel(new GridBagLayout()); p.setBackground(CARD_BG);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE,20)); p.setAlignmentX(LEFT_ALIGNMENT);
        GridBagConstraints gc=new GridBagConstraints(); gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1;
        JSeparator s1=new JSeparator(); s1.setForeground(BORDER_CLR);
        JSeparator s2=new JSeparator(); s2.setForeground(BORDER_CLR);
        JLabel or=new JLabel("  or  "); or.setFont(new Font("Segoe UI",Font.PLAIN,12)); or.setForeground(TEXT_MUT);
        p.add(s1,gc); gc.weightx=0; p.add(or,gc); gc.weightx=1; p.add(s2,gc); return p;
    }
 
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext","true");
        SwingUtilities.invokeLater(()->new RegisterFrame().setVisible(true));
    }
 
    // ── Inner classes (reused from LoginFrame) ────────────────────────────────
    static class PlaceholderTextField extends JTextField {
        private final String hint;
        PlaceholderTextField(String hint){this.hint=hint;}
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if(getText().isEmpty()&&!isFocusOwner()){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(139,148,158)); g2.setFont(new Font("Segoe UI",Font.ITALIC,13));
                g2.drawString(hint,13,getHeight()/2+5); g2.dispose();
            }
        }
    }
    static class PlaceholderPasswordField extends JPasswordField {
        private final String hint;
        PlaceholderPasswordField(String hint){this.hint=hint;setEchoChar('\u2022');}
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if(getPassword().length==0&&!isFocusOwner()){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(139,148,158)); g2.setFont(new Font("Segoe UI",Font.ITALIC,13));
                g2.drawString(hint,13,getHeight()/2+5); g2.dispose();
            }
        }
    }
    static class RoundedPanel extends JPanel {
        private final int r; private final Color fill,border;
        RoundedPanel(int r,Color fill,Color border){this.r=r;this.fill=fill;this.border=border;setOpaque(false);}
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill); g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),r*2,r*2));
            g2.setColor(border); g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0.5f,0.5f,getWidth()-1,getHeight()-1,r*2,r*2));
            g2.dispose(); super.paintComponent(g);
        }
    }
    static class RoundedLineBorder extends AbstractBorder {
        private final Color color; private final int thick,radius;
        RoundedLineBorder(Color color,int thick,int radius){this.color=color;this.thick=thick;this.radius=radius;}
        @Override public void paintBorder(Component c,Graphics g,int x,int y,int w,int h){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(thick));
            g2.draw(new RoundRectangle2D.Float(x+thick/2f,y+thick/2f,w-thick,h-thick,radius*2,radius*2));
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c){return new Insets(thick+2,thick+2,thick+2,thick+2);}
    }
}