// 파일 이름: RefundStrategy.java
public interface RefundStrategy {
    /**
     * 환불 정책에 따라 최종 환불액을 계산합니다.
     * @param deposit 원본 보증금
     * @return 최종 환불 금액
     */
    double calculateRefund(double deposit);
    String getPolicyName();
}