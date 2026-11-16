"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";

import { findNote } from "@/src/note/api/find-note.api";
import type { NoteResponse } from
  "@/src/note/types/response/note-response.type";
import { deleteNote } from "@/src/note/api/delete-note.api";

export default function NoteDetailPage() {
  const params = useParams();
  const router = useRouter();

  const [note, setNote] = useState<NoteResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");
  const [deleting, setDeleting] = useState(false);

  const handleDelete = async () => {
  const confirmed = window.confirm(
    "このメモを削除してもよろしいですか？"
  );

  if (!confirmed) return;

    try {
        setDeleting(true);
        await deleteNote(note!.id);
        router.push("/dashboard/notes");
    } catch (error) {
        console.error(error);
        setErrorMessage("メモの削除に失敗しました");
        setDeleting(false);
    }
  };

  useEffect(() => {
    const loadNote = async () => {
      try {
        const response = await findNote(Number(params.id));
        setNote(response);
      } catch (error) {
        console.error(error);
        setErrorMessage("メモの取得に失敗しました");
      } finally {
        setLoading(false);
      }
    };

    loadNote();
  }, [params.id]);
  

  if (loading) {
    return <p>読み込み中...</p>;
  }

  if (errorMessage) {
    return <p className="text-red-500">{errorMessage}</p>;
  }

  if (!note) {
    return <p>メモが見つかりません。</p>;
  }

  return (
    <main className="p-6">
      <button
        type="button"
        onClick={() => router.push("/dashboard/notes")}
        className="mb-6 text-blue-600"
      >
        ← メモ一覧に戻る
      </button>

      <h1 className="text-2xl font-bold mb-4">
        {note.title}
      </h1>

      <p className="whitespace-pre-wrap">
        {note.content}
      </p>

      <p className="text-gray-400 text-sm mt-6">
        最終更新：{note.updatedAt}
      </p>

      <div className="mt-6 flex gap-3">
        <button
            type="button"
            onClick={() => router.push(`/dashboard/notes/${note.id}/edit`)}
            className="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
        >
            編集する
        </button>

        <button
            type="button"
            onClick={handleDelete}
            disabled={deleting}
            className="rounded bg-red-600 px-4 py-2 text-white hover:bg-red-700 disabled:opacity-50"
        >
            {deleting ? "削除中..." : "削除する"}
        </button>
      </div>
    </main>
  );
}