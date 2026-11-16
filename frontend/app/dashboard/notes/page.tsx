import NoteList from "@/src/note/components/NoteList";
import Link from "next/link";

export default function NotesPage() {
  return (
    <main className="mx-auto max-w-4xl p-6">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-bold">
          メモ一覧
        </h1>

        <Link
          href="/dashboard/notes/new"
          className="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
        >
          ＋ 新規作成
        </Link>
      </div>

      <NoteList />
    </main>
  );
}