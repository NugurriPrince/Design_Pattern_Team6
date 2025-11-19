// 파일 이름: PartialRefundStrategy.java
public class PartialRefundStrategy implements RefundStrategy {
    private final double refundRate;
    private final String policyName;

    /**
     * @param refundRate 환불 비율 (예: 0.9 = 90% 환불)
     * @param policyName 정책 이름 (예: "수수료 10% 차감")
     */
    public PartialRefundStrategy(double refundRate, String policyName) {
        this.refundRate = refundRate;
        this.policyName = policyName;
    }

    @Override
    public double calculateRefund(double deposit) {
        return deposit * this.refundRate;
    }

    @Override
    public String getPolicyName() {
        return this.policyName;
    }
}