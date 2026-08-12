import { cookies } from "next/headers";
import { CookieType } from "@/src/auth/cookie/types/cookie-type";
import {CookieHeader} from "@/src/auth/services/statics/get-cookie-header";


export async function serverApiFetch(
  url:string,
  options:RequestInit={}
){

  const cookieStore = await cookies();
  const accessToken =
    (await cookies()).get(
      CookieType.ACCESS_TOKEN.name
    );

  return fetch(
    `${process.env.NEXT_PUBLIC_API_URL}${url}`,
    {
      ...options,
      headers:{
        ...options.headers,
        ...(accessToken && {
          Cookie:CookieHeader.getCookieHeader(CookieType.ACCESS_TOKEN.name,accessToken.value)
        })
      },

      cache:"no-store"
    }
  );
}