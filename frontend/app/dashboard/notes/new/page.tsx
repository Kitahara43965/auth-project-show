import NoteCreateForm from "@/src/note/components/NoteCreateForm";
import Link from "next/link";

export default function NoteCreatePage() {
  return (
    <main className="mx-auto max-w-4xl p-6">
      <div className="mb-6">
        <Link
          href="/dashboard/notes"
          className="text-blue-600 hover:underline"
        >
          ← メモ一覧に戻る
        </Link>
      </div>

      <h1 className="text-2xl font-bold mb-6">
        メモ新規作成
      </h1>

      <NoteCreateForm />
    </main>
  );
}