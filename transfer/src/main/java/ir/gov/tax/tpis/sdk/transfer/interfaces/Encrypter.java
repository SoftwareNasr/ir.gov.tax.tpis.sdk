package ir.gov.tax.tpis.sdk.transfer.interfaces;

import ir.gov.tax.tpis.sdk.transfer.dto.PacketDto;
import ir.gov.tax.tpis.sdk.transfer.exception.TaxApiException;

import java.util.List;

public interface Encrypter {

    void encrypt(List<PacketDto> packets) throws TaxApiException;
}
