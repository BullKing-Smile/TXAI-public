package com.txai.apipassenger.intercetpor;

import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.txai.common.dto.ResponseResult;
import com.txai.common.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;
import org.json.JSONML;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String authorizetion = request.getHeader("Authorization");
        String resultString = "";
        boolean result = true;
        try {
            JwtUtils.parseToken(authorizetion);
        } catch (SignatureVerificationException e) {
            resultString = "token sign error";
            result = false;
        } catch (TokenExpiredException e) {
            resultString = "token expired";
            result = false;
        } catch (AlgorithmMismatchException e) {
            resultString = "token Algorithm mismatch";
            result = false;
        } catch (Exception e) {
            resultString = "token invalid";
            result = false;
        }
        if (!result) {
            System.out.println("Error message : "+ resultString);
            response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            response.getWriter().write(JSONStringer.valueToString(new JSONObject(ResponseResult.fail().setMessage(resultString))));
        }
        return result;
    }
}
