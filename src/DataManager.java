// 파일 이름: DataManager.java

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 애플리케이션의 데이터를 파일에 저장하고 불러오는 역할을 담당하는 클래스입니다.
 * 사용자(User), 물품(Item), 대여기록(RentalRecord) 객체 리스트를 직렬화(Serialization)하여
 * 각각 별도의 '.dat' 파일에 저장하고, 필요할 때 역직렬화하여 메모리로 불러옵니다.
 */
public class DataManager {

    // 데이터 저장을 위한 파일 이름 상수 정의
    private static final String USERS_FILE = "users.dat";       // 사용자 정보 파일
    private static final String ITEMS_FILE = "items.dat";       // 물품 정보 파일
    private static final String HISTORY_FILE = "history.dat";   // 대여 기록 파일

    /**
     * 현재 애플리케이션의 모든 데이터를 파일에 저장합니다.
     * @param users 저장할 사용자 정보 리스트
     * @param items 저장할 물품 정보 리스트
     * @param history 저장할 대여 기록 리스트
     */
    public void saveData(List<User> users, List<Item> items, List<RentalRecord> history) {
        // try-with-resources 구문을 사용하여 AutoCloseable 스트림들을 안전하게 관리합니다.
        try (ObjectOutputStream oosUsers = new ObjectOutputStream(new FileOutputStream(USERS_FILE));
             ObjectOutputStream oosItems = new ObjectOutputStream(new FileOutputStream(ITEMS_FILE));
             ObjectOutputStream oosHistory = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE))) {

            // 각 리스트 객체를 해당하는 파일에 직렬화하여 쓴다.
            oosUsers.writeObject(users);
            oosItems.writeObject(items);
            oosHistory.writeObject(history);

            System.out.println("모든 데이터가 성공적으로 저장되었습니다.");

        } catch (IOException e) {
            System.err.println("데이터 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 파일에서 사용자 데이터를 불러옵니다.
     * @return 불러온 사용자 정보 리스트. 파일이 없거나 오류 발생 시 비어있는 리스트를 반환합니다.
     */
    @SuppressWarnings("unchecked") // readObject()의 반환 타입을 캐스팅할 때 발생하는 경고를 무시합니다.
    public List<User> loadUsers() {
        File file = new File(USERS_FILE);
        if (file.exists()) { // 파일이 존재하는 경우에만 로딩 시도
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                // 파일로부터 객체를 역직렬화하고 List<User> 타입으로 캐스팅하여 반환
                return (List<User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("사용자 데이터 로딩 중 오류 발생: " + e.getMessage());
            }
        }
        // 파일이 없거나 로딩 중 오류가 발생하면 비어있는 새 리스트를 반환
        return new ArrayList<>();
    }

    /**
     * 파일에서 물품 데이터를 불러옵니다.
     * @return 불러온 물품 정보 리스트. 파일이 없거나 오류 발생 시 비어있는 리스트를 반환합니다.
     */
    @SuppressWarnings("unchecked")
    public List<Item> loadItems() {
        File file = new File(ITEMS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (List<Item>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("물품 데이터 로딩 중 오류 발생: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }

    /**
     * 파일에서 대여 기록 데이터를 불러옵니다.
     * @return 불러온 대여 기록 리스트. 파일이 없거나 오류 발생 시 비어있는 리스트를 반환합니다.
     */
    @SuppressWarnings("unchecked")
    public List<RentalRecord> loadHistory() {
        File file = new File(HISTORY_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (List<RentalRecord>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("대여 기록 로딩 중 오류 발생: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }
}