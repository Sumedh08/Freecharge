package com.invest.Trading.controller;

import com.invest.Trading.model.MutualFund;
import com.invest.Trading.service.MutualFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mutual-funds")
public class MutualFundController {

    @Autowired
    private MutualFundService mutualFundService;

    @GetMapping
    public ResponseEntity<List<MutualFund>> getTopMutualFunds() {
        return new ResponseEntity<>(mutualFundService.getTopMutualFunds(), HttpStatus.OK);
    }

    @GetMapping("/{schemeCode}")
    public ResponseEntity<MutualFund> getMutualFundDetails(@PathVariable String schemeCode) {
        return new ResponseEntity<>(mutualFundService.getMutualFundDetails(schemeCode), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MutualFund>> searchMutualFunds(@RequestParam String keyword) {
        return new ResponseEntity<>(mutualFundService.searchMutualFunds(keyword), HttpStatus.OK);
    }
}
