
import { redirect } from "next/navigation";
import { PageRouteKind } from "@/src/routes/enums/page-route-kind.enum";
import { UserResponse } from "@/src/user/types/user-response.type";

export async function routeRedirectToDashboard(
  userResponse:UserResponse|null,
  pageRouteKind:PageRouteKind,
) {
  let stringRedirectedRoute:string|null = null;

  console.log(
    "[auth page] userResponse=",
    userResponse
  );
        
  if(pageRouteKind !== PageRouteKind.DASHBOARD){
    if(userResponse){
        stringRedirectedRoute = `/dashboard?reason=before_server_component_from_${pageRouteKind}`;
    }//userResponse
  }//pageRouteKind

  console.log("[routeRedirectToDashboard]stringRedirectedRoute=",stringRedirectedRoute);

  if(stringRedirectedRoute){
    redirect(stringRedirectedRoute);
  }//stringRedirectedRoute
  
}