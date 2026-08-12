
import DashboardForm from "@/src/dashboard/components/DashboardForm";
import {getServerUserResponse} from "@/src/auth/services/get-server-user-response.service";

type Props = {
  searchParams: Promise<{
    reason?: string;
  }>;
};

export default async function DashboardPage(
  {searchParams}: Props
) {
  const params = await searchParams;
  const reason = params.reason ?? null;
  const userName = null;
  const userResponse =
    await getServerUserResponse();

  if (!userResponse) {
    return (
      <p>
        ログインしてください
      </p>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="mx-auto max-w-4xl">
        <h1 className="text-3xl font-bold mb-4">
          ダッシュボード
        </h1>

        <p className="mb-6 text-gray-700">
          ログイン中:{userResponse.name} 
        </p>

        <DashboardForm/>
      </div>
    </div>
  );
}