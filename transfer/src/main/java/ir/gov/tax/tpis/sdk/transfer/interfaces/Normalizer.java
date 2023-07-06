package ir.gov.tax.tpis.sdk.transfer.interfaces;

import java.util.Map;

public interface Normalizer {

    String normalize(Object data, Map<String, String> headers);
}
