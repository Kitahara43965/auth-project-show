"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { findNotes } from "@/src/note/api/find-notes.api";
import type { NoteResponse } from
  "@/src/note/types/response/note-response.type";

export default function NoteList() {

  const [notes, setNotes] = useState<NoteResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  const router = useRouter();

  useEffect(() => {
    const loadNotes = async () => {
      try {
        const response = await findNotes();
        setNotes(response.noteResponses);
      } catch (error) {
        console.error(error);
        setErrorMessage("メモ一覧の取得に失敗しました");
      } finally {
        setLoading(false);
      }
    };

    loadNotes();
  }, []);

  if (loading) {
    return <p>読み込み中...</p>;
  }

  if (errorMessage) {
    return <p className="text-red-500">{errorMessage}</p>;
  }

  if (notes.length === 0) {
    return <p>メモはまだありません。</p>;
  }

  return (
    <div className="flex flex-col gap-3">
      {notes.map((note) => (
        <button
          key={note.id}
          type="button"
          onClick={() =>
            router.push(`/dashboard/notes/${note.id}`)
          }
          className="border rounded-lg p-4 text-left hover:bg-gray-50"
        >
          <h2 className="font-bold">
            {note.title}
          </h2>

          <p className="text-gray-600 mt-2 line-clamp-2">
            {note.content}
          </p>

          <p className="text-gray-400 text-xs mt-3">
            更新日時：{note.updatedAt}
          </p>
        </button>
      ))}
    </div>
  );
}