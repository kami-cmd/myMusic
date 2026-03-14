package com.gwc.utils;


import io.jsonwebtoken.*;

import java.util.Date;

public class JwtUtils {
    private static final String SECRET = "WoDuiZheGeJueWangDeShiJieYiJingBuBaoYouRengHeQiDaiLe";
    private static final long EXPIRATION = 3600000;//1小时
    private static final long TEMPTIME = 300000;//5分钟

    public static String generateToken(Long userId) {
        return Jwts.builder()
                .claim("userId", userId) //设置信息
                .setIssuedAt(new Date())//设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))//过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET)//密钥和算法
                .compact();//完成
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parse(token);//解析
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static Long getUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("userId", Long.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }


    public static String generateTempToken(String message) {
        return Jwts.builder()
                .claim("message", message) //设置信息
                .setIssuedAt(new Date())//设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + TEMPTIME))//过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET)//密钥和算法
                .compact();//完成
    }


    public static String getemail(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("message", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
