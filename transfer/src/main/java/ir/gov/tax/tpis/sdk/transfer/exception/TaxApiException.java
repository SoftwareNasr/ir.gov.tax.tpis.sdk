package ir.gov.tax.tpis.sdk.transfer.exception;

public class TaxApiException extends RuntimeException {

    private String code;

    public TaxApiException() {
    }


    public TaxApiException(String message) {
        super(message);
    }

    public TaxApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaxApiException(String code, String message) {
        super(message);
        this.code = code;
    }

    public TaxApiException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public TaxApiException(Throwable cause) {
        super(cause);
    }

    public TaxApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getCode() {
        return code;
    }
}
