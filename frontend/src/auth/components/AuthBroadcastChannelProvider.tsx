"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { getAuthBroadcastChannel }
   from "@/src/auth/lib/auth-broadcast-channel/get-auth-broadcast-channel";

export default function AuthBroadcastChannelProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const router = useRouter();

  useEffect(() => {
    const authBroadcastChannel = getAuthBroadcastChannel();
    
    const handler = async (messageEvent: MessageEvent) => {
        switch (messageEvent.data?.type) {
          case "LOGIN":
            router.replace("/dashboard");
            router.refresh();
            break;
          case "LOGOUT":
            router.replace("/login");
            router.refresh();
            break;
        }
    };

    if (authBroadcastChannel){
      authBroadcastChannel.onmessage = handler;
    }//authBroadcastChannel

    return () => {
      if(authBroadcastChannel){
        authBroadcastChannel.close();
      }//authBroadcastChannel
    };

  }, [router]);

  return <>{children}</>;
}