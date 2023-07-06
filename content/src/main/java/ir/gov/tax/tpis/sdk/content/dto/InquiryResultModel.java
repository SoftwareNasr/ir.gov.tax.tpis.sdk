package ir.gov.tax.tpis.sdk.content.dto;


public class InquiryResultModel {

    private String referenceNumber;

    private String uid;

    private String fiscalId;

    private String status;

    private CreateInvoiceResponse data;

    private String packetType;

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFiscalId() {
        return fiscalId;
    }

    public void setFiscalId(String fiscalId) {
        this.fiscalId = fiscalId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CreateInvoiceResponse getData() {
        return data;
    }

    public void setData(CreateInvoiceResponse data) {
        this.data = data;
    }

    public String getPacketType() {
        return packetType;
    }

    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }

    @Override
    public String toString() {
        return "InquiryResultModel{" +
                "referenceNumber='" + referenceNumber + '\'' +
                ", uid='" + uid + '\'' +
                ", fiscalId='" + fiscalId + '\'' +
                ", status='" + status + '\'' +
                ", packetType='" + packetType + '\'' +
                '}';
    }
}
