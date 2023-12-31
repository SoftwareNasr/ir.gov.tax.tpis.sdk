package ir.gov.tax.tpis.sdk.transfer.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.gov.tax.tpis.sdk.transfer.config.ApiConfig;
import ir.gov.tax.tpis.sdk.transfer.config.TransferConstants;
import ir.gov.tax.tpis.sdk.transfer.dto.*;
import ir.gov.tax.tpis.sdk.transfer.exception.TaxApiException;
import ir.gov.tax.tpis.sdk.transfer.http.HttpResponse;
import ir.gov.tax.tpis.sdk.transfer.interfaces.Signatory;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

public class SimpleTransferApiImpl implements TransferApi {

    private static Logger logger = Logger.getLogger(SimpleTransferApiImpl.class.getName());

    private final ApiConfig config;

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public SimpleTransferApiImpl(ApiConfig config) {
        this.config = config;
    }

    @Override
    public ApiConfig getConfig() {
        return config;
    }

    @Override
    public AsyncResponseModel sendPackets(List<PacketDto> packets,
                                          Map<String, String> headers,
                                          boolean encrypt,
                                          boolean sign) {

        if (packets == null || packets.isEmpty()) {
            return null;
        }
        if (headers == null) {
            headers = new HashMap<>();
        }
        fillEssentialHeaders(headers);

        if (sign) {
            packets.forEach(this::signPacket);
        }

        if (encrypt) {
            config.getEncrypter().encrypt(packets);
        }

        AsyncRequestDto request = new AsyncRequestDto();
        request.setPackets(packets);

        String sendData;
        try {
            sendData = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new TaxApiException("error in pars request json", e);
        }

        String normalText = config.getNormalizer().normalize(sendData, headers);
        String signData = config.getTransferSignatory().sign(normalText);
        headers.put(TransferConstants.SIGNATURE_HEADER, signData);
        if (config.getTransferSignatory().getKeyId() != null) {
            headers.put(TransferConstants.SIGNATURE_KEY_ID_HEADER, config.getTransferSignatory().getKeyId());
        }

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(config.getBaseUrl()).append('/');
        if (config.getApiVersion() != null) {
            urlBuilder.append(config.getApiVersion()).append('/');
        }
        urlBuilder.append("async/").append(config.getPriorityLevel() == PriorityLevel.HIGH ? "fast-enqueue" : "normal-enqueue");

        HttpResponse httpResponse = config.getHttpRequestSender().sendPostRequest(urlBuilder.toString(), sendData, headers);
        try {
            return mapper.readValue(httpResponse.getBody(), AsyncResponseModel.class);
        } catch (JsonProcessingException e) {
            throw new TaxApiException("error in pars response json", e);
        }
    }

    @Override
    public SyncResponseModel sendPacket(PacketDto packet,
                                        Map<String, String> headers,
                                        boolean encrypt,
                                        boolean sign) {
        if (packet == null) {
            return null;
        }
        if (headers == null) {
            headers = new HashMap<>();
        }
        fillEssentialHeaders(headers);

        if (sign) {
            signPacket(packet);
        }

        if (encrypt) {
            config.getEncrypter().encrypt(Collections.singletonList(packet));
        }

        SyncRequestDto request = new SyncRequestDto();
        request.setPacket(packet);
        String sendData;
        try {
            sendData = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new TaxApiException("error in pars request json", e);
        }

        String normalText = config.getNormalizer().normalize(sendData, headers);
        String signData = config.getTransferSignatory().sign(normalText);
        headers.put(TransferConstants.SIGNATURE_HEADER, signData);
        if (config.getTransferSignatory().getKeyId() != null) {
            headers.put(TransferConstants.SIGNATURE_KEY_ID_HEADER, config.getTransferSignatory().getKeyId());
        }

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(config.getBaseUrl()).append('/');
        if (config.getApiVersion() != null) {
            urlBuilder.append(config.getApiVersion()).append('/');
        }
        urlBuilder.append("sync/").append(packet.getPacketType().toUpperCase());

        HttpResponse httpResponse = config.getHttpRequestSender().sendPostRequest(urlBuilder.toString(), sendData, headers);

        if (httpResponse.getBody() == null || httpResponse.getBody().equals("")) {
            return null;
        }

        try {
            return mapper.readValue(httpResponse.getBody(), SyncResponseModel.class);
        } catch (JsonProcessingException e) {
            throw new TaxApiException("error in pars response json", e);
        }
    }

    private void fillEssentialHeaders(Map<String, String> essentialHeaders) {
        long now = Instant.now().toEpochMilli();
        if (!essentialHeaders.containsKey(TransferConstants.REQUEST_TRACE_ID_HEADER)) {
            essentialHeaders.put(TransferConstants.REQUEST_TRACE_ID_HEADER, UUID.randomUUID().toString());
        }
        if (!essentialHeaders.containsKey(TransferConstants.TIMESTAMP_HEADER)) {
            essentialHeaders.put(TransferConstants.TIMESTAMP_HEADER, String.valueOf(now));
        }
    }

    private void signPacket(PacketDto packet) {
        if (!(packet.getData() instanceof String)) {
            try {
                packet.setData(mapper.writeValueAsString(packet.getData()));
            } catch (JsonProcessingException e) {
                throw new TaxApiException("error is packet json process", e);
            }
        }
        Signatory signatory = config.getPacketSignatory();
        if (signatory == null) {
            signatory = config.getTransferSignatory();
        }

        String packetNormalize = config.getNormalizer().normalize(packet.getData(), null);
        String packetSignature = signatory.sign(packetNormalize);
        packet.setDataSignature(packetSignature);
        packet.setSignatureKeyId(signatory.getKeyId());
    }
}
