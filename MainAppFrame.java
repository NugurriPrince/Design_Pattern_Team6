import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

// ... (UI 클래스들) ...
class MainAppFrame extends JFrame implements Observer {
    private final DataManager dataManager;
    private final RentalService rentalService;
    private final User loggedInUser;
    private final List<Item> items;
    private final List<User> users;
    private final List<RentalRecord> rentalHistory;
    private JComboBox<Item> itemComboBox;
    private JLabel strategyLabel;
    private JTextArea itemStatusArea;
    private JTextArea logArea;
    private DiscountStrategy userStrategy;

    public MainAppFrame(DataManager dataManager, RentalService rentalService, User loggedInUser, List<User> users, List<Item> items, List<RentalRecord> rentalHistory) {
        this.dataManager = dataManager;
        this.rentalService = rentalService;
        this.loggedInUser = loggedInUser;
        this.users = users;
        this.items = items;
        this.rentalHistory = rentalHistory;
        items.forEach(item -> item.addObserver(this));
        if ("Student".equals(loggedInUser.getType())) {
            this.userStrategy = new StudentDiscountStrategy();
        } else {
            this.userStrategy = new NoDiscountStrategy();
        }
        setupUI();
        updateItemStatusArea();
    }

    private void setupUI() {
        setTitle("교내 물품 대여 시스템");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dataManager.saveData(users, items, rentalHistory); // 저장 시 history 추가
                System.exit(0);
            }
        });

        JMenuBar menuBar = new JMenuBar();
        if ("Admin".equals(loggedInUser.getType())) {
            JMenu adminMenu = new JMenu("관리 (Admin)");
            JMenuItem manageUsersItems = new JMenuItem("사용자/물품 관리...");
            manageUsersItems.addActionListener(e -> new AdminDialog(this, users, items).setVisible(true));

            // [새로운 메뉴] 대여 기록 보기
            JMenuItem viewHistory = new JMenuItem("전체 대여 기록 보기...");
            viewHistory.addActionListener(e -> new RentalHistoryDialog(this, rentalHistory).setVisible(true));

            adminMenu.add(manageUsersItems);
            adminMenu.add(viewHistory); // 메뉴에 추가
            menuBar.add(adminMenu);
        }
        setJMenuBar(menuBar);
        // ... (이하 setupUI의 나머지 부분은 이전과 동일)
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel);
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        JSplitPane splitPane = createInfoPanel();
        mainPanel.add(splitPane, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    // ... (MainAppFrame의 나머지 메서드들은 이전과 동일)
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.setBorder(new TitledBorder("대여/반납"));
        JLabel userLabel = new JLabel("<html><b>" + loggedInUser.getName() + "</b>님, 환영합니다.</html>");
        itemComboBox = new JComboBox<>(items.toArray(new Item[0]));
        strategyLabel = new JLabel("적용 정책: " + userStrategy.getStrategyName());
        JButton rentButton = new JButton("대여하기");
        rentButton.addActionListener(e -> rentAction());
        JButton returnButton = new JButton("반납하기");
        returnButton.addActionListener(e -> returnAction());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(userLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("물품 선택:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(itemComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(strategyLabel, gbc);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(rentButton);
        buttonPanel.add(returnButton);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        return panel;
    }

    private JSplitPane createInfoPanel() {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(new TitledBorder("실시간 물품 재고"));
        itemStatusArea = new JTextArea();
        itemStatusArea.setEditable(false);
        itemStatusArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        itemPanel.add(new JScrollPane(itemStatusArea), BorderLayout.CENTER);
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(new TitledBorder("대여/반납 기록"));
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, itemPanel, logPanel);
        splitPane.setResizeWeight(0.4);
        return splitPane;
    }

    private void rentAction() {
        Item selectedItem = (Item) itemComboBox.getSelectedItem();
        if (selectedItem != null) {
            String result = rentalService.rentItem(loggedInUser, selectedItem, userStrategy);
            logArea.append(result + "\n");
        }
    }

    private void returnAction() {
        Item selectedItem = (Item) itemComboBox.getSelectedItem();
        if (selectedItem != null) {
            String result = rentalService.returnItem(loggedInUser, selectedItem);
            logArea.append(result + "\n");
        }
    }

    public void refreshItemComboBox() {
        DefaultComboBoxModel<Item> model = (DefaultComboBoxModel<Item>) itemComboBox.getModel();
        model.removeAllElements();
        items.forEach(model::addElement);
        updateItemStatusArea();
    }

    private void updateItemStatusArea() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s | %s\n", "물품명", "현재 재고"));
        sb.append("---------------------------\n");
        items.forEach(item -> sb.append(String.format("%-15s | %d개\n", item.getName(), item.getCurrentStock())));
        itemStatusArea.setText(sb.toString());
    }

    @Override
    public void update(Item item) {
        updateItemStatusArea();
    }
}
