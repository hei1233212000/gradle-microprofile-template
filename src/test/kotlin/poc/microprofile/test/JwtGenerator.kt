package poc.microprofile.test

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.eclipse.microprofile.jwt.Claims
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun main(args: Array<String>) {
    println("NO access right token")
    println(JwtGenerator.generateJWT())
    println()
    println("\"admin-group\" token")
    println(JwtGenerator.generateJWT("admin-group"))
    println()
    println("\"user-group\" token")
    println(JwtGenerator.generateJWT("user-group"))

    //JwtGenerator.generateKeys()
}

object JwtGenerator {
    private val signatureAlgorithm: SignatureAlgorithm = SignatureAlgorithm.RS256
    private const val keySize = 2048 // A key of size 2048 bits or larger MUST be used with these algorithms.
    private const val privateKeyPath = "/privateKey.pem"

    fun generateJWT(group: String = ""): String {
        /*
         * generate JWT as
           {
              "typ": "JWT",
              "kid": "/privateKey.pem",
              "alg": "RS256"
           }
           {
              "iss": "sample-issuer",
              "aud": "any value",
              "jti": "2e9afdb1-ecc1-4749-ae0f-3091ae919436",
              "exp": 1531574100,
              "iat": 1531570500,
              "sub": "any-subject",
              "upn": "any-principal-name",
              "groups": [
                "admin-group",
                "guest-group"
              ]
            }
         */
        val jwt = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setHeaderParam(Claims.kid.name, privateKeyPath)
            .setIssuer("sample-issuer")
            .setAudience("any value")
            .setId(UUID.randomUUID().toString())
            .setExpiration(createDateWhichIsOneHourLater())
            .setIssuedAt(Date())
            .setSubject("any-subject")
            .claim(Claims.upn.name, "any-principal-name") /* name of the Java Principal */
            .claim(Claims.groups.name, listOf(group))
            .signWith(signatureAlgorithm, findPrivateKey(signatureAlgorithm))
            .compact()

        // print out the JTW
        val jws = Jwts.parser().setSigningKey(findPublicKey(signatureAlgorithm)).parseClaimsJws(jwt)
        println(jws.header)
        println(jws.body)

        return jwt
    }

    fun generateKeys(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance(signatureAlgorithm.familyName)
        keyGen.initialize(keySize)

        val keyPair = keyGen.genKeyPair()
        val privateKey = keyPair.private
        val publicKey = keyPair.public

        printKey(privateKey)
        printKey(publicKey, false /* isPrivateKey */)

        return keyPair
    }

    private fun printKey(key: Key, isPrivateKey: Boolean = true) {
        val keyType = if (isPrivateKey) "PRIVATE" else "PUBLIC"
        println("-----BEGIN RSA $keyType KEY-----")
        println(Base64.getMimeEncoder().encodeToString(key.encoded))
        println("-----END RSA $keyType KEY-----")
        println()
    }

    private fun findPrivateKey(signatureAlgorithm: SignatureAlgorithm) = KeyFactory
        .getInstance(signatureAlgorithm.familyName)
        .generatePrivate(
            PKCS8EncodedKeySpec(
                Base64.getDecoder().decode(readKey(privateKeyPath))
            )
        )

    private fun findPublicKey(signatureAlgorithm: SignatureAlgorithm) = KeyFactory
        .getInstance(signatureAlgorithm.familyName)
        .generatePublic(
            X509EncodedKeySpec(
                Base64.getDecoder().decode(readKey("/publicKey.pem"))
            )
        )

    private fun readKey(keyPath: String): String {
        return JwtGenerator::class.java.getResource(keyPath)
            .readText()
            .replace("-----BEGIN (.*)-----".toRegex(), "")
            .replace("-----END (.*)----".toRegex(), "")
            .replace("\r\n", "")
            .replace("\n", "")
            .trim()
    }

    private fun createDateWhichIsOneHourLater(): Date {
        return Date.from(
            LocalDateTime.now().plusHours(1)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        )
    }
}
