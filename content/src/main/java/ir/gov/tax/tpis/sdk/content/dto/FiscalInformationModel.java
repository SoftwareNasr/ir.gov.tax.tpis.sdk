package ir.gov.tax.tpis.sdk.content.dto;


import ir.gov.tax.tpis.sdk.content.enumeration.FiscalStatus;

public class FiscalInformationModel {

    private String nameTrade;
    private FiscalStatus fiscalStatus;
    private String economicCode;

    public String getNameTrade() {
        return nameTrade;
    }

    public void setNameTrade(String nameTrade) {
        this.nameTrade = nameTrade;
    }

    public FiscalStatus getFiscalStatus() {
        return fiscalStatus;
    }

    public void setFiscalStatus(FiscalStatus fiscalStatus) {
        this.fiscalStatus = fiscalStatus;
    }

    public String getEconomicCode() {
        return economicCode;
    }

    public void setEconomicCode(String economicCode) {
        this.economicCode = economicCode;
    }

    @Override
    public String toString() {
        return "FiscalInformationModel{" +
                "nameTrade='" + nameTrade + '\'' +
                ", fiscalStatus=" + fiscalStatus +
                ", codeEconomic='" + economicCode + '\'' +
                '}';
    }
}
