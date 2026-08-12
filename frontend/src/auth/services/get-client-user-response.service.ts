import { clientApiFetch } from "@/src/common/lib/api/client-api-fetch";

export async function getClientUserResponse(){

  const response =
    await clientApiFetch("/api/me");

  if(!response.ok){
    return null;
  }

  return response.json();

}