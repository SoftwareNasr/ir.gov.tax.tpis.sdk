package ir.gov.tax.tpis.sdk.transfer.dto;


import java.util.List;

public class AsyncRequestDto extends SignedRequest {

    private List<PacketDto> packets;

    public List<PacketDto> getPackets() {
        return packets;
    }

    public void setPackets(List<PacketDto> packets) {
        this.packets = packets;
    }
}
