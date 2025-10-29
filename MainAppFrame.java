// 파일 이름: MainAppFrame.java

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * 물품 대여 시스템의 메인 애플리케이션 프레임(창) 클래스입니다.
 * 사용자가 로그인한 후 보게 되는 주 화면을 구성하고 관리합니다.
 * Observer 인터페이스를 구현하여, Item 객체의 상태(재고) 변경을 감지하고
 * 실시간으로 UI를 업데이트하는 역할을 합니다.
 */
public class MainAppFrame extends JFrame implements Observer {
    // --- 필드(Fields) ---
    private final DataManager dataManager;
    private final RentalService rentalService;
    private final User loggedInUser;
    private final List<Item> items;
    private final List<User> users;
    private final List<RentalRecord> rentalHistory;

    // UI 컴포넌트 참조
    private JComboBox<Item> itemComboBox;
    private JLabel strategyLabel;
    private JPanel itemListPanel;
    private JTextArea logArea;
    private DiscountStrategy userStrategy;

    // UI 스타일링을 위한 상수 색상 정의
    private static final Color DK_BLUE = new Color(0, 44, 122);
    private static final Color BG_LIGHT_GRAY = new Color(245, 245, 245);

    /**
     * MainAppFrame 생성자
     */
    public MainAppFrame(DataManager dataManager, RentalService rentalService, User loggedInUser, List<User> users, List<Item> items, List<RentalRecord> rentalHistory) {
        this.dataManager = dataManager;
        this.rentalService = rentalService;
        this.loggedInUser = loggedInUser;
        this.users = users;
        this.items = items;
        this.rentalHistory = rentalHistory;

        // 옵저버 패턴: 현재 프레임(this)을 모든 아이템의 옵저버로 등록
        items.forEach(item -> item.addObserver(this));

        // 전략 패턴: 로그인한 사용자의 타입에 따라 적절한 할인 정책을 설정
        if ("Student".equals(loggedInUser.getType())) {
            this.userStrategy = new StudentDiscountStrategy();
        } else {
            this.userStrategy = new NoDiscountStrategy();
        }

        // UI 초기 설정 및 데이터 표시
        setupUI();
        updateItemDisplay();
    }

    /**
     * 메인 프레임의 전체적인 UI를 설정하고 구성합니다.
     */
    private void setupUI() {
        // 프레임 기본 설정
        setTitle("단국대학교 물품 대여 시스템");
        setSize(900, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 창을 직접 닫지 않도록 설정

        // 창 닫기 버튼을 눌렀을 때의 동작 정의 (데이터 저장 후 종료)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dataManager.saveData(users, items, rentalHistory);
                System.exit(0);
            }
        });

        getContentPane().setBackground(BG_LIGHT_GRAY);

        // 메뉴바 생성
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JMenu systemMenu = new JMenu("시스템");
        systemMenu.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JMenuItem logoutItem = new JMenuItem("로그아웃");
        JMenuItem exitItem = new JMenuItem("종료");

        // 로그아웃 메뉴 아이템 액션
        logoutItem.addActionListener(e -> {
            dataManager.saveData(users, items, rentalHistory);
            dispose();
            RentalSystem_Final.main(null);
        });

        // 종료 메뉴 아이템 액션 (windowClosing 이벤트 호출)
        exitItem.addActionListener(e -> {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });

        systemMenu.add(logoutItem);
        systemMenu.add(exitItem);
        menuBar.add(systemMenu);

        // 관리자(Admin)일 경우에만 관리 메뉴를 추가
        if ("Admin".equals(loggedInUser.getType())) {
            JMenu adminMenu = new JMenu("관리 (Admin)");
            adminMenu.setForeground(DK_BLUE);
            adminMenu.setFont(new Font("맑은 고딕", Font.BOLD, 12));

            JMenuItem manageUsersItems = new JMenuItem("사용자/물품 관리...");
            manageUsersItems.addActionListener(e -> new AdminDialog(this, users, items).setVisible(true));

            JMenuItem viewHistory = new JMenuItem("전체 대여 기록 보기...");
            viewHistory.addActionListener(e -> new RentalHistoryDialog(this, rentalHistory).setVisible(true));

            adminMenu.add(manageUsersItems);
            adminMenu.add(viewHistory);
            menuBar.add(adminMenu);
        }
        setJMenuBar(menuBar);

        // 메인 패널 레이아웃 설정
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BG_LIGHT_GRAY);
        add(mainPanel, BorderLayout.CENTER);

        // 상단 헤더 패널 (로고, 타이틀)
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(DK_BLUE);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        try {
            ImageIcon dankookLogo = new ImageIcon("단국대 로고.png");
            Image image = dankookLogo.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            headerPanel.add(imageLabel, BorderLayout.WEST);
        } catch (Exception e) {
            System.err.println("메인 로고 이미지 로딩 실패. 이미지를 생략합니다.");
        }

        JLabel titleLabel = new JLabel("물품 대여 시스템");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 중앙 컨텐츠 패널 (컨트롤 + 정보)
        JPanel controlPanel = createControlPanel();
        JSplitPane splitPane = createInfoPanel();
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(controlPanel, BorderLayout.NORTH);
        centerPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 화면 중앙에 창 표시
        setLocationRelativeTo(null);
    }

    /**
     * 제목이 있는 테두리(TitledBorder)를 생성하는 헬퍼 메소드
     * @param title 테두리에 표시할 제목
     * @return 스타일이 적용된 TitledBorder 객체
     */
    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(DK_BLUE), title);
        border.setTitleColor(DK_BLUE);
        border.setTitleFont(new Font("맑은 고딕", Font.BOLD, 14));
        return border;
    }

    /**
     * "대여 / 반납" 컨트롤 패널을 생성합니다.
     * @return 생성된 컨트롤 JPanel
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(createTitledBorder("대여 / 반납"), new EmptyBorder(5, 5, 5, 5)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("<html><b>" + loggedInUser.getName() + "</b> 님, 환영합니다.</html>");
        userLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        itemComboBox = new JComboBox<>(items.toArray(new Item[0]));
        itemComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        itemComboBox.setRenderer(new ItemRenderer());

        strategyLabel = new JLabel("적용 정책: " + userStrategy.getStrategyName());
        strategyLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        JButton rentButton = new JButton("대여하기");
        JButton returnButton = new JButton("반납하기");
        Font btnFont = new Font("맑은 고딕", Font.BOLD, 14);

        rentButton.setBackground(DK_BLUE);
        rentButton.setForeground(Color.BLACK);
        rentButton.setFont(btnFont);

        returnButton.setBackground(Color.WHITE);
        returnButton.setForeground(DK_BLUE);
        returnButton.setFont(btnFont);

        rentButton.setBorder(BorderFactory.createLineBorder(DK_BLUE, 2));
        returnButton.setBorder(BorderFactory.createLineBorder(DK_BLUE, 2));

        rentButton.addActionListener(e -> rentAction());
        returnButton.addActionListener(e -> returnAction());

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        panel.add(userLabel, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("물품 선택:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(itemComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(strategyLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(rentButton);
        buttonPanel.add(returnButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    /**
     * 재고 현황과 활동 기록을 보여주는 분할(Split) 패널을 생성합니다.
     * @return 생성된 JSplitPane
     */
    private JSplitPane createInfoPanel() {
        // 상단: 실시간 물품 재고 패널
        JPanel itemDisplayPanel = new JPanel(new BorderLayout());
        itemDisplayPanel.setBackground(Color.WHITE);
        itemDisplayPanel.setBorder(createTitledBorder("실시간 물품 재고"));

        itemListPanel = new JPanel();
        itemListPanel.setLayout(new GridLayout(0, 1, 0, 0)); // 아이템 카드를 세로로 쌓음
        itemListPanel.setBackground(Color.WHITE);
        itemListPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // itemListPanel을 다른 패널로 감싸서 GridLayout이 위쪽부터 채워지도록 함
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(itemListPanel, BorderLayout.NORTH);
        itemDisplayPanel.add(new JScrollPane(wrapperPanel), BorderLayout.CENTER);

        // 하단: 실시간 활동 기록 패널
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(Color.WHITE);
        logPanel.setBorder(createTitledBorder("실시간 활동 기록"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBorder(new EmptyBorder(5, 10, 5, 10));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        // 상단과 하단 패널을 수직으로 분할
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, itemDisplayPanel, logPanel);
        splitPane.setResizeWeight(0.5); // 초기 분할 비율 50%
        splitPane.setBorder(null);

        return splitPane;
    }

    /**
     * 개별 물품의 정보를 보여주는 '카드' 형태의 패널을 생성합니다.
     * @param item 정보를 표시할 Item 객체
     * @return 생성된 카드 JPanel
     */
    private JPanel createItemCardPanel(Item item) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        Border dashedBorder = BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 1, 3);
        card.setBorder(BorderFactory.createCompoundBorder(dashedBorder, new EmptyBorder(8, 8, 8, 8)));

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        card.add(nameLabel, BorderLayout.CENTER);

        int currentStock = item.getCurrentStock();
        JLabel stockLabel = new JLabel("재고: " + currentStock + "개");
        stockLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));

        // 재고 수량에 따라 글자색 변경
        if (currentStock == 0) {
            stockLabel.setForeground(Color.RED);
        } else if (currentStock <= 5) {
            stockLabel.setForeground(new Color(255, 128, 0));
        } else {
            stockLabel.setForeground(DK_BLUE);
        }
        card.add(stockLabel, BorderLayout.EAST);

        return card;
    }

    /**
     * '실시간 물품 재고' 패널의 내용을 최신 데이터로 업데이트(다시 그리기)합니다.
     */
    private void updateItemDisplay() {
        itemListPanel.removeAll(); // 기존의 모든 아이템 카드를 제거
        for (Item item : items) {
            itemListPanel.add(createItemCardPanel(item)); // 최신 정보로 카드 다시 생성 및 추가
        }
        itemListPanel.revalidate(); // 레이아웃을 다시 계산
        itemListPanel.repaint();    // 화면을 다시 그림
    }

    /**
     * Observer 인터페이스의 구현 메소드.
     * 관찰하던 Item 객체의 상태가 변경되면 이 메소드가 실행됩니다.
     */
    @Override
    public void update(Item item) {
        updateItemDisplay();
    }

    /**
     * '대여하기' 버튼 클릭 시 실행되는 액션입니다.
     */
    private void rentAction() {
        Item selectedItem = (Item) itemComboBox.getSelectedItem();
        if (selectedItem != null) {
            String result = rentalService.rentItem(loggedInUser, selectedItem, userStrategy);
            logArea.append(result + "\n");
        }
    }

    /**
     * '반납하기' 버튼 클릭 시 실행되는 액션입니다.
     */
    private void returnAction() {
        Item selectedItem = (Item) itemComboBox.getSelectedItem();
        if (selectedItem != null) {
            String result = rentalService.returnItem(loggedInUser, selectedItem);
            logArea.append(result + "\n");
        }
    }

    /**
     * 관리자 패널에서 물품 정보가 변경되었을 때,
     * 메인 화면의 물품 선택 콤보박스를 새로고침합니다.
     */
    public void refreshItemComboBox() {
        DefaultComboBoxModel<Item> model = (DefaultComboBoxModel<Item>) itemComboBox.getModel();
        model.removeAllElements(); // 기존 항목 모두 제거
        items.forEach(model::addElement); // 최신 items 리스트로 다시 채움
        updateItemDisplay(); // 재고 표시 패널도 함께 갱신
    }

    /**
     * JComboBox의 각 항목을 어떻게 표시할지 정의하는 커스텀 렌더러 클래스입니다.
     */
    class ItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Item) {
                Item item = (Item) value;
                if (item.getCurrentStock() > 0) {
                    // 재고가 있으면 일반 텍스트로 표시
                    setText(item.getName());
                    setForeground(Color.BLACK);
                } else {
                    // 재고가 없으면 "(대여 불가)" 문구와 함께 회색으로 표시
                    setText(item.getName() + " (대여 불가)");
                    setForeground(Color.GRAY);
                }
            }
            return this;
        }
    }
}