package ir.gov.tax.tpis.sdk.transfer.api;

import ir.gov.tax.tpis.sdk.transfer.config.ApiConfig;
import ir.gov.tax.tpis.sdk.transfer.dto.AsyncResponseModel;
import ir.gov.tax.tpis.sdk.transfer.dto.PacketDto;
import ir.gov.tax.tpis.sdk.transfer.dto.SyncResponseModel;

import java.util.List;
import java.util.Map;

public interface TransferApi {

    ApiConfig getConfig();

    AsyncResponseModel sendPackets(List<PacketDto> packets, Map<String, String> headers, boolean encrypt, boolean sign);

    SyncResponseModel sendPacket(PacketDto packet, Map<String, String> headers, boolean encrypt, boolean sign);
}
