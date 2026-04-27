import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Main extends JFrame {

    // ── colours ──────────────────────────────────────────────
    static final Color BG      = new Color(15, 20, 40);
    static final Color PANEL   = new Color(25, 35, 65);
    static final Color ACCENT  = new Color(99, 179, 237);
    static final Color GREEN   = new Color(72, 199, 142);
    static final Color ORANGE  = new Color(246, 173, 85);
    static final Color TEXT    = new Color(226, 232, 240);
    static final Color MUTED   = new Color(113, 128, 150);
    static final Color RED     = new Color(252, 129, 129);

    // ── state ─────────────────────────────────────────────────
    User currentUser;
    RentalService service = new RentalService();
    Billing billing = new Billing();
    Vehicle selectedVehicle;
    int rentalDays = 1;

    // ── panels ────────────────────────────────────────────────
    CardLayout cards = new CardLayout();
    JPanel root = new JPanel(cards);

    // Login
    JTextField loginEmail = field(); JPasswordField loginPass = passField();

    // Dashboard
    JPanel vehiclePanel = new JPanel(new GridLayout(0, 2, 14, 14));
    JLabel welcomeLabel = label("Welcome!", 20, Font.BOLD);

    // Booking
    JLabel bookVehicleName = label("", 18, Font.BOLD);
    JLabel bookPrice = label("", 14, Font.PLAIN);
    JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(1,1,30,1));
    JLabel totalLabel = label("Total: 0.00 Tk", 16, Font.BOLD);

    // Payment
    JLabel payTotal = label("", 16, Font.BOLD);
    JComboBox<String> methodBox = new JComboBox<>(new String[]{"Cash","bKash","Nagad","Card"});
    JTextField accountField = field();
    JTextField cardNum = field(); JTextField cardExp = field(); JTextField cardCVV = field();
    JPanel mobilePanel = new JPanel(); JPanel cardPanel = new JPanel();

    // Receipt
    JTextArea receiptArea = new JTextArea();

    public Main() {
        setTitle("🚗 Vehicle Rental System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 640);
        setMinimumSize(new Dimension(760, 560));
        setLocationRelativeTo(null);
        setBackground(BG);

        root.setBackground(BG);
        root.add(buildLoginPanel(),     "LOGIN");
        root.add(buildDashboard(),      "DASHBOARD");
        root.add(buildBookingPanel(),   "BOOKING");
        root.add(buildPaymentPanel(),   "PAYMENT");
        root.add(buildReceiptPanel(),   "RECEIPT");

        add(root);
        cards.show(root, "LOGIN");
        setVisible(true);
    }

    // ─────────────────────── LOGIN ────────────────────────────
    JPanel buildLoginPanel() {
        JPanel p = dark(new JPanel(new GridBagLayout()));
        JPanel box = panel(PANEL);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(40, 50, 40, 50));
        box.setMaximumSize(new Dimension(400, 500));

        JLabel title = label("🚗 Vehicle Rental", 26, Font.BOLD);
        title.setForeground(ACCENT); title.setAlignmentX(CENTER_ALIGNMENT);
        JLabel sub = label("Sign in to continue", 13, Font.PLAIN);
        sub.setForeground(MUTED); sub.setAlignmentX(CENTER_ALIGNMENT);

        JButton loginBtn = accentBtn("Login", ACCENT);
        JButton regBtn   = accentBtn("Register", GREEN);
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        regBtn.setAlignmentX(CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            if (loginEmail.getText().isEmpty() || new String(loginPass.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            currentUser = new User(loginEmail.getText(), new String(loginPass.getPassword()));
            welcomeLabel.setText("Welcome, " + currentUser.getEmailOrPhone() + " 👋");
            loadVehicles("Car");
            cards.show(root, "DASHBOARD");
        });

        regBtn.addActionListener(e -> {
            if (loginEmail.getText().isEmpty() || new String(loginPass.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Save registered user info
            String regEmail = loginEmail.getText();
            String regPass  = new String(loginPass.getPassword());
            // Clear fields and go back to Login
            loginEmail.setText("");
            loginPass.setText("");
            JOptionPane.showMessageDialog(this,
                "Registration successful!\nPlease login with your credentials.",
                "Registered", JOptionPane.INFORMATION_MESSAGE);
            // Pre-fill login fields for convenience
            loginEmail.setText(regEmail);
            loginPass.setText(regPass);
            cards.show(root, "LOGIN");
        });

        box.add(title); box.add(Box.createVerticalStrut(6)); box.add(sub);
        box.add(Box.createVerticalStrut(28));
        box.add(formRow("Email / Phone", loginEmail));
        box.add(Box.createVerticalStrut(14));
        box.add(formRow("Password", loginPass));
        box.add(Box.createVerticalStrut(28));
        box.add(loginBtn); box.add(Box.createVerticalStrut(10)); box.add(regBtn);

        p.add(box);
        return p;
    }

    // ─────────────────────── DASHBOARD ───────────────────────
    JPanel buildDashboard() {
        JPanel p = dark(new JPanel(new BorderLayout(0,0)));

        // sidebar
        JPanel side = panel(new Color(18,26,52));
        side.setPreferredSize(new Dimension(200,0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(30,16,20,16));

        JLabel logo = label("🚗 RentEase", 18, Font.BOLD);
        logo.setForeground(ACCENT); logo.setAlignmentX(CENTER_ALIGNMENT);
        side.add(logo); side.add(Box.createVerticalStrut(30));

        for (String type : new String[]{"Car","Bike","Truck"}) {
            JButton b = sideBtn(type.equals("Car") ? "🚗 "+type : type.equals("Bike") ? "🏍 "+type : "🚛 "+type);
            b.addActionListener(e -> loadVehicles(type));
            side.add(b); side.add(Box.createVerticalStrut(8));
        }

        side.add(Box.createVerticalGlue());
        JButton logout = sideBtn("🚪 Logout");
        logout.setForeground(RED);
        logout.addActionListener(e -> { loginEmail.setText(""); loginPass.setText(""); cards.show(root,"LOGIN"); });
        side.add(logout);

        // main content
        JPanel main = dark(new JPanel(new BorderLayout(0,14)));
        main.setBorder(new EmptyBorder(24,24,24,24));

        JPanel topBar = dark(new JPanel(new BorderLayout()));
        welcomeLabel.setForeground(TEXT);
        topBar.add(welcomeLabel, BorderLayout.WEST);
        JLabel subtitle = label("Select a vehicle to book", 13, Font.PLAIN);
        subtitle.setForeground(MUTED);
        topBar.add(subtitle, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(vehiclePanel);
        scroll.setBackground(BG); scroll.getViewport().setBackground(BG);
        scroll.setBorder(null);

        main.add(topBar, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);

        p.add(side, BorderLayout.WEST);
        p.add(main, BorderLayout.CENTER);
        return p;
    }

    void loadVehicles(String type) {
        service = new RentalService();
        if (type.equals("Car")) {
            service.addVehicle(new Car("Toyota Corolla", 5000));
            service.addVehicle(new Car("Honda Civic", 4500));
            service.addVehicle(new Car("BMW X5", 12000));
            service.addVehicle(new Car("Tesla Model 3", 15000));
        } else if (type.equals("Bike")) {
            service.addVehicle(new Bike("Yamaha R15", 1000));
            service.addVehicle(new Bike("Suzuki Gixxer", 1200));
            service.addVehicle(new Bike("Honda CBR", 1800));
        } else {
            service.addVehicle(new Truck("Volvo Truck", 8000));
            service.addVehicle(new Truck("Tata Truck", 7000));
            service.addVehicle(new Truck("Mercedes Axor", 10000));
        }

        vehiclePanel.removeAll();
        vehiclePanel.setBackground(BG);
        for (Vehicle v : service.getVehicles()) {
            vehiclePanel.add(vehicleCard(v));
        }
        vehiclePanel.revalidate(); vehiclePanel.repaint();
    }

    JPanel vehicleCard(Vehicle v) {
        JPanel card = panel(PANEL);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(
            new LineBorder(ACCENT.darker(), 1, true),
            new EmptyBorder(20, 20, 20, 20)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        String icon = v.getType().equals("Car") ? "🚗" : v.getType().equals("Bike") ? "🏍" : "🚛";
        JLabel ico = label(icon, 36, Font.PLAIN); ico.setAlignmentX(CENTER_ALIGNMENT);
        JLabel nm  = label(v.getVehicleName(), 15, Font.BOLD); nm.setForeground(TEXT); nm.setAlignmentX(CENTER_ALIGNMENT);
        JLabel pr  = label(String.format("%.0f Tk / day", v.getPricePerDay()), 13, Font.PLAIN);
        pr.setForeground(ORANGE); pr.setAlignmentX(CENTER_ALIGNMENT);

        JButton bookBtn = accentBtn("Book Now", ACCENT);
        bookBtn.setAlignmentX(CENTER_ALIGNMENT);
        bookBtn.addActionListener(e -> openBooking(v));

        card.add(ico); card.add(Box.createVerticalStrut(8));
        card.add(nm);  card.add(Box.createVerticalStrut(4));
        card.add(pr);  card.add(Box.createVerticalStrut(16));
        card.add(bookBtn);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(35,50,90)); }
            public void mouseExited(MouseEvent e)  { card.setBackground(PANEL); }
        });
        return card;
    }

    // ─────────────────────── BOOKING ─────────────────────────
    JPanel buildBookingPanel() {
        JPanel p = dark(new JPanel(new GridBagLayout()));
        JPanel box = panel(PANEL);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(36, 50, 36, 50));
        box.setMaximumSize(new Dimension(480, 600));

        JLabel title = label("📋 Booking Details", 22, Font.BOLD);
        title.setForeground(ACCENT); title.setAlignmentX(CENTER_ALIGNMENT);

        bookVehicleName.setAlignmentX(CENTER_ALIGNMENT);
        bookPrice.setForeground(ORANGE); bookPrice.setAlignmentX(CENTER_ALIGNMENT);
        totalLabel.setForeground(GREEN); totalLabel.setAlignmentX(CENTER_ALIGNMENT);

        // style spinner
        daysSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JSpinner.DefaultEditor ed = (JSpinner.DefaultEditor) daysSpinner.getEditor();
        ed.getTextField().setBackground(BG); ed.getTextField().setForeground(TEXT);
        ed.getTextField().setFont(new Font("SansSerif", Font.PLAIN, 14));
        daysSpinner.setBackground(BG);

        daysSpinner.addChangeListener(e -> updateTotal());

        JButton proceed = accentBtn("Proceed to Payment", GREEN);
        proceed.setAlignmentX(CENTER_ALIGNMENT);
        proceed.addActionListener(e -> {
            rentalDays = (int) daysSpinner.getValue();
            openPayment();
        });

        JButton back = accentBtn("← Back", MUTED);
        back.setAlignmentX(CENTER_ALIGNMENT);
        back.addActionListener(e -> cards.show(root, "DASHBOARD"));

        box.add(title); box.add(Box.createVerticalStrut(24));
        box.add(bookVehicleName); box.add(Box.createVerticalStrut(4));
        box.add(bookPrice); box.add(Box.createVerticalStrut(24));
        box.add(formRow("Rental Days", daysSpinner));
        box.add(Box.createVerticalStrut(18));
        box.add(totalLabel); box.add(Box.createVerticalStrut(28));
        box.add(proceed); box.add(Box.createVerticalStrut(10));
        box.add(back);

        p.add(box);
        return p;
    }

    void openBooking(Vehicle v) {
        selectedVehicle = v;
        bookVehicleName.setText(v.getVehicleName());
        bookPrice.setText(String.format("%.0f Tk / day", v.getPricePerDay()));
        daysSpinner.setValue(1);
        updateTotal();
        cards.show(root, "BOOKING");
    }

    void updateTotal() {
        if (selectedVehicle == null) return;
        int days = (int) daysSpinner.getValue();
        double t = billing.calculateBill(selectedVehicle.getPricePerDay(), days);
        totalLabel.setText(String.format("Total: %.2f Tk", t));
        payTotal.setText(String.format("Amount Due: %.2f Tk", t));
    }

    // ─────────────────────── PAYMENT ─────────────────────────
    JPanel buildPaymentPanel() {
        JPanel p = dark(new JPanel(new GridBagLayout()));
        JPanel box = panel(PANEL);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(32, 50, 32, 50));
        box.setMaximumSize(new Dimension(500, 700));

        JLabel title = label("💳 Bill Payment", 22, Font.BOLD);
        title.setForeground(ACCENT); title.setAlignmentX(CENTER_ALIGNMENT);
        payTotal.setForeground(GREEN); payTotal.setAlignmentX(CENTER_ALIGNMENT);

        // Method selector
        methodBox.setBackground(Color.WHITE);
        methodBox.setForeground(Color.BLACK);
        methodBox.setFont(new Font("SansSerif", Font.BOLD, 14));
        methodBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        methodBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setForeground(Color.BLACK);
                setFont(new Font("SansSerif", Font.BOLD, 14));
                if (isSelected) {
                    setBackground(ACCENT);
                    setForeground(Color.BLACK);
                } else {
                    setBackground(Color.WHITE);
                }
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });

        // Mobile panel
        mobilePanel.setLayout(new BoxLayout(mobilePanel, BoxLayout.Y_AXIS));
        mobilePanel.setBackground(BG);
        mobilePanel.add(formRow("Mobile Number", accountField));

        // Card panel
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(BG);
        cardPanel.add(formRow("Card Number", cardNum));
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(formRow("Expiry (MM/YY)", cardExp));
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(formRow("CVV", cardCVV));

        // Cash info panel
        JPanel cashPanel = new JPanel();
        cashPanel.setLayout(new BoxLayout(cashPanel, BoxLayout.Y_AXIS));
        cashPanel.setBackground(new Color(30, 45, 70));
        cashPanel.setBorder(new CompoundBorder(
            new LineBorder(ORANGE, 1, true),
            new EmptyBorder(14, 18, 14, 18)));
        JLabel cashIcon = label("💵", 30, Font.PLAIN); cashIcon.setAlignmentX(CENTER_ALIGNMENT);
        JLabel cashInfo = label("Pay at the counter", 13, Font.BOLD);
        cashInfo.setForeground(ORANGE); cashInfo.setAlignmentX(CENTER_ALIGNMENT);
        JLabel cashNote = label("Please bring the exact amount in cash.", 12, Font.PLAIN);
        cashNote.setForeground(TEXT); cashNote.setAlignmentX(CENTER_ALIGNMENT);
        JButton cashConfirmBtn = new JButton("✔ I Will Pay in Cash");
        cashConfirmBtn.setBackground(ORANGE);
        cashConfirmBtn.setForeground(Color.BLACK);
        cashConfirmBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        cashConfirmBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        cashConfirmBtn.setFocusPainted(false);
        cashConfirmBtn.setOpaque(true);
        cashConfirmBtn.setContentAreaFilled(true);
        cashConfirmBtn.setAlignmentX(CENTER_ALIGNMENT);
        cashPanel.add(cashIcon); cashPanel.add(Box.createVerticalStrut(6));
        cashPanel.add(cashInfo); cashPanel.add(Box.createVerticalStrut(4));
        cashPanel.add(cashNote); cashPanel.add(Box.createVerticalStrut(12));
        cashPanel.add(cashConfirmBtn);

        JPanel dynPanel = dark(new JPanel(new CardLayout()));
        dynPanel.add(cashPanel,   "Cash");
        dynPanel.add(mobilePanel, "bKash");
        dynPanel.add(mobilePanel, "Nagad");
        dynPanel.add(cardPanel,   "Card");

        methodBox.addActionListener(e -> {
            String sel = (String) methodBox.getSelectedItem();
            ((CardLayout) dynPanel.getLayout()).show(dynPanel, sel);
        });

        JButton payBtn = accentBtn("✅ Confirm Payment", GREEN);
        payBtn.setAlignmentX(CENTER_ALIGNMENT);
        payBtn.addActionListener(e -> processPayment());

        JButton back = accentBtn("← Back", MUTED);
        back.setAlignmentX(CENTER_ALIGNMENT);
        back.addActionListener(e -> cards.show(root, "BOOKING"));

        box.add(title); box.add(Box.createVerticalStrut(16));
        box.add(payTotal); box.add(Box.createVerticalStrut(20));
        box.add(formRow("Payment Method", methodBox));
        box.add(Box.createVerticalStrut(14));
        box.add(dynPanel);
        box.add(Box.createVerticalStrut(24));
        box.add(payBtn); box.add(Box.createVerticalStrut(10));
        box.add(back);

        p.add(box);
        return p;
    }

    void openPayment() {
        updateTotal();
        methodBox.setSelectedIndex(0);
        cards.show(root, "PAYMENT");
    }

    void processPayment() {
        String method = (String) methodBox.getSelectedItem();
        int days = (int) daysSpinner.getValue();
        double total = billing.calculateBill(selectedVehicle.getPricePerDay(), days);
        String account = accountField.getText().trim();

        if ((method.equals("bKash") || method.equals("Nagad")) && account.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your mobile number.", "Error", JOptionPane.ERROR_MESSAGE); return;
        }
        if (method.equals("Card") && (cardNum.getText().isEmpty() || cardExp.getText().isEmpty() || cardCVV.getText().isEmpty())) {
            JOptionPane.showMessageDialog(this, "Please fill all card details.", "Error", JOptionPane.ERROR_MESSAGE); return;
        }

        String result = billing.processPayment(total, method, account.isEmpty() ? cardNum.getText() : account);
        String[] parts = result.split("\\|");
        showReceipt(method, total, days, parts[1]);
    }

    // ─────────────────────── RECEIPT ─────────────────────────
    JPanel buildReceiptPanel() {
        JPanel p = dark(new JPanel(new BorderLayout(0,20)));
        p.setBorder(new EmptyBorder(30,40,30,40));

        JLabel title = label("✅ Booking Confirmed!", 24, Font.BOLD);
        title.setForeground(GREEN);

        receiptArea.setEditable(false);
        receiptArea.setBackground(PANEL);
        receiptArea.setForeground(TEXT);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        receiptArea.setBorder(new EmptyBorder(20,20,20,20));

        JScrollPane scroll = new JScrollPane(receiptArea);
        scroll.setBorder(new LineBorder(ACCENT.darker(), 1, true));

        JButton home = accentBtn("🏠 Back to Home", ACCENT);
        home.addActionListener(e -> { loadVehicles("Car"); cards.show(root,"DASHBOARD"); });

        JPanel south = dark(new JPanel(new FlowLayout(FlowLayout.CENTER)));
        south.add(home);

        p.add(title, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        p.add(south,  BorderLayout.SOUTH);
        return p;
    }

    void showReceipt(String method, double total, int days, String msg) {
        String line = "═".repeat(44);
        String receipt = line + "\n" +
            "        VEHICLE RENTAL SYSTEM\n" +
            "             RECEIPT\n" +
            line + "\n" +
            String.format("  Customer  : %s%n", currentUser.getEmailOrPhone()) +
            String.format("  Vehicle   : %s%n", selectedVehicle.getVehicleName()) +
            String.format("  Type      : %s%n", selectedVehicle.getType()) +
            String.format("  Rate/Day  : %.2f Tk%n", selectedVehicle.getPricePerDay()) +
            String.format("  Days      : %d%n", days) +
            line + "\n" +
            String.format("  TOTAL     : %.2f Tk%n", total) +
            String.format("  Method    : %s%n", method) +
            line + "\n" +
            "  STATUS    : PAYMENT SUCCESSFUL ✓\n" +
            line + "\n" +
            "  " + msg + "\n" +
            line + "\n" +
            "  Thank you for choosing RentEase!\n" +
            line;

        receiptArea.setText(receipt);
        cards.show(root, "RECEIPT");
    }

    // ─────────────────────── HELPERS ─────────────────────────
    JPanel dark(JPanel p)          { p.setBackground(BG); return p; }
    JPanel panel(Color c)          { JPanel p = new JPanel(); p.setBackground(c); return p; }
    JPanel panel(LayoutManager lm) { JPanel p = new JPanel(lm); p.setBackground(BG); return p; }

    JLabel label(String t, int sz, int style) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif", style, sz));
        l.setForeground(TEXT);
        return l;
    }

    JTextField field() {
        JTextField f = new JTextField();
        f.setBackground(BG); f.setForeground(TEXT); f.setCaretColor(TEXT);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(new CompoundBorder(new LineBorder(MUTED, 1, true), new EmptyBorder(6,10,6,10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return f;
    }

    JPasswordField passField() {
        JPasswordField f = new JPasswordField();
        f.setBackground(BG); f.setForeground(TEXT); f.setCaretColor(TEXT);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(new CompoundBorder(new LineBorder(MUTED, 1, true), new EmptyBorder(6,10,6,10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return f;
    }

    JButton accentBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color); b.setForeground(Color.BLACK);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(new EmptyBorder(10, 28, 10, 28));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true); b.setContentAreaFilled(true);
        b.addMouseListener(new MouseAdapter() {
            Color orig = color;
            public void mouseEntered(MouseEvent e) { b.setBackground(orig.brighter()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(orig); }
        });
        return b;
    }

    JButton sideBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(18,26,52)); b.setForeground(Color.BLACK);
        b.setFont(new Font("SansSerif", Font.PLAIN, 14));
        b.setBorder(new EmptyBorder(10, 16, 10, 16));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setOpaque(true); b.setContentAreaFilled(true); b.setAlignmentX(LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(30,45,85)); }
            public void mouseExited(MouseEvent e)  { b.setBackground(new Color(18,26,52)); }
        });
        return b;
    }

    JPanel formRow(String labelText, JComponent comp) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(BG);
        row.setAlignmentX(CENTER_ALIGNMENT);
        JLabel lbl = label(labelText, 12, Font.PLAIN);
        lbl.setForeground(MUTED);
        row.add(lbl); row.add(Box.createVerticalStrut(4));
        comp.setAlignmentX(LEFT_ALIGNMENT);
        row.add(comp);
        return row;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(Main::new);
    }
}