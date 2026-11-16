package com.example.myauthapp.note.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.example.myauthapp.note.dto.NoteCreateDTO;
import com.example.myauthapp.note.dto.NoteDTO;
import com.example.myauthapp.note.dto.NoteUpdateDTO;
import com.example.myauthapp.note.service.NoteService;
import com.example.myauthapp.note.request.NoteCreateRequest;
import com.example.myauthapp.note.request.NoteUpdateRequest;
import com.example.myauthapp.note.response.NoteResponse;
import com.example.myauthapp.note.response.NoteListResponse;

@RestController
@RequestMapping("/api")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // Create
    @PostMapping("/note")
    public ResponseEntity<NoteResponse> createNote(
        @Valid @RequestBody NoteCreateRequest noteCreateRequest
    ) {

        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        Long userId = (Long) authentication.getPrincipal();

        NoteCreateDTO noteCreateDTO = new NoteCreateDTO(
            noteCreateRequest.getTitle(),
            noteCreateRequest.getContent()
        );

        NoteDTO createdNoteDTO = noteService.createNote(
            noteCreateDTO,
            userId
        );

        NoteResponse noteResponse = new NoteResponse(createdNoteDTO);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(noteResponse);
    }

    // Read: 一覧
    @GetMapping("/note")
    public ResponseEntity<NoteListResponse> findNotes() {

        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        Long userId = (Long) authentication.getPrincipal();

        List<NoteResponse> noteResponses =
            noteService.findNotes(userId)
                .stream()
                .map(NoteResponse::new)
                .toList();

        NoteListResponse noteListResponse =
            new NoteListResponse(noteResponses);

        return ResponseEntity.ok(noteListResponse);
    }

    // Read: 詳細
    @GetMapping("/note/{id}")
    public ResponseEntity<NoteResponse> findNote(
        @PathVariable Long id
    ) {

        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        Long userId = (Long) authentication.getPrincipal();

        NoteDTO noteDTO = noteService.findNote(
            id,
            userId
        );

        NoteResponse noteResponse = new NoteResponse(noteDTO);

        return ResponseEntity.ok(noteResponse);
    }

    // Update
    @PutMapping("/note/{id}")
    public ResponseEntity<Void> updateNote(
        @PathVariable Long id,
        @Valid @RequestBody NoteUpdateRequest noteUpdateRequest
    ) {

        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        Long userId = (Long) authentication.getPrincipal();

        NoteUpdateDTO noteUpdateDTO = new NoteUpdateDTO(
            noteUpdateRequest.getTitle(),
            noteUpdateRequest.getContent(),
            noteUpdateRequest.getVersion()
        );

        noteService.updateNote(id, userId, noteUpdateDTO);

        return ResponseEntity.noContent().build();
    }

    // Delete
    @DeleteMapping("/note/{id}")
    public ResponseEntity<Void> deleteNote(
        @PathVariable Long id
    ) {

        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        Long userId = (Long) authentication.getPrincipal();

        noteService.deleteNote(
            id,
            userId
        );

        return ResponseEntity.noContent().build();
    }
}