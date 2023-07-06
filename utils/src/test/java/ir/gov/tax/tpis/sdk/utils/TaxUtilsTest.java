package ir.gov.tax.tpis.sdk.utils;

import java.time.Instant;

public class TaxUtilsTest {

    public static void main(String[] args) {
        String t1 = TaxUtils.generateTaxId("SAU5BC", 12, Instant.parse("2020-07-20T00:00:00.00Z"));
        System.out.println(t1);
    }
}
