package ir.gov.tax.tpis.sdk.transfer.impl.normalize;

import ir.gov.tax.tpis.sdk.transfer.config.TransferConstants;
import ir.gov.tax.tpis.sdk.transfer.exception.TaxApiException;
import ir.gov.tax.tpis.sdk.transfer.interfaces.Normalizer;

import java.util.Map;

public class SimpleNormalizer implements Normalizer {

    @Override
    public String normalize(Object data, Map<String, String> headers) {

        if (!(data instanceof String)) {
            throw new TaxApiException("only normalize the string data");
        }

        if (headers == null || headers.isEmpty()) {
            return (String) data;
        }

        StringBuilder normalText = new StringBuilder((String) data);

        if (headers.containsKey(TransferConstants.AUTHORIZATION_HEADER)) {
            normalText.append(headers.get(TransferConstants.AUTHORIZATION_HEADER));
        }
        if (headers.containsKey(TransferConstants.REQUEST_TRACE_ID_HEADER)) {
            normalText.append(headers.get(TransferConstants.REQUEST_TRACE_ID_HEADER));
        }
        if (headers.containsKey(TransferConstants.TIMESTAMP_HEADER)) {
            normalText.append(headers.get(TransferConstants.TIMESTAMP_HEADER));
        }

        return normalText.toString();
    }
}
