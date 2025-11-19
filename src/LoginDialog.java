// 파일 이름: LoginDialog.java

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 로그인을 위한 GUI 대화 상자(Dialog) 클래스입니다.
 * 이 창은 모달(Modal)로 동작하여, 사용자가 로그인을 완료하거나 창을 닫기 전까지
 * 다른 애플리케이션 창과 상호작용할 수 없습니다.
 */
public class LoginDialog extends JDialog {

    // 로그인에 성공한 사용자 객체를 저장하는 변수. 실패 시 null 유지.
    private User loggedInUser = null;

    // UI 스타일링을 위한 상수 색상 정의
    private static final Color DK_BLUE = new Color(0, 44, 122);       // 진한 파란색
    private static final Color BG_LIGHT_GRAY = new Color(245, 245, 245); // 밝은 회색 배경

    /**
     * LoginDialog 생성자
     * @param parent 부모 프레임
     * @param users 로그인 인증에 사용할 전체 사용자 목록
     */
    public LoginDialog(Frame parent, List<User> users) {
        super(parent, "시스템 로그인", true); // 부모, 타이틀, 모달 설정

        // --- 1. 다이얼로그 기본 설정 ---
        setUndecorated(true); // 타이틀 바와 테두리 제거
        setSize(400, 500);
        setLayout(new BorderLayout());
        // 루트 패널에 파란색 테두리 추가
        getRootPane().setBorder(BorderFactory.createLineBorder(DK_BLUE, 2));

        // --- 2. 상단 헤더 패널 (로고 및 타이틀) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        headerPanel.setBackground(DK_BLUE);

        // 로고 이미지 로드 및 추가
        try {
            ImageIcon dankookLogo = new ImageIcon("단국대 로고.png");
            Image image = dankookLogo.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            headerPanel.add(imageLabel);
        } catch (Exception e) {
            System.err.println("로그인 로고 이미지 로딩 실패. 이미지를 생략합니다.");
        }

        // 타이틀 라벨 설정
        JLabel titleLabel = new JLabel("물품 대여 시스템");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH); // 헤더 패널을 다이얼로그 상단에 추가

        // --- 3. 중앙 입력 패널 (ID, 비밀번호) ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(BG_LIGHT_GRAY);
        inputPanel.setBorder(new EmptyBorder(40, 40, 40, 40)); // 내부 여백 설정

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // 컴포넌트가 셀의 가로를 꽉 채우도록 설정
        gbc.insets = new Insets(10, 5, 10, 5); // 컴포넌트 간 여백 설정

        // 폰트 설정
        Font labelFont = new Font("맑은 고딕", Font.BOLD, 14);
        Font fieldFont = new Font("맑은 고딕", Font.PLAIN, 14);

        // ID 입력 필드
        JLabel idLabel = new JLabel("사용자 ID ");
        idLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(idLabel, gbc);

        JTextField idField = new JTextField(15);
        idField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(idField, gbc);

        // 비밀번호 입력 필드
        JLabel passwordLabel = new JLabel("비밀번호 ");
        passwordLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(passwordField, gbc);

        add(inputPanel, BorderLayout.CENTER); // 입력 패널을 다이얼로그 중앙에 추가

        // --- 4. 하단 버튼 패널 (로그인, 종료) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setBackground(BG_LIGHT_GRAY);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JButton loginButton = new JButton("로그인");
        JButton cancelButton = new JButton("종료");

        // 버튼 스타일 설정
        Dimension btnSize = new Dimension(150, 40);
        loginButton.setPreferredSize(btnSize);
        cancelButton.setPreferredSize(btnSize);
        loginButton.setBackground(DK_BLUE);
        loginButton.setForeground(Color.RED);
        loginButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        cancelButton.setBackground(Color.BLUE);
        cancelButton.setForeground(DK_BLUE);
        cancelButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH); // 버튼 패널을 다이얼로그 하단에 추가

        // Enter 키를 누르면 로그인 버튼이 클릭되도록 설정
        getRootPane().setDefaultButton(loginButton);

        // --- 5. 이벤트 리스너 설정 ---
        // "로그인" 버튼 클릭 시 동작
        loginButton.addActionListener(e -> {
            // 입력된 ID와 비밀번호가 일치하는 사용자를 users 리스트에서 찾음 (Java Stream API 사용)
            Optional<User> user = users.stream()
                    .filter(u -> u.getId().equals(idField.getText()) && u.getPassword().equals(new String(passwordField.getPassword())))
                    .findFirst();

            if (user.isPresent()) { // 사용자를 찾았다면
                this.loggedInUser = user.get(); // 로그인 성공한 사용자 정보 저장
                dispose(); // 다이얼로그 창 닫기
            } else { // 사용자를 찾지 못했다면
                JOptionPane.showMessageDialog(this, "ID 또는 비밀번호가 잘못되었습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }
        });

        // "종료" 버튼 클릭 시 동작
        cancelButton.addActionListener(e -> {
            this.loggedInUser = null; // 로그인한 사용자가 없음을 명시
            dispose(); // 다이얼로그 창 닫기 (결과적으로 프로그램 종료로 이어짐)
        });

        // 다이얼로그를 부모 창의 중앙에 위치시킴
        setLocationRelativeTo(parent);
    }

    /**
     * 로그인 결과를 반환합니다.
     * @return 로그인에 성공한 User 객체. 로그인 실패 또는 취소 시 null을 반환합니다.
     */
    public User getLoggedInUser() {
        return this.loggedInUser;
    }
}