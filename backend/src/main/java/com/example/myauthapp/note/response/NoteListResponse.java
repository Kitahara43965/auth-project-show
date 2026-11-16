package com.example.myauthapp.note.response;

import java.util.List;
import com.example.myauthapp.note.response.NoteResponse;

public class NoteListResponse {

    private final List<NoteResponse> notes;

    public NoteListResponse(List<NoteResponse> notes) {
        this.notes = notes;
    }

    public List<NoteResponse> getNoteResponses() {
        return notes;
    }
}