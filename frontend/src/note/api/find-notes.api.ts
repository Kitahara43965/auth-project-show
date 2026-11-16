
import type { NoteListResponse } 
    from "@/src/note/types/response/note-list-response.type";

export const findNotes = async (): Promise<NoteListResponse> => {
  const response = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/api/note`,
    {
        method: "GET",
        credentials: "include",
    }
  );

  if (!response.ok) {
    throw new Error("メモ一覧の取得に失敗しました");
  }

  return await response.json();
};
