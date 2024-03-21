package com.zhaoyi.yijiaoyou.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static final Long expire = 86400000L;

    public static String generateJwt(Map<String ,Object> claims,String signKey){
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256,signKey)
                .setExpiration(new Date(System.currentTimeMillis()+expire))
                .compact();
    }
    public static Claims parseJwt(String jwt,String signKey){
        return Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();

    }
}
