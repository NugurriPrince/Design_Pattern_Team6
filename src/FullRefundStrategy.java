// 파일 이름: FullRefundStrategy.java
public class FullRefundStrategy implements RefundStrategy {
    @Override
    public double calculateRefund(double deposit) {
        return deposit;
    }
    @Override
    public String getPolicyName() {
        return "보증금 전액 환불";
    }
}