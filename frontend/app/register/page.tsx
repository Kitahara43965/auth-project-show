import RegisterForm from "@/src/auth/components/RegisterForm";
import {getServerUserResponse} from "@/src/auth/services/get-server-user-response.service";
import { PageRouteKind } from "@/src/routes/enums/page-route-kind.enum";
import { routeRedirectToDashboard } from "@/src/routes/services/route-redirect-to-dashboard";

export default async function LoginPage() {
  const userResponse = await getServerUserResponse();
  await routeRedirectToDashboard(userResponse,PageRouteKind.LOGIN);

  return (
    <div>
      <RegisterForm />
    </div>
  );
}