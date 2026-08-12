"use client";

import { useState } from "react";
import { getClientUserResponse } from "@/src/auth/services/get-client-user-response.service";

export default function ClientRefreshTokenButton(){

    const [loading,setLoading] = useState(false);

    async function handleClick(){

        setLoading(true);

        try{

            await getClientUserResponse();

        }finally{

            setLoading(false);

        }
    }

    return (
        <button
            onClick={handleClick} 
            className="
                rounded
                bg-blue-500
                px-4
                py-2
                text-white
                active:bg-green-500
            "
        >
        JWT / Refresh Token 確認
        </button>
    );
}