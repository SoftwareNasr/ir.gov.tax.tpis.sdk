package ir.gov.tax.tpis.sdk.content.dto;

import java.util.List;


public class CreateInvoiceResponse {
    private String confirmationReferenceId;
    private List<InvoiceErrorModel> error;
    private List<InvoiceErrorModel> warning;
    private boolean success = true;

    public String getConfirmationReferenceId() {
        return confirmationReferenceId;
    }

    public void setConfirmationReferenceId(String confirmationReferenceId) {
        this.confirmationReferenceId = confirmationReferenceId;
    }

    public List<InvoiceErrorModel> getError() {
        return error;
    }

    public void setError(List<InvoiceErrorModel> error) {
        this.error = error;
    }

    public List<InvoiceErrorModel> getWarning() {
        return warning;
    }

    public void setWarning(List<InvoiceErrorModel> warning) {
        this.warning = warning;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
