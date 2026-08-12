import { JwtPayload } from "@/src/auth/jwt/types/jwt-payload.type";

export type RefreshTokenJwtPayload = JwtPayload & {
  jti: string;
};