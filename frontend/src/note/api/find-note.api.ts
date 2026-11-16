
import type { NoteResponse } 
  from "@/src/note/types/response/note-response.type";

export const findNote = async (
  id: number
): Promise<NoteResponse> => {
  const response = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/api/note/${id}`,
    {
      method: "GET",
      credentials: "include",
    }
  );

  if (!response.ok) {
    throw new Error("メモの取得に失敗しました");
  }

  return await response.json();
};
