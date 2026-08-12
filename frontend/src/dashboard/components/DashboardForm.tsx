"use client";

import { clientApiFetch } from "@/src/common/lib/api/client-api-fetch";

import { useEffect, useState } from "react";
import { z } from "zod";
import { useRouter } from "next/navigation";
import ClientRefreshTokenButton from "@/src/auth/components/ClientRefreshTokenButton";

export default function DashboardForm() {
  const router = useRouter();
  useEffect(() => {
    
    const handler = () => {
      if (document.visibilityState === "visible") {
        router.refresh();
      }
    };

    document.addEventListener(
      "visibilitychange",
      handler
    );

    return () => {
      document.removeEventListener(
        "visibilitychange",
        handler
      );
    };
  }, []);


  return (
      <ClientRefreshTokenButton />
  );
}