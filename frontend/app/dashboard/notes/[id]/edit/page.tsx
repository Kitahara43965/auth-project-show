
"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";

import { findNote } from "@/src/note/api/find-note.api";
import { updateNote } from "@/src/note/api/update-note.api";

import type { NoteResponse } from
  "@/src/note/types/response/note-response.type";
import { NoteApiError } from "@/src/note/error/note-api-error";
import type { NoteValidationDetail } from
  "@/src/note/types/validation/note-validation-detail.type";

export default function NoteEditPage() {
  const params = useParams();
  const router = useRouter();

  const id = Number(params.id);

  const [note, setNote] = useState<NoteResponse | null>(null);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");
  const [validationErrors, setValidationErrors] =
    useState<NoteValidationDetail[]>([]);

  useEffect(() => {
    if (!Number.isInteger(id) || id <= 0) {
        return;
    }

    const loadNote = async () => {
        try {
        const response = await findNote(id);

        setNote(response);
        setTitle(response.title);
        setContent(response.content);
        } catch (error) {
        console.error(error);
        setErrorMessage("メモの取得に失敗しました");
        } finally {
        setLoading(false);
        }
    };

    loadNote();
  }, [id]);

  const handleSubmit = async (
    event: React.FormEvent<HTMLFormElement>
    ) => {
    event.preventDefault();

    if (!note) return;

    setErrorMessage("");
    setValidationErrors([]);

    try {
        await updateNote(id, {
            title,
            content,
            version: note.version,
        });

        router.push(`/dashboard/notes/${id}`);
    } catch (error) {
        if (error instanceof NoteApiError) {
            setErrorMessage(error.message);
            setValidationErrors(
                error.noteValidationDetails ?? []
            );
            } else {
                setErrorMessage("メモの更新に失敗しました");
            }
        }
    };

  if (loading) {
    return <p>読み込み中...</p>;
  }

  if (errorMessage && !note) {
    return <p className="text-red-500">{errorMessage}</p>;
  }

  if (!note) {
    return <p>メモが見つかりません。</p>;
  }

  return (
    <main className="mx-auto max-w-4xl p-6">
      <h1 className="text-2xl font-bold mb-6">
        メモ編集
      </h1>

      {errorMessage && (
        <p className="mb-4 text-red-500">
          {errorMessage}
        </p>
      )}

      <form onSubmit={handleSubmit} className="space-y-5" noValidate>
        <div>
          <label className="block mb-2 font-bold">
            タイトル
          </label>

          <input
            type="text"
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            maxLength={200}
            required
            className="w-full rounded border p-3"
          />

          {validationErrors
            .filter((error) => error.field === "title")
            .map((error, index) => (
                <p key={index} className="mt-1 text-sm text-red-500">
                {error.message}
                </p>
            ))}
        </div>

        <div>
          <label className="block mb-2 font-bold">
            本文
          </label>

          <textarea
            value={content}
            onChange={(event) => setContent(event.target.value)}
            required
            rows={10}
            className="w-full rounded border p-3"
          />

          {validationErrors
            .filter((error) => error.field === "content")
            .map((error, index) => (
            <p
                key={index}
                className="mt-1 text-sm text-red-500"
            >
                {error.message}
            </p>
          ))}
        </div>

        <div className="flex gap-3">
          <button
            type="submit"
            className="rounded bg-blue-600 px-5 py-2 text-white hover:bg-blue-700"
          >
            更新する
          </button>

          <button
            type="button"
            onClick={() => router.push(`/dashboard/notes/${id}`)}
            className="rounded border px-5 py-2"
          >
            キャンセル
          </button>
        </div>
      </form>
    </main>
  );
}
