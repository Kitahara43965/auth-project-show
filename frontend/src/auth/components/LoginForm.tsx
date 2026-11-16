"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { getAuthBroadcastChannel } 
  from "@/src/auth/lib/auth-broadcast-channel/get-auth-broadcast-channel";
import type { AuthResponse } from "@/src/auth/types/auth-response.type";
import type { AuthErrorResponse } from "@/src/auth/types/auth-error-response.type";
import type {AuthValidationDetail} from "@/src/auth/types/auth-validation-detail.type";

export default function LoginForm() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [errors, setErrors] = useState<Record<string, string>>({});

  const router = useRouter();
  const getRegister = () => router.push("/register");

  const handleLogin = async () => {
    const validationErrorMap: Record<string, string> = {};
    let response:Response|null = null;
    let authResponse:AuthResponse|null = null;
    let authErrorResponse:AuthErrorResponse|null = null;
    let userId:number|null = null;
    let authValidationDetails:AuthValidationDetail[]|null = null;
    const authBroadcastChannel =
            getAuthBroadcastChannel();

    setLoading(true);
    setMessage("");
    setErrorMessage("");
    setErrors({});

    try {
      response = await fetch("/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });


      if(response.ok){
        if(response instanceof Response){
          authResponse = await response.json();
        }//response
        if(authResponse){
          userId = authResponse.userId;
        }//authResponse
        setErrorMessage("");
        setMessage("ログインしました。");
        setEmail("");
        setPassword("");

        if(authBroadcastChannel){
            authBroadcastChannel.postMessage({
                type:"LOGIN",
            });
            authBroadcastChannel.close();
        }

      }else{//response.ok
         if(response instanceof Response){
          authErrorResponse = await response.json();
        }//response
        if(authErrorResponse){
          authValidationDetails = authErrorResponse.authValidationDetails;
          if(authErrorResponse.errorMessage){
            setErrorMessage(authErrorResponse.errorMessage);
          }//authErrorResponse.errorMessage

          if(authValidationDetails){
            authValidationDetails.forEach(authValidationDetail => {
              validationErrorMap[authValidationDetail.field] = authValidationDetail.message;
            });
            setErrors(validationErrorMap);
          }
        }//authErrorResponse
      }//authResponse

      console.log();


    } catch (error) {
      console.error(error);
      setMessage("サーバーへ接続できませんでした。");
    } finally {
      setLoading(false);
    }

  };

  return (
    <div className="h-screen flex items-center justify-center bg-gray-100">
      <div className="w-80 p-6 bg-white rounded-xl shadow-md flex flex-col gap-3">
        <h2 className="text-center text-xl font-bold">ログイン</h2>

        <input
          className="w-full rounded border p-2"
          placeholder="メールアドレス"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        {errors.email && (
          <p className="text-sm text-red-500">
            {errors.email}
          </p>
        )}

        <input
          className="w-full rounded border p-2"
          type="password"
          placeholder="パスワード"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        {errors.password && (
          <p className="text-sm text-red-500">
            {errors.password}
          </p>
        )}

        {message && (
          <p className="text-center text-sm text-gray-700">
            {message}
          </p>
        )}

        <button
          type="button"
          onClick={handleLogin}
          className="p-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition"
        >
          ログイン
        </button>

        {errorMessage && (
          <p className="text-sm text-red-500">
            {errorMessage}
          </p>
        )}

        <button
          onClick={getRegister}
          className="w-full mt-4 text-blue-600 hover:no-underline text-center"
        >
          会員登録
        </button>
        
      </div>
    </div>
  );
}