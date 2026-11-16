
export const deleteNote = async (
  id: number
): Promise<void> => {
  const response = await fetch(
    `/api/note/${id}`,
    {
      method: "DELETE",
      credentials: "include",
    }
  );

  if (!response.ok) {
    throw new Error("メモの削除に失敗しました");
  }
};
