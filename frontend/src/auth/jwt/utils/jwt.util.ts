import { AccessTokenJwtPayload } from "@/src/auth/jwt/types/access-token-jwt-payload.type";

export class JwtUtil {
    static isTokenExpired(token: string|null) {
      const now = Math.floor(Date.now() / 1000);
      const bufferTime = 0;
      let parts:string[]|null = null;
      let isJwtStructureValid:boolean = false;
      let stringPayload:string = "";
      let isTokenExpiredResult:boolean = false;
      let accessTokenJwtPayload:AccessTokenJwtPayload|null = null;
      let accessTokenJwtPayloadExp:number = 0;
    
      if(token){
        parts = token.split(".");
        if(parts.length === 3){
          stringPayload = Buffer.from(parts[2-1],"base64").toString();
          isJwtStructureValid = true;
        }
      }//token
    
      try {
        if(isJwtStructureValid === true){
          accessTokenJwtPayload = JSON.parse(stringPayload) as AccessTokenJwtPayload;
          if(accessTokenJwtPayload){
            accessTokenJwtPayloadExp = accessTokenJwtPayload.exp;
          }//accessTokenJwtPayload
          if(accessTokenJwtPayloadExp != undefined){
            isTokenExpiredResult = (accessTokenJwtPayloadExp <= now + bufferTime);
          }else{
            isTokenExpiredResult = true;
          }
        }else{//isJwtStructureValid
          isTokenExpiredResult = true;
        }//isJwtStructureValid
      } catch {
        isTokenExpiredResult = true;
      }
    
      return isTokenExpiredResult;
    
    }
}