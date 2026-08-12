"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { getAuthBroadcastChannel }
  from "@/src/auth/lib/auth-broadcast-channel/get-auth-broadcast-channel";
import type { AuthResponse } from "@/src/auth/types/auth-response.type";
import type { ErrorResponse } from "@/src/common/types/error-response.type";
import type {ValidationDetail} from "@/src/common/types/validation-detail.type";

export default function RegisterForm() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [errors, setErrors] = useState<Record<string, string>>({});
  const router = useRouter();
  const getLogin = () => router.push("/login");

  const handleRegister = async () => {
    const validationErrorMap: Record<string, string> = {};
    let response:Response|null = null;
    let userId:number|null = null;
    let authResponse:AuthResponse|null = null;
    let errorResponse:ErrorResponse|null = null;
    let validationDetails:ValidationDetail[]|null = null;
    const authBroadcastChannel =
            getAuthBroadcastChannel();

    setLoading(true);
    setMessage("");
    setErrorMessage("");
    setErrors({});

    try {
      response = await fetch("/api/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name,
          email,
          password,
          confirmPassword,
        }),
      });
      
      if(response.ok){
        if(response instanceof Response){
          authResponse = await response.json();
        }//response
        if(authResponse){
          userId = authResponse.userId;
        }//authResponse
        setMessage("登録しました。認証メールを確認してください。");
        setErrorMessage("");
        setName("");
        setEmail("");
        setPassword("");
        setConfirmPassword("");
        if(authBroadcastChannel){
            authBroadcastChannel.postMessage({
                type:"LOGIN",
            });
            authBroadcastChannel.close();
        }
      }else{
        if(response instanceof Response){
          errorResponse = await response.json();
        }//response
        if(errorResponse){
          validationDetails = errorResponse.validationDetails;
          if(errorResponse.errorMessage){
            setErrorMessage(errorResponse.errorMessage);
          }//errorResponse.errorMessage
          if(validationDetails){
            validationDetails.forEach(validationDetail => {
              validationErrorMap[validationDetail.field] = validationDetail.message;
            });
            setErrors(validationErrorMap);
          }
        }//errorResponse
      }//response

    } catch (error) {
      console.error(error);
      setMessage("サーバーへ接続できませんでした。");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="w-96 rounded-xl bg-white p-8 shadow-lg">
        <h1 className="mb-6 text-center text-2xl font-bold">
          会員登録
        </h1>

        <div className="space-y-4">
          <input
            className="w-full rounded border p-2"
            type="text"
            placeholder="ユーザー名"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          {errors.name && (
            <p className="text-sm text-red-500">
              {errors.name}
            </p>
          )}

          <input
            className="w-full rounded border p-2"
            type="email"
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

          <input
            className="w-full rounded border p-2"
            type="password"
            placeholder="パスワード（確認）"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />
          {errors.confirmPassword && (
            <p className="text-sm text-red-500">
              {errors.confirmPassword}
            </p>
          )}

          {message && (
            <p className="text-center text-sm text-gray-700">
              {message}
            </p>
          )}

          <button
            onClick={handleRegister}
            disabled={loading}
            className="w-full rounded bg-blue-600 py-2 text-white hover:bg-blue-700 disabled:bg-gray-400"
          >
            {loading ? "登録中..." : "登録"}
          </button>

          {errorMessage && (
            <p className="text-sm text-red-500">
              {errorMessage}
            </p>
          )}

          <button
            onClick={getLogin}
            className="w-full mt-4 text-blue-600 hover:no-underline text-center"
          >
            ログイン
          </button>

          
        </div>
      </div>
    </div>
  );
}