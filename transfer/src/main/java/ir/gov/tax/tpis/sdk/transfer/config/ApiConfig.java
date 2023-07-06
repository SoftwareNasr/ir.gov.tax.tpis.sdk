package ir.gov.tax.tpis.sdk.transfer.config;

import ir.gov.tax.tpis.sdk.transfer.dto.PriorityLevel;
import ir.gov.tax.tpis.sdk.transfer.http.HttpRequestSender;
import ir.gov.tax.tpis.sdk.transfer.http.OkHttpRequestSender;
import ir.gov.tax.tpis.sdk.transfer.impl.normalize.ObjectNormalizer;
import ir.gov.tax.tpis.sdk.transfer.interfaces.Encrypter;
import ir.gov.tax.tpis.sdk.transfer.interfaces.Normalizer;
import ir.gov.tax.tpis.sdk.transfer.interfaces.Signatory;

public class ApiConfig {

    private HttpRequestSender httpRequestSender;

    private Normalizer normalizer;

    private Encrypter encrypter;

    private Signatory transferSignatory;

    private Signatory packetSignatory;

    private String baseUrl;

    private String apiVersion;

    private PriorityLevel priorityLevel;

    public ApiConfig() {
        this.httpRequestSender = new OkHttpRequestSender();
        this.normalizer = new ObjectNormalizer();
        this.baseUrl = "https://tp.tax.gov.ir/req/api/self-tsp";
        this.apiVersion = null;
        this.priorityLevel = PriorityLevel.NORMAL;
    }

    public ApiConfig httpRequestSender(HttpRequestSender httpRequestSender) {
        this.httpRequestSender = httpRequestSender;
        return this;
    }

    public ApiConfig normalizer(Normalizer normalizer) {
        this.normalizer = normalizer;
        return this;
    }

    public ApiConfig encrypter(Encrypter encrypter) {
        this.encrypter = encrypter;
        return this;
    }

    public ApiConfig transferSignatory(Signatory signatory) {
        this.transferSignatory = signatory;
        return this;
    }

    public ApiConfig packetSignatory(Signatory signatory) {
        this.packetSignatory = signatory;
        return this;
    }

    public ApiConfig baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ApiConfig apiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    public ApiConfig priorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
        return this;
    }

    public HttpRequestSender getHttpRequestSender() {
        return httpRequestSender;
    }

    public Normalizer getNormalizer() {
        return normalizer;
    }

    public Encrypter getEncrypter() {
        return encrypter;
    }

    public Signatory getTransferSignatory() {
        return transferSignatory;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public Signatory getPacketSignatory() {
        return packetSignatory;
    }
}
