import type {AuthValidationDetail} from "@/src/auth/types/auth-validation-detail.type";

export type AuthErrorResponse = {
  errorCode: string;
  errorMessage: string;
  authValidationDetails: AuthValidationDetail[]|null;
};