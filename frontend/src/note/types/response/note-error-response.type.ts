import type { NoteValidationDetail } 
    from "@/src/note/types/validation/note-validation-detail.type";

export type NoteErrorResponse = {
  errorCode: string;
  errorMessage: string;
  noteValidationDetails: NoteValidationDetail[] | null;
};