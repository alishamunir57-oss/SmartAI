package com.mycompany.smartai;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
 
public class ChatFrame extends JFrame {
 
    private boolean isFirstMessage = true;
    private int userId, chatId = -1;
 
    private JTextArea   chatArea;
    private JTextField  messageField;
    private JList<String>          historyList;
    private DefaultListModel<String> historyModel;
    private JTextField  searchField;
    private String      username;
 
    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG_DARK    = new Color(13,  17,  23);
    private static final Color SIDEBAR_BG = new Color(22,  27,  34);
    private static final Color CHAT_BG    = new Color(13,  17,  23);
    private static final Color BORDER_CLR = new Color(48,  54,  61);
    private static final Color ACCENT     = new Color(88, 166, 255);
    private static final Color ACCENT_HOV = new Color(58, 140, 230);
    private static final Color DANGER     = new Color(218, 54,  51);
    private static final Color DANGER_HOV = new Color(180, 35,  35);
    private static final Color TEXT_PRI   = new Color(230, 237, 243);
    private static final Color TEXT_MUT   = new Color(139, 148, 158);
    private static final Color INPUT_BG   = new Color(22,  27,  34);
    private static final Color INPUT_FOC  = new Color(31,  111, 235);
    private static final Color USER_CLR   = new Color(88, 166, 255);
    private static final Color AI_CLR     = new Color(63, 185,  80);
 
    public ChatFrame(String username) {
        this.username = username;
        this.userId   = DatabaseManager.getUserId(username);
 
        setTitle("SmartAI — " + username);
        setSize(960, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 450));
 
        buildUI();
        loadChatHistory();
 
        SwingUtilities.invokeLater(() -> messageField.requestFocusInWindow());
    }
 
    // ── Build UI ──────────────────────────────────────────────────────────────
    private void buildUI() {
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());
 
        add(buildSidebar(),  BorderLayout.WEST);
        add(buildChatArea(), BorderLayout.CENTER);
        add(buildInputBar(), BorderLayout.SOUTH);
    }
 
    // ── LEFT SIDEBAR ──────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, BORDER_CLR));
 
        // Top: app name
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(SIDEBAR_BG);
        topBar.setBorder(new EmptyBorder(16, 16, 12, 16));
        JLabel appName = new JLabel("✦  SmartAI");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(ACCENT);
        topBar.add(appName, BorderLayout.CENTER);
 
        // New chat button
        JButton newChatBtn = mkBtn("＋  New Chat", ACCENT, ACCENT_HOV, true);
        newChatBtn.addActionListener(e -> {
            chatArea.setText(""); chatId = -1; isFirstMessage = true;
            messageField.requestFocusInWindow();
        });
        JPanel newBtnWrap = new JPanel(new BorderLayout());
        newBtnWrap.setBackground(SIDEBAR_BG);
        newBtnWrap.setBorder(new EmptyBorder(0, 12, 10, 12));
        newBtnWrap.add(newChatBtn);
 
        // Search
        searchField = new JTextField();
        searchField.setBackground(new Color(13,17,23));
        searchField.setForeground(TEXT_PRI);
        searchField.setCaretColor(ACCENT);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(new CompoundBorder(
            new MatteBorder(1,1,1,1, BORDER_CLR),
            new EmptyBorder(7,10,7,10)
        ));
        searchField.putClientProperty("JTextField.placeholderText", "Search chats...");
        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setBackground(SIDEBAR_BG);
        searchWrap.setBorder(new EmptyBorder(0,12,10,12));
        searchWrap.add(searchField);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { searchChats(searchField.getText()); }
        });
 
        // History label
        JLabel histLbl = new JLabel("  Recent Chats");
        histLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        histLbl.setForeground(TEXT_MUT);
        histLbl.setBorder(new EmptyBorder(4,12,4,12));
 
        // History list
        historyModel = new DefaultListModel<>();
        historyList  = new JList<>(historyModel);
        historyList.setBackground(SIDEBAR_BG);
        historyList.setForeground(TEXT_PRI);
        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyList.setSelectionBackground(new Color(48,54,61));
        historyList.setSelectionForeground(TEXT_PRI);
        historyList.setFixedCellHeight(38);
        historyList.setBorder(new EmptyBorder(0,4,0,4));
        historyList.setCellRenderer(new HistoryCellRenderer());
        historyList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) openSelectedChat();
            }
        });
        JScrollPane histScroll = new JScrollPane(historyList);
        histScroll.setBackground(SIDEBAR_BG);
        histScroll.setBorder(null);
        histScroll.getViewport().setBackground(SIDEBAR_BG);
 
        // Bottom: delete + logout
        JButton deleteBtn  = mkBtn("🗑  Delete Chat", DANGER, DANGER_HOV, false);
        JButton logoutBtn  = mkBtn("↩  Logout",       new Color(48,54,61), new Color(60,67,75), false);
        deleteBtn.addActionListener(e -> { deleteChat(); messageField.requestFocusInWindow(); });
        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
 
        JPanel bottomBtns = new JPanel(new GridLayout(2,1,0,6));
        bottomBtns.setBackground(SIDEBAR_BG);
        bottomBtns.setBorder(new EmptyBorder(10,12,16,12));
        bottomBtns.add(deleteBtn);
        bottomBtns.add(logoutBtn);
 
        // User badge at very bottom
        JLabel userBadge = new JLabel("  👤 " + username);
        userBadge.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userBadge.setForeground(TEXT_MUT);
        userBadge.setBorder(new EmptyBorder(0,12,10,12));
 
        JPanel centerStack = new JPanel();
        centerStack.setLayout(new BoxLayout(centerStack, BoxLayout.Y_AXIS));
        centerStack.setBackground(SIDEBAR_BG);
        centerStack.add(histLbl);
        centerStack.add(histScroll);
 
        JPanel bottomStack = new JPanel(new BorderLayout());
        bottomStack.setBackground(SIDEBAR_BG);
        bottomStack.setBorder(new MatteBorder(1,0,0,0, BORDER_CLR));
        bottomStack.add(bottomBtns, BorderLayout.CENTER);
        bottomStack.add(userBadge,  BorderLayout.SOUTH);
 
        sidebar.add(topBar,      BorderLayout.NORTH);
        sidebar.add(newBtnWrap,  BorderLayout.NORTH); // replaces topBar — fix below
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.removeAll();
        sidebar.setLayout(new BorderLayout());
 
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBackground(SIDEBAR_BG);
        topSection.add(topBar);
        topSection.add(newBtnWrap);
        topSection.add(searchWrap);
 
        sidebar.add(topSection,  BorderLayout.NORTH);
        sidebar.add(centerStack, BorderLayout.CENTER);
        sidebar.add(bottomStack, BorderLayout.SOUTH);
 
        return sidebar;
    }
 
    // ── CHAT AREA ─────────────────────────────────────────────────────────────
    private JScrollPane buildChatArea() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(CHAT_BG);
        chatArea.setForeground(TEXT_PRI);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBorder(new EmptyBorder(16, 20, 16, 20));
        chatArea.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { messageField.requestFocusInWindow(); }
        });
 
        // Welcome message
        chatArea.setText("  SmartAI  —  Ask me anything!\n\n");
 
        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CHAT_BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }
 
    // ── INPUT BAR ─────────────────────────────────────────────────────────────
    private JPanel buildInputBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(SIDEBAR_BG);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(1,0,0,0, BORDER_CLR),
            new EmptyBorder(12,16,12,16)
        ));
 
        messageField = new JTextField();
        messageField.setBackground(INPUT_BG);
        messageField.setForeground(TEXT_PRI);
        messageField.setCaretColor(ACCENT);
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageField.setBorder(new CompoundBorder(
            new RoundedLineBorder(BORDER_CLR, 1, 10),
            new EmptyBorder(10,14,10,14)
        ));
        messageField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                messageField.setBorder(new CompoundBorder(
                    new RoundedLineBorder(INPUT_FOC,2,10), new EmptyBorder(9,13,9,13)));
            }
            public void focusLost(FocusEvent e) {
                messageField.setBorder(new CompoundBorder(
                    new RoundedLineBorder(BORDER_CLR,1,10), new EmptyBorder(10,14,10,14)));
            }
        });
        messageField.addActionListener(e -> sendMessage());
 
        JButton sendBtn = mkBtn("Send  ➤", ACCENT, ACCENT_HOV, true);
        sendBtn.setPreferredSize(new Dimension(110, 44));
        sendBtn.addActionListener(e -> sendMessage());
 
        bar.add(messageField, BorderLayout.CENTER);
        bar.add(sendBtn,      BorderLayout.EAST);
        return bar;
    }
 
    // ── SEND MESSAGE ──────────────────────────────────────────────────────────
private void sendMessage() {

    String message = messageField.getText().trim();

    if (message.isEmpty()) return;

    // Disable input
    messageField.setEnabled(false);

    appendChat("You", message, USER_CLR);

    messageField.setText("");

    // Create chat first time
    if (isFirstMessage) {

        chatId = DatabaseManager.createChat(userId, generateTitle(message));

        loadChatHistory();

        isFirstMessage = false;
    }

    // Show thinking text
    int thinkingStart = chatArea.getDocument().getLength();

    chatArea.append("SmartAI:\nThinking...\n\n");

    chatArea.setCaretPosition(chatArea.getDocument().getLength());

    new Thread(() -> {

        try {

            // API call
            String reply = getReply(message);

            SwingUtilities.invokeLater(() -> {

                try {

                    // Remove ONLY current thinking text
                    int currentLength = chatArea.getDocument().getLength();

                    chatArea.getDocument().remove(
                        thinkingStart,
                        currentLength - thinkingStart
                    );

                } catch (Exception ex) {

                    ex.printStackTrace();
                }

                // Show final response
                appendChat("SmartAI", reply, AI_CLR);

                // Re-enable input
                messageField.setEnabled(true);

                messageField.requestFocusInWindow();
            });

            // Save in database
            DatabaseManager.saveMessage(chatId, message, reply);

        } catch (Exception ex) {

            ex.printStackTrace();

            SwingUtilities.invokeLater(() -> {

                try {

                    int currentLength = chatArea.getDocument().getLength();

                    chatArea.getDocument().remove(
                        thinkingStart,
                        currentLength - thinkingStart
                    );

                } catch (Exception ignored) {}

                appendChat("SmartAI",
                    "Error getting response.",
                    AI_CLR
                );

                messageField.setEnabled(true);

                messageField.requestFocusInWindow();
            });
        }

    }).start();
}
 
    // ── Typewriter effect ─────────────────────────────────────────────────────
    private void typewriterEffect(String fullText) {
        char[] chars = fullText.toCharArray();
        int[] index = {0};
        javax.swing.Timer t = new javax.swing.Timer(12, null);
        t.addActionListener(e -> {
            if (index[0] < chars.length) {
                chatArea.append(String.valueOf(chars[index[0]]));
                index[0]++;
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            } else {
                ((javax.swing.Timer)e.getSource()).stop();
            }
        });
        t.start();
    }
 
    private void appendChat(String sender, String text, Color nameColor) {
        chatArea.append(sender + ":\n" + text + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
 
    // ── GEMINI API — 90s timeout fix ─────────────────────────────────────────
    private String getReply(String message) {

    try {

        System.out.println("Starting API request...");

        String apiKey = "AIzaSyBilIv5nypmbFqXb4Ze2qpsyPXjdg-jfwk";

        String apiUrl =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
            + apiKey;

        String safe = message
            .replace("\\", "\\\\")
            .replace("\"", "'")
            .replace("\n", " ")
            .replace("\r", " ");

        String body =
            "{\"contents\":[{\"parts\":[{\"text\":\""
            + safe +
            "\"}]}]}";

        java.net.URL url = new java.net.URL(apiUrl);

        java.net.HttpURLConnection conn =
            (java.net.HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");

        conn.setRequestProperty(
            "Content-Type",
            "application/json"
        );

        conn.setConnectTimeout(10000);

        conn.setReadTimeout(15000);

        conn.setDoOutput(true);

        System.out.println("Sending request...");

        try (java.io.OutputStream os = conn.getOutputStream()) {

            os.write(body.getBytes("utf-8"));
        }

        System.out.println("Waiting for response...");

        int rc = conn.getResponseCode();

        System.out.println("Response code: " + rc);

        java.io.InputStream is =
            (rc >= 200 && rc < 300)
            ? conn.getInputStream()
            : conn.getErrorStream();

        java.io.BufferedReader br =
            new java.io.BufferedReader(
                new java.io.InputStreamReader(is, "utf-8")
            );

        StringBuilder sb = new StringBuilder();

        String line;

        while ((line = br.readLine()) != null) {

            sb.append(line);
        }

        String response = sb.toString();

        System.out.println(response);

        java.util.regex.Matcher m =
            java.util.regex.Pattern
            .compile("\"text\":\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
            .matcher(response);

        if (m.find()) {

            return m.group(1)
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
        }

        return "API Error:\n" + response;

    }

    catch (java.net.SocketTimeoutException e) {

        e.printStackTrace();

        return "Request timed out.";
    }

    catch (Exception e) {

        e.printStackTrace();

        return "Error:\n" + e.getMessage();
    }
}
 
    // ── HISTORY helpers (logic unchanged) ─────────────────────────────────────
    private String generateTitle(String msg) {
        return msg.length()>25 ? msg.substring(0,25)+"..." : msg;
    }
    private void loadChatHistory() {
        historyModel.clear();
        for(String[] c : DatabaseManager.getChats(userId))
            historyModel.addElement(c[0] + " — " + c[1]);
    }
    private void searchChats(String key) {
        historyModel.clear();
        for(String[] c : DatabaseManager.getChats(userId))
            if(c[1].toLowerCase().contains(key.toLowerCase()))
                historyModel.addElement(c[0] + " — " + c[1]);
    }
    private void openSelectedChat() {
        String sel = historyList.getSelectedValue();
        if(sel==null) return;
        int id = Integer.parseInt(sel.split("—")[0].trim());
        chatId = id; isFirstMessage = false;
        chatArea.setText("");
        for(String line : DatabaseManager.getMessages(id)) {
            chatArea.append(line + "\n");
 
        }
    }
    private void deleteChat() {
        String sel = historyList.getSelectedValue();
        if(sel==null){ JOptionPane.showMessageDialog(this,"Select a chat first."); return; }
        int id = Integer.parseInt(sel.split("—")[0].trim());
        DatabaseManager.deleteChat(id);
        loadChatHistory();
        chatArea.setText(""); chatId=-1; isFirstMessage=true;
    }
 
    // ── Button factory ────────────────────────────────────────────────────────
    private JButton mkBtn(String text, Color n, Color h, boolean filled) {
        JButton btn = new JButton(text) {
            private Color cur=n; private boolean hov=false;
            { addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){cur=h;hov=true;repaint();}
                public void mouseExited(MouseEvent e){cur=n;hov=false;repaint();}
            }); }
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cur);
                if(filled) g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                else {
                    g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                    g2.setColor(new Color(n.getRed(),n.getGreen(),n.getBlue(),80));
                }
                g2.setColor(filled?Color.WHITE:TEXT_PRI);
                g2.setFont(new Font("Segoe UI",filled?Font.BOLD:Font.PLAIN,13));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
 
    // ── Custom list cell renderer ─────────────────────────────────────────────
    class HistoryCellRenderer extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(JList<?> l, Object val,
                int i, boolean sel, boolean foc) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(l,val,i,sel,foc);
            String text = val.toString();
            // strip the id prefix for display
            int dash = text.indexOf('—');
            lbl.setText("  💬 " + (dash>=0 ? text.substring(dash+1).trim() : text));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(sel ? Color.WHITE : TEXT_PRI);
            lbl.setBackground(sel ? new Color(48,54,61) : SIDEBAR_BG);
            lbl.setBorder(new EmptyBorder(0,4,0,4));
            return lbl;
        }
    }
 
    // ── Inner border classes ──────────────────────────────────────────────────
    static class RoundedLineBorder extends AbstractBorder {
        private final Color c; private final int t,r;
        RoundedLineBorder(Color c,int t,int r){this.c=c;this.t=t;this.r=r;}
        @Override public void paintBorder(Component comp,Graphics g,int x,int y,int w,int h){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c); g2.setStroke(new BasicStroke(t));
            g2.draw(new RoundRectangle2D.Float(x+t/2f,y+t/2f,w-t,h-t,r*2,r*2));
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component comp){return new Insets(t+2,t+2,t+2,t+2);}
    }
}