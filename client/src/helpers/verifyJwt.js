import * as jose from 'jose';

export const verifyJwt = async (token) => {
    const jwtSecretKey = "TaqlmGv1iEDMRiFp/pHuID1+T84IABfuA0xXh4GhiUI=";
    try {
        const key = await jose.importJWK({
            alg: 'HS256',
            kty: 'oct',
            k: jwtSecretKey
        });
        const { payload } = await jose.jwtVerify(token, key);
        return payload;
    } catch (error) {
        console.error(error);
    }
}