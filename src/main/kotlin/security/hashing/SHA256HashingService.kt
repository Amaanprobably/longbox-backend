package com.example.security.hashing

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

class SHA256HashingService:HashingService {
    override fun generateSaltedHash(value: String, saltLength: Int): SaltedHash {
        //Generate 32 bit random long string (bytearray) in a secure way
        val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
        //Convert the salt bytearray to a hexadecimal value that can be used with and added to hash string
        val saltAsHex = Hex.encodeHexString(salt)
        val hash = DigestUtils.sha256Hex(saltAsHex + value)
        return SaltedHash(hash = hash,salt = saltAsHex)
    }
    override fun verify(password: String, saltedHash: SaltedHash): Boolean {
        return DigestUtils.sha256Hex(saltedHash.salt + password)==(saltedHash.hash)
    }
}