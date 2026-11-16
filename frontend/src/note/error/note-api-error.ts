
import type { NoteValidationDetail }
  from "@/src/note/types/validation/note-validation-detail.type";

export class NoteApiError extends Error {

  constructor(
    public readonly errorCode: string,
      message: string,
    public readonly noteValidationDetails:
      NoteValidationDetail[] | null
  ) {
    super(message);
    this.name = "NoteApiError";
  }
}
