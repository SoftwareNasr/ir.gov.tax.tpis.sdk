package ir.gov.tax.tpis.sdk.content.api;

import ir.gov.tax.tpis.sdk.content.dto.*;
import ir.gov.tax.tpis.sdk.transfer.dto.AsyncResponseModel;
import ir.gov.tax.tpis.sdk.transfer.exception.TaxApiException;

import java.util.List;
import java.util.Map;

public interface TaxApi {

    AsyncResponseModel sendInvoices(List<InvoiceDtoWrapper> invoices) throws TaxApiException;

    AsyncResponseModel sendMyInvoices(List<InvoiceDto> invoices) throws TaxApiException;

    AsyncResponseModel sendInvoices(List<InvoiceDtoWrapper> invoices, Map<String, String> headers) throws TaxApiException;

    AsyncResponseModel sendMyInvoices(List<InvoiceDto> invoices, Map<String, String> headers) throws TaxApiException;

    TokenModel requestToken() throws TaxApiException;

    ServerInformationModel getServerInformation() throws TaxApiException;

    @Deprecated
    List<InquiryResultModel> inquiryByUidAndFiscalId(List<UidAndFiscalId> uidAndFiscalIds) throws TaxApiException;

    List<InquiryResultModel> inquiryByTime(String persianTime) throws TaxApiException;

    List<InquiryResultModel> inquiryByTimeRange(String startDatePersian, String toDatePersian) throws TaxApiException;

    List<InquiryResultModel> inquiryByReferenceId(List<String> referenceIds) throws TaxApiException;

    FiscalInformationModel getFiscalInformation(String fiscalId) throws TaxApiException;

    SearchResultModel<ServiceStuffModel> getServiceStuffList(SearchDto searchDTO) throws TaxApiException;

    EconomicCodeModel getEconomicCodeInformation(String economicCode) throws TaxApiException;

    TokenModel getToken();

    void setToken(TokenModel token);
}
