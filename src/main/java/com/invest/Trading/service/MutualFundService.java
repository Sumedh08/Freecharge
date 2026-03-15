package com.invest.Trading.service;

import com.invest.Trading.model.MutualFund;
import java.util.List;

public interface MutualFundService {
    List<MutualFund> getTopMutualFunds();

    MutualFund getMutualFundDetails(String schemeCode);

    List<MutualFund> searchMutualFunds(String keyword);
}
