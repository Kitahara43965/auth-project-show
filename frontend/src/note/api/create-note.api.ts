import type { NoteCreateRequest } from
  "@/src/note/types/request/note-create-request.type";
import type { NoteResponse } from
  "@/src/note/types/response/note-response.type";
import type { NoteErrorResponse } from
  "@/src/note/types/response/note-error-response.type";
import { NoteApiError } from
  "@/src/note/error/note-api-error";

export const createNote = async (
  noteCreateRequest: NoteCreateRequest
): Promise<NoteResponse> => {

  const response = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/api/note`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(noteCreateRequest),
    }
  );

  if (!response.ok) {
  const errorResponse =
    await response.json().catch(() => null);

  console.log("API Error Response:", errorResponse);

  throw new NoteApiError(
    errorResponse?.errorCode
      ?? errorResponse?.code
      ?? "UNKNOWN_ERROR",

    errorResponse?.errorMessage
      ?? errorResponse?.message
      ?? `メモの作成に失敗しました（HTTP ${response.status}）`,

    errorResponse?.noteValidationDetails ?? null
  );
}

  return await response.json();
};