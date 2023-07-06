package ir.gov.tax.tpis.sdk.samples.content;

import ir.gov.tax.tpis.sdk.content.api.DefaultTaxApiClient;
import ir.gov.tax.tpis.sdk.content.api.TaxApi;
import ir.gov.tax.tpis.sdk.content.dto.*;
import ir.gov.tax.tpis.sdk.transfer.api.SimpleTransferApiImpl;
import ir.gov.tax.tpis.sdk.transfer.api.TransferApi;
import ir.gov.tax.tpis.sdk.transfer.config.ApiConfig;
import ir.gov.tax.tpis.sdk.transfer.dto.AsyncResponseModel;
import ir.gov.tax.tpis.sdk.transfer.dto.PacketResponse;
import ir.gov.tax.tpis.sdk.transfer.impl.encrypter.DefaultEncrypter;
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


class SelfTspV1Test {

    private static final String ORG_PUBLIC_KEY = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAxdzREOEfk3vBQogDPGTMqdDQ7t0oDhuKMZkA+Wm1lhzjjhAGfSUOuDvOKRoUEQwP8oUcXRmYzcvCUgcfoRT5iz7HbovqH+bIeJwT4rmLmFcbfPke+E3DLUxOtIZifEXrKXWgSVPkRnhMgym6UiAtnzwA1rmKstJoWpk9Nv34CYgTk8DKQN5jQJqb9L/Ng0zOEEtI3zA424tsd9zv/kP4/SaSnbbnj0evqsZ29X6aBypvnTnwH9t3gbWM4I9eAVQhPYClawHTqvdaz/O/feqfm06QBFnCgL+CBdjLs30xQSLsPICjnlV1jMzoTZnAabWP6FRzzj6C2sxw9a/WwlXrKn3gldZ7Ctv6Jso72cEeCeUI1tzHMDJPU3Qy12RQzaXujpMhCz1DVa47RvqiumpTNyK9HfFIdhgoupFkxT14XLDl65S55MF6HuQvo/RHSbBJ93FQ+2/x/Q2MNGB3BXOjNwM2pj3ojbDv3pj9CHzvaYQUYM1yOcFmIJqJ72uvVf9Jx9iTObaNNF6pl52ADmh85GTAH1hz+4pR/E9IAXUIl/YiUneYu0G4tiDY4ZXykYNknNfhSgxmn/gPHT+7kL31nyxgjiEEhK0B0vagWvdRCNJSNGWpLtlq4FlCWTAnPI5ctiFgq925e+sySjNaORCoHraBXNEwyiHT2hu5ZipIW2cCAwEAAQ==";
    private static final String ORG_KEY_ID = "6a2bcd88-a871-4245-a393-2843eafe6e02";
    private static final String PRIVATE_KEY_FILE = "private.pem";
    private static final String MEMORY_ID = "A11216";
    private TaxApi taxApi;
    private String token;
    private String uid;
    private String referenceNumber;

    public static void main(String[] args) throws URISyntaxException, IOException {
        SelfTspV1Test test = new SelfTspV1Test();
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
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        test.inquiryByReferenceId();
    }

    void setUp() throws URISyntaxException, IOException {
        ApiConfig apiConfig = new ApiConfig()
                .baseUrl("https://wantolan.ir/requestsmanager/api/self-tsp")
                .normalizer(new SimpleNormalizer())
                .apiVersion("v1")
                .encrypter(new DefaultEncrypter(ORG_PUBLIC_KEY, ORG_KEY_ID))
                .transferSignatory(SignatoryFactory.getInstance().createPKCS8Signatory(
                        new File(
                                getClass().getClassLoader().getResource(PRIVATE_KEY_FILE).toURI()
                        ), null
                ));
        TransferApi transferApi = new SimpleTransferApiImpl(apiConfig);
        this.taxApi = new DefaultTaxApiClient(transferApi, MEMORY_ID);
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
        Instant invoiceCreatedDate = Instant.now().minusSeconds(1000);
        String taxId = TaxUtils.generateTaxId(MEMORY_ID, randomSerialDecimal, invoiceCreatedDate);

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
        body.setMu("1689");
        body.setAm(BigDecimal.valueOf(1));
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
        payment.setIinn("113124421");
        payment.setAcn("2131244212");
        payment.setTrmn("31312442");
        payment.setTrn("4131244214");
        payment.setPdt(1667224740000L);

        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setBody(Collections.singletonList(body));
        invoiceDto.setHeader(header);
        invoiceDto.setPayments(Collections.singletonList(payment));

        AsyncResponseModel responseModel = taxApi.sendMyInvoices(Collections.singletonList(invoiceDto));
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
        uidAndFiscalId.setFiscalId(MEMORY_ID);
        List<InquiryResultModel> inquiryResultModels = this.taxApi.inquiryByUidAndFiscalId(
                Collections.singletonList(uidAndFiscalId));
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
        List<InquiryResultModel> inquiryResultModels = this.taxApi.inquiryByReferenceId(
                Collections.singletonList(this.referenceNumber));
        System.out.println("inquiry By Reference Id result: " + inquiryResultModels);
        if (inquiryResultModels != null && !inquiryResultModels.isEmpty()) {
            System.out.println("inquiry By Reference Id result: " + inquiryResultModels.get(0).getData());
        }
    }

    void getFiscalInformation() {
        FiscalInformationModel fiscalInformation = taxApi.getFiscalInformation(MEMORY_ID);
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
        EconomicCodeModel economicCodeInformation = taxApi.getEconomicCodeInformation("10660111880");
        System.out.println("Economic Code Information: " + economicCodeInformation);
    }
}
