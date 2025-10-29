// 파일 이름: RentalSystem_Final.java

import javax.swing.*;
import java.util.List;

/**
 * 물품 대여 시스템 애플리케이션을 시작하는 메인 클래스입니다.
 * 프로그램의 진입점(main method) 역할을 하며, 초기 데이터 로딩,
 * 로그인 창 표시, 그리고 메인 애플리케이션 창을 실행하는 전체적인 흐름을 관리합니다.
 */
public class RentalSystem_Final {

    /**
     * 애플리케이션의 시작점(Entry Point)입니다.
     * @param args 커맨드 라인 인자 (사용되지 않음)
     */
    public static void main(String[] args) {
        // --- 1. UI 룩앤필(Look and Feel) 설정 ---
        // 프로그램의 GUI가 실행되는 운영체제(Windows, macOS 등)의 기본 스타일과 유사하게 보이도록 설정합니다.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(); // 룩앤필 설정 실패 시 오류 출력
        }

        // --- 2. 핵심 객체 초기화 및 데이터 로딩 ---
        DataManager dataManager = new DataManager();

        // 파일로부터 사용자, 물품, 대여 기록 데이터를 불러옵니다.
        List<User> users = dataManager.loadUsers();
        List<Item> items = dataManager.loadItems();
        List<RentalRecord> rentalHistory = dataManager.loadHistory();

        // --- 3. 초기 데이터 생성 (프로그램 최초 실행 시) ---
        // 만약 저장된 사용자 데이터가 없다면, 기본 관리자/학생/교직원 계정을 생성합니다.
        if (users.isEmpty()) {
            users.add(new User("admin", "관리자", "Admin", "admin123"));
            users.add(new User("student1", "김민준", "Student", "1234"));
            users.add(new User("staff1", "박선우", "Staff", "abcd"));
        }

        // 만약 저장된 물품 데이터가 없다면, 기본 대여 물품들을 생성합니다.
        if (items.isEmpty()) {
            items.add(new Item("3단 우산", 10, 1000.0));
            items.add(new Item("축구공", 5, 2000.0));
            items.add(new Item("보조배터리", 15, 1500.0));
            items.add(new Item("C타입 충전기", 20, 500.0));
            items.add(new Item("8핀 충전기", 20, 500.0));
        }

        // --- 4. 로그인 절차 진행 ---
        // 대여/반납 로직을 처리할 서비스 객체 생성
        RentalService rentalService = new RentalService(rentalHistory);

        // 로그인 다이얼로그를 생성하고 화면에 표시. 사용자가 로그인하거나 창을 닫을 때까지 여기서 대기.
        LoginDialog loginDialogInstance = new LoginDialog(null, users);
        loginDialogInstance.setVisible(true);

        // 로그인 다이얼로그가 닫힌 후, 로그인 결과를 가져옴
        User loggedInUser = loginDialogInstance.getLoggedInUser();

        // --- 5. 로그인 결과에 따른 분기 처리 ---
        // 로그인에 성공했다면 (loggedInUser 객체가 null이 아니라면)
        if (loggedInUser != null) {
            // Swing GUI는 이벤트 디스패치 스레드(EDT)에서 실행하는 것이 안전하므로,
            // SwingUtilities.invokeLater을 사용하여 메인 프레임을 실행합니다.
            SwingUtilities.invokeLater(() -> {
                MainAppFrame mainFrame = new MainAppFrame(dataManager, rentalService, loggedInUser, users, items, rentalHistory);
                mainFrame.setVisible(true); // 메인 애플리케이션 창을 화면에 표시
            });
        } else {
            // 로그인에 실패하거나 사용자가 '종료'를 선택했다면
            System.out.println("로그인하지 않거나 종료를 선택하여 프로그램을 종료합니다.");
            // 별도의 동작 없이 main 메소드가 종료되면서 프로그램이 끝남.
        }
    }
}