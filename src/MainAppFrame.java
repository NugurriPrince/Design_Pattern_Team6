// 파일 이름: MainAppFrame.java
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter; // <-- 이 줄이 추가되었습니다!
import java.util.List;

public class MainAppFrame extends JFrame implements Observer {
    private final DataManager dataManager; private final RentalService rentalService; private final User loggedInUser;
    private final List<Item> items; private final List<User> users; private final List<RentalRecord> rentalHistory;
    private JComboBox<Item> itemComboBox;
    private JPanel itemListPanel;
    private JTextArea logArea;
    private static final Color DK_BLUE = new Color(0, 44, 122); private static final Color BG_LIGHT_GRAY = new Color(245, 245, 245);
    
    public MainAppFrame(DataManager dataManager, RentalService rentalService, User loggedInUser, List<User> users, List<Item> items, List<RentalRecord> rentalHistory) {
        this.dataManager = dataManager; this.rentalService = rentalService; this.loggedInUser = loggedInUser;
        this.users = users; this.items = items; this.rentalHistory = rentalHistory;
        items.forEach(item -> item.addObserver(this));
        
        setupUI();
        updateItemDisplay();
    }
    
    private void setupUI() {
        setTitle("단국대학교 물품 대여 시스템"); setSize(900, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { dataManager.saveData(users, items, rentalHistory); System.exit(0); } });
        getContentPane().setBackground(BG_LIGHT_GRAY);
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        JMenu systemMenu = new JMenu("시스템");
        systemMenu.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JMenuItem logoutItem = new JMenuItem("로그아웃");
        JMenuItem exitItem = new JMenuItem("종료");
        
        // --- 여기를 수정했습니다! ---
        logoutItem.addActionListener(e -> { dataManager.saveData(users, items, rentalHistory); dispose(); RentalSystem_Final.main(null); });
        
        exitItem.addActionListener(e -> { dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)); });
        systemMenu.add(logoutItem); systemMenu.add(exitItem); menuBar.add(systemMenu);
        if ("Admin".equals(loggedInUser.getType())) {
            JMenu adminMenu = new JMenu("관리 (Admin)");
            adminMenu.setForeground(DK_BLUE);
            adminMenu.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            JMenuItem manageUsersItems = new JMenuItem("사용자/물품 관리...");
            manageUsersItems.addActionListener(e -> new AdminDialog(this, users, items).setVisible(true));
            JMenuItem viewHistory = new JMenuItem("전체 대여 기록 보기...");
            viewHistory.addActionListener(e -> new RentalHistoryDialog(this, rentalHistory).setVisible(true));
            adminMenu.add(manageUsersItems); adminMenu.add(viewHistory); menuBar.add(adminMenu);
        }
        setJMenuBar(menuBar);
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BG_LIGHT_GRAY);
        add(mainPanel, BorderLayout.CENTER);
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(DK_BLUE);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        try {
            ImageIcon dankookLogo = new ImageIcon("단국대 로고.png");
            Image image = dankookLogo.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            headerPanel.add(imageLabel, BorderLayout.WEST);
        } catch (Exception e) { System.err.println("메인 로고 이미지 로딩 실패. 이미지를 생략합니다."); }
        JLabel titleLabel = new JLabel("물품 대여 시스템");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        JPanel controlPanel = createControlPanel();
        JSplitPane splitPane = createInfoPanel();
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(controlPanel, BorderLayout.NORTH); centerPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }
    
    private TitledBorder createTitledBorder(String title) { TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(DK_BLUE), title); border.setTitleColor(DK_BLUE); border.setTitleFont(new Font("맑은 고딕", Font.BOLD, 14)); return border; }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout()); panel.setBackground(Color.WHITE); panel.setBorder(BorderFactory.createCompoundBorder(createTitledBorder("대여 / 반납"), new EmptyBorder(5, 5, 5, 5))); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.HORIZONTAL; JLabel userLabel = new JLabel("<html><b>" + loggedInUser.getName() + "</b> 님, 환영합니다.</html>"); userLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        itemComboBox = new JComboBox<>(items.toArray(new Item[0]));
        itemComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        itemComboBox.setRenderer(new ItemRenderer());
        
        JButton rentButton = new JButton("대여하기");
        JButton returnButton = new JButton("반납하기");
        Font btnFont = new Font("맑은 고딕", Font.BOLD, 14);
        rentButton.setBackground(DK_BLUE); rentButton.setForeground(Color.BLACK); rentButton.setFont(btnFont);
        returnButton.setBackground(Color.WHITE); returnButton.setForeground(DK_BLUE); returnButton.setFont(btnFont); rentButton.setBorder(BorderFactory.createLineBorder(DK_BLUE, 2)); returnButton.setBorder(BorderFactory.createLineBorder(DK_BLUE, 2)); rentButton.addActionListener(e -> rentAction()); returnButton.addActionListener(e -> returnAction()); gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST; panel.add(userLabel, gbc); gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST; panel.add(new JLabel("물품 선택:"), gbc); gbc.gridx = 1; gbc.weightx = 1.0; panel.add(itemComboBox, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); buttonPanel.setBackground(Color.WHITE); buttonPanel.add(rentButton); buttonPanel.add(returnButton); gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST; panel.add(buttonPanel, gbc); return panel;
    }
    
    private JSplitPane createInfoPanel() {
        JPanel itemDisplayPanel = new JPanel(new BorderLayout());
        itemDisplayPanel.setBackground(Color.WHITE);
        itemDisplayPanel.setBorder(createTitledBorder("실시간 물품 재고"));
        itemListPanel = new JPanel();
        itemListPanel.setLayout(new GridLayout(0, 1, 0, 0));
        itemListPanel.setBackground(Color.WHITE);
        itemListPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(itemListPanel, BorderLayout.NORTH);
        itemDisplayPanel.add(new JScrollPane(wrapperPanel), BorderLayout.CENTER);
        JPanel logPanel = new JPanel(new BorderLayout()); logPanel.setBackground(Color.WHITE); logPanel.setBorder(createTitledBorder("실시간 활동 기록"));
        logArea = new JTextArea(); logArea.setEditable(false); logArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); logArea.setBorder(new EmptyBorder(5, 10, 5, 10));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, itemDisplayPanel, logPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        return splitPane;
    }
    
    private JPanel createItemCardPanel(Item item) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 2));
        card.setBackground(Color.WHITE);
        Border dashedBorder = BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 1, 3);
        card.setBorder(BorderFactory.createCompoundBorder(dashedBorder, new EmptyBorder(8, 8, 8, 8)));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        topPanel.add(nameLabel, BorderLayout.CENTER);
        
        int currentStock = item.getCurrentStock();
        JLabel stockLabel = new JLabel("재고: " + currentStock + "개");
        stockLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        if (currentStock == 0) { stockLabel.setForeground(Color.RED); }
        else if (currentStock <= 5) { stockLabel.setForeground(new Color(255, 128, 0)); }
        else { stockLabel.setForeground(DK_BLUE); }
        topPanel.add(stockLabel, BorderLayout.EAST);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        JLabel depositLabel = new JLabel(String.format("보증금: %d원", item.getDeposit()));
        depositLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        depositLabel.setForeground(Color.GRAY);
        bottomPanel.add(depositLabel, BorderLayout.WEST);
        
        JLabel deadlineLabel = new JLabel(String.format("반납기한: %d일", item.getReturnDeadlineDays()));
        deadlineLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        deadlineLabel.setForeground(Color.GRAY);
        bottomPanel.add(deadlineLabel, BorderLayout.EAST);

        card.add(topPanel);
        card.add(bottomPanel);
        
        return card;
    }
    
    private void updateItemDisplay() {
        itemListPanel.removeAll();
        for (Item item : items) { itemListPanel.add(createItemCardPanel(item)); }
        itemListPanel.revalidate(); itemListPanel.repaint();
    }
    
    @Override public void update(Item item) { updateItemDisplay(); }
    
    private void rentAction() { 
        Item selectedItem = (Item) itemComboBox.getSelectedItem(); 
        if (selectedItem != null) { 
            
            RentalRecord record = rentalService.rentItem(loggedInUser, selectedItem); 
            
            if (record != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");
                String dueDate = record.getDueDate().format(formatter);
                String message = String.format(
                    "<html>성공적으로 대여하셨습니다.<br><br>" +
                    "<b>물품명:</b> %s<br>" +
                    "<b>보증금:</b> %.0f원<br>" +
                    "<b>반납기한:</b> %s 까지입니다.<br><br>" +
                    "<small>반납기한이 지날 시 보증금의 일부가 연체비로 부과됩니다.</small></html>",
                    record.getItemName(),
                    record.getDepositPaid(),
                    dueDate
                );
                
                JOptionPane.showMessageDialog(this, message, "대여 성공", JOptionPane.INFORMATION_MESSAGE);
                
                String logMessage = String.format("[대여 성공] %s -> %s (보증금: %.0f원)\n", 
                    loggedInUser.getName(), selectedItem.getName(), (double)selectedItem.getDeposit());
                logArea.append(logMessage);

            } else {
                String failMessage = "[대여 실패] " + selectedItem.getName() + " 재고가 없습니다.";
                JOptionPane.showMessageDialog(this, failMessage, "대여 실패", JOptionPane.ERROR_MESSAGE);
                logArea.append(failMessage + "\n");
            }
        } 
    }
    
    private void returnAction() { Item selectedItem = (Item) itemComboBox.getSelectedItem(); if (selectedItem != null) { String result = rentalService.returnItem(loggedInUser, selectedItem); logArea.append(result + "\n"); } }
    
    public void refreshItemComboBox() { DefaultComboBoxModel<Item> model = (DefaultComboBoxModel<Item>) itemComboBox.getModel(); model.removeAllElements(); items.forEach(model::addElement); updateItemDisplay(); }
    
    class ItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Item) {
                Item item = (Item) value;
                if (item.getCurrentStock() > 0) { setText(item.getName()); setForeground(Color.BLACK); }
                else { setText(item.getName() + " (대여 불가)"); setForeground(Color.GRAY); }
            }
            return this;
        }
    }
}