package ir.gov.tax.tpis.sdk.samples.content;

import ir.gov.tax.tpis.sdk.content.api.DefaultTaxApiClient;
import ir.gov.tax.tpis.sdk.content.api.TaxApi;
import ir.gov.tax.tpis.sdk.content.dto.*;
import ir.gov.tax.tpis.sdk.transfer.api.SimpleTransferApiImpl;
import ir.gov.tax.tpis.sdk.transfer.api.TransferApi;
import ir.gov.tax.tpis.sdk.transfer.config.ApiConfig;
import ir.gov.tax.tpis.sdk.transfer.dto.AsyncResponseModel;
import ir.gov.tax.tpis.sdk.transfer.dto.PacketResponse;
import ir.gov.tax.tpis.sdk.transfer.impl.normalize.SimpleNormalizer;
import ir.gov.tax.tpis.sdk.transfer.impl.signatory.SignatoryFactory;
import ir.gov.tax.tpis.sdk.utils.TaxUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Random;


class TspWithOwnKeyV1Test {

    private TaxApi taxApi;

    private String token;

    private String uid;

    private String referenceNumber;

    private static final String PRIVATE_KEY_FILE = "tsp_private_key.pem";
    private static final String CLIENT_ID = "102";
    private static final String MEMORY = "A11222";

    public static void main(String[] args) throws URISyntaxException, IOException {
        TspWithOwnKeyV1Test test = new TspWithOwnKeyV1Test();
        test.setUp();
        test.getServerInformation();
        test.getToken();
        test.sendInvoices();
        test.inquiryByUID();
        test.inquiryByReferenceId();
        test.inquiryByTime();
        test.inquiryByTimeRange();
        test.getFiscalInformation();
        test.getEconomicCodeInformation();
        test.getServiceStuffList();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        test.inquiryByReferenceId();
    }

    void setUp() throws URISyntaxException, IOException {
        ApiConfig apiConfig = new ApiConfig()
                .baseUrl("https://wantolan.ir/requestsmanager/api/tsp")
                .apiVersion("v1")
                .normalizer(new SimpleNormalizer())
                .transferSignatory(
                        SignatoryFactory.getInstance().createPKCS8Signatory(
                                new File(
                                        getClass().getClassLoader().getResource(PRIVATE_KEY_FILE).toURI()
                                ), null
                        ));
        TransferApi transferApi = new SimpleTransferApiImpl(apiConfig);
        this.taxApi = new DefaultTaxApiClient(transferApi, CLIENT_ID);
    }

    void getServerInformation() {
        ServerInformationModel serverInformation = this.taxApi.getServerInformation();
        System.out.println("success load server information: " + serverInformation);
    }

    void getToken() {
        TokenModel token = this.taxApi.requestToken();
        this.token = token.getToken();
        if (this.token != null && this.token.length() > 0) {
            System.out.println("success login, token: " + this.token);
        } else {
            System.out.println("error in login");
            System.exit(-1);
        }
    }

    void sendInvoices() {

        //Generate Random Serial number
        Random random = new Random();
        long randomSerialDecimal = random.nextInt(999999999);
        Instant invoiceCreatedDate = Instant.now();
        String taxId = TaxUtils.generateTaxId(MEMORY, randomSerialDecimal, invoiceCreatedDate);

        InvoiceHeaderDto header = new InvoiceHeaderDto();
        header.setIndatim(invoiceCreatedDate.toEpochMilli());
        header.setIndati2m(invoiceCreatedDate.toEpochMilli());
        header.setTaxid(taxId);
        header.setInty(1);
        header.setInp(1);
        header.setInno(String.valueOf(randomSerialDecimal));
        header.setIns(1);
        header.setTins("14003778990");
        header.setTprdis(BigDecimal.valueOf(10_000));
        header.setTdis(BigDecimal.valueOf(500));
        header.setTvam(BigDecimal.valueOf(855));
        header.setTodam(BigDecimal.valueOf(1900));
        header.setTbill(BigDecimal.valueOf(12255));
        header.setCap(BigDecimal.valueOf(9500));
        header.setSetm(1);
        header.setInsp(BigDecimal.valueOf(0));
        header.setTvop(null);
        header.setTax17(BigDecimal.valueOf(500));
        header.setTob(2);
        header.setTinb("14003778990");
        header.setTadis(BigDecimal.valueOf(9500));

        InvoiceBodyDto body = new InvoiceBodyDto();
        body.setSstid("2902230386252");
        body.setSstt("شیر کم چرب پاستوریزه");
        body.setMu("035");
        body.setAm(BigDecimal.ONE);
        body.setFee(BigDecimal.valueOf(10_000));
        body.setPrdis(BigDecimal.valueOf(10_000));
        body.setDis(BigDecimal.valueOf(500));
        body.setAdis(BigDecimal.valueOf(9_500));
        body.setVra(BigDecimal.valueOf(9));
        body.setVam(BigDecimal.valueOf(855));
        body.setTsstam(BigDecimal.valueOf(12_255));
        body.setOdam(BigDecimal.valueOf(950));
        body.setOlam(BigDecimal.valueOf(950));

        PaymentDto payment = new PaymentDto();
        payment.setIinn("1131244211");
        payment.setAcn("2131244212");
        payment.setTrmn("3131244213");
        payment.setTrn("4131244214");
        payment.setPdt(1667224740000L);

        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setBody(Collections.singletonList(body));
        invoiceDto.setHeader(header);
        invoiceDto.setPayments(Collections.singletonList(payment));

        InvoiceDtoWrapper invoiceDtoWrapper = new InvoiceDtoWrapper();
        invoiceDtoWrapper.setInvoice(invoiceDto);
        invoiceDtoWrapper.setFiscalId(MEMORY);

        AsyncResponseModel responseModel = taxApi.sendInvoices(Collections.singletonList(invoiceDtoWrapper));
        if (responseModel.getResult() != null && !responseModel.getResult().isEmpty()) {
            System.out.println("success send invoice, response" + responseModel.getResult());
            PacketResponse packetResponse = responseModel.getResult().get(0);
            this.uid = packetResponse.getUid();
            this.referenceNumber = packetResponse.getReferenceNumber();
        } else {
            System.out.println(responseModel.getErrors());
            System.exit(-1);
        }
    }

    void inquiryByUID() {
        UidAndFiscalId uidAndFiscalId = new UidAndFiscalId();
        uidAndFiscalId.setUid(this.uid);
        uidAndFiscalId.setFiscalId(MEMORY);
        List<InquiryResultModel> inquiryResultModels = this.taxApi.inquiryByUidAndFiscalId(Collections.singletonList(uidAndFiscalId));
        System.out.println("inquiry By UID result: " + inquiryResultModels);
    }

    void inquiryByTime() {
        List<InquiryResultModel> inquiryResultModels = this.taxApi.inquiryByTime("14010101");
        System.out.println("inquiry By Time result: " + inquiryResultModels);
    }

    void inquiryByTimeRange() {
        List<InquiryResultModel> inquiryResultModels = this.taxApi.inquiryByTimeRange("14010101", "14020101");
        System.out.println("inquiry By Time Range result: " + inquiryResultModels);
    }

    void inquiryByReferenceId() {
        List<InquiryResultModel> inquiryResultModels = this.taxApi.inquiryByReferenceId(Collections.singletonList(this.referenceNumber));
        System.out.println("inquiry By Reference Id result: " + inquiryResultModels);
    }

    void getFiscalInformation() {
        FiscalInformationModel fiscalInformation = taxApi.getFiscalInformation(MEMORY);
        System.out.println("Fiscal Information: " + fiscalInformation);
    }

    void getServiceStuffList() {
        SearchDto searchDto = new SearchDto();
        searchDto.setPage(1);
        searchDto.setSize(10);
        SearchResultModel<ServiceStuffModel> serviceStuffList = taxApi.getServiceStuffList(searchDto);
        System.out.println("Stuff List: " + serviceStuffList);
    }

    void getEconomicCodeInformation() {
        EconomicCodeModel economicCodeInformation = taxApi.getEconomicCodeInformation("14003778990");
        System.out.println("Economic Code Information: " + economicCodeInformation);
    }
}
