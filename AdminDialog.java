// 파일 이름: AdminDialog.java

/**
 * 관리자 전용 기능을 제공하는 GUI 대화 상자(Dialog) 클래스입니다.
 * 이 클래스는 사용자 관리와 물품 관리 두 개의 탭으로 구성되어 있으며,
 * 관리자는 이 패널을 통해 사용자 및 물품 데이터를 생성(Create), 조회(Read), 삭제(Delete)할 수 있습니다.
 *
 * 주요 기능:
 * 1. 사용자 관리: 사용자 목록 조회, 신규 사용자 추가, 기존 사용자 삭제
 * 2. 물품 관리: 물품 목록 조회, 신규 물품 추가, 기존 물품 삭제
 * 3. 데이터 연동: 물품 데이터 변경 시 MainAppFrame의 UI를 갱신합니다.
 *
 * @author (작성자 이름)
 * @version 1.0
 * @since (작성 날짜, 예: 2023-10-27)
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * 관리자 기능을 제공하는 다이얼로그 창 클래스
 * 사용자 관리와 물품 관리 탭으로 구성
 */
public class AdminDialog extends JDialog {
    private MainAppFrame parentFrame; // 부모 프레임 참조 (MainAppFrame)
    private List<User> users; // 전체 사용자 목록
    private List<Item> items; // 전체 물품 목록

    private JTable userTable; // 사용자 정보를 표시할 테이블
    private JTable itemTable; // 물품 정보를 표시할 테이블

    private DefaultTableModel userTableModel; // 사용자 테이블의 데이터를 관리하는 모델
    private DefaultTableModel itemTableModel; // 물품 테이블의 데이터를 관리하는 모델

    // UI 스타일링을 위한 상수 색상 정의
    private static final Color DK_BLUE = new Color(0, 44, 122);             // 진한 파란색
    private static final Color BG_LIGHT_GRAY = new Color(245, 245, 245);    // 밝은 회색 배경


    /**
     * AdminDialog 생성자
     * @param parent 부모 프레임 (MainAppFrame)
     * @param users 사용자 데이터 리스트
     * @param items 물품 데이터 리스트
     */
    public AdminDialog(MainAppFrame parent, List<User> users, List<Item> items) {
        super(parent, "관리자 패널", true); // JDialog의 생성자를 호출하여 타이틀과 Modal 설정

        this.parentFrame = parent; 
        this.users = users; 
        this.items = items;

        // 다이얼로그 기본 설정
        setSize(800, 600); 
        setLayout(new BorderLayout());
        
        // 탭 패널 생성
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        tabbedPane.addTab("사용자 관리", createUserManagementPanel());  // "사용자 관리" 탭 추가
        tabbedPane.addTab("물품 관리", createItemManagementPanel());    // "물품 관리" 탭 추가
        
        add(tabbedPane, BorderLayout.CENTER);   // 탭 패널을 다이얼로그 중앙에 추가
        setLocationRelativeTo(parent);          // 다이얼로그를 부모 프레임 중앙에 위치시킴
    }

    /**
     * 패널의 제목이 있는 테두리(TitledBorder)를 생성하는 헬퍼 메소드
     * @param title 테두리에 표시할 제목
     * @return 스타일이 적용된 TitledBorder 객체
     */
    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), title);
        border.setTitleColor(DK_BLUE); 
        border.setTitleFont(new Font("맑은 고딕", Font.BOLD, 12));
        return border;
    }
    
    /**
     * JTable의 공통 스타일을 적용하는 헬퍼 메소드
     * @param table 스타일을 적용할 JTable 객체
     */
    private void styleTable(JTable table) {
        // 테이블 기본 스타일 설정
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(184, 207, 229));
        table.setSelectionForeground(Color.BLACK);

        table.setDefaultRenderer(Object.class, new ZebraTableCellRenderer()); // 테이블 행을 번갈아 다른 색으로 칠하는 렌더러 설정

        // 테이블 헤더 스타일 설정
        JTableHeader header = table.getTableHeader();
        header.setBackground(DK_BLUE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(100, 32));
    }

    /**
     * "사용자 관리" 탭에 표시될 패널을 생성합니다.
     * @return 사용자 관리 기능이 구현된 JPanel 객체
     */
    private JPanel createUserManagementPanel() {
        // 메인 패널 생성 (BorderLayout)
        JPanel panel = new JPanel(new BorderLayout(10, 10)); 
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); 
        panel.setBackground(BG_LIGHT_GRAY);

        // --- 상단: 신규 사용자 추가 패널 ---
        JPanel addUserPanel = new JPanel(new GridBagLayout());
        addUserPanel.setBorder(createTitledBorder("신규 사용자 추가"));
        addUserPanel.setOpaque(false); // 배경을 투명하게 하여 부모 패널의 배경색이 보이도록 함

        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.insets = new Insets(5, 5, 5, 5); // 컴포넌트 간의 여백
        
        // 사용자 정보 입력을 위한 컴포넌트 생성
        JTextField userIdField = new JTextField(10); 
        JTextField userNameField = new JTextField(10); 
        JTextField userPassField = new JTextField(10);
        JComboBox<String> userTypeCombo = new JComboBox<>(new String[]{"Student", "Staff", "Admin"});
        JButton addUserButton = new JButton("추가");
        addUserButton.setBackground(DK_BLUE); 
        addUserButton.setForeground(Color.BLACK);
        
        // GridBagLayout을 사용하여 컴포넌트 배치
        gbc.gridx = 0; gbc.gridy = 0; addUserPanel.add(new JLabel("ID:"), gbc); 
        gbc.gridx = 1; gbc.gridy = 0; addUserPanel.add(userIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; addUserPanel.add(new JLabel("이름:"), gbc); 
        gbc.gridx = 1; gbc.gridy = 1; addUserPanel.add(userNameField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; addUserPanel.add(new JLabel("비밀번호:"), gbc); 
        gbc.gridx = 3; gbc.gridy = 0; addUserPanel.add(userPassField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; addUserPanel.add(new JLabel("타입:"), gbc); 
        gbc.gridx = 3; gbc.gridy = 1; addUserPanel.add(userTypeCombo, gbc);

        gbc.gridx = 4; gbc.gridy = 1; 
        gbc.anchor = GridBagConstraints.EAST; // 오른쪽 정렬
        addUserPanel.add(addUserButton, gbc);

        panel.add(addUserPanel, BorderLayout.NORTH); // 메인 패널의 상단에 추가
        
        // --- 중앙: 사용자 목록 테이블 ---
        String[] userColumns = {"ID", "이름", "타입"};
        // 테이블 셀을 직접 편집할 수 없도록 설정
        userTableModel = new DefaultTableModel(userColumns, 0) { 
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };
        userTable = new JTable(userTableModel);
        styleTable(userTable);  // 공통 스타일 적용
        refreshUserTable();     // 테이블 데이터 새로고침
        
        // 테이블을 스크롤 패널에 추가
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(createTitledBorder("사용자 목록"));
        panel.add(scrollPane, BorderLayout.CENTER); // 메인 패널의 중앙에 추가
        
        // --- 하단: 사용자 삭제 버튼 ---
        JButton deleteUserButton = new JButton("선택한 사용자 삭제");
        deleteUserButton.setBackground(new Color(225, 225, 225)); 
        deleteUserButton.setForeground(Color.RED.darker()); 
        deleteUserButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        panel.add(deleteUserButton, BorderLayout.SOUTH); // 메인 패널의 하단에 추가
        
        // --- 이벤트 리스너 등록 ---
        // "추가" 버튼 클릭 이벤트
        addUserButton.addActionListener(e -> {
            String newUserId = userIdField.getText().trim();

            // ID 입력 유효성 검사
            if (newUserId.isEmpty()) { 
                JOptionPane.showMessageDialog(this, "사용자 ID를 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return; 
            }

            // ID 중복 검사
            boolean idExists = users.stream().anyMatch(user -> user.getId().equals(newUserId));
            if (idExists) { 
                JOptionPane.showMessageDialog(this, "이미 존재하는 사용자 ID입니다.", "중복 오류", JOptionPane.ERROR_MESSAGE); 
                return; 
            }

            // 새 사용자 추가
            users.add(new User(newUserId, userNameField.getText(), (String)userTypeCombo.getSelectedItem(), userPassField.getText()));
            JOptionPane.showMessageDialog(this, "사용자가 추가되었습니다.");

            // 입력 필드 초기화
            userIdField.setText(""); 
            userNameField.setText(""); 
            userPassField.setText("");
            refreshUserTable(); // 테이블 새로고침
        });

        // "삭제" 버튼 클릭 이벤트
        deleteUserButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();

            // 삭제할 행 선택 유무 확인
            if (selectedRow == -1) { 
                JOptionPane.showMessageDialog(this, "삭제할 사용자를 목록에서 선택해주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE); 
                return; 
            }
            String userIdToDelete = (String) userTableModel.getValueAt(selectedRow, 0);

            // admin 계정 삭제 방지
            if ("admin".equalsIgnoreCase(userIdToDelete)) { 
                JOptionPane.showMessageDialog(this, "관리자(admin) 계정은 삭제할 수 없습니다.", "삭제 불가", JOptionPane.ERROR_MESSAGE); 
                return; 
            }

            // 삭제 확인 대화상자 표시
            int confirm = JOptionPane.showConfirmDialog(this, "정말로 '" + userIdToDelete + "' 사용자를 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
            // "예"를 선택한 경우에만 삭제 수행
            if (confirm == JOptionPane.YES_OPTION) { 
                users.removeIf(user -> user.getId().equals(userIdToDelete)); 
                refreshUserTable(); 
            }
        });
        return panel;
    }
    
    /**
     * "물품 관리" 탭에 표시될 패널을 생성합니다.
     * @return 물품 관리 기능이 구현된 JPanel 객체
     */
    private JPanel createItemManagementPanel() {
        // 메인 패널 생성 (BorderLayout)
        JPanel panel = new JPanel(new BorderLayout(10, 10)); 
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); 
        panel.setBackground(BG_LIGHT_GRAY);

        // --- 상단: 신규 물품 추가 패널 ---
        JPanel addItemPanel = new JPanel(new GridBagLayout());
        addItemPanel.setBorder(createTitledBorder("신규 물품 추가"));
        addItemPanel.setOpaque(false); // 배경 투명 처리

        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.insets = new Insets(5, 5, 5, 5);

        // 물품 정보 입력을 위한 컴포넌트 생성
        JTextField itemNameField = new JTextField(10); 
        JTextField itemStockField = new JTextField(5); 
        JTextField itemFeeField = new JTextField(5);
        JButton addItemButton = new JButton("추가");
        addItemButton.setBackground(DK_BLUE); 
        addItemButton.setForeground(Color.WHITE);

        // GridBagLayout을 사용하여 컴포넌트 배치
        gbc.gridx = 0; gbc.gridy = 0; addItemPanel.add(new JLabel("물품명:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; addItemPanel.add(itemNameField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; addItemPanel.add(new JLabel("최대재고:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; addItemPanel.add(itemStockField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; addItemPanel.add(new JLabel("기본요금:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; addItemPanel.add(itemFeeField, gbc);

        gbc.gridx = 3; gbc.gridy = 1; 
        gbc.anchor = GridBagConstraints.EAST; // 오른쪽 정렬
        addItemPanel.add(addItemButton, gbc);

        panel.add(addItemPanel, BorderLayout.NORTH); // 메인 패널의 상단에 추가
        
        // --- 중앙: 물품 목록 테이블 ---
        String[] itemColumns = {"물품명", "현재/최대 재고", "기본 요금"};

        // 테이블 셀을 직접 편집할 수 없도록 설정
        itemTableModel = new DefaultTableModel(itemColumns, 0) { 
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };
        itemTable = new JTable(itemTableModel);
        styleTable(itemTable);  // 공통 스타일 적용
        refreshItemTable();     // 테이블 데이터 새로고침

        // 테이블을 스크롤 패널에 추가
        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.setBorder(createTitledBorder("물품 목록"));
        panel.add(scrollPane, BorderLayout.CENTER);      // 메인 패널의 중앙에 추가

        // --- 하단: 물품 삭제 버튼 ---
        JButton deleteItemButton = new JButton("선택한 물품 삭제");
        deleteItemButton.setBackground(new Color(225, 225, 225));
        deleteItemButton.setForeground(Color.RED.darker());
        deleteItemButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        panel.add(deleteItemButton, BorderLayout.SOUTH); // 메인 패널의 하단에 추가
        
        // --- 이벤트 리스너 등록 ---
        // "추가" 버튼 클릭 이벤트
        addItemButton.addActionListener(e -> {
            try {
                String newItemName = itemNameField.getText().trim();
                // 물품명 입력 유효성 검사
                if (newItemName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "물품명을 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // 물품명 중복 검사
                boolean nameExists = items.stream().anyMatch(item -> item.getName().equalsIgnoreCase(newItemName));
                if (nameExists) {
                    JOptionPane.showMessageDialog(this, "이미 존재하는 물품 이름입니다.", "중복 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // 새 물품 객체 생성
                Item newItem = new Item(newItemName,
                        Integer.parseInt(itemStockField.getText()),
                        Double.parseDouble(itemFeeField.getText()));
                
                // MainAppFrame을 Observer로 등록하여 재고 변경 시 UI가 업데이트되도록 함
                newItem.addObserver(parentFrame);
                items.add(newItem);
                JOptionPane.showMessageDialog(this, "물품이 추가되었습니다.");

                // 입력 필드 초기화
                itemNameField.setText("");
                itemStockField.setText("");
                itemFeeField.setText("");
                
                // 관련 UI 컴포넌트 새로고침
                refreshItemTable(); // 관리자 패널의 물품 테이블
                parentFrame.refreshItemComboBox(); // 메인 화면의 물품 콤보박스

            } catch (NumberFormatException ex) {
                // 재고와 요금이 숫자가 아닐 경우 예외 처리
                JOptionPane.showMessageDialog(this, "재고와 요금은 숫자로 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // "삭제" 버튼 클릭 이벤트
        deleteItemButton.addActionListener(e -> {
            int selectedRow = itemTable.getSelectedRow();
            // 삭제할 행 선택 유무 확인
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "삭제할 물품을 목록에서 선택해주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String itemNameToDelete = (String) itemTableModel.getValueAt(selectedRow, 0);
            
            // 삭제 확인 대화상자 표시
            int confirm = JOptionPane.showConfirmDialog(this,
                    "정말로 '" + itemNameToDelete + "' 물품을 삭제하시겠습니까?",
                    "삭제 확인", JOptionPane.YES_NO_OPTION);

            // "예"를 선택한 경우에만 삭제 수행
            if (confirm == JOptionPane.YES_OPTION) {
                items.removeIf(item -> item.getName().equals(itemNameToDelete));
                
                // 관련 UI 컴포넌트 새로고침
                refreshItemTable(); // 관리자 패널의 물품 테이블
                parentFrame.refreshItemComboBox(); // 메인 화면의 물품 콤보박스
            }
        });
        return panel;
    }

    /**
     * 사용자 목록 테이블의 내용을 최신 데이터로 새로고침합니다.
     */
    private void refreshUserTable() {
        userTableModel.setRowCount(0); // 기존의 모든 행을 삭제
        // users 리스트의 모든 사용자 정보를 테이블에 다시 추가
        for (User user : users) {
            userTableModel.addRow(new Object[]{user.getId(), user.getName(), user.getType()});
        }
    }

    /**
     * 물품 목록 테이블의 내용을 최신 데이터로 새로고침합니다.
     */
    private void refreshItemTable() {
        itemTableModel.setRowCount(0); // 기존의 모든 행을 삭제
        // items 리스트의 모든 물품 정보를 테이블에 다시 추가
        for (Item item : items) {
            String stockInfo = item.getCurrentStock() + "/" + item.getMaxStock();
            String feeInfo = String.format("%.0f", item.getBaseFee());
            itemTableModel.addRow(new Object[]{item.getName(), stockInfo, feeInfo});
        }
    }
}