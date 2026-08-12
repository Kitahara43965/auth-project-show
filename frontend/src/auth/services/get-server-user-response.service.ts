import { serverApiFetch } from "@/src/auth/lib/api/server-api-fetch";
import { UserResponse } from "@/src/user/types/user-response.type";

export async function getServerUserResponse(): Promise<UserResponse | null> {

    const response =
        await serverApiFetch("/api/me");

    if(!response.ok){
        return null;
    }

    return response.json();
}