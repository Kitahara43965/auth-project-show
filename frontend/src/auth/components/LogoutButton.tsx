"use client";

import { useRouter } from "next/navigation";
import { getAuthBroadcastChannel } from "@/src/auth/lib/auth-broadcast-channel/get-auth-broadcast-channel";

export default function LogoutButton() {

  const router = useRouter();

  const handleLogout = async () => {

    const authBroadcastChannel =
            getAuthBroadcastChannel();

    const response = await fetch(
      `/api/logout`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
      }
    );

    if(authBroadcastChannel){
        authBroadcastChannel.postMessage({
            type:"LOGOUT",
        });
        authBroadcastChannel.close();
    }
  };

  return (
    <button
      onClick={handleLogout}
      className="text-red-500"
    >
      ログアウト
    </button>
  );
}