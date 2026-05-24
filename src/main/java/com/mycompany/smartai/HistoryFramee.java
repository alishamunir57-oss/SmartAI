package com.mycompany.smartai;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
 
public class HistoryFramee extends JFrame {
 
    private String    currentUser;
    private JTextArea textArea;
 
    private static final Color BG_DARK   = new Color(13,  17,  23);
    private static final Color CARD_BG   = new Color(22,  27,  34);
    private static final Color BORDER_CLR= new Color(48,  54,  61);
    private static final Color ACCENT    = new Color(88, 166, 255);
    private static final Color ACCENT_HOV= new Color(58, 140, 230);
    private static final Color TEXT_PRI  = new Color(230, 237, 243);
    private static final Color TEXT_MUT  = new Color(139, 148, 158);
 
    public HistoryFramee(String username) {
        this.currentUser = username;
        setTitle("Chat History — " + username);
        setSize(520, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        buildUI();
        loadHistory();
    }
 
    private void buildUI() {
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());
 
        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0,0,1,0,BORDER_CLR),
            new EmptyBorder(16,20,16,20)
        ));
        JLabel title = new JLabel("📋  Chat History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRI);
        JLabel user = new JLabel("👤 " + currentUser);
        user.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        user.setForeground(TEXT_MUT);
        header.add(title, BorderLayout.WEST);
        header.add(user,  BorderLayout.EAST);
 
        // ── Text area ─────────────────────────────────────────────────────────
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(BG_DARK);
        textArea.setForeground(TEXT_PRI);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(16,20,16,20));
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_DARK);
 
        // ── Footer buttons ────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(CARD_BG);
        footer.setBorder(new MatteBorder(1,0,0,0,BORDER_CLR));
 
        JButton refreshBtn = roundBtn("↻  Refresh", ACCENT,     ACCENT_HOV);
        JButton closeBtn   = roundBtn("✕  Close",   new Color(48,54,61), new Color(60,67,75));
        refreshBtn.addActionListener(e -> { loadHistory(); });
        closeBtn.addActionListener(e -> dispose());
 
        footer.add(refreshBtn);
        footer.add(closeBtn);
 
        add(header, BorderLayout.NORTH);
        add(scroll,  BorderLayout.CENTER);
        add(footer,  BorderLayout.SOUTH);
    }
 
    private void loadHistory() {
        textArea.setText("");
        int userId = DatabaseManager.getUserId(currentUser);
        List<String[]> chats = DatabaseManager.getChats(userId);
 
        if (chats.isEmpty()) {
            textArea.append("No chats found for " + currentUser + ".\n");
            return;
        }
 
        textArea.append("Chat History for: " + currentUser + "\n");
        textArea.append("━".repeat(44) + "\n\n");
 
        for (String[] chat : chats) {
            textArea.append("Chat ID : " + chat[0] + "\n");
            textArea.append("Title   : " + chat[1] + "\n");
            textArea.append("─".repeat(40) + "\n\n");
        }
    }
 
    private JButton roundBtn(String text, Color n, Color h) {
        JButton btn = new JButton(text) {
            private Color cur=n;
            { addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){cur=h;repaint();}
                public void mouseExited(MouseEvent e){cur=n;repaint();}
            }); }
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cur); g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI",Font.BOLD,13));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(120,38));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return btn;
    }
}
