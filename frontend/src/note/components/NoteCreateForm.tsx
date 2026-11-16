
"use client";

import { useState } from "react";

import { createNote } from "@/src/note/api/create-note.api";

import type { NoteCreateRequest }
  from "@/src/note/types/request/note-create-request.type";

import type { NoteValidationDetail }
  from "@/src/note/types/validation/note-validation-detail.type";

import { NoteApiError }
  from "@/src/note/error/note-api-error";

export default function CreateNoteForm() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const [validationErrors, setValidationErrors] =
    useState<NoteValidationDetail[]>([]);

  const handleCreateNote = async () => {
    setLoading(true);
    setMessage("");
    setErrorMessage("");
    setValidationErrors([]);

    try {
      const noteCreateRequest: NoteCreateRequest = {
        title,
        content,
      };

      await createNote(noteCreateRequest);

      setTitle("");
      setContent("");
      setMessage("メモを作成しました。");

    } catch (error) {
      console.error("発生したエラー:", error);
        console.error("NoteApiErrorか:", error instanceof NoteApiError);

        if (error instanceof NoteApiError) {
            console.log("errorCode:", error.errorCode);
            console.log("message:", error.message);
            console.log(
            "noteValidationDetails:",
            error.noteValidationDetails
            );

            setValidationErrors(
            error.noteValidationDetails ?? []
            );

            if (error.errorCode !== "VALIDATION_ERROR") {
            setErrorMessage(error.message);
            }

        } else {
            setErrorMessage(
            error instanceof Error
                ? error.message
                : "メモの作成に失敗しました。"
            );
        }

    } finally {
      setLoading(false);
    }
  };

  const getFieldErrors = (field: string) => {
    return validationErrors.filter(
      (error) => error.field === field
    );
  };

  return (
    <div className="flex flex-col gap-4 w-full max-w-xl">

      <h2 className="text-xl font-bold">
        メモ作成
      </h2>

      {/* タイトル */}
      <div className="flex flex-col gap-1">
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="タイトル"
          maxLength={200}
          aria-invalid={getFieldErrors("title").length > 0}
          className="border rounded p-2"
        />

        {getFieldErrors("title").map((error, index) => (
          <p
            key={index}
            role="alert"
            className="text-red-500 text-sm"
          >
            {error.message}
          </p>
        ))}
      </div>

      {/* 本文 */}
      <div className="flex flex-col gap-1">
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="本文"
          rows={10}
          aria-invalid={getFieldErrors("content").length > 0}
          className="border rounded p-2"
        />

        {getFieldErrors("content").map((error, index) => (
          <p
            key={index}
            role="alert"
            className="text-red-500 text-sm"
          >
            {error.message}
          </p>
        ))}
      </div>

      {/* 成功メッセージ */}
      {message && (
        <p className="text-green-600">
          {message}
        </p>
      )}

      {/* 通常エラー */}
      {errorMessage && (
        <p role="alert" className="text-red-500">
          {errorMessage}
        </p>
      )}

      <button
        type="button"
        onClick={handleCreateNote}
        disabled={loading}
        className="bg-blue-500 text-white rounded p-2 disabled:opacity-50"
      >
        {loading ? "作成中..." : "メモを作成"}
      </button>

    </div>
  );
}
