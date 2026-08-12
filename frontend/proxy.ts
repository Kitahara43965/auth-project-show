import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { CookieType } from "@/src/auth/cookie/types/cookie-type";
import { JwtUtil } from "@/src/auth/jwt/utils/jwt.util";


export async function proxy(
  nextRequest: NextRequest
) {
  const accessToken =
    nextRequest.cookies.get(
      CookieType.ACCESS_TOKEN.name
    );
  const refreshToken =
    nextRequest.cookies.get(
      CookieType.REFRESH_TOKEN.name
    );
  const pathname = nextRequest.nextUrl.pathname;
  const isAuthPage =
    pathname === "/login" ||
    pathname === "/register";
  let isAccessTokenValueExpired:boolean = false;
  let accessTokenValue:string|null  = null;
  let refreshTokenValue:string|null  = null;
  let refreshResponse:Response|null = null;
  let nextResponse:NextResponse = NextResponse.next();
  let setCookies:string[]|null = null;
  let proxyExitMarker:number = 0;
  
  console.log("Middleware Entrance");

  if(accessToken){
    accessTokenValue = accessToken.value;
  }//accessToken
  if(refreshToken){
    refreshTokenValue = refreshToken.value;
  }//refreshToken

  isAccessTokenValueExpired = false;
  if(accessTokenValue){
    isAccessTokenValueExpired = JwtUtil.isTokenExpired(
      accessTokenValue
    )
  }else{//accessTokenValue
    isAccessTokenValueExpired = true;
  }//accessToken

  if (isAccessTokenValueExpired) {
    if(refreshTokenValue){
      refreshResponse =
        await fetch(
          `${process.env.NEXT_PUBLIC_API_URL}/api/refresh`,
          {
            method:"POST",

            headers:{
              Cookie:
              `REFRESH_TOKEN=${refreshTokenValue}`
            }
          }
        );


      if(refreshResponse.ok){

        setCookies = refreshResponse.headers.getSetCookie();
        nextResponse = NextResponse.redirect(
          nextRequest.url
        );

        for(const cookie of setCookies){
          nextResponse.headers.append(
            "set-cookie",
            cookie
          );
        }

        proxyExitMarker = 1;

      }else{

        if(isAuthPage){
          nextResponse = NextResponse.next();
          proxyExitMarker = 2;
        }else{//isAuthPage
          nextResponse =  NextResponse.redirect(
            new URL(
              "/login",
              nextRequest.url
            )
          );

          proxyExitMarker = 3;
        }

      }

    }else{//refreshTokenValue

      if(isAuthPage){
        nextResponse = NextResponse.next();
        proxyExitMarker = 4;
      }else{//isAuthPage
        console.log("refreshtokenがない[middleware]");
        nextResponse = NextResponse.redirect(
          new URL(
            "/login",
            nextRequest.url
          )
        );
        proxyExitMarker = 5;
      }//isAuthPage
    }
  }else{//isAccessTokenValueExpired
    nextResponse = NextResponse.next();
    proxyExitMarker = 6;
  }//isAccessTokenValueExpired
  
  return nextResponse;

}//middleware


export const config = {
  matcher:[
    "/dashboard/:path*",
    "/login",
    "/register"
  ],
};