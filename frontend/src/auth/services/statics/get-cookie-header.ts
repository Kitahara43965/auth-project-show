export class CookieHeader{
    static getCookieHeader(
        tokenName:string,
        tokenValue:string
    ){
        return `${tokenName}=${tokenValue}`;
    };
}//CookieHeader