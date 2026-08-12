import type {ValidationDetail} from "@/src/common/types/validation-detail.type";

export type ErrorResponse = {
  errorCode: string;
  errorMessage: string;
  validationDetails: ValidationDetail[]|null;
};