// 파일 이름: User.java

import java.io.Serializable;

/**
 * 시스템 사용자의 정보를 담는 데이터 모델 클래스(Data Model)입니다.
 * 사용자의 ID, 이름, 타입(학생, 교직원, 관리자), 비밀번호를 저장합니다.
 * Serializable 인터페이스를 구현하여 객체 상태를 파일에 저장하고 불러올 수 있습니다.
 * 모든 필드가 final로 선언되어 있어, 한 번 생성되면 내부 상태가 변하지 않는 불변(Immutable) 객체입니다.
 */
public class User implements Serializable {

    // 직렬화/역직렬화 시 클래스 버전 관리를 위한 고유 ID
    private static final long serialVersionUID = 1L;

    // --- 필드(Fields) ---
    private final String id;       // 사용자 ID (고유 식별자)
    private final String name;     // 사용자 이름
    private final String type;     // 사용자 타입 (e.g., "Student", "Staff", "Admin")
    private final String password; // 사용자 비밀번호

    /**
     * User 객체를 생성하는 생성자입니다.
     * @param id       사용자 ID
     * @param name     사용자 이름
     * @param type     사용자 타입
     * @param password 사용자 비밀번호
     */
    public User(String id, String name, String type, String password) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.password = password;
    }

    // --- Getter 메소드 ---

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getPassword() { return password; }

    // --- Object 클래스 메소드 오버라이드 ---

    /**
     * 객체를 문자열로 표현할 때 사용되는 메소드입니다.
     * 예: "김민준 (Student)" 형태로 반환됩니다.
     * @return "이름 (타입)" 형식의 문자열
     */
    @Override
    public String toString() {
        return name + " (" + type + ")";
    }

    /**
     * 두 User 객체가 동일한지 비교하는 메소드입니다.
     * 여기서는 사용자의 ID가 같으면 동일한 사용자로 간주합니다.
     * @param o 비교할 객체
     * @return ID가 같으면 true, 아니면 false
     */
    @Override
    public boolean equals(Object o) {
        // 1. 자기 자신과의 비교는 항상 true
        if (this == o) return true;
        // 2. null이거나 클래스 타입이 다르면 false
        if (o == null || getClass() != o.getClass()) return false;
        // 3. ID를 기준으로 비교
        User user = (User) o;
        return id.equals(user.id);
    }

    /**
     * 객체의 해시 코드를 반환하는 메소드입니다.
     * equals() 메소드를 ID 기준으로 재정의했으므로, hashCode()도 일관성을 위해
     * ID의 해시 코드를 반환하도록 재정의해야 합니다.
     * @return ID 문자열의 해시 코드
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}