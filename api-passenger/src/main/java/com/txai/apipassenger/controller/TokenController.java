package com.txai.apipassenger.controller;

import com.txai.apipassenger.service.TokenService;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.TokenResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/refresh-token")
    public ResponseResult refreshToken(@RequestBody TokenResponse tokenResponse) {
        return tokenService.getRefreshToken(tokenResponse.getRefreshToken());
    }
}
