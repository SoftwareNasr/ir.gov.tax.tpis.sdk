package ir.gov.tax.tpis.sdk.transfer.interfaces;

import ir.gov.tax.tpis.sdk.transfer.exception.TaxApiException;

public interface Signatory {

    String sign(String data) throws TaxApiException;

    String getKeyId();
}
