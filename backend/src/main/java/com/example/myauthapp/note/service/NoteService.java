package com.example.myauthapp.note.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.myauthapp.note.dto.NoteCreateDTO;
import com.example.myauthapp.note.dto.NoteDTO;
import com.example.myauthapp.note.dto.NoteUpdateDTO;
import com.example.myauthapp.note.repository.create.NoteCreateJdbcRepository;
import com.example.myauthapp.note.repository.delete.NoteDeleteJdbcRepository;
import com.example.myauthapp.note.repository.discovery.NoteDiscoveryJdbcRepository;
import com.example.myauthapp.note.repository.update.NoteUpdateJdbcRepository;

@Service
public class NoteService {

    private final NoteCreateJdbcRepository noteCreateJdbcRepository;
    private final NoteDiscoveryJdbcRepository noteDiscoveryJdbcRepository;
    private final NoteUpdateJdbcRepository noteUpdateJdbcRepository;
    private final NoteDeleteJdbcRepository noteDeleteJdbcRepository;

    public NoteService(
        NoteCreateJdbcRepository noteCreateJdbcRepository,
        NoteDiscoveryJdbcRepository noteDiscoveryJdbcRepository,
        NoteUpdateJdbcRepository noteUpdateJdbcRepository,
        NoteDeleteJdbcRepository noteDeleteJdbcRepository
    ) {
        this.noteCreateJdbcRepository = Objects.requireNonNull(
            noteCreateJdbcRepository
        );
        this.noteDiscoveryJdbcRepository = Objects.requireNonNull(
            noteDiscoveryJdbcRepository
        );
        this.noteUpdateJdbcRepository = Objects.requireNonNull(
            noteUpdateJdbcRepository
        );
        this.noteDeleteJdbcRepository = Objects.requireNonNull(
            noteDeleteJdbcRepository
        );
    }

    // Create
    public NoteDTO createNote(
        NoteCreateDTO noteCreateDTO,
        Long userId
    ) {
        return noteCreateJdbcRepository
            .createNoteByNoteCreateDTOAndUserId(
                noteCreateDTO,
                userId
            );
    }

    // Read: 一覧取得
    public List<NoteDTO> findNotes(Long userId) {
        return noteDiscoveryJdbcRepository
            .findNoteDTOListByUserId(userId);
    }

    // Read: 詳細取得
    public NoteDTO findNote(
        Long id,
        Long userId
    ) {
        return noteDiscoveryJdbcRepository
            .findNoteDTOByIdAndUserId(id, userId);
    }

    // Update
    public void updateNote(
        Long id,
        Long userId,
        NoteUpdateDTO noteUpdateDTO
    ) {
        noteUpdateJdbcRepository
            .updateNoteByIdAndUserIdAndNoteUpdateDTO(
                id,
                userId,
                noteUpdateDTO
            );
    }

    // Delete
    public void deleteNote(
        Long id,
        Long userId
    ) {
        noteDeleteJdbcRepository
            .deleteNoteByIdAndUserId(id, userId);
    }
}