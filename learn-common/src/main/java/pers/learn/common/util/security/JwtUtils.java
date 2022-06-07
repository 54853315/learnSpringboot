package pers.learn.common.util.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pers.learn.common.exception.ApiException;

import java.util.*;

@Slf4j
@Component
public class JwtUtils {
    public static String ISSUER;

    public static long EXPIRE_TIME;

    public static String SECRET_KEY;

    public static Algorithm ALGORITHM;

    @Value("${jwt.issuer}")
    public void setISSUER(String issuer) {
        ISSUER = issuer;
    }

    @Value("${jwt.expireTime}")
    public void setExpireTime(long expireTime) {
        EXPIRE_TIME = expireTime;
    }

    @Value("${jwt.secretKey}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
        ALGORITHM = Algorithm.HMAC256(SECRET_KEY);
    }

    public static long getExpireTimeForReal() {
        return EXPIRE_TIME * 1000 * 60;
    }

    /**
     * 从数据声明生成令牌
     * @param username
     * @param endpoint
     * @return
     */
    public static String generateToken(String username, String endpoint) {
        long currentTime = System.currentTimeMillis();
        log.info("=====generateToken===now    is " + new Date(currentTime));
        log.info("=====generateToken===expire is " + new Date(currentTime + getExpireTimeForReal()));

        String token = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(new Date(currentTime))// 签发时间
                .withExpiresAt(new Date(currentTime + getExpireTimeForReal()))// 过期时间戳
                .withClaim("username", username)//自定义参数
                .withClaim("endpoint", endpoint)
                .sign(ALGORITHM);

        return token;
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        try {
            //获得token中的信息无需secret解密也能获得
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 验证令牌，如果返回username，则代表验证通过，如果返回null代表验证不通过
     *
     * @param token
     * @return
     */
    public static Map<String, Claim> validateToken(String token) {
//    public static String validateToken(String token) {
        JWTVerifier verifier = JWT.require(ALGORITHM).withIssuer(ISSUER).acceptExpiresAt(EXPIRE_TIME).build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaims();
//            return jwt.getClaim("username").asString();
        } catch (TokenExpiredException exception) {
            log.trace("token过期了: {}", exception.getClass().getName());
        } catch (SignatureVerificationException exception) {
            log.trace("token错误: {}", exception.getClass().getName());
        } catch (JWTDecodeException exception) {
            log.trace("token解码错误: {}", exception.getClass().getName());
        }
        return null;
    }

}
