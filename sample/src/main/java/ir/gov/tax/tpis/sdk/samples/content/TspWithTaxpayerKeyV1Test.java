package ir.gov.tax.tpis.sdk.samples.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.gov.tax.tpis.sdk.content.api.DefaultTaxApiClient;
import ir.gov.tax.tpis.sdk.content.api.TaxApi;
import ir.gov.tax.tpis.sdk.content.dto.*;
import ir.gov.tax.tpis.sdk.transfer.api.SimpleTransferApiImpl;
import ir.gov.tax.tpis.sdk.transfer.api.TransferApi;
import ir.gov.tax.tpis.sdk.transfer.config.ApiConfig;
import ir.gov.tax.tpis.sdk.transfer.config.TransferConstants;
import ir.gov.tax.tpis.sdk.transfer.dto.AsyncResponseModel;
import ir.gov.tax.tpis.sdk.transfer.dto.PacketDto;
import ir.gov.tax.tpis.sdk.transfer.dto.PacketResponse;
import ir.gov.tax.tpis.sdk.transfer.impl.normalize.SimpleNormalizer;
import ir.gov.tax.tpis.sdk.transfer.impl.signatory.SignatoryFactory;
import ir.gov.tax.tpis.sdk.transfer.interfaces.Signatory;
import ir.gov.tax.tpis.sdk.utils.TaxUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;


public class TspWithTaxpayerKeyV1Test {

    private TransferApi transferApi;

    private TaxApi taxApi;

    private String token;

    private String uid;

    private String referenceNumber;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String TSP_PRIVATE_KEY_FILE = "tsp_private_key.pem";
    private static final String TAXPAYER_PRIVATE_KEY_FILE = "private.pem";
    private static final String CLIENT_ID = "102";
    private static final String MEMORY = "A11223";

    public static void main(String[] args) throws IOException, URISyntaxException {
        TspWithTaxpayerKeyV1Test test = new TspWithTaxpayerKeyV1Test();
        test.setUp();
        test.getServerInformation();
        test.getToken();
        test.sendInvoices();
        test.inquiryByReferenceId();
    }

    void setUp() throws URISyntaxException, IOException {
        ApiConfig apiConfig = new ApiConfig()
                .baseUrl("https://wantolan.ir/requestsmanager/api/tsp")
                .normalizer(new SimpleNormalizer())
                .apiVersion("v1")
                .transferSignatory(
                        SignatoryFactory.getInstance().createPKCS8Signatory(
                                new File(
                                        getClass().getClassLoader().getResource(TSP_PRIVATE_KEY_FILE).toURI()
                                ), null
                        ))
                .packetSignatory(
                        SignatoryFactory.getInstance().createPKCS8Signatory(
                                new File(
                                        getClass().getClassLoader().getResource(TAXPAYER_PRIVATE_KEY_FILE).toURI()
                                ), null
                        ));
        this.transferApi = new SimpleTransferApiImpl(apiConfig);
        this.taxApi = new DefaultTaxApiClient(transferApi, CLIENT_ID);
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

    void getServerInformation() {
        ServerInformationModel serverInformation = this.taxApi.getServerInformation();
        System.out.println("success load server information: " + serverInformation);
    }

    void sendInvoices() throws IOException, URISyntaxException {

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


        String data = mapper.writeValueAsString(invoiceDto);
        SimpleNormalizer normalizer = new SimpleNormalizer();
        String normalize = normalizer.normalize(data, null);

        Signatory signatory = SignatoryFactory.getInstance().createPKCS8Signatory(
                new File(
                        getClass().getClassLoader().getResource(TAXPAYER_PRIVATE_KEY_FILE).toURI()
                ), null
        );
        String sign = signatory.sign(normalize);

        PacketDto packetDto = new PacketDto();
        packetDto.setUid(UUID.randomUUID().toString());
        packetDto.setPacketType("INVOICE.V01");
        packetDto.setFiscalId(MEMORY);
        packetDto.setData(data);
        packetDto.setDataSignature(sign);
        packetDto.setRetry(false);

        HashMap<String, String> headers = new HashMap<>();
        if (token != null) {
            headers.put(TransferConstants.AUTHORIZATION_HEADER, "Bearer " + token);
        }
        AsyncResponseModel responseModel = transferApi.sendPackets(Collections.singletonList(packetDto), headers, true, true);

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


    void inquiryByReferenceId() {
        List<InquiryResultModel> inquiryResultModels = this.taxApi.inquiryByReferenceId(Collections.singletonList(this.referenceNumber));
        System.out.println("inquiry By Reference Id result: " + inquiryResultModels);
    }

}
