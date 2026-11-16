import Link from "next/link";
import { getServerUserResponse } from
  "@/src/auth/services/get-server-user-response.service";

export default async function DashboardPage() {
  const userResponse = await getServerUserResponse();

  if (!userResponse) {
    return <p>ログインしてください</p>;
  }

  return (
    <main className="min-h-screen bg-gray-50 p-8">
      <div className="mx-auto max-w-4xl">
        <h1 className="mb-4 text-3xl font-bold">
          ダッシュボード
        </h1>

        <p className="mb-6 text-gray-700">
          ログイン中: {userResponse.name}
        </p>

        <div className="flex gap-4">
          <Link
            href="/dashboard/notes"
            className="rounded bg-blue-600 px-5 py-3 text-white"
          >
            メモ一覧を見る
          </Link>

          <Link
            href="/dashboard/notes/new"
            className="rounded bg-green-600 px-5 py-3 text-white"
          >
            新しいメモを作成
          </Link>
        </div>
      </div>
    </main>
  );
}