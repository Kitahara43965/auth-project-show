import { JwtPayload } from "./jwt-payload.type";

export type AccessTokenJwtPayload = JwtPayload & {
  name: string;
  userId: number;
};