package com.invest.Trading.controller;

import com.invest.Trading.exception.AssetNotFoundException;
import com.invest.Trading.exception.UserNotFoundException;
import com.invest.Trading.model.Asset;
import com.invest.Trading.model.User;
import com.invest.Trading.service.AssetService;
import com.invest.Trading.service.UserService;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;
    private final UserService userService;

    @Autowired
    public AssetController(AssetService assetService, UserService userService) {
        this.assetService = assetService;
        this.userService = userService;
    }

    @GetMapping("/{assetId}")
    public ResponseEntity<Asset> getAssetById(@PathVariable Long assetId) {
        Asset asset = assetService.getAssetById(assetId);
        return ResponseEntity.ok(asset);
    }

    @GetMapping("/stock/{stockId}/user")
    public ResponseEntity<?> getAssetByUserIdAndStockId(
            @PathVariable String stockId,
            @RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserProfileByJwt(jwt);
            Asset asset = assetService.findAssetByUserIdAndStockId(user.getId(), stockId);
            return ResponseEntity.ok(asset);
        } catch (UserNotFoundException | AssetNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAssetsForUser(
            @RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserProfileByJwt(jwt);
            List<Asset> assets = assetService.getUsersAssets(user.getId());
            return ResponseEntity.ok(assets);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}