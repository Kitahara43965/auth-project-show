import type { NoteUpdateRequest } from
  "@/src/note/types/request/note-update-request.type";

import { NoteApiError } from
  "@/src/note/error/note-api-error";

export const updateNote = async (
  id: number,
  noteUpdateRequest: NoteUpdateRequest
): Promise<void> => {
  const response = await fetch(
    `/api/note/${id}`,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(noteUpdateRequest),
    }
  );

  if (!response.ok) {
    const noteErrorResponse = await response.json().catch(() => null);

    throw new NoteApiError(
      noteErrorResponse?.errorCode ?? noteErrorResponse?.code ?? "UNKNOWN_ERROR",
      noteErrorResponse?.errorMessage ?? noteErrorResponse?.message ?? "メモの更新に失敗しました",
      noteErrorResponse?.noteValidationDetails ?? null
    );
  }
};